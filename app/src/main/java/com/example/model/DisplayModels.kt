package com.example.model

enum class DisplayType {
  INTERNAL,
  EXTERNAL_HDMI,
  EXTERNAL_TYPE_C,
  WIRELESS_CAST,
  VIRTUAL_SIMULATOR
}

data class ConnectedDisplay(
  val id: Int,
  val name: String,
  val type: DisplayType,
  val width: Int,
  val height: Int,
  val refreshRate: Float,
  val densityDpi: Int,
  val isHdrSupported: Boolean,
  val isWideColorGamut: Boolean,
  val supportedRefreshRates: List<Float>,
  val supportedResolutions: List<Pair<Int, Int>>,
  val isCurrentlyActive: Boolean = false
)

enum class CalibrationPatternType(val title: String, val description: String) {
  COLOR_BARS("SMPTE Color Bars", "Standard 75%/100% video color calibration pattern"),
  GRAYSCALE_16("16-Step Grayscale", "Evaluates black crush, gamma ramp, and highlight clipping"),
  UNIFORMITY_RGB("Color Uniformity", "Check panel uniformity across Pure White, Black, Red, Green, Blue"),
  CONVERGENCE_GRID("Alignment & Sharpness", "1-pixel fine grid to verify overscan, scaling, and sharpness"),
  MOTION_FPS_UFO("Motion & Refresh Sync", "Moving frame-pacing bar to inspect ghosting and 60/120Hz sync"),
  CONTRAST_CHECKER("Dynamic Range & Contrast", "ANSI contrast checkerboard with high-contrast target squares")
}

data class DisplayTelemetry(
  val currentFps: Float = 60.0f,
  val targetFps: Float = 60.0f,
  val frameTimeMs: Float = 16.6f,
  val droppedFrames: Int = 0,
  val frameJitterMs: Float = 0.2f,
  val fpsHistory: List<Float> = emptyList(),
  val deltaEAccuracy: Float = 0.8f, // < 1.0 is indistinguishable to human eye
  val sRgbCoveragePercent: Float = 99.8f,
  val dciP3CoveragePercent: Float = 96.4f,
  val rec2020CoveragePercent: Float = 78.2f,
  val estimatedKelvin: Int = 6500,
  val peakLuminanceNits: Int = 850,
  val hdrToneMapActive: Boolean = true
)

data class TouchGestureEvent(
  val action: String,
  val x: Float,
  val y: Float,
  val timestamp: Long = System.currentTimeMillis()
)
