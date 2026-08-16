package com.example.ui.presentation

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BrightnessHigh
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.HighQuality
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material3.Icon
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.model.CalibrationPatternType
import com.example.model.DisplayTelemetry
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayCardElevated
import com.example.ui.theme.DisplayCardSurface
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.PurpleAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun ExternalPresentationContent(
  profile: ProfileEntity?,
  telemetry: DisplayTelemetry,
  calibrationPattern: CalibrationPatternType?,
  pointerPosition: Offset? = null,
  isExternal: Boolean = false,
  modifier: Modifier = Modifier
) {
  Box(
    modifier = modifier
      .fillMaxSize()
      .background(DisplayBlack)
  ) {
    if (calibrationPattern != null) {
      CalibrationPatternCanvas(
        patternType = calibrationPattern,
        telemetry = telemetry,
        modifier = Modifier.fillMaxSize()
      )
    } else {
      PresentationDefaultWorkspace(
        profile = profile,
        telemetry = telemetry,
        modifier = Modifier.fillMaxSize()
      )
    }

    // Interactive pointer/laser dot from phone touch pad
    if (pointerPosition != null) {
      Box(
        modifier = Modifier
          .offset(x = pointerPosition.x.dp, y = pointerPosition.y.dp)
          .size(24.dp)
          .background(Color(0x8800E5FF), CircleShape)
          .border(2.dp, CyanAccent, CircleShape),
        contentAlignment = Alignment.Center
      ) {
        Box(
          modifier = Modifier
            .size(8.dp)
            .background(Color.White, CircleShape)
        )
      }
    }
  }
}

