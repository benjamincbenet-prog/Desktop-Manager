package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CoPresent
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.HdrOn
import androidx.compose.material.icons.filled.LockClock
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.model.CalibrationPatternType
import com.example.model.DisplayTelemetry
import com.example.ui.components.ExternalDisplaySimulatorCard
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayCardElevated
import com.example.ui.theme.DisplayCardSurface
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityOnPrimary
import com.example.ui.theme.HighDensityOnPrimaryContainer
import com.example.ui.theme.HighDensityOnSecondaryContainer
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import java.util.Locale

@Composable
fun ProfilesScreen(
  activeProfile: ProfileEntity?,
  allProfiles: List<ProfileEntity>,
  telemetry: DisplayTelemetry,
  calibrationPattern: CalibrationPatternType?,
  isMonitorConnected: Boolean = true,
  onToggleMonitorConnection: (() -> Unit)? = null,
  onSelectProfile: (ProfileEntity) -> Unit,
  onEditProfile: (ProfileEntity) -> Unit,
  onCreateProfile: () -> Unit,
  onEnterBlackScreen: () -> Unit,
  modifier: Modifier = Modifier
) {
  Box(modifier = modifier.fillMaxSize().background(DisplayBlack)) {
    LazyColumn(
      modifier = Modifier.fillMaxSize(),
      contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 96.dp),
      verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
      // High Density Metric Overview Grid (2 Columns)
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
          // Refresh Rate Card
          Card(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, DisplayBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "REFRESH RATE",
                  color = TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
                Icon(
                  imageVector = Icons.Default.Speed,
                  contentDescription = null,
                  tint = HighDensityPrimary,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = "${activeProfile?.refreshRate?.toInt() ?: 144} Hz",
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .background(HighDensityGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Stable Performance",
                  color = HighDensityGreen,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }

          // Color Accuracy Card
          Card(
            modifier = Modifier
              .weight(1f)
              .clip(RoundedCornerShape(16.dp))
              .border(1.dp, DisplayBorder, RoundedCornerShape(16.dp)),
            colors = CardDefaults.cardColors(containerColor = DisplayDarkSurface),
            shape = RoundedCornerShape(16.dp)
          ) {
            Column(modifier = Modifier.padding(14.dp)) {
              Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
              ) {
                Text(
                  text = "COLOR ACCURACY",
                  color = TextSecondary,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.Bold,
                  letterSpacing = 0.5.sp
                )
                Icon(
                  imageVector = Icons.Default.Palette,
                  contentDescription = null,
                  tint = HighDensityPrimary,
                  modifier = Modifier.size(16.dp)
                )
              }
              Spacer(modifier = Modifier.height(6.dp))
              Text(
                text = String.format(Locale.US, "ΔE %.1f", telemetry.deltaEAccuracy),
                color = TextPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                fontFamily = FontFamily.Monospace
              )
              Spacer(modifier = Modifier.height(4.dp))
              Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                  modifier = Modifier
                    .size(6.dp)
                    .background(HighDensityGreen, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                  text = "Calibrated • ${activeProfile?.colorGamut ?: "sRGB"}",
                  color = HighDensityGreen,
                  fontSize = 10.sp,
                  fontWeight = FontWeight.SemiBold
                )
              }
            }
          }
        }
      }

      // Profile Presets Section (Horizontal quick pills)
      item {
        Column {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = "PROFILE PRESETS",
              color = HighDensityPrimary,
              fontSize = 11.sp,
              fontWeight = FontWeight.SemiBold,
              letterSpacing = 1.2.sp
            )
            Text(
              text = "${allProfiles.size} Available",
              color = TextTertiary,
              fontSize = 10.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))

          LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(horizontal = 2.dp)
          ) {
            items(allProfiles, key = { it.id }) { profile ->
              val isSelected = profile.id == activeProfile?.id
              PresetQuickChip(
                profile = profile,
                isSelected = isSelected,
                onClick = { onSelectProfile(profile) }
              )
            }
          }
        }
      }

      // Live External Screen Simulator Preview
      item {
        ExternalDisplaySimulatorCard(
          profile = activeProfile,
          telemetry = telemetry,
          calibrationPattern = calibrationPattern,
          isMonitorConnected = isMonitorConnected,
          onToggleMonitorConnection = onToggleMonitorConnection
        )
      }

      // Full Detailed Profiles List Title
      item {
        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Text(
            text = "CUSTOM DISPLAY PROFILES",
            color = TextSecondary,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Text(
            text = "Touch card to activate",
            color = TextTertiary,
            fontSize = 10.sp
          )
        }
      }

      // Detailed Profiles List
      items(allProfiles, key = { it.id }) { profile ->
        val isActive = profile.id == activeProfile?.id
        HighDensityProfileCardItem(
          profile = profile,
          isActive = isActive,
          onSelect = { onSelectProfile(profile) },
          onEdit = { onEditProfile(profile) }
        )
      }

      // Stealth Mode Trigger Button (High Density Footer Capsule)
      item {
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(top = 4.dp),
          horizontalAlignment = Alignment.CenterHorizontally
        ) {
          Button(
            onClick = onEnterBlackScreen,
            modifier = Modifier
              .fillMaxWidth()
              .height(52.dp)
              .testTag("stealth_mode_action_button"),
            colors = ButtonDefaults.buttonColors(
              containerColor = HighDensityPrimary,
              contentColor = HighDensityOnPrimary
            ),
            shape = RoundedCornerShape(26.dp)
          ) {
            Icon(
              imageVector = Icons.Default.VisibilityOff,
              contentDescription = null,
              tint = HighDensityOnPrimary,
              modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "ENTER STEALTH MODE",
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold,
              letterSpacing = 1.5.sp
            )
          }

          Spacer(modifier = Modifier.height(8.dp))
          Text(
            text = "PHONE UI WILL BLACKOUT • EXTERNAL OUTPUT REMAINS ACTIVE",
            color = TextTertiary,
            fontSize = 10.sp,
            fontWeight = FontWeight.Medium,
            letterSpacing = 0.5.sp
          )
        }
      }
    }

    // Floating Action Button to create custom profile
    FloatingActionButton(
      onClick = onCreateProfile,
      containerColor = HighDensityPrimary,
      contentColor = HighDensityOnPrimary,
      shape = CircleShape,
      modifier = Modifier
        .align(Alignment.BottomEnd)
        .padding(16.dp)
        .testTag("create_profile_fab")
    ) {
      Icon(
        imageVector = Icons.Default.Add,
        contentDescription = "New Profile",
        modifier = Modifier.size(24.dp)
      )
    }
  }
}

