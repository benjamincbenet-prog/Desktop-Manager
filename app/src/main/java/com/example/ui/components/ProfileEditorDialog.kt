package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityOnPrimary
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.RoseError
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ProfileEditorDialog(
  profile: ProfileEntity,
  onSave: (ProfileEntity) -> Unit,
  onDelete: (ProfileEntity) -> Unit,
  onDismiss: () -> Unit
) {
  var name by remember { mutableStateOf(profile.name) }
  var description by remember { mutableStateOf(profile.description) }
  var selectedResolution by remember { mutableStateOf(Pair(profile.resolutionWidth, profile.resolutionHeight)) }
  var selectedRefreshRate by remember { mutableFloatStateOf(profile.refreshRate) }
  var isHdrEnabled by remember { mutableStateOf(profile.isHdrEnabled) }
  var selectedHdrFormat by remember { mutableStateOf(profile.hdrFormat) }
  var selectedGamut by remember { mutableStateOf(profile.colorGamut) }
  var colorTemperatureK by remember { mutableIntStateOf(profile.colorTemperatureK) }
  var brightnessNits by remember { mutableIntStateOf(profile.brightnessNits) }
  var keepAwake by remember { mutableStateOf(profile.keepAwake) }
  var blackScreenPhone by remember { mutableStateOf(profile.blackScreenPhone) }

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

  AlertDialog(
    onDismissRequest = onDismiss,
    title = {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Text(
          text = if (profile.id == 0L) "Create Display Profile" else "Edit Profile",
          color = TextPrimary,
          fontSize = 16.sp,
          fontWeight = FontWeight.Bold
        )

        if (!profile.isPreset && profile.id != 0L) {
          IconButton(
            onClick = { onDelete(profile); onDismiss() },
            modifier = Modifier.size(32.dp)
          ) {
            Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = RoseError)
          }
        }
      }
    },
    text = {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .verticalScroll(rememberScrollState()),
        verticalArrangement = Arrangement.spacedBy(14.dp)
      ) {
        // Name & Description
        OutlinedTextField(
          value = name,
          onValueChange = { name = it },
          label = { Text("Profile Name") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HighDensityPrimary,
            unfocusedBorderColor = DisplayBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = HighDensityPrimary,
            unfocusedLabelColor = TextSecondary
          ),
          modifier = Modifier.fillMaxWidth().testTag("profile_name_input")
        )

        OutlinedTextField(
          value = description,
          onValueChange = { description = it },
          label = { Text("Description") },
          colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = HighDensityPrimary,
            unfocusedBorderColor = DisplayBorder,
            focusedTextColor = TextPrimary,
            unfocusedTextColor = TextPrimary,
            focusedLabelColor = HighDensityPrimary,
            unfocusedLabelColor = TextSecondary
          ),
          modifier = Modifier.fillMaxWidth()
        )

        // Custom Resolution Picker
        Text(
          text = "OUTPUT RESOLUTION",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          resolutions.forEach { (res, label) ->
            val isSelected = selectedResolution == res
            FilterChip(
              selected = isSelected,
              onClick = { selectedResolution = res },
              label = { Text(label, fontSize = 11.sp) },
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

        // Refresh Rate Selector
        Text(
          text = "REFRESH RATE",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          refreshRates.forEach { rate ->
            val isSelected = selectedRefreshRate == rate
            FilterChip(
              selected = isSelected,
              onClick = { selectedRefreshRate = rate },
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

        // HDR Format & Mode
        Text(
          text = "HDR MODE & FORMAT",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          hdrFormats.forEach { format ->
            val isSelected = selectedHdrFormat == format
            FilterChip(
              selected = isSelected,
              onClick = {
                selectedHdrFormat = format
                isHdrEnabled = format != "SDR"
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

        // Color Gamut
        Text(
          text = "COLOR GAMUT (COLOR SPACE)",
          color = TextSecondary,
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold,
          letterSpacing = 0.5.sp
        )
        FlowRow(
          horizontalArrangement = Arrangement.spacedBy(6.dp),
          verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
          colorGamuts.forEach { gamut ->
            val isSelected = selectedGamut == gamut
            FilterChip(
              selected = isSelected,
              onClick = { selectedGamut = gamut },
              label = { Text(gamut, fontSize = 12.sp) },
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

        // Color Temperature Kelvin
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "COLOR TEMPERATURE", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "${colorTemperatureK}K", color = HighDensityPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
          Slider(
            value = colorTemperatureK.toFloat(),
            onValueChange = { colorTemperatureK = it.toInt() },
            valueRange = 4000f..9500f,
            steps = 11,
            colors = SliderDefaults.colors(
              thumbColor = HighDensityPrimary,
              activeTrackColor = HighDensityPrimary
            )
          )
        }

        // Peak Brightness Slider
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
          ) {
            Text(text = "PEAK BRIGHTNESS", color = TextSecondary, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            Text(text = "$brightnessNits Nits", color = HighDensityGreen, fontSize = 12.sp, fontWeight = FontWeight.Bold, fontFamily = FontFamily.Monospace)
          }
          Slider(
            value = brightnessNits.toFloat(),
            onValueChange = { brightnessNits = it.toInt() },
            valueRange = 100f..1200f,
            colors = SliderDefaults.colors(
              thumbColor = HighDensityGreen,
              activeTrackColor = HighDensityGreen
            )
          )
        }

        // Feature Toggles: Keep Awake & Black Screen Phone
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Keep External Display Awake", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Prevent monitor from dimming/sleeping", color = TextSecondary, fontSize = 11.sp)
          }
          Switch(
            checked = keepAwake,
            onCheckedChange = { keepAwake = it },
            colors = SwitchDefaults.colors(checkedThumbColor = HighDensityGreen, checkedTrackColor = HighDensityGreen.copy(alpha = 0.4f), uncheckedTrackColor = DisplayBlack)
          )
        }

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Column {
            Text(text = "Phone Black Screen (AMOLED)", color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
            Text(text = "Turn off phone screen while projecting", color = TextSecondary, fontSize = 11.sp)
          }
          Switch(
            checked = blackScreenPhone,
            onCheckedChange = { blackScreenPhone = it },
            colors = SwitchDefaults.colors(checkedThumbColor = HighDensityPrimary, checkedTrackColor = HighDensityPrimary.copy(alpha = 0.4f), uncheckedTrackColor = DisplayBlack)
          )
        }
      }
    },
    confirmButton = {
      Button(
        onClick = {
          val entity = profile.copy(
            name = name.ifBlank { "Custom Profile" },
            description = description.ifBlank { "Configured display settings" },
            resolutionWidth = selectedResolution.first,
            resolutionHeight = selectedResolution.second,
            refreshRate = selectedRefreshRate,
            isHdrEnabled = selectedHdrFormat != "SDR",
            hdrFormat = selectedHdrFormat,
            colorGamut = selectedGamut,
            colorTemperatureK = colorTemperatureK,
            brightnessNits = brightnessNits,
            keepAwake = keepAwake,
            blackScreenPhone = blackScreenPhone
          )
          onSave(entity)
        },
        colors = ButtonDefaults.buttonColors(containerColor = HighDensityPrimary, contentColor = HighDensityOnPrimary),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier.testTag("save_profile_button")
      ) {
        Text("Save Profile", fontWeight = FontWeight.Bold)
      }
    },
    dismissButton = {
      TextButton(onClick = onDismiss) {
        Text("Cancel", color = TextSecondary)
      }
    },
    containerColor = DisplayDarkSurface,
    shape = RoundedCornerShape(20.dp)
  )
}