@Composable
private fun PresentationDefaultWorkspace(
  profile: ProfileEntity?,
  telemetry: DisplayTelemetry,
  modifier: Modifier = Modifier
) {
  var currentTime by remember { mutableStateOf("") }
  var sessionSeconds by remember { mutableStateOf(0L) }

  LaunchedEffect(Unit) {
    while (true) {
      val sdf = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
      currentTime = sdf.format(Date())
      sessionSeconds++
      delay(1000)
    }
  }

  val hours = sessionSeconds / 3600
  val minutes = (sessionSeconds % 3600) / 60
  val seconds = sessionSeconds % 60
  val sessionFormatted = String.format(Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)

  Column(
    modifier = modifier
      .fillMaxSize()
      .padding(24.dp),
    verticalArrangement = Arrangement.SpaceBetween
  ) {
    // Top Bar on External Screen
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(DisplayDarkSurface.copy(alpha = 0.85f), RoundedCornerShape(16.dp))
        .border(1.dp, DisplayCardElevated, RoundedCornerShape(16.dp))
        .padding(horizontal = 20.dp, vertical = 12.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = Icons.Default.Tv,
          contentDescription = "External Display",
          tint = CyanAccent,
          modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = "EXTERNAL DISPLAY REFERENCE OUTPUT",
            color = TextPrimary,
            fontWeight = FontWeight.Bold,
            fontSize = 13.sp,
            letterSpacing = 1.sp
          )
          Text(
            text = "${profile?.resolutionWidth ?: 3840}x${profile?.resolutionHeight ?: 2160} @ ${profile?.refreshRate?.toInt() ?: 60}Hz • ${profile?.hdrFormat ?: "HDR10"}",
            color = CyanAccent,
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(16.dp)
      ) {
        // Keep awake active badge
        Row(
          modifier = Modifier
            .background(EmeraldSuccess.copy(alpha = 0.15f), RoundedCornerShape(8.dp))
            .border(1.dp, EmeraldSuccess.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 10.dp, vertical = 4.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Icon(
            imageVector = Icons.Default.LockClock,
            contentDescription = "Awake Lock",
            tint = EmeraldSuccess,
            modifier = Modifier.size(14.dp)
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = "AWAKE LOCKED: $sessionFormatted",
            color = EmeraldSuccess,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            fontFamily = FontFamily.Monospace
          )
        }

        // Live Clock
        Text(
          text = currentTime,
          color = TextPrimary,
          fontSize = 18.sp,
          fontWeight = FontWeight.Bold,
          fontFamily = FontFamily.Monospace
        )
      }
    }

    // Center Stage: Visual Studio Reference Canvas / Test Patterns
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .weight(1f)
        .padding(vertical = 16.dp),
      horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // Left Pane: Active Profile & Gamut Spectrum
      Column(
        modifier = Modifier
          .weight(0.38f)
          .fillMaxHeight()
          .background(DisplayDarkSurface.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
          .border(1.dp, DisplayCardElevated, RoundedCornerShape(16.dp))
          .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Column {
          Text(
            text = "ACTIVE PROFILE",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(4.dp))
          Text(
            text = profile?.name ?: "Cinema 4K HDR",
            color = TextPrimary,
            fontSize = 20.sp,
            fontWeight = FontWeight.ExtraBold
          )
          Text(
            text = profile?.description ?: "Mastered display output profile",
            color = TextSecondary,
            fontSize = 12.sp
          )

          Spacer(modifier = Modifier.height(16.dp))

          // Profile Spec Grid
          SpecRow("Color Gamut", profile?.colorGamut ?: "DCI-P3", PurpleAccent)
          SpecRow("HDR Standard", profile?.hdrFormat ?: "HDR10", CyanAccent)
          SpecRow("Color Temp", "${profile?.colorTemperatureK ?: 6500}K (D65)", IndigoAccent)
          SpecRow("Peak Brightness", "${profile?.brightnessNits ?: 850} Nits", EmeraldSuccess)
          SpecRow("Phone OLED Mode", if (profile?.blackScreenPhone == true) "Pure Black AMOLED" else "Standard Screen", TextPrimary)
        }

        // Color Gamut Bar Graphic
        Column {
          Text(
            text = "GAMUT COVERAGE (CIE-1931)",
            color = TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
          Spacer(modifier = Modifier.height(6.dp))
          GamutProgressBar("sRGB / Rec.709", telemetry.sRgbCoveragePercent, CyanAccent)
          GamutProgressBar("DCI-P3 Wide", telemetry.dciP3CoveragePercent, PurpleAccent)
          GamutProgressBar("Rec.2020 UHD", telemetry.rec2020CoveragePercent, IndigoAccent)
        }
      }

      // Center/Right Pane: High Refresh Rate Live Animation & Frame Pacing Canvas
      Column(
        modifier = Modifier
          .weight(0.62f)
          .fillMaxHeight()
          .background(DisplayDarkSurface.copy(alpha = 0.9f), RoundedCornerShape(16.dp))
          .border(1.dp, DisplayCardElevated, RoundedCornerShape(16.dp))
          .padding(18.dp),
        verticalArrangement = Arrangement.SpaceBetween
      ) {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(
              text = "LIVE MOTION & FRAME PACING ENGINE",
              color = TextSecondary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.sp
            )
            Text(
              text = "Testing V-Sync alignment & frame latency in real-time",
              color = TextSecondary,
              fontSize = 11.sp
            )
          }

          Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            MetricPill("FPS", String.format(Locale.US, "%.1f", telemetry.currentFps), CyanAccent)
            MetricPill("Frame Time", String.format(Locale.US, "%.2f ms", telemetry.frameTimeMs), EmeraldSuccess)
            MetricPill("ΔE Accuracy", String.format(Locale.US, "%.2f", telemetry.deltaEAccuracy), PurpleAccent)
          }
        }

        // Live UFO / Moving Frame Pacing Test Loop
        Box(
          modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(vertical = 12.dp)
            .background(DisplayBlack, RoundedCornerShape(12.dp))
            .border(1.dp, DisplayCardElevated, RoundedCornerShape(12.dp))
        ) {
          MovingMotionBarAnimation(targetFps = telemetry.targetFps)
        }

        // Bottom Color Scale Bar
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .height(24.dp)
            .background(Color.Transparent, RoundedCornerShape(6.dp)),
          horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
          val colors = listOf(
            Color(0xFFFFFFFF),
            Color(0xFFFFFF00),
            Color(0xFF00FFFF),
            Color(0xFF00FF00),
            Color(0xFFFF00FF),
            Color(0xFFFF0000),
            Color(0xFF0000FF),
            Color(0xFF1E1E1E),
            Color(0xFF0A0A0A)
          )
          colors.forEach { col ->
            Box(
              modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .background(col, RoundedCornerShape(4.dp))
            )
          }
        }
      }
    }

    // Bottom Telemetry Footer
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .background(DisplayDarkSurface.copy(alpha = 0.85f), RoundedCornerShape(12.dp))
        .padding(horizontal = 16.dp, vertical = 8.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = "MONITOR STATUS: ACTIVE (ZERO SLEEP / NO DIMMING)",
        color = EmeraldSuccess,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        fontFamily = FontFamily.Monospace
      )
      Text(
        text = "Jitter: ${String.format(Locale.US, "%.2f", telemetry.frameJitterMs)}ms • Dropped Frames: ${telemetry.droppedFrames} • Delta-E: ${String.format(Locale.US, "%.2f", telemetry.deltaEAccuracy)}",
        color = TextSecondary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}

