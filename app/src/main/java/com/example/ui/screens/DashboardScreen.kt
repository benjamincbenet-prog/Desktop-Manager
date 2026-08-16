package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoGraph
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.model.DisplayTelemetry
import com.example.ui.theme.AmberWarning
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayCardElevated
import com.example.ui.theme.DisplayCardSurface
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.RoseError
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.util.Locale

@Composable
fun DashboardScreen(
  activeProfile: ProfileEntity?,
  telemetry: DisplayTelemetry,
  onResetDroppedFrames: () -> Unit,
  modifier: Modifier = Modifier
) {
  LazyColumn(
    modifier = modifier.fillMaxSize().background(DisplayBlack).testTag("dashboard_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Top Hero: Real-Time Refresh Rate & Frame Pacing
    item {
      RealTimeFpsCard(
        telemetry = telemetry,
        targetFps = activeProfile?.refreshRate ?: 144f,
        onResetDroppedFrames = onResetDroppedFrames
      )
    }

    // Color Accuracy & Delta-E Meter
    item {
      ColorAccuracyCard(telemetry = telemetry, activeProfile = activeProfile)
    }

    // CIE 1931 Chromaticity Gamut Coverage Visualizer
    item {
      CieGamutDiagramCard(telemetry = telemetry, activeProfile = activeProfile)
    }

    // Color Calibration Fidelity Swatches
    item {
      ColorCalibrationSwatchesCard(telemetry = telemetry, activeProfile = activeProfile)
    }
  }
}

@Composable
private fun RealTimeFpsCard(
  telemetry: DisplayTelemetry,
  targetFps: Float,
  onResetDroppedFrames: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
    shape = RoundedCornerShape(18.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Speed,
            contentDescription = null,
            tint = HighDensityPrimary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "REAL-TIME REFRESH RATE & V-SYNC",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        IconButton(onClick = onResetDroppedFrames, modifier = Modifier.size(28.dp)) {
          Icon(
            imageVector = Icons.Default.Refresh,
            contentDescription = "Reset Stats",
            tint = TextTertiary,
            modifier = Modifier.size(16.dp)
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      // Main FPS Gauge Display
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Bottom
      ) {
        Column {
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = String.format(Locale.US, "%.1f", telemetry.currentFps),
              color = HighDensityPrimary,
              fontSize = 36.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
              text = "FPS / Hz",
              color = TextSecondary,
              fontSize = 13.sp,
              fontWeight = FontWeight.SemiBold,
              modifier = Modifier.padding(bottom = 6.dp)
            )
          }

          Text(
            text = "Target Sync: ${targetFps.toInt()} Hz (${String.format(Locale.US, "%.2f", 1000f / targetFps)} ms/f)",
            color = TextTertiary,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        // Stability indicator badge
        val fpsDeviation = kotlin.math.abs(telemetry.currentFps - targetFps)
        val (stabilityText, stabilityColor) = when {
          fpsDeviation < 1.0f -> "LOCKED 100%" to HighDensityGreen
          fpsDeviation < 3.0f -> "STABLE" to HighDensityPrimary
          else -> "FLUCTUATING" to AmberWarning
        }

        Box(
          modifier = Modifier
            .background(stabilityColor.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .border(1.dp, stabilityColor.copy(alpha = 0.35f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
          Text(
            text = stabilityText,
            color = stabilityColor,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Live Dynamic FPS Rolling Waveform Canvas
      FpsWaveformCanvas(
        history = telemetry.fpsHistory,
        targetFps = targetFps,
        modifier = Modifier
          .fillMaxWidth()
          .height(88.dp)
          .background(DisplayBlack, RoundedCornerShape(10.dp))
          .border(1.dp, DisplayBorder, RoundedCornerShape(10.dp))
      )

      Spacer(modifier = Modifier.height(14.dp))

      // Frame Telemetry Metrics Grid
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        TelemetryStatBox(
          label = "Frame Time",
          value = String.format(Locale.US, "%.2f ms", telemetry.frameTimeMs),
          color = HighDensityGreen,
          modifier = Modifier.weight(1f)
        )
        TelemetryStatBox(
          label = "Frame Jitter",
          value = String.format(Locale.US, "%.2f ms", telemetry.frameJitterMs),
          color = if (telemetry.frameJitterMs < 1.0f) HighDensityGreen else AmberWarning,
          modifier = Modifier.weight(1f)
        )
        TelemetryStatBox(
          label = "Dropped",
          value = "${telemetry.droppedFrames}",
          color = if (telemetry.droppedFrames == 0) HighDensityGreen else RoseError,
          modifier = Modifier.weight(1f)
        )
      }
    }
  }
}

@Composable
private fun FpsWaveformCanvas(
  history: List<Float>,
  targetFps: Float,
  modifier: Modifier = Modifier
) {
  Canvas(modifier = modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
    if (history.isEmpty()) return@Canvas

    val maxFps = (targetFps * 1.25f).coerceAtLeast(75f)
    val minFps = (targetFps * 0.75f).coerceAtLeast(0f)
    val fpsRange = (maxFps - minFps).coerceAtLeast(10f)

    // Target reference line
    val targetY = size.height * (1f - (targetFps - minFps) / fpsRange)
    drawLine(
      color = HighDensityPrimary.copy(alpha = 0.4f),
      start = Offset(0f, targetY),
      end = Offset(size.width, targetY),
      strokeWidth = 1.5f,
      pathEffect = androidx.compose.ui.graphics.PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
    )

    // Draw Line Chart
    val stepX = size.width / (history.size - 1).coerceAtLeast(1)
    val path = Path()
    val fillPath = Path()

    history.forEachIndexed { index, fps ->
      val normalizedY = (size.height * (1f - (fps - minFps) / fpsRange)).coerceIn(0f, size.height)
      val x = index * stepX

      if (index == 0) {
        path.moveTo(x, normalizedY)
        fillPath.moveTo(x, size.height)
        fillPath.lineTo(x, normalizedY)
      } else {
        path.lineTo(x, normalizedY)
        fillPath.lineTo(x, normalizedY)
      }
    }

    fillPath.lineTo(size.width, size.height)
    fillPath.close()

    // Gradient Fill
    drawPath(
      path = fillPath,
      brush = Brush.verticalGradient(
        colors = listOf(
          HighDensityPrimary.copy(alpha = 0.25f),
          Color.Transparent
        )
      )
    )

    // Stroke
    drawPath(
      path = path,
      color = HighDensityPrimary,
      style = Stroke(width = 2.5f, cap = StrokeCap.Round)
    )
  }
}

@Composable
private fun ColorAccuracyCard(
  telemetry: DisplayTelemetry,
  activeProfile: ProfileEntity?
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
    shape = RoundedCornerShape(18.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.Verified,
            contentDescription = null,
            tint = HighDensityGreen,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "COLOR ACCURACY & CALIBRATION (ΔE)",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        val accuracyRating = when {
          telemetry.deltaEAccuracy < 1.0f -> "REFERENCE GRADE"
          telemetry.deltaEAccuracy < 2.0f -> "EXCELLENT"
          else -> "ACCEPTABLE"
        }

        Box(
          modifier = Modifier
            .background(HighDensityGreen.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
          Text(
            text = accuracyRating,
            color = HighDensityGreen,
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold
          )
        }
      }

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Column {
          Row(verticalAlignment = Alignment.Bottom) {
            Text(
              text = String.format(Locale.US, "%.2f", telemetry.deltaEAccuracy),
              color = HighDensityGreen,
              fontSize = 34.sp,
              fontWeight = FontWeight.Bold,
              fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
              text = "ΔE",
              color = TextSecondary,
              fontSize = 15.sp,
              fontWeight = FontWeight.Bold,
              modifier = Modifier.padding(bottom = 4.dp)
            )
          }
          Text(
            text = "Delta-E < 1.0 is indistinguishable to human eye",
            color = TextTertiary,
            fontSize = 11.sp
          )
        }

        // Color temperature badge
        Column(horizontalAlignment = Alignment.End) {
          Text(text = "White Point (CCT)", color = TextTertiary, fontSize = 11.sp)
          Text(
            text = "${activeProfile?.colorTemperatureK ?: 6500}K (D65)",
            color = HighDensityPrimary,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Accuracy Level Progression Bar
      DeltaEBar(deltaE = telemetry.deltaEAccuracy)
    }
  }
}

@Composable
private fun DeltaEBar(deltaE: Float) {
  Column {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = "0.0 (Perfect)", color = HighDensityGreen, fontSize = 10.sp)
      Text(text = "1.0 (Broadcast)", color = HighDensityPrimary, fontSize = 10.sp)
      Text(text = "2.0 (Consumer)", color = AmberWarning, fontSize = 10.sp)
      Text(text = "4.0+ (Drift)", color = RoseError, fontSize = 10.sp)
    }
    Spacer(modifier = Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(8.dp)
        .background(
          Brush.horizontalGradient(
            colors = listOf(
              HighDensityGreen,
              HighDensityPrimary,
              AmberWarning,
              RoseError
            )
          ),
          RoundedCornerShape(4.dp)
        )
    )
  }
}

@Composable
private fun CieGamutDiagramCard(
  telemetry: DisplayTelemetry,
  activeProfile: ProfileEntity?
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
    shape = RoundedCornerShape(18.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
          Icon(
            imageVector = Icons.Default.ColorLens,
            contentDescription = null,
            tint = HighDensityPrimary,
            modifier = Modifier.size(18.dp)
          )
          Spacer(modifier = Modifier.width(8.dp))
          Text(
            text = "COLOR GAMUT COVERAGE (CIE 1931)",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }

        Text(
          text = activeProfile?.colorGamut ?: "DCI-P3",
          color = HighDensityPrimary,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold
        )
      }

      Spacer(modifier = Modifier.height(14.dp))

      // Gamut breakdown bars
      GamutMeterRow("sRGB (Standard Rec.709)", telemetry.sRgbCoveragePercent, HighDensityPrimary)
      GamutMeterRow("DCI-P3 (Cinema Wide Gamut)", telemetry.dciP3CoveragePercent, HighDensityGreen)
      GamutMeterRow("Rec.2020 (UHD Master)", telemetry.rec2020CoveragePercent, IndigoAccent)

      Spacer(modifier = Modifier.height(12.dp))

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(text = "Peak Luminance:", color = TextSecondary, fontSize = 12.sp)
        Text(
          text = "${activeProfile?.brightnessNits ?: 850} Nits (HDR Boost)",
          color = HighDensityGreen,
          fontSize = 12.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
    }
  }
}

@Composable
private fun GamutMeterRow(label: String, percent: Float, color: Color) {
  Column(modifier = Modifier.padding(vertical = 4.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, color = TextPrimary, fontSize = 12.sp)
      Text(
        text = "${String.format(Locale.US, "%.1f", percent)}%",
        color = color,
        fontSize = 12.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
    }
    Spacer(modifier = Modifier.height(4.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(6.dp)
        .background(DisplayBlack, RoundedCornerShape(3.dp))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(percent / 100f)
          .height(6.dp)
          .background(color, RoundedCornerShape(3.dp))
      )
    }
  }
}

@Composable
private fun ColorCalibrationSwatchesCard(
  telemetry: DisplayTelemetry,
  activeProfile: ProfileEntity?
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp)),
    colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
    shape = RoundedCornerShape(18.dp)
  ) {
    Column(modifier = Modifier.padding(16.dp)) {
      Text(
        text = "TARGET VS MEASURED FIDELITY SWATCHES",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
      Spacer(modifier = Modifier.height(10.dp))

      val swatches = listOf(
        Triple("Skin 1", Color(0xFFE0A899), Color(0xFFDFAB9B)),
        Triple("Foliage", Color(0xFF4C7B38), Color(0xFF4B7936)),
        Triple("Sky Blue", Color(0xFF4A7EBB), Color(0xFF4880BD)),
        Triple("Red Patch", Color(0xFFB5322F), Color(0xFFB3302D)),
        Triple("Pure White", Color(0xFFFFFFFF), Color(0xFFFEFEFD)),
        Triple("Deep Neutral", Color(0xFF3B3B3B), Color(0xFF3A3A3A))
      )

      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
      ) {
        swatches.forEach { (name, targetColor, measuredColor) ->
          Column(
            modifier = Modifier.weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally
          ) {
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(targetColor, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
                .border(0.5.dp, DisplayBorder, RoundedCornerShape(topStart = 8.dp, topEnd = 8.dp))
            )
            Box(
              modifier = Modifier
                .fillMaxWidth()
                .height(36.dp)
                .background(measuredColor, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
                .border(0.5.dp, DisplayBorder, RoundedCornerShape(bottomStart = 8.dp, bottomEnd = 8.dp))
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = name, color = TextTertiary, fontSize = 9.sp, maxLines = 1)
          }
        }
      }
    }
  }
}

@Composable
private fun TelemetryStatBox(
  label: String,
  value: String,
  color: Color,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .background(DisplayBlack, RoundedCornerShape(12.dp))
      .border(1.dp, DisplayBorder, RoundedCornerShape(12.dp))
      .padding(10.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(text = label, color = TextTertiary, fontSize = 10.sp)
    Spacer(modifier = Modifier.height(2.dp))
    Text(
      text = value,
      color = color,
      fontSize = 13.sp,
      fontWeight = FontWeight.Bold,
      fontFamily = FontFamily.Monospace
    )
  }
}

