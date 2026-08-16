package com.example.service

import android.app.Activity
import android.app.Presentation
import android.content.Context
import android.hardware.display.DisplayManager
import android.os.Build
import android.os.Bundle
import android.os.PowerManager
import android.util.DisplayMetrics
import android.view.Display
import android.view.View
import android.view.Window
import android.view.WindowManager
import android.widget.FrameLayout
import androidx.compose.ui.platform.ComposeView
import com.example.data.ProfileEntity
import com.example.model.CalibrationPatternType
import com.example.model.ConnectedDisplay
import com.example.model.DisplayTelemetry
import com.example.model.DisplayType
import com.example.ui.presentation.ExternalPresentationContent
import com.example.ui.theme.DisplayMasterTheme
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class DisplayPresentationManager(private val activity: Activity) {

  private val displayManager = activity.getSystemService(Context.DISPLAY_SERVICE) as DisplayManager
  private val powerManager = activity.getSystemService(Context.POWER_SERVICE) as? PowerManager

  private var wakeLock: PowerManager.WakeLock? = null
  private var activePresentation: Presentation? = null

  private val _displays = MutableStateFlow<List<ConnectedDisplay>>(emptyList())
  val displays: StateFlow<List<ConnectedDisplay>> = _displays.asStateFlow()

  private val _selectedDisplayId = MutableStateFlow<Int>(0)
  val selectedDisplayId: StateFlow<Int> = _selectedDisplayId.asStateFlow()

  private val _isMonitorConnected = MutableStateFlow<Boolean>(true)
  val isMonitorConnected: StateFlow<Boolean> = _isMonitorConnected.asStateFlow()

  private val _isAwakeLocked = MutableStateFlow(true)
  val isAwakeLocked: StateFlow<Boolean> = _isAwakeLocked.asStateFlow()

  private val _isPhoneBlackScreen = MutableStateFlow(false)
  val isPhoneBlackScreen: StateFlow<Boolean> = _isPhoneBlackScreen.asStateFlow()

  private val _activeCalibrationPattern = MutableStateFlow<CalibrationPatternType?>(null)
  val activeCalibrationPattern: StateFlow<CalibrationPatternType?> = _activeCalibrationPattern.asStateFlow()

  private val displayListener = object : DisplayManager.DisplayListener {
    override fun onDisplayAdded(displayId: Int) {
      refreshDisplays()
    }

    override fun onDisplayRemoved(displayId: Int) {
      refreshDisplays()
    }

    override fun onDisplayChanged(displayId: Int) {
      refreshDisplays()
    }
  }

  fun init() {
    displayManager.registerDisplayListener(displayListener, null)
    refreshDisplays()
    setKeepAwake(true)
  }

  fun refreshDisplays() {
    val androidDisplays = displayManager.displays
    val displayList = mutableListOf<ConnectedDisplay>()

    for (d in androidDisplays) {
      val metrics = DisplayMetrics()
      @Suppress("DEPRECATION")
      d.getRealMetrics(metrics)

      val isInternal = d.displayId == Display.DEFAULT_DISPLAY
      val type = if (isInternal) {
        DisplayType.INTERNAL
      } else {
        if ((d.flags and Display.FLAG_PRESENTATION) != 0) {
          DisplayType.EXTERNAL_HDMI
        } else {
          DisplayType.WIRELESS_CAST
        }
      }

      val supportedModesList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        d.supportedModes.map { Pair(it.physicalWidth, it.physicalHeight) }.distinct()
      } else {
        listOf(Pair(metrics.widthPixels, metrics.heightPixels))
      }

      val supportedRefreshList = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        d.supportedModes.map { it.refreshRate }.distinct()
      } else {
        listOf(d.refreshRate)
      }

      val hdrSupported = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
        d.hdrCapabilities?.supportedHdrTypes?.isNotEmpty() == true
      } else false

      val isWideColor = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        d.isWideColorGamut
      } else false

      displayList.add(
        ConnectedDisplay(
          id = d.displayId,
          name = if (isInternal) "Phone Screen (Built-in)" else "External Display (${d.name})",
          type = type,
          width = metrics.widthPixels,
          height = metrics.heightPixels,
          refreshRate = d.refreshRate,
          densityDpi = metrics.densityDpi,
          isHdrSupported = hdrSupported,
          isWideColorGamut = isWideColor,
          supportedRefreshRates = if (supportedRefreshList.isNotEmpty()) supportedRefreshList else listOf(60f),
          supportedResolutions = if (supportedModesList.isNotEmpty()) supportedModesList else listOf(Pair(metrics.widthPixels, metrics.heightPixels)),
          isCurrentlyActive = d.displayId == _selectedDisplayId.value
        )
      )
    }

    // Always include a Virtual / Simulator External Display option if only internal is detected
    val hasExternal = displayList.any { it.type != DisplayType.INTERNAL }
    if (!hasExternal) {
      displayList.add(
        ConnectedDisplay(
          id = 9999,
          name = "4K UHD External Display (Virtual Monitor)",
          type = DisplayType.VIRTUAL_SIMULATOR,
          width = 3840,
          height = 2160,
          refreshRate = 60f,
          densityDpi = 320,
          isHdrSupported = true,
          isWideColorGamut = true,
          supportedRefreshRates = listOf(24f, 30f, 60f, 120f, 144f),
          supportedResolutions = listOf(
            Pair(3840, 2160),
            Pair(2560, 1440),
            Pair(1920, 1080),
            Pair(1280, 720)
          ),
          isCurrentlyActive = _selectedDisplayId.value == 9999
        )
      )
    }

    _displays.value = displayList

    // If current selected display ID is not in list, select external or first
    if (displayList.none { it.id == _selectedDisplayId.value }) {
      val preferred = displayList.find { it.type != DisplayType.INTERNAL } ?: displayList.firstOrNull()
      preferred?.let { _selectedDisplayId.value = it.id }
    }
  }

  fun selectDisplay(id: Int) {
    _selectedDisplayId.value = id
    _displays.value = _displays.value.map { it.copy(isCurrentlyActive = it.id == id) }
  }

  fun setMonitorConnected(connected: Boolean) {
    _isMonitorConnected.value = connected
    if (!connected) {
      activity.runOnUiThread {
        activePresentation?.dismiss()
        activePresentation = null
      }
    }
  }

  fun toggleMonitorConnection() {
    setMonitorConnected(!_isMonitorConnected.value)
  }

  fun setKeepAwake(keepAwake: Boolean) {
    _isAwakeLocked.value = keepAwake
    activity.runOnUiThread {
      if (keepAwake) {
        activity.window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        try {
          if (wakeLock == null) {
            wakeLock = powerManager?.newWakeLock(
              PowerManager.SCREEN_BRIGHT_WAKE_LOCK or PowerManager.ACQUIRE_CAUSES_WAKEUP,
              "DisplayMaster:KeepAwakeLock"
            )
          }
          if (wakeLock?.isHeld == false) {
            wakeLock?.acquire(24 * 60 * 60 * 1000L) // 24 hours max safe timeout
          }
        } catch (e: Exception) {
          // Ignored if wakelock fails
        }
      } else {
        activity.window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        try {
          if (wakeLock?.isHeld == true) {
            wakeLock?.release()
          }
        } catch (e: Exception) {
          // Ignored
        }
      }
    }
  }

  fun setPhoneBlackScreen(blackScreen: Boolean) {
    _isPhoneBlackScreen.value = blackScreen
    activity.runOnUiThread {
      val layoutParams = activity.window.attributes
      if (blackScreen) {
        layoutParams.screenBrightness = 0.005f // Lowest possible AMOLED dim
        activity.window.attributes = layoutParams
      } else {
        layoutParams.screenBrightness = WindowManager.LayoutParams.BRIGHTNESS_OVERRIDE_NONE
        activity.window.attributes = layoutParams
      }
    }
  }

  fun setCalibrationPattern(pattern: CalibrationPatternType?) {
    _activeCalibrationPattern.value = pattern
  }

  fun applyDisplayResolutionAndRefresh(
    resolutionWidth: Int,
    resolutionHeight: Int,
    refreshRate: Float,
    hdrEnabled: Boolean
  ) {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
      val window = activity.window
      val display = activity.window.decorView.display ?: return
      val matchingMode = display.supportedModes.find {
        it.physicalWidth == resolutionWidth &&
          it.physicalHeight == resolutionHeight &&
          kotlin.math.abs(it.refreshRate - refreshRate) < 1.0f
      } ?: display.supportedModes.find {
        kotlin.math.abs(it.refreshRate - refreshRate) < 1.0f
      }

      matchingMode?.let {
        val params = window.attributes
        params.preferredDisplayModeId = it.modeId
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && hdrEnabled) {
          params.colorMode = ActivityInfoColorMode(true)
        }
        window.attributes = params
      }
    }
  }

  private fun ActivityInfoColorMode(isHdr: Boolean): Int {
    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
      if (isHdr) ActivityInfoHdr() else ActivityInfoSdr()
    } else 0
  }

  private fun ActivityInfoHdr(): Int = 2 // ActivityInfo.COLOR_MODE_HDR
  private fun ActivityInfoSdr(): Int = 1 // ActivityInfo.COLOR_MODE_DEFAULT

  fun updateSecondaryPresentation(
    profile: ProfileEntity?,
    telemetry: DisplayTelemetry,
    calibrationPattern: CalibrationPatternType?
  ) {
    if (!_isMonitorConnected.value) {
      activePresentation?.dismiss()
      activePresentation = null
      return
    }

    val externalDisplay = displayManager.getDisplays(DisplayManager.DISPLAY_CATEGORY_PRESENTATION)
      .firstOrNull() ?: displayManager.displays.firstOrNull { it.displayId != Display.DEFAULT_DISPLAY }

    if (externalDisplay != null) {
      if (activePresentation == null || activePresentation?.display?.displayId != externalDisplay.displayId) {
        activePresentation?.dismiss()
        activePresentation = object : Presentation(activity, externalDisplay) {
          override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            window?.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
            val composeView = ComposeView(context).apply {
              setContent {
                DisplayMasterTheme {
                  ExternalPresentationContent(
                    profile = profile,
                    telemetry = telemetry,
                    calibrationPattern = calibrationPattern,
                    isExternal = true
                  )
                }
              }
            }
            setContentView(composeView)
          }
        }
        try {
          activePresentation?.show()
        } catch (e: Exception) {
          activePresentation = null
        }
      }
    } else {
      activePresentation?.dismiss()
      activePresentation = null
    }
  }

  fun cleanup() {
    displayManager.unregisterDisplayListener(displayListener)
    activePresentation?.dismiss()
    activePresentation = null
    setKeepAwake(false)
    setPhoneBlackScreen(false)
  }
}