@Composable
private fun MovingMotionBarAnimation(targetFps: Float) {
  val infiniteTransition = rememberInfiniteTransition(label = "motion_test")
  val speedMillis = (2000f * (60f / targetFps.coerceAtLeast(30f))).toInt()
  val animatedProgress by infiniteTransition.animateFloat(
    initialValue = 0f,
    targetValue = 1f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = speedMillis, easing = LinearEasing),
      repeatMode = RepeatMode.Restart
    ),
    label = "ufo_bar"
  )

  Canvas(modifier = Modifier.fillMaxSize()) {
    val barWidth = 60.dp.toPx()
    val startX = (size.width - barWidth) * animatedProgress

    // Draw Grid Lines
    val step = size.width / 12f
    for (i in 0..12) {
      drawLine(
        color = Color(0xFF1E293B),
        start = Offset(step * i, 0f),
        end = Offset(step * i, size.height),
        strokeWidth = 1f
      )
    }

    // Draw Top Sync Bar (Cyan)
    drawRoundRect(
      color = CyanAccent,
      topLeft = Offset(startX, size.height * 0.15f),
      size = Size(barWidth, size.height * 0.3f),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
    )

    // Draw Bottom Inverted Sync Bar (Purple)
    val startXBottom = (size.width - barWidth) * (1f - animatedProgress)
    drawRoundRect(
      color = PurpleAccent,
      topLeft = Offset(startXBottom, size.height * 0.55f),
      size = Size(barWidth, size.height * 0.3f),
      cornerRadius = androidx.compose.ui.geometry.CornerRadius(8f, 8f)
    )

    // Center Reference Marker
    drawLine(
      color = EmeraldSuccess,
      start = Offset(size.width / 2f, 0f),
      end = Offset(size.width / 2f, size.height),
      strokeWidth = 2f
    )
  }
}

@Composable
private fun SpecRow(label: String, value: String, color: Color) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, color = TextSecondary, fontSize = 12.sp)
    Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
  }
}

@Composable
private fun GamutProgressBar(label: String, percent: Float, color: Color) {
  Column(modifier = Modifier.padding(vertical = 2.dp)) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween
    ) {
      Text(text = label, color = TextSecondary, fontSize = 10.sp)
      Text(
        text = "${String.format(Locale.US, "%.1f", percent)}%",
        color = color,
        fontSize = 10.sp,
        fontWeight = FontWeight.Bold
      )
    }
    Spacer(modifier = Modifier.height(2.dp))
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .height(5.dp)
        .background(DisplayBlack, RoundedCornerShape(3.dp))
    ) {
      Box(
        modifier = Modifier
          .fillMaxWidth(percent / 100f)
          .fillMaxHeight()
          .background(color, RoundedCornerShape(3.dp))
      )
    }
  }
}

