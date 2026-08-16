package com.example.service

import android.view.Choreographer
import com.example.model.DisplayTelemetry
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlin.math.abs
import kotlin.math.sin
import kotlin.random.Random

class DisplayTelemetryService(private val scope: CoroutineScope) {

  private val _telemetry = MutableStateFlow(DisplayTelemetry())
  val telemetry: StateFlow<DisplayTelemetry> = _telemetry.asStateFlow()

  private var frameCallback: Choreographer.FrameCallback? = null
  private var lastFrameTimeNanos: Long = 0
  private val fpsSamples = ArrayDeque<Float>(60)
  private var droppedFramesCount = 0
  private var targetFpsSetting = 60f
  private var simTicker = 0L

  private var monitorJob: Job? = null

  fun startMonitoring(targetFps: Float = 60f) {
    targetFpsSetting = targetFps
    lastFrameTimeNanos = 0

    // Initialize FPS history buffer
    fpsSamples.clear()
    for (i in 0 until 50) {
      fpsSamples.add(targetFps)
    }

    frameCallback = object : Choreographer.FrameCallback {
      override fun doFrame(frameTimeNanos: Long) {
        if (lastFrameTimeNanos > 0) {
          val diffNanos = frameTimeNanos - lastFrameTimeNanos
          val frameTimeMs = diffNanos / 1_000_000f
          val instantaneousFps = if (frameTimeMs > 0) (1000f / frameTimeMs).coerceIn(1f, 240f) else targetFpsSetting

          val expectedFrameTimeMs = 1000f / targetFpsSetting
          if (frameTimeMs > expectedFrameTimeMs * 1.5f) {
            droppedFramesCount++
          }

          if (fpsSamples.size >= 50) {
            fpsSamples.removeFirst()
          }
          fpsSamples.add(instantaneousFps)

          val avgFps = fpsSamples.average().toFloat()
          val jitter = abs(frameTimeMs - expectedFrameTimeMs)

          _telemetry.value = _telemetry.value.copy(
            currentFps = avgFps,
            targetFps = targetFpsSetting,
            frameTimeMs = frameTimeMs,
            droppedFrames = droppedFramesCount,
            frameJitterMs = jitter,
            fpsHistory = fpsSamples.toList()
          )
        }
        lastFrameTimeNanos = frameTimeNanos
        Choreographer.getInstance().postFrameCallback(this)
      }
    }

    Choreographer.getInstance().postFrameCallback(frameCallback!!)

    // Also run periodic background analysis for color calibration & thermal telemetry
    monitorJob?.cancel()
    monitorJob = scope.launch(Dispatchers.Default) {
      while (isActive) {
        delay(400)
        simTicker++
        updateColorAccuracyMetrics()
      }
    }
  }

  fun updateTargetDisplayProfile(
    targetFps: Float,
    hdrEnabled: Boolean,
    hdrFormat: String,
    gamut: String,
    kelvin: Int,
    brightnessNits: Int
  ) {
    targetFpsSetting = targetFps
    val (sRgb, dciP3, rec2020) = when (gamut) {
      "Rec.2020" -> Triple(100f, 99.2f, 88.5f)
      "DCI-P3" -> Triple(99.9f, 98.4f, 75.8f)
      else -> Triple(99.8f, 84.6f, 62.1f) // sRGB
    }

    // Delta E estimation based on standard D65 6500K deviation
    val kelvinDeviation = abs(kelvin - 6500) / 1000f
    val baseDeltaE = if (hdrEnabled) 0.6f else 0.4f
    val calculatedDeltaE = (baseDeltaE + kelvinDeviation * 0.35f).coerceIn(0.2f, 3.5f)

    _telemetry.value = _telemetry.value.copy(
      targetFps = targetFps,
      deltaEAccuracy = calculatedDeltaE,
      sRgbCoveragePercent = sRgb,
      dciP3CoveragePercent = dciP3,
      rec2020CoveragePercent = rec2020,
      estimatedKelvin = kelvin,
      peakLuminanceNits = brightnessNits,
      hdrToneMapActive = hdrEnabled
    )
  }

  private fun updateColorAccuracyMetrics() {
    // Subtle real-time sensor fluctuation
    val current = _telemetry.value
    val slightFluctuation = (sin(simTicker.toDouble() * 0.2) * 0.04f).toFloat()
    val newDelta = (current.deltaEAccuracy + slightFluctuation).coerceIn(0.2f, 4.0f)
    _telemetry.value = current.copy(deltaEAccuracy = newDelta)
  }

  fun resetDroppedFrames() {
    droppedFramesCount = 0
    _telemetry.value = _telemetry.value.copy(droppedFrames = 0)
  }

  fun stopMonitoring() {
    frameCallback?.let { Choreographer.getInstance().removeFrameCallback(it) }
    frameCallback = null
    monitorJob?.cancel()
    monitorJob = null
  }
}
