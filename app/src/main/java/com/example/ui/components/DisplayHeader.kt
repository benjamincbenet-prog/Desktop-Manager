package com.example.ui.components

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cast
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.ScreenShare
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.model.ConnectedDisplay
import com.example.model.DisplayType
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayCardElevated
import com.example.ui.theme.DisplayCardSurface
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityOnPrimary
import com.example.ui.theme.HighDensityOnPrimaryContainer
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun DisplayHeader(
  activeProfile: ProfileEntity?,
  connectedDisplays: List<ConnectedDisplay>,
  selectedDisplayId: Int,
  isAwakeLocked: Boolean,
  isMonitorConnected: Boolean,
  onToggleKeepAwake: () -> Unit,
  onToggleMonitorConnection: () -> Unit,
  onEnterBlackScreen: () -> Unit,
  onSelectDisplay: (Int) -> Unit,
  modifier: Modifier = Modifier
) {
  var showDisplaySelectorDialog by remember { mutableStateOf(false) }

  val activeDisplay = connectedDisplays.find { it.id == selectedDisplayId }
    ?: connectedDisplays.firstOrNull()

  // Pulsing animation for active monitor status
  val infiniteTransition = rememberInfiniteTransition(label = "pulse_trans")
  val pulseAlpha by infiniteTransition.animateFloat(
    initialValue = 0.4f,
    targetValue = 1.0f,
    animationSpec = infiniteRepeatable(
      animation = tween(1000),
      repeatMode = RepeatMode.Reverse
    ),
    label = "pulse_alpha"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(DisplayBlack)
      .padding(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 6.dp)
  ) {
    // Top Row: App Title & Settings Icon Button
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Column {
        Text(
          text = "DisplayLink Pro",
          color = TextPrimary,
          fontSize = 20.sp,
          fontWeight = FontWeight.Medium,
          letterSpacing = (-0.5).sp
        )
        Row(
          verticalAlignment = Alignment.CenterVertically,
          modifier = Modifier.padding(top = 2.dp)
        ) {
          Box(
            modifier = Modifier
              .size(8.dp)
              .background(
                if (isMonitorConnected) HighDensityGreen.copy(alpha = pulseAlpha) else TextTertiary,
                CircleShape
              )
          )
          Spacer(modifier = Modifier.width(6.dp))
          Text(
            text = if (isMonitorConnected) {
              "${activeDisplay?.name?.uppercase() ?: "EXTERNAL MONITOR"} ACTIVE"
            } else {
              "MONITOR DISCONNECTED (STANDBY)"
            },
            color = if (isMonitorConnected) HighDensityGreen else TextSecondary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
        }
      }

      IconButton(
        onClick = { showDisplaySelectorDialog = true },
        modifier = Modifier
          .size(40.dp)
          .background(DisplayBorder, CircleShape)
          .testTag("header_settings_button")
      ) {
        Icon(
          imageVector = Icons.Default.Settings,
          contentDescription = "Display Output Selection",
          tint = TextPrimary,
          modifier = Modifier.size(20.dp)
        )
      }
    }

    Spacer(modifier = Modifier.height(10.dp))

    // High Density Quick Bar: Active Display Pill + Connect/Disconnect Monitor + Keep Awake Toggle
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = Arrangement.spacedBy(8.dp),
      verticalAlignment = Alignment.CenterVertically
    ) {
      // Display info badge
      Row(
        modifier = Modifier
          .weight(1f)
          .clip(RoundedCornerShape(12.dp))
          .clickable { showDisplaySelectorDialog = true }
          .background(DisplayDarkSurface)
          .border(1.dp, DisplayBorder, RoundedCornerShape(12.dp))
          .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
      ) {
        val displayIcon = if (!isMonitorConnected) {
          Icons.Default.TvOff
        } else {
          when (activeDisplay?.type) {
            DisplayType.INTERNAL -> Icons.Default.Tv
            DisplayType.EXTERNAL_HDMI -> Icons.Default.Tv
            DisplayType.EXTERNAL_TYPE_C -> Icons.Default.CastConnected
            DisplayType.WIRELESS_CAST -> Icons.Default.Cast
            DisplayType.VIRTUAL_SIMULATOR -> Icons.Default.ScreenShare
            null -> Icons.Default.Tv
          }
        }

        Icon(
          imageVector = displayIcon,
          contentDescription = null,
          tint = if (isMonitorConnected) HighDensityPrimary else TextTertiary,
          modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Column {
          Text(
            text = if (isMonitorConnected) (activeDisplay?.name ?: "Display") else "Monitor Off",
            color = if (isMonitorConnected) TextPrimary else TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1
          )
          Text(
            text = if (isMonitorConnected) {
              "${activeProfile?.resolutionWidth ?: 3840}x${activeProfile?.resolutionHeight ?: 2160} • ${activeProfile?.refreshRate?.toInt() ?: 60}Hz"
            } else {
              "Standby • Link Paused"
            },
            color = TextTertiary,
            fontSize = 10.sp,
            fontFamily = FontFamily.Monospace
          )
        }
      }

      // Connect / Disconnect Monitor Button
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onToggleMonitorConnection() }
          .background(
            if (isMonitorConnected) DisplayDarkSurface else HighDensityPrimary
          )
          .border(
            1.dp,
            if (isMonitorConnected) DisplayBorder else HighDensityPrimary,
            RoundedCornerShape(12.dp)
          )
          .padding(horizontal = 10.dp, vertical = 8.dp)
          .testTag("connect_disconnect_monitor_button"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = if (isMonitorConnected) Icons.Default.PowerSettingsNew else Icons.Default.CastConnected,
          contentDescription = if (isMonitorConnected) "Disconnect Monitor" else "Connect Monitor",
          tint = if (isMonitorConnected) HighDensityPrimary else HighDensityOnPrimary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
          text = if (isMonitorConnected) "Disconnect" else "Connect",
          color = if (isMonitorConnected) TextPrimary else HighDensityOnPrimary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }

      // Awake Lock Action Button
      Row(
        modifier = Modifier
          .clip(RoundedCornerShape(12.dp))
          .clickable { onToggleKeepAwake() }
          .background(
            if (isAwakeLocked) HighDensityPrimary.copy(alpha = 0.2f) else DisplayDarkSurface
          )
          .border(
            1.dp,
            if (isAwakeLocked) HighDensityPrimary else DisplayBorder,
            RoundedCornerShape(12.dp)
          )
          .padding(horizontal = 10.dp, vertical = 8.dp)
          .testTag("keep_awake_toggle"),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Icon(
          imageVector = if (isAwakeLocked) Icons.Default.Lock else Icons.Default.LockOpen,
          contentDescription = "Awake Lock",
          tint = if (isAwakeLocked) HighDensityPrimary else TextSecondary,
          modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
          text = if (isAwakeLocked) "Awake" else "Normal",
          color = if (isAwakeLocked) HighDensityPrimary else TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }

  // Display Selection Dialog
  if (showDisplaySelectorDialog) {
    AlertDialog(
      onDismissRequest = { showDisplaySelectorDialog = false },
      title = {
        Text(
          text = "Target Video Output & Link",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )
      },
      text = {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
          // Monitor Connection State Box inside Dialog
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(14.dp))
              .background(if (isMonitorConnected) HighDensityPrimary.copy(alpha = 0.15f) else DisplayBorder.copy(alpha = 0.3f))
              .border(
                1.dp,
                if (isMonitorConnected) HighDensityPrimary else DisplayBorder,
                RoundedCornerShape(14.dp)
              )
              .clickable { onToggleMonitorConnection() }
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = if (isMonitorConnected) Icons.Default.CastConnected else Icons.Default.TvOff,
                contentDescription = null,
                tint = if (isMonitorConnected) HighDensityPrimary else TextTertiary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = if (isMonitorConnected) "Monitor Connected" else "Monitor Disconnected",
                  color = TextPrimary,
                  fontWeight = FontWeight.Bold,
                  fontSize = 13.sp
                )
                Text(
                  text = if (isMonitorConnected) "Signal output active" else "Video link is currently paused",
                  color = if (isMonitorConnected) HighDensityGreen else TextTertiary,
                  fontSize = 11.sp
                )
              }
            }

            Box(
              modifier = Modifier
                .background(
                  if (isMonitorConnected) HighDensityPrimary else DisplayDarkSurface,
                  RoundedCornerShape(8.dp)
                )
                .border(1.dp, if (isMonitorConnected) HighDensityPrimary else DisplayBorder, RoundedCornerShape(8.dp))
                .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
              Text(
                text = if (isMonitorConnected) "Disconnect" else "Connect",
                color = if (isMonitorConnected) HighDensityOnPrimary else TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Text(
            text = "AVAILABLE OUTPUT INTERFACES",
            color = HighDensityPrimary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.8.sp,
            modifier = Modifier.padding(top = 4.dp)
          )

          connectedDisplays.forEach { display ->
            val isSelected = display.id == selectedDisplayId
            Row(
              modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(14.dp))
                .clickable {
                  onSelectDisplay(display.id)
                  showDisplaySelectorDialog = false
                }
                .background(if (isSelected) HighDensityPrimaryContainer else DisplayDarkSurface)
                .border(
                  1.dp,
                  if (isSelected) HighDensityPrimary else DisplayBorder,
                  RoundedCornerShape(14.dp)
                )
                .padding(12.dp),
              horizontalArrangement = Arrangement.SpaceBetween,
              verticalAlignment = Alignment.CenterVertically
            ) {
              Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                  imageVector = if (display.type == DisplayType.INTERNAL) Icons.Default.Tv else Icons.Default.CastConnected,
                  contentDescription = null,
                  tint = if (isSelected) HighDensityOnPrimaryContainer else HighDensityPrimary,
                  modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                  Text(
                    text = display.name,
                    color = if (isSelected) HighDensityOnPrimaryContainer else TextPrimary,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    fontSize = 13.sp
                  )
                  Text(
                    text = "${display.width}x${display.height} @ ${display.refreshRate.toInt()}Hz ${if (display.isHdrSupported) "• HDR" else ""}",
                    color = if (isSelected) HighDensityOnPrimaryContainer.copy(alpha = 0.8f) else TextTertiary,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                  )
                }
              }

              if (isSelected) {
                Icon(
                  imageVector = Icons.Default.Check,
                  contentDescription = "Selected",
                  tint = HighDensityOnPrimaryContainer,
                  modifier = Modifier.size(18.dp)
                )
              }
            }
          }
        }
      },
      confirmButton = {
        TextButton(onClick = { showDisplaySelectorDialog = false }) {
          Text("Done", color = HighDensityPrimary)
        }
      },
      containerColor = DisplayDarkSurface,
      shape = RoundedCornerShape(24.dp)
    )
  }
}

