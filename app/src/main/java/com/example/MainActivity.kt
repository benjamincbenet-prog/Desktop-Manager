package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material.icons.outlined.Dashboard
import androidx.compose.material.icons.outlined.HighQuality
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Tune
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalibrationPatternType
import com.example.service.DisplayPresentationManager
import com.example.ui.DisplayViewModel
import com.example.ui.components.BlackScreenPhoneOverlay
import com.example.ui.components.DisplayHeader
import com.example.ui.components.ProfileEditorDialog
import com.example.ui.screens.CalibrationScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DisplaySettingsScreen
import com.example.ui.screens.ProfilesScreen
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.DisplayMasterTheme
import com.example.ui.theme.HighDensityOnPrimary
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

class MainActivity : ComponentActivity() {

  private val viewModel: DisplayViewModel by viewModels()
  private lateinit var presentationManager: DisplayPresentationManager

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    enableEdgeToEdge()

    presentationManager = DisplayPresentationManager(this)
    presentationManager.init()
    viewModel.presentationManager = presentationManager

    setContent {
      DisplayMasterTheme {
        DisplayMasterApp(viewModel = viewModel, presentationManager = presentationManager)
      }
    }
  }

  override fun onResume() {
    super.onResume()
    presentationManager.refreshDisplays()
    val targetFps = viewModel.activeProfile.value?.refreshRate ?: 60f
    viewModel.telemetryService.startMonitoring(targetFps)
  }

  override fun onPause() {
    super.onPause()
    // In presentation mode, we keep presentation alive if keepAwake is enabled
  }

  override fun onDestroy() {
    super.onDestroy()
    presentationManager.cleanup()
  }
}