@Composable
private fun PresetQuickChip(
  profile: ProfileEntity,
  isSelected: Boolean,
  onClick: () -> Unit
) {
  val containerColor = if (isSelected) HighDensityPrimaryContainer else HighDensitySecondaryContainer
  val contentColor = if (isSelected) HighDensityOnPrimaryContainer else HighDensityOnSecondaryContainer

  Column(
    modifier = Modifier
      .clip(RoundedCornerShape(16.dp))
      .clickable { onClick() }
      .background(containerColor)
      .padding(horizontal = 14.dp, vertical = 10.dp)
      .width(108.dp)
  ) {
    Icon(
      imageVector = getProfileIcon(profile.iconName),
      contentDescription = null,
      tint = contentColor,
      modifier = Modifier.size(20.dp)
    )
    Spacer(modifier = Modifier.height(4.dp))
    Text(
      text = profile.name,
      color = contentColor,
      fontSize = 12.sp,
      fontWeight = FontWeight.Bold,
      maxLines = 1
    )
    Text(
      text = "${profile.refreshRate.toInt()}Hz • ${profile.hdrFormat}",
      color = contentColor.copy(alpha = 0.75f),
      fontSize = 9.sp,
      fontFamily = FontFamily.Monospace,
      maxLines = 1
    )
  }
}

