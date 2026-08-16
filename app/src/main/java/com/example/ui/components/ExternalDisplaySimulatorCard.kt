package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.model.CalibrationPatternType
import com.example.model.DisplayTelemetry
import com.example.ui.presentation.ExternalPresentationContent
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityOnPrimary
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun ExternalDisplaySimulatorCard(
  profile: ProfileEntity?,
  telemetry: DisplayTelemetry,
  calibrationPattern: CalibrationPatternType?,
  isMonitorConnected: Boolean = true,
  onToggleMonitorConnection: (() -> Unit)? = null,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(DisplayDarkSurface, RoundedCornerShape(18.dp))
      .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp))
      .padding(14.dp)
  ) {
    // Header
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(verticalAlignment = Alignment.CenterVertically) {
        Icon(
          imageVector = if (isMonitorConnected) Icons.Default.Tv else Icons.Default.TvOff,
          contentDescription = null,
          tint = if (isMonitorConnected) HighDensityPrimary else TextTertiary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
          text = "EXTERNAL DISPLAY SIMULATOR",
          color = TextPrimary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
      }

      Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        // Status Badge
        Row(
          modifier = Modifier
            .background(
              if (isMonitorConnected) HighDensityGreen.copy(alpha = 0.15f) else DisplayBorder.copy(alpha = 0.4f),
              RoundedCornerShape(6.dp)
            )
            .padding(horizontal = 8.dp, vertical = 3.dp),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(6.dp)
              .background(
                if (isMonitorConnected) HighDensityGreen else TextTertiary,
                CircleShape
              )
          )
          Spacer(modifier = Modifier.width(4.dp))
          Text(
            text = if (isMonitorConnected) "LIVE OUTPUT" else "STANDBY",
            color = if (isMonitorConnected) HighDensityGreen else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold
          )
        }

        // Quick Connect / Disconnect Action in Header
        if (onToggleMonitorConnection != null) {
          Box(
            modifier = Modifier
              .clip(RoundedCornerShape(6.dp))
              .clickable { onToggleMonitorConnection() }
              .background(
                if (isMonitorConnected) DisplayBorder.copy(alpha = 0.6f) else HighDensityPrimary
              )
              .padding(horizontal = 8.dp, vertical = 3.dp)
              .testTag("simulator_header_toggle_button")
          ) {
            Text(
              text = if (isMonitorConnected) "Disconnect" else "Connect",
              color = if (isMonitorConnected) TextPrimary else HighDensityOnPrimary,
              fontSize = 10.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Monitor Bezel & Live Screen View
    Box(
      modifier = Modifier
        .fillMaxWidth()
        .aspectRatio(16f / 9f)
        .background(DisplayBlack, RoundedCornerShape(10.dp))
        .border(1.5.dp, DisplayBorder, RoundedCornerShape(10.dp))
        .clip(RoundedCornerShape(10.dp))
    ) {
      if (isMonitorConnected) {
        ExternalPresentationContent(
          profile = profile,
          telemetry = telemetry,
          calibrationPattern = calibrationPattern,
          modifier = Modifier.fillMaxSize()
        )
      } else {
        // Disconnected Standby State Screen
        Column(
          modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF141218))
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.Center
        ) {
          Icon(
            imageVector = Icons.Default.TvOff,
            contentDescription = "Monitor Disconnected",
            tint = TextTertiary,
            modifier = Modifier.size(36.dp)
          )
          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "Monitor Output Disconnected",
            color = TextPrimary,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = "Video stream is currently paused to conserve power",
            color = TextTertiary,
            fontSize = 10.sp
          )
          if (onToggleMonitorConnection != null) {
            Spacer(modifier = Modifier.height(12.dp))
            Button(
              onClick = { onToggleMonitorConnection() },
              colors = ButtonDefaults.buttonColors(
                containerColor = HighDensityPrimary,
                contentColor = HighDensityOnPrimary
              ),
              shape = RoundedCornerShape(10.dp),
              modifier = Modifier
                .height(34.dp)
                .testTag("simulator_reconnect_button")
            ) {
              Icon(
                imageVector = Icons.Default.CastConnected,
                contentDescription = null,
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "Connect Monitor",
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }
        }
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // Info footer under monitor frame
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Text(
        text = if (isMonitorConnected) {
          "${profile?.resolutionWidth ?: 3840}x${profile?.resolutionHeight ?: 2160} @ ${profile?.refreshRate?.toInt() ?: 144}Hz"
        } else {
          "Signal: Suspended (Standby)"
        },
        color = if (isMonitorConnected) HighDensityPrimary else TextTertiary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace,
        fontWeight = FontWeight.SemiBold
      )
      Text(
        text = if (isMonitorConnected) {
          "Color: ${profile?.colorGamut ?: "DCI-P3"} • ${profile?.hdrFormat ?: "HDR10"}"
        } else {
          "Port: USB-C / HDMI Ready"
        },
        color = TextTertiary,
        fontSize = 11.sp,
        fontFamily = FontFamily.Monospace
      )
    }
  }
}


