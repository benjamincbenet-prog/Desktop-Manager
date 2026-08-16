package com.example.ui

import android.app.Application
import androidx.compose.ui.geometry.Offset
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.AppDatabase
import com.example.data.ProfileDao
import com.example.data.ProfileEntity
import com.example.model.CalibrationPatternType
import com.example.model.ConnectedDisplay
import com.example.model.DisplayTelemetry
import com.example.service.DisplayPresentationManager
import com.example.service.DisplayTelemetryService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DisplayViewModel(application: Application) : AndroidViewModel(application) {

  private val database = AppDatabase.getDatabase(application, viewModelScope)
  private val profileDao: ProfileDao = database.profileDao()

  val profiles: StateFlow<List<ProfileEntity>> = profileDao.getAllProfiles()
    .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

  private val _activeProfile = MutableStateFlow<ProfileEntity?>(null)
  val activeProfile: StateFlow<ProfileEntity?> = _activeProfile.asStateFlow()

  val telemetryService = DisplayTelemetryService(viewModelScope)
  val telemetry: StateFlow<DisplayTelemetry> = telemetryService.telemetry

  var presentationManager: DisplayPresentationManager? = null

  private val _isPhoneBlackScreen = MutableStateFlow(false)
  val isPhoneBlackScreen: StateFlow<Boolean> = _isPhoneBlackScreen.asStateFlow()

  private val _isAwakeLocked = MutableStateFlow(true)
  val isAwakeLocked: StateFlow<Boolean> = _isAwakeLocked.asStateFlow()

  private val _isMonitorConnected = MutableStateFlow(true)
  val isMonitorConnected: StateFlow<Boolean> = _isMonitorConnected.asStateFlow()

  private val _activeCalibrationPattern = MutableStateFlow<CalibrationPatternType?>(null)
  val activeCalibrationPattern: StateFlow<CalibrationPatternType?> = _activeCalibrationPattern.asStateFlow()

  private val _remotePointer = MutableStateFlow<Offset?>(null)
  val remotePointer: StateFlow<Offset?> = _remotePointer.asStateFlow()

  private val _selectedTab = MutableStateFlow(0) // 0: Profiles, 1: Telemetry Dashboard, 2: Calibration, 3: Settings/Hardware
  val selectedTab: StateFlow<Int> = _selectedTab.asStateFlow()

  // Trackpad / Touch mode on phone
  private val _showStealthHud = MutableStateFlow(false)
  val showStealthHud: StateFlow<Boolean> = _showStealthHud.asStateFlow()

  private val _showProfileEditorDialog = MutableStateFlow<ProfileEntity?>(null)
  val showProfileEditorDialog: StateFlow<ProfileEntity?> = _showProfileEditorDialog.asStateFlow()

  init {
    viewModelScope.launch {
      // Seed if necessary and select first active profile
      AppDatabase.populateInitialProfiles(profileDao)
      profileDao.getAllProfiles().collect { list ->
        if (_activeProfile.value == null && list.isNotEmpty()) {
          selectProfile(list.first())
        }
      }
    }
  }

  fun setTab(index: Int) {
    _selectedTab.value = index
  }

  fun selectProfile(profile: ProfileEntity) {
    _activeProfile.value = profile
    _isAwakeLocked.value = profile.keepAwake

    presentationManager?.setKeepAwake(profile.keepAwake)
    if (profile.blackScreenPhone) {
      setPhoneBlackScreen(true)
    }

    presentationManager?.applyDisplayResolutionAndRefresh(
      resolutionWidth = profile.resolutionWidth,
      resolutionHeight = profile.resolutionHeight,
      refreshRate = profile.refreshRate,
      hdrEnabled = profile.isHdrEnabled
    )

    telemetryService.updateTargetDisplayProfile(
      targetFps = profile.refreshRate,
      hdrEnabled = profile.isHdrEnabled,
      hdrFormat = profile.hdrFormat,
      gamut = profile.colorGamut,
      kelvin = profile.colorTemperatureK,
      brightnessNits = profile.brightnessNits
    )

    presentationManager?.updateSecondaryPresentation(profile, telemetry.value, _activeCalibrationPattern.value)
  }

  fun toggleKeepAwake() {
    val newState = !_isAwakeLocked.value
    _isAwakeLocked.value = newState
    presentationManager?.setKeepAwake(newState)
    _activeProfile.value?.let { current ->
      viewModelScope.launch {
        profileDao.updateProfile(current.copy(keepAwake = newState))
      }
    }
  }

  fun toggleMonitorConnection() {
    val newState = !_isMonitorConnected.value
    setMonitorConnected(newState)
  }

  fun setMonitorConnected(connected: Boolean) {
    _isMonitorConnected.value = connected
    presentationManager?.setMonitorConnected(connected)
    if (connected) {
      _activeProfile.value?.let { profile ->
        selectProfile(profile)
      }
    } else {
      presentationManager?.updateSecondaryPresentation(null, telemetry.value, null)
    }
  }

  fun setPhoneBlackScreen(blackScreen: Boolean) {
    _isPhoneBlackScreen.value = blackScreen
    presentationManager?.setPhoneBlackScreen(blackScreen)
    if (blackScreen) {
      _showStealthHud.value = false
    }
  }

  fun toggleStealthHud() {
    _showStealthHud.value = !_showStealthHud.value
  }

  fun setCalibrationPattern(pattern: CalibrationPatternType?) {
    _activeCalibrationPattern.value = pattern
    presentationManager?.setCalibrationPattern(pattern)
    presentationManager?.updateSecondaryPresentation(_activeProfile.value, telemetry.value, pattern)
  }

  fun updateRemotePointer(offset: Offset?) {
    _remotePointer.value = offset
  }

  fun updateResolutionAndHdr(
    width: Int,
    height: Int,
    refreshRate: Float,
    isHdr: Boolean,
    hdrFormat: String,
    gamut: String,
    kelvin: Int,
    nits: Int
  ) {
    val current = _activeProfile.value ?: return
    val updated = current.copy(
      resolutionWidth = width,
      resolutionHeight = height,
      refreshRate = refreshRate,
      isHdrEnabled = isHdr,
      hdrFormat = hdrFormat,
      colorGamut = gamut,
      colorTemperatureK = kelvin,
      brightnessNits = nits
    )
    _activeProfile.value = updated
    viewModelScope.launch {
      profileDao.updateProfile(updated)
    }

    presentationManager?.applyDisplayResolutionAndRefresh(width, height, refreshRate, isHdr)
    telemetryService.updateTargetDisplayProfile(
      targetFps = refreshRate,
      hdrEnabled = isHdr,
      hdrFormat = hdrFormat,
      gamut = gamut,
      kelvin = kelvin,
      brightnessNits = nits
    )
    presentationManager?.updateSecondaryPresentation(updated, telemetry.value, _activeCalibrationPattern.value)
  }

  fun openProfileEditor(profile: ProfileEntity?) {
    _showProfileEditorDialog.value = profile ?: ProfileEntity(
      name = "Custom Profile",
      description = "User configured display profile",
      resolutionWidth = 3840,
      resolutionHeight = 2160,
      refreshRate = 60f,
      isHdrEnabled = true,
      hdrFormat = "HDR10",
      colorGamut = "DCI-P3",
      colorTemperatureK = 6500,
      brightnessNits = 600,
      contrastRatio = 1.2f,
      keepAwake = true,
      blackScreenPhone = false,
      iconName = "tune",
      isPreset = false
    )
  }

  fun dismissProfileEditor() {
    _showProfileEditorDialog.value = null
  }

  fun saveProfile(profile: ProfileEntity) {
    viewModelScope.launch {
      if (profile.id == 0L) {
        val newId = profileDao.insertProfile(profile)
        val saved = profile.copy(id = newId)
        selectProfile(saved)
      } else {
        profileDao.updateProfile(profile)
        if (_activeProfile.value?.id == profile.id) {
          selectProfile(profile)
        }
      }
      _showProfileEditorDialog.value = null
    }
  }

  fun deleteProfile(profile: ProfileEntity) {
    if (profile.isPreset) return
    viewModelScope.launch {
      profileDao.deleteProfile(profile)
      if (_activeProfile.value?.id == profile.id) {
        profiles.value.firstOrNull()?.let { selectProfile(it) }
      }
    }
  }

  override fun onCleared() {
    super.onCleared()
    telemetryService.stopMonitoring()
    presentationManager?.cleanup()
  }
}
