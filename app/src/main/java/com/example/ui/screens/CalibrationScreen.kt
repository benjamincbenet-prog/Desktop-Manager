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
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CastConnected
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GridOn
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.ViewCompact
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.CalibrationPatternType
import com.example.model.DisplayTelemetry
import com.example.ui.presentation.CalibrationPatternCanvas
import com.example.ui.theme.DisplayBlack
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.DisplayCardElevated
import com.example.ui.theme.DisplayDarkSurface
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityOnPrimary
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.HighDensityPrimaryContainer
import com.example.ui.theme.HighDensitySecondaryContainer
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary

@Composable
fun CalibrationScreen(
  telemetry: DisplayTelemetry,
  activePattern: CalibrationPatternType?,
  onSelectPattern: (CalibrationPatternType?) -> Unit,
  modifier: Modifier = Modifier
) {
  var selectedLocalPattern by remember {
    mutableStateOf(activePattern ?: CalibrationPatternType.COLOR_BARS)
  }

  LazyColumn(
    modifier = modifier.fillMaxSize().background(DisplayBlack).testTag("calibration_screen"),
    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
    verticalArrangement = Arrangement.spacedBy(16.dp)
  ) {
    // Large Live Test Pattern Canvas Preview
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
            Column {
              Text(
                text = selectedLocalPattern.title,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
              )
              Text(
                text = selectedLocalPattern.description,
                color = TextSecondary,
                fontSize = 11.sp
              )
            }

            if (activePattern == selectedLocalPattern) {
              Box(
                modifier = Modifier
                  .background(HighDensityGreen.copy(alpha = 0.2f), RoundedCornerShape(6.dp))
                  .border(1.dp, HighDensityGreen, RoundedCornerShape(6.dp))
                  .padding(horizontal = 8.dp, vertical = 4.dp)
              ) {
                Text(
                  text = "PROJECTING ON MONITOR",
                  color = HighDensityGreen,
                  fontSize = 9.sp,
                  fontWeight = FontWeight.Bold
                )
              }
            }
          }

          Spacer(modifier = Modifier.height(12.dp))

          // 16:9 Screen Bezel Preview Box
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .aspectRatio(16f / 9f)
              .clip(RoundedCornerShape(10.dp))
              .background(Color.Black)
              .border(1.5.dp, HighDensityPrimary.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
          ) {
            CalibrationPatternCanvas(
              patternType = selectedLocalPattern,
              telemetry = telemetry,
              modifier = Modifier.fillMaxSize()
            )
          }

          Spacer(modifier = Modifier.height(14.dp))

          // Actions
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            if (activePattern == selectedLocalPattern) {
              Button(
                onClick = { onSelectPattern(null) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                  containerColor = HighDensitySecondaryContainer,
                  contentColor = TextPrimary
                ),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(imageVector = Icons.Default.Close, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Stop Projection", fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
              }
            } else {
              Button(
                onClick = { onSelectPattern(selectedLocalPattern) },
                modifier = Modifier.weight(1f),
                colors = ButtonDefaults.buttonColors(
                  containerColor = HighDensityPrimary,
                  contentColor = HighDensityOnPrimary
                ),
                shape = RoundedCornerShape(12.dp)
              ) {
                Icon(imageVector = Icons.Default.CastConnected, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Project to External Display", fontWeight = FontWeight.Bold, fontSize = 12.sp)
              }
            }
          }
        }
      }
    }

    // Pattern Selector List Header
    item {
      Text(
        text = "REFERENCE CALIBRATION TEST PATTERNS",
        color = TextSecondary,
        fontSize = 11.sp,
        fontWeight = FontWeight.Bold,
        letterSpacing = 1.sp
      )
    }

    // Pattern Cards
    items(CalibrationPatternType.values()) { pattern ->
      val isSelected = selectedLocalPattern == pattern
      val isCurrentlyProjecting = activePattern == pattern

      PatternItemCard(
        pattern = pattern,
        isSelected = isSelected,
        isProjecting = isCurrentlyProjecting,
        onSelect = {
          selectedLocalPattern = pattern
        },
        onToggleProject = {
          if (isCurrentlyProjecting) onSelectPattern(null) else onSelectPattern(pattern)
        }
      )
    }
  }
}

@Composable
private fun PatternItemCard(
  pattern: CalibrationPatternType,
  isSelected: Boolean,
  isProjecting: Boolean,
  onSelect: () -> Unit,
  onToggleProject: () -> Unit
) {
  Card(
    modifier = Modifier
      .fillMaxWidth()
      .clip(RoundedCornerShape(14.dp))
      .clickable { onSelect() }
      .border(
        width = if (isSelected) 1.5.dp else 1.dp,
        color = if (isSelected) HighDensityPrimary else DisplayBorder,
        shape = RoundedCornerShape(14.dp)
      ),
    colors = CardDefaults.cardColors(
      containerColor = if (isSelected) DisplayCardElevated else DisplayDarkSurface
    ),
    shape = RoundedCornerShape(14.dp)
  ) {
    Row(
      modifier = Modifier
        .fillMaxWidth()
        .padding(14.dp),
      horizontalArrangement = Arrangement.SpaceBetween,
      verticalAlignment = Alignment.CenterVertically
    ) {
      Row(
        modifier = Modifier.weight(1f),
        verticalAlignment = Alignment.CenterVertically
      ) {
        Box(
          modifier = Modifier
            .size(38.dp)
            .background(
              if (isSelected) HighDensityPrimary.copy(alpha = 0.2f) else DisplayBlack,
              RoundedCornerShape(8.dp)
            ),
          contentAlignment = Alignment.Center
        ) {
          Icon(
            imageVector = getPatternIcon(pattern),
            contentDescription = null,
            tint = if (isSelected) HighDensityPrimary else TextSecondary,
            modifier = Modifier.size(20.dp)
          )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
          Text(
            text = pattern.title,
            color = TextPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
          )
          Text(
            text = pattern.description,
            color = TextSecondary,
            fontSize = 11.sp,
            maxLines = 1
          )
        }
      }

      OutlinedButton(
        onClick = onToggleProject,
        shape = RoundedCornerShape(8.dp),
        colors = ButtonDefaults.outlinedButtonColors(
          contentColor = if (isProjecting) HighDensityGreen else HighDensityPrimary
        ),
        border = androidx.compose.foundation.BorderStroke(
          1.dp,
          if (isProjecting) HighDensityGreen else HighDensityPrimary.copy(alpha = 0.5f)
        ),
        modifier = Modifier.padding(start = 8.dp)
      ) {
        Text(
          text = if (isProjecting) "Projecting" else "Cast",
          fontSize = 11.sp,
          fontWeight = FontWeight.Bold
        )
      }
    }
  }
}

private fun getPatternIcon(pattern: CalibrationPatternType): ImageVector {
  return when (pattern) {
    CalibrationPatternType.COLOR_BARS -> Icons.Default.Movie
    CalibrationPatternType.GRAYSCALE_16 -> Icons.Default.ViewCompact
    CalibrationPatternType.UNIFORMITY_RGB -> Icons.Default.Tv
    CalibrationPatternType.CONVERGENCE_GRID -> Icons.Default.GridOn
    CalibrationPatternType.MOTION_FPS_UFO -> Icons.Default.Speed
    CalibrationPatternType.CONTRAST_CHECKER -> Icons.Default.GridOn
  }
}

