package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.HdrOn
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.TvOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayCardElevated
import com.example.ui.theme.DisplayCardSurface
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityOnPrimary
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.IndigoAccent
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DisplaySettingsScreen(
  activeProfile: ProfileEntity?,
  connectedDisplays: List<ConnectedDisplay>,
  selectedDisplayId: Int,
  isAwakeLocked: Boolean,
  isMonitorConnected: Boolean,
  onToggleKeepAwake: () -> Unit,
  onToggleMonitorConnection: () -> Unit,
  onEnterBlackScreen: () -> Unit,
  onUpdateResolutionAndHdr: (width: Int, height: Int, refreshRate: Float, isHdr: Boolean, hdrFormat: String, gamut: String, kelvin: Int, nits: Int) -> Unit,
  modifier: Modifier = Modifier
) {
  val activeDisplay = connectedDisplays.find { it.id == selectedDisplayId }
    ?: connectedDisplays.firstOrNull()

  var selectedResolution by remember(activeProfile) {
    mutableStateOf(Pair(activeProfile?.resolutionWidth ?: 3840, activeProfile?.resolutionHeight ?: 2160))
  }
  var selectedRefreshRate by remember(activeProfile) {
    mutableFloatStateOf(activeProfile?.refreshRate ?: 144f)
  }
  var selectedHdrFormat by remember(activeProfile) {
    mutableStateOf(activeProfile?.hdrFormat ?: "HDR10")
  }
  var selectedGamut by remember(activeProfile) {
    mutableStateOf(activeProfile?.colorGamut ?: "DCI-P3")
  }
  var colorTempKelvin by remember(activeProfile) {
    mutableIntStateOf(activeProfile?.colorTemperatureK ?: 6500)
  }
  var brightnessNits by remember(activeProfile) {
    mutableIntStateOf(activeProfile?.brightnessNits ?: 850)
  }

  val resolutions = listOf(
    Pair(3840, 2160) to "4K UHD (3840x2160)",
    Pair(2560, 1440) to "QHD (2560x1440)",
    Pair(1920, 1080) to "1080p FHD (1920x1080)",
    Pair(3440, 1440) to "Ultrawide (3440x1440)",
    Pair(1280, 720) to "720p HD (1280x720)"
  )

  val refreshRates = listOf(24f, 30f, 60f, 90f, 120f, 144f)
  val hdrFormats = listOf("SDR", "HDR10", "HDR10+", "HLG", "Dolby Vision")
  val colorGamuts = listOf("sRGB", "DCI-P3", "Rec.2020")

  fun applyChanges(
    res: Pair<Int, Int> = selectedResolution,
    rate: Float = selectedRefreshRate,
    hdr: String = selectedHdrFormat,
    gamut: String = selectedGamut,
    kelvin: Int = colorTempKelvin,
    nits: Int = brightnessNits
  ) {
    onUpdateResolutionAndHdr(
      res.first,
      res.second,
      rate,
      hdr != "SDR",
      hdr,
      gamut,
      kelvin,
      nits
    )
  }

  LazyColumn(
    modifier = modifier.fillMaxSize().background(DisplayBlack).testTag("display_settings_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Monitor Connection & Link Control Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(
            1.dp,
            if (isMonitorConnected) HighDensityPrimary.copy(alpha = 0.5f) else DisplayBorder,
            RoundedCornerShape(18.dp)
          ),
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
                imageVector = if (isMonitorConnected) Icons.Default.CastConnected else Icons.Default.TvOff,
                contentDescription = null,
                tint = if (isMonitorConnected) HighDensityPrimary else TextTertiary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Column {
                Text(
                  text = "MONITOR CONNECTION LINK",
                  color = TextPrimary,
                  fontSize = 11.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
                Text(
                  text = if (isMonitorConnected) "Active Presentation Stream" else "Port Standby / Disconnected",
                  color = if (isMonitorConnected) HighDensityGreen else TextTertiary,
                  fontSize = 11.sp
                )
              }
            }

            Box(
              modifier = Modifier
                .background(
                  if (isMonitorConnected) HighDensityGreen.copy(alpha = 0.15f) else DisplayBorder.copy(alpha = 0.4f),
                  RoundedCornerShape(6.dp)
                )
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = if (isMonitorConnected) "CONNECTED" else "DISCONNECTED",
                color = if (isMonitorConnected) HighDensityGreen else TextSecondary,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
              )
            }
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Big Connect/Disconnect Action Button
          Button(
            onClick = { onToggleMonitorConnection() },
            modifier = Modifier
              .fillMaxWidth()
              .height(44.dp)
              .testTag("settings_connect_disconnect_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = if (isMonitorConnected) DisplayBorder else HighDensityPrimary,
              contentColor = if (isMonitorConnected) TextPrimary else HighDensityOnPrimary
            ),
            shape = RoundedCornerShape(12.dp)
          ) {
            Icon(
              imageVector = if (isMonitorConnected) Icons.Default.PowerSettingsNew else Icons.Default.CastConnected,
              contentDescription = null,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = if (isMonitorConnected) "Disconnect Monitor Output" else "Connect External Monitor",
              fontSize = 13.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Hardware Display Info Card
    item {
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
                imageVector = Icons.Default.Info,
                contentDescription = null,
                tint = HighDensityPrimary,
                modifier = Modifier.size(18.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "DETECTED HARDWARE INTERFACE",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
            }

            Box(
              modifier = Modifier
                .background(HighDensityPrimary.copy(alpha = 0.15f), RoundedCornerShape(6.dp))
                .padding(horizontal = 8.dp, vertical = 3.dp)
            ) {
              Text(
                text = activeDisplay?.name ?: "DisplayLink Pro",
                color = HighDensityPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
              )
            }
          }

          Spacer(modifier = Modifier.height(10.dp))

          HardwareDetailRow("Display ID", "${activeDisplay?.id ?: 0}")
          HardwareDetailRow("Native Resolution", "${activeDisplay?.width ?: 3840} x ${activeDisplay?.height ?: 2160} px")
          HardwareDetailRow("Physical Refresh Rate", "${activeDisplay?.refreshRate?.toInt() ?: 144} Hz")
          HardwareDetailRow("Density DPI", "${activeDisplay?.densityDpi ?: 320} dpi")
          HardwareDetailRow("Hardware HDR Capable", if (activeDisplay?.isHdrSupported == true) "YES (HDR10/HLG)" else "Standard SDR")
          HardwareDetailRow("Wide Color Gamut", if (activeDisplay?.isWideColorGamut == true) "DCI-P3 / BT.2020" else "sRGB")
        }
      }
    }

    // Keep Display Awake & Black Phone Controls Card
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
        shape = RoundedCornerShape(18.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
              imageVector = Icons.Default.LockClock,
              contentDescription = null,
              tint = HighDensityGreen,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "AWAKE LOCK & POWER MANAGEMENT",
              color = TextPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 0.5.sp
            )
          }

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Column(modifier = Modifier.weight(1f)) {
              Text(
                text = "Keep External Display Awake",
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold
              )
              Text(
                text = "Locks screen power flags so the monitor never sleeps or dims during long sessions",
                color = TextSecondary,
                fontSize = 11.sp
              )
            }
            Switch(
              checked = isAwakeLocked,
              onCheckedChange = { onToggleKeepAwake() },
              colors = SwitchDefaults.colors(
                checkedThumbColor = HighDensityGreen,
                checkedTrackColor = HighDensityGreen.copy(alpha = 0.4f),
                uncheckedThumbColor = TextTertiary,
                uncheckedTrackColor = DisplayBlack
              )
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(RoundedCornerShape(10.dp))
              .clickable { onEnterBlackScreen() }
              .background(DisplayBlack)
              .border(1.dp, DisplayBorder, RoundedCornerShape(10.dp))
              .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.DarkMode,
                contentDescription = null,
                tint = HighDensityPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(10.dp))
              Column {
                Text(
                  text = "Turn Phone Screen Pitch Black (AMOLED)",
                  color = TextPrimary,
                  fontSize = 13.sp,
                  fontWeight = FontWeight.SemiBold
                )
                Text(
                  text = "Double-tap phone to wake anytime",
                  color = TextTertiary,
                  fontSize = 11.sp
                )
              }
            }

            Text(
              text = "Activate",
              color = HighDensityPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }
        }
      }
    }

    // Resolution Settings
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
        shape = RoundedCornerShape(18.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "CUSTOM RESOLUTION SETTINGS",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            resolutions.forEach { (res, label) ->
              val isSelected = selectedResolution == res
              FilterChip(
                selected = isSelected,
                onClick = {
                  selectedResolution = res
                  applyChanges(res = res)
                },
                label = { Text(label, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = HighDensityPrimaryContainer,
                  selectedLabelColor = HighDensityPrimary,
                  labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                  borderColor = if (isSelected) HighDensityPrimary else DisplayBorder,
                  selectedBorderColor = HighDensityPrimary,
                  enabled = true,
                  selected = isSelected
                )
              )
            }
          }
        }
      }
    }

    // Refresh Rate Settings
    item {
      Card(
        modifier = Modifier
          .fillMaxWidth()
          .border(1.dp, DisplayBorder, RoundedCornerShape(18.dp)),
        colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
        shape = RoundedCornerShape(18.dp)
      ) {
        Column(modifier = Modifier.padding(16.dp)) {
          Text(
            text = "TARGET REFRESH RATE (HZ)",
            color = TextPrimary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
          )
          Spacer(modifier = Modifier.height(10.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            refreshRates.forEach { rate ->
              val isSelected = selectedRefreshRate == rate
              FilterChip(
                selected = isSelected,
                onClick = {
                  selectedRefreshRate = rate
                  applyChanges(rate = rate)
                },
                label = { Text("${rate.toInt()} Hz", fontSize = 12.sp, fontWeight = FontWeight.SemiBold) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = HighDensityGreen.copy(alpha = 0.2f),
                  selectedLabelColor = HighDensityGreen,
                  labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                  borderColor = if (isSelected) HighDensityGreen else DisplayBorder,
                  selectedBorderColor = HighDensityGreen,
                  enabled = true,
                  selected = isSelected
                )
              )
            }
          }
        }
      }
    }

    // HDR Mode Toggling & Color Format
    item {
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
                imageVector = Icons.Default.HdrOn,
                contentDescription = null,
                tint = HighDensityPrimary,
                modifier = Modifier.size(20.dp)
              )
              Spacer(modifier = Modifier.width(8.dp))
              Text(
                text = "HDR MODE TOGGLING & COLOR GRADING",
                color = TextPrimary,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 0.5.sp
              )
            }

            Text(
              text = selectedHdrFormat,
              color = HighDensityPrimary,
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          Spacer(modifier = Modifier.height(10.dp))

          FlowRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            hdrFormats.forEach { format ->
              val isSelected = selectedHdrFormat == format
              FilterChip(
                selected = isSelected,
                onClick = {
                  selectedHdrFormat = format
                  applyChanges(hdr = format)
                },
                label = { Text(format, fontSize = 12.sp) },
                colors = FilterChipDefaults.filterChipColors(
                  selectedContainerColor = HighDensityPrimaryContainer,
                  selectedLabelColor = HighDensityPrimary,
                  labelColor = TextSecondary
                ),
                border = FilterChipDefaults.filterChipBorder(
                  borderColor = if (isSelected) HighDensityPrimary else DisplayBorder,
                  selectedBorderColor = HighDensityPrimary,
                  enabled = true,
                  selected = isSelected
                )
              )
            }
          }

          Spacer(modifier = Modifier.height(16.dp))

          // Color Temperature Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "White Point Temperature", color = TextSecondary, fontSize = 12.sp)
            Text(text = "${colorTempKelvin}K (D65)", color = HighDensityPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
          Slider(
            value = colorTempKelvin.toFloat(),
            onValueChange = {
              colorTempKelvin = it.toInt()
              applyChanges(kelvin = it.toInt())
            },
            valueRange = 4000f..9500f,
            steps = 11,
            colors = SliderDefaults.colors(thumbColor = HighDensityPrimary, activeTrackColor = HighDensityPrimary)
          )

          Spacer(modifier = Modifier.height(10.dp))

          // Peak Luminance Slider
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "HDR Peak Luminance", color = TextSecondary, fontSize = 12.sp)
            Text(text = "$brightnessNits Nits", color = HighDensityGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
          Slider(
            value = brightnessNits.toFloat(),
            onValueChange = {
              brightnessNits = it.toInt()
              applyChanges(nits = it.toInt())
            },
            valueRange = 100f..1200f,
            colors = SliderDefaults.colors(thumbColor = HighDensityGreen, activeTrackColor = HighDensityGreen)
          )
        }
      }
    }
  }
}

@Composable
private fun HardwareDetailRow(label: String, value: String) {
  Row(
    modifier = Modifier
      .fillMaxWidth()
      .padding(vertical = 3.dp),
    horizontalArrangement = Arrangement.SpaceBetween,
    verticalAlignment = Alignment.CenterVertically
  ) {
    Text(text = label, color = TextSecondary, fontSize = 12.sp)
    Text(
      text = value,
      color = TextPrimary,
      fontSize = 12.sp,
      fontWeight = FontWeight.SemiBold,
      fontFamily = FontFamily.Monospace
    )
  }
}