@Composable
fun DisplayMasterApp(
  viewModel: DisplayViewModel,
  presentationManager: DisplayPresentationManager
) {
  val profiles by viewModel.profiles.collectAsState()
  val activeProfile by viewModel.activeProfile.collectAsState()
  val telemetry by viewModel.telemetry.collectAsState()
  val connectedDisplays by presentationManager.displays.collectAsState()
  val selectedDisplayId by presentationManager.selectedDisplayId.collectAsState()
  val isAwakeLocked by viewModel.isAwakeLocked.collectAsState()
  val isMonitorConnected by viewModel.isMonitorConnected.collectAsState()
  val isPhoneBlackScreen by viewModel.isPhoneBlackScreen.collectAsState()
  val activeCalibrationPattern by viewModel.activeCalibrationPattern.collectAsState()
  val selectedTab by viewModel.selectedTab.collectAsState()
  val editingProfile by viewModel.showProfileEditorDialog.collectAsState()

  // Keep telemetry service in sync with active profile rate
  LaunchedEffect(activeProfile?.refreshRate, isMonitorConnected) {
    if (isMonitorConnected) {
      viewModel.telemetryService.startMonitoring(activeProfile?.refreshRate ?: 60f)
    }
  }

  Box(
    modifier = Modifier
      .fillMaxSize()
      .background(DisplayBlack)
  ) {
    Scaffold(
      modifier = Modifier
        .fillMaxSize()
        .statusBarsPadding(),
      containerColor = DisplayBlack,
      topBar = {
        DisplayHeader(
          activeProfile = activeProfile,
          connectedDisplays = connectedDisplays,
          selectedDisplayId = selectedDisplayId,
          isAwakeLocked = isAwakeLocked,
          isMonitorConnected = isMonitorConnected,
          onToggleKeepAwake = { viewModel.toggleKeepAwake() },
          onToggleMonitorConnection = { viewModel.toggleMonitorConnection() },
          onEnterBlackScreen = { viewModel.setPhoneBlackScreen(true) },
          onSelectDisplay = { id -> presentationManager.selectDisplay(id) }
        )
      },
      bottomBar = {
        NavigationBar(
          containerColor = DisplayDarkSurface,
          tonalElevation = 8.dp,
          modifier = Modifier
            .navigationBarsPadding()
            .border(1.dp, DisplayBorder.copy(alpha = 0.5f))
            .testTag("main_bottom_nav")
        ) {
          val items = listOf(
            Triple(0, "Profiles", Icons.Filled.Tune to Icons.Outlined.Tune),
            Triple(1, "Dashboard", Icons.Filled.Speed to Icons.Outlined.Speed),
            Triple(2, "Calibration", Icons.Filled.HighQuality to Icons.Outlined.HighQuality),
            Triple(3, "Settings", Icons.Filled.Settings to Icons.Outlined.Settings)
          )

          items.forEach { (index, label, icons) ->
            val isSelected = selectedTab == index
            NavigationBarItem(
              selected = isSelected,
              onClick = { viewModel.setTab(index) },
              icon = {
                Icon(
                  imageVector = if (isSelected) icons.first else icons.second,
                  contentDescription = label,
                  modifier = Modifier.size(20.dp)
                )
              },
              label = {
                Text(
                  text = label,
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                )
              },
              colors = NavigationBarItemDefaults.colors(
                selectedIconColor = HighDensityOnPrimary,
                selectedTextColor = HighDensityPrimary,
                indicatorColor = HighDensityPrimary,
                unselectedIconColor = TextSecondary,
                unselectedTextColor = TextSecondary
              ),
              modifier = Modifier.testTag("nav_tab_$label")
            )
          }
        }
      }
    ) { innerPadding ->
      Box(
        modifier = Modifier
          .fillMaxSize()
          .padding(innerPadding)
      ) {
        when (selectedTab) {
          0 -> ProfilesScreen(
            activeProfile = activeProfile,
            allProfiles = profiles,
            telemetry = telemetry,
            calibrationPattern = activeCalibrationPattern,
            isMonitorConnected = isMonitorConnected,
            onToggleMonitorConnection = { viewModel.toggleMonitorConnection() },
            onSelectProfile = { viewModel.selectProfile(it) },
            onEditProfile = { viewModel.openProfileEditor(it) },
            onCreateProfile = { viewModel.openProfileEditor(null) },
            onEnterBlackScreen = { viewModel.setPhoneBlackScreen(true) }
          )

          1 -> DashboardScreen(
            activeProfile = activeProfile,
            telemetry = telemetry,
            onResetDroppedFrames = { viewModel.telemetryService.resetDroppedFrames() }
          )

          2 -> CalibrationScreen(
            telemetry = telemetry,
            activePattern = activeCalibrationPattern,
            onSelectPattern = { viewModel.setCalibrationPattern(it) }
          )

          3 -> DisplaySettingsScreen(
            activeProfile = activeProfile,
            connectedDisplays = connectedDisplays,
            selectedDisplayId = selectedDisplayId,
            isAwakeLocked = isAwakeLocked,
            isMonitorConnected = isMonitorConnected,
            onToggleKeepAwake = { viewModel.toggleKeepAwake() },
            onToggleMonitorConnection = { viewModel.toggleMonitorConnection() },
            onEnterBlackScreen = { viewModel.setPhoneBlackScreen(true) },
            onUpdateResolutionAndHdr = { width, height, refreshRate, isHdr, hdrFormat, gamut, kelvin, nits ->
              viewModel.updateResolutionAndHdr(width, height, refreshRate, isHdr, hdrFormat, gamut, kelvin, nits)
            }
          )
        }
      }
    }

    // Full Screen Pure AMOLED Black Phone Overlay
    AnimatedVisibility(
      visible = isPhoneBlackScreen,
      enter = fadeIn(),
      exit = fadeOut()
    ) {
      BlackScreenPhoneOverlay(
        activeProfile = activeProfile,
        allProfiles = profiles,
        isAwakeLocked = isAwakeLocked,
        onExitBlackScreen = { viewModel.setPhoneBlackScreen(false) },
        onSelectProfile = { viewModel.selectProfile(it) },
        onPointerMove = { offset -> viewModel.updateRemotePointer(offset) }
      )
    }

    // Profile Editor Dialog / Bottom Sheet
    editingProfile?.let { profileToEdit ->
      ProfileEditorDialog(
        profile = profileToEdit,
        onSave = { saved -> viewModel.saveProfile(saved) },
        onDelete = { toDelete -> viewModel.deleteProfile(toDelete) },
        onDismiss = { viewModel.dismissProfileEditor() }
      )
    }
  }
}