@Composable
private fun MetricPill(title: String, value: String, color: Color) {
  Column(
    modifier = Modifier
      .background(DisplayCardSurface, RoundedCornerShape(8.dp))
      .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
      .padding(horizontal = 10.dp, vertical = 4.dp),
    horizontalAlignment = Alignment.CenterHorizontally
  ) {
    Text(text = title, color = TextSecondary, fontSize = 9.sp)
    Text(text = value, color = color, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
  }
}

@Composable
fun CalibrationPatternCanvas(
  patternType: CalibrationPatternType,
  telemetry: DisplayTelemetry,
  modifier: Modifier = Modifier
) {
  when (patternType) {
    CalibrationPatternType.COLOR_BARS -> SmpteColorBarsPattern(modifier)
    CalibrationPatternType.GRAYSCALE_16 -> Grayscale16Pattern(modifier)
    CalibrationPatternType.UNIFORMITY_RGB -> UniformityRgbPattern(modifier)
    CalibrationPatternType.CONVERGENCE_GRID -> ConvergenceGridPattern(modifier)
    CalibrationPatternType.MOTION_FPS_UFO -> MotionUfoPattern(telemetry.targetFps, modifier)
    CalibrationPatternType.CONTRAST_CHECKER -> ContrastCheckerPattern(modifier)
  }
}

@Composable
private fun SmpteColorBarsPattern(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.fillMaxSize()) {
    val topColors = listOf(
      Color(0xFFC0C0C0), // 75% White
      Color(0xFFC0C000), // Yellow
      Color(0xFF00C0C0), // Cyan
      Color(0xFF00C000), // Green
      Color(0xFFC000C0), // Magenta
      Color(0xFFC00000), // Red
      Color(0xFF0000C0)  // Blue
    )
    val barWidth = size.width / topColors.size
    val topHeight = size.height * 0.67f

    topColors.forEachIndexed { index, color ->
      drawRect(
        color = color,
        topLeft = Offset(index * barWidth, 0f),
        size = Size(barWidth, topHeight)
      )
    }

    // Middle Bar (Cast inverters)
    val midColors = listOf(
      Color(0xFF0000C0),
      Color(0xFF131313),
      Color(0xFFC000C0),
      Color(0xFF131313),
      Color(0xFF00C0C0),
      Color(0xFF131313),
      Color(0xFFC0C0C0)
    )
    val midHeight = size.height * 0.08f
    midColors.forEachIndexed { index, color ->
      drawRect(
        color = color,
        topLeft = Offset(index * barWidth, topHeight),
        size = Size(barWidth, midHeight)
      )
    }

    // Bottom Pluge (Black Level & White step)
    val botHeight = size.height - (topHeight + midHeight)
    val botY = topHeight + midHeight
    val botBarWidth = size.width / 5f

    drawRect(Color(0xFF00214C), topLeft = Offset(0f, botY), size = Size(botBarWidth, botHeight))
    drawRect(Color(0xFFFFFFFF), topLeft = Offset(botBarWidth, botY), size = Size(botBarWidth, botHeight))
    drawRect(Color(0xFF32006A), topLeft = Offset(botBarWidth * 2, botY), size = Size(botBarWidth, botHeight))
    drawRect(Color(0xFF131313), topLeft = Offset(botBarWidth * 3, botY), size = Size(botBarWidth, botHeight))

    // PLUGE stripes (-2%, 0%, +2%, +4%)
    val plugeWidth = botBarWidth / 4f
    drawRect(Color(0xFF0A0A0A), topLeft = Offset(botBarWidth * 4, botY), size = Size(plugeWidth, botHeight))
    drawRect(Color(0xFF131313), topLeft = Offset(botBarWidth * 4 + plugeWidth, botY), size = Size(plugeWidth, botHeight))
    drawRect(Color(0xFF1F1F1F), topLeft = Offset(botBarWidth * 4 + plugeWidth * 2, botY), size = Size(plugeWidth, botHeight))
    drawRect(Color(0xFF2E2E2E), topLeft = Offset(botBarWidth * 4 + plugeWidth * 3, botY), size = Size(plugeWidth, botHeight))
  }
}