@Composable
private fun HighDensityProfileCardItem(
  profile: ProfileEntity,
  isActive: Boolean,
  onSelect: () -> Unit,
  onEdit: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(16.dp))
      .clickable { onSelect() }
      .border(
        width = if (isActive) 1.5.dp else 1.dp,
        color = if (isActive) HighDensityPrimary else DisplayBorder,
        shape = RoundedCornerShape(16.dp)
      )
      .testTag("profile_item_${profile.name.lowercase().replace(" ", "_")}"),
    colors = CardDefaults.cardColors(
      containerColor = if (isActive) DisplayCardElevated else DisplayDarkSurface
    ),
    shape = RoundedCornerShape(16.dp)
  ) {
    Column(modifier = Modifier.padding(14.dp)) {
      Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
      ) {
        Row(
          modifier = Modifier.weight(1f),
          verticalAlignment = Alignment.CenterVertically
        ) {
          Box(
            modifier = Modifier
              .size(36.dp)
              .background(
                if (isActive) HighDensityPrimary.copy(alpha = 0.2f) else DisplayBorder.copy(alpha = 0.3f),
                RoundedCornerShape(10.dp)
              ),
            contentAlignment = Alignment.Center
          ) {
            Icon(
              imageVector = getProfileIcon(profile.iconName),
              contentDescription = null,
              tint = if (isActive) HighDensityPrimary else TextSecondary,
              modifier = Modifier.size(18.dp)
            )
          }
          Spacer(modifier = Modifier.width(10.dp))
          Column {
            Text(
              text = profile.name,
              color = TextPrimary,
              fontSize = 14.sp,
              fontWeight = FontWeight.Bold
            )
            Text(
              text = "${profile.resolutionWidth}x${profile.resolutionHeight} @ ${profile.refreshRate.toInt()}Hz",
              color = if (isActive) HighDensityPrimary else TextTertiary,
              fontSize = 11.sp,
              fontFamily = FontFamily.Monospace
            )
          }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
          if (isActive) {
            Box(
              modifier = Modifier
                .background(HighDensityGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                .border(1.dp, HighDensityGreen, RoundedCornerShape(6.dp))
                .padding(horizontal = 6.dp, vertical = 2.dp)
            ) {
              Text(
                text = "ACTIVE",
                color = HighDensityGreen,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Spacer(modifier = Modifier.width(6.dp))
          }
          IconButton(onClick = onEdit, modifier = Modifier.size(32.dp)) {
            Icon(
              imageVector = Icons.Default.Edit,
              contentDescription = "Edit Profile",
              tint = TextTertiary,
              modifier = Modifier.size(16.dp)
            )
          }
        }
      }

      Spacer(modifier = Modifier.height(6.dp))
      Text(
        text = profile.description,
        color = TextSecondary,
        fontSize = 11.sp,
        maxLines = 2
      )

      Spacer(modifier = Modifier.height(8.dp))

      // Tags Row
      Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
        HighDensityTag(profile.hdrFormat, HighDensityPrimary)
        HighDensityTag(profile.colorGamut, HighDensityGreen)
        HighDensityTag("${profile.colorTemperatureK}K", TextSecondary)
        if (profile.blackScreenPhone) {
          HighDensityTag("AMOLED Blackout", HighDensityPrimary)
        }
      }
    }
  }
}

@Composable
private fun HighDensityTag(text: String, color: Color) {
  Box(
    modifier = Modifier
      .background(DisplayBorder.copy(alpha = 0.35f), RoundedCornerShape(4.dp))
      .padding(horizontal = 6.dp, vertical = 2.dp)
  ) {
    Text(
      text = text,
      color = color,
      fontSize = 9.sp,
      fontFamily = FontFamily.Monospace,
      fontWeight = FontWeight.SemiBold
    )
  }
}

private fun getProfileIcon(name: String): ImageVector {
  return when (name) {
    "movie" -> Icons.Default.Movie
    "sports_esports" -> Icons.Default.SportsEsports
    "palette" -> Icons.Default.Palette
    "co_present" -> Icons.Default.CoPresent
    "hdr_on" -> Icons.Default.HdrOn
    "eco" -> Icons.Default.Eco
    else -> Icons.Default.Tune
  }
}