@Composable
private fun Grayscale16Pattern(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.fillMaxSize()) {
    val steps = 16
    val barWidth = size.width / steps

    for (i in 0 until steps) {
      val lum = (i.toFloat() / (steps - 1))
      val color = Color(lum, lum, lum, 1f)
      drawRect(
        color = color,
        topLeft = Offset(i * barWidth, 0f),
        size = Size(barWidth, size.height)
      )
    }
  }
}

@Composable
private fun UniformityRgbPattern(modifier: Modifier = Modifier) {
  var activeColorIndex by remember { mutableStateOf(0) }
  val colors = listOf(
    Pair("100% Full White", Color.White),
    Pair("0% AMOLED Black (Uniformity / Bleed)", Color.Black),
    Pair("100% Red Subpixel", Color.Red),
    Pair("100% Green Subpixel", Color.Green),
    Pair("100% Blue Subpixel", Color.Blue)
  )

  LaunchedEffect(Unit) {
    while (true) {
      delay(3000)
      activeColorIndex = (activeColorIndex + 1) % colors.size
    }
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(colors[activeColorIndex].second)
  )
}

@Composable
private fun ConvergenceGridPattern(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.fillMaxSize()) {
    drawRect(Color.Black)
    val gridSpacing = 40.dp.toPx()
    val cols = (size.width / gridSpacing).toInt()
    val rows = (size.height / gridSpacing).toInt()

    for (c in 0..cols) {
      val x = c * gridSpacing
      drawLine(
        color = Color(0xFFFFFFFF),
        start = Offset(x, 0f),
        end = Offset(x, size.height),
        strokeWidth = 1f
      )
    }

    for (r in 0..rows) {
      val y = r * gridSpacing
      drawLine(
        color = Color(0xFFFFFFFF),
        start = Offset(0f, y),
        end = Offset(size.width, y),
        strokeWidth = 1f
      )
    }

    // Center Crosshair
    drawCircle(
      color = Color(0xFFFF0055),
      radius = 120.dp.toPx(),
      center = Offset(size.width / 2f, size.height / 2f),
      style = Stroke(width = 2f)
    )
    drawLine(
      color = Color(0xFFFF0055),
      start = Offset(size.width / 2f, 0f),
      end = Offset(size.width / 2f, size.height),
      strokeWidth = 2f
    )
    drawLine(
      color = Color(0xFFFF0055),
      start = Offset(0f, size.height / 2f),
      end = Offset(size.width, size.height / 2f),
      strokeWidth = 2f
    )
  }
}

@Composable
private fun MotionUfoPattern(targetFps: Float, modifier: Modifier = Modifier) {
  Box(modifier = modifier.fillMaxSize().background(DisplayBlack)) {
    MovingMotionBarAnimation(targetFps = targetFps)
  }
}

@Composable
private fun ContrastCheckerPattern(modifier: Modifier = Modifier) {
  Canvas(modifier = modifier.fillMaxSize()) {
    val cols = 8
    val rows = 6
    val cellWidth = size.width / cols
    val cellHeight = size.height / rows

    for (r in 0 until rows) {
      for (c in 0 until cols) {
        val isWhite = (r + c) % 2 == 0
        drawRect(
          color = if (isWhite) Color.White else Color.Black,
          topLeft = Offset(c * cellWidth, r * cellHeight),
          size = Size(cellWidth, cellHeight)
        )
      }
    }
  }
}
