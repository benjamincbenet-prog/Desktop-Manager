package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mouse
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ProfileEntity
import com.example.ui.theme.DisplayBorder
import com.example.ui.theme.HighDensityGreen
import com.example.ui.theme.HighDensityPrimary
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.TextTertiary
import kotlinx.coroutines.delay

@Composable
fun BlackScreenPhoneOverlay(
  activeProfile: ProfileEntity?,
  allProfiles: List<ProfileEntity>,
  isAwakeLocked: Boolean,
  onExitBlackScreen: () -> Unit,
  onSelectProfile: (ProfileEntity) -> Unit,
  onPointerMove: (Offset?) -> Unit,
  modifier: Modifier = Modifier
) {
  var showHintBanner by remember { mutableStateOf(true) }
  var showStealthControls by remember { mutableStateOf(false) }
  var pointerOffset by remember { mutableStateOf<Offset?>(null) }

  // Auto-hide hint banner after 4 seconds to achieve 100% black AMOLED screen
  LaunchedEffect(Unit) {
    delay(4000)
    showHintBanner = false
  }

  Box(
    modifier = modifier
      .fillMaxSize()
      .background(Color.Black) // True OLED #000000
      .pointerInput(Unit) {
        detectTapGestures(
          onDoubleTap = {
            onExitBlackScreen()
          },
          onTap = {
            // Single tap toggles stealth HUD for quick adjustments
            showStealthControls = !showStealthControls
          }
        )
      }
      .pointerInput(Unit) {
        detectDragGestures(
          onDragStart = { offset ->
            pointerOffset = offset
            onPointerMove(offset)
          },
          onDrag = { change, dragAmount ->
            change.consume()
            val current = pointerOffset ?: Offset.Zero
            val newOffset = Offset(
              x = (current.x + dragAmount.x).coerceIn(0f, 1000f),
              y = (current.y + dragAmount.y).coerceIn(0f, 800f)
            )
            pointerOffset = newOffset
            onPointerMove(newOffset)
          },
          onDragEnd = {
            onPointerMove(null)
            pointerOffset = null
          },
          onDragCancel = {
            onPointerMove(null)
            pointerOffset = null
          }
        )
      }
      .testTag("black_screen_overlay")
  ) {
    // Initial Fade-out Hint Banner
    AnimatedVisibility(
      visible = showHintBanner,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier
        .align(Alignment.Center)
        .padding(24.dp)
    ) {
      Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
          .background(Color(0x221C1B1F), RoundedCornerShape(18.dp))
          .border(1.dp, HighDensityPrimary.copy(alpha = 0.4f), RoundedCornerShape(18.dp))
          .padding(20.dp)
      ) {
        Icon(
          imageVector = Icons.Default.Visibility,
          contentDescription = "OLED Black Screen",
          tint = HighDensityPrimary.copy(alpha = 0.9f),
          modifier = Modifier.size(32.dp)
        )
        Spacer(modifier = Modifier.height(10.dp))
        Text(
          text = "PHONE BLACK SCREEN MODE ACTIVE",
          color = Color(0xEEF8FAFC),
          fontWeight = FontWeight.Bold,
          fontSize = 13.sp,
          letterSpacing = 1.sp
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
          text = "Double-tap anywhere to wake phone",
          color = HighDensityPrimary.copy(alpha = 0.95f),
          fontSize = 12.sp,
          fontWeight = FontWeight.SemiBold
        )
        Text(
          text = "Single tap for stealth controls • Drag for remote pointer",
          color = Color(0x9994A3B8),
          fontSize = 11.sp
        )
      }
    }

    // Stealth HUD for adjusting profiles without exiting black mode
    AnimatedVisibility(
      visible = showStealthControls,
      enter = fadeIn(),
      exit = fadeOut(),
      modifier = Modifier.fillMaxSize()
    ) {
      Box(
        modifier = Modifier
          .fillMaxSize()
          .background(Color(0xEE1C1B1F))
          .padding(20.dp)
      ) {
        // Top Row: Status and Exit
        Row(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.TopCenter),
          horizontalArrangement = Arrangement.SpaceBetween,
          verticalAlignment = Alignment.CenterVertically
        ) {
          Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
              modifier = Modifier
                .size(8.dp)
                .background(HighDensityGreen, CircleShape)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
              text = "EXTERNAL DISPLAY ACTIVE: ${activeProfile?.name ?: "4K Cinema"}",
              color = Color(0xCCF8FAFC),
              fontSize = 12.sp,
              fontWeight = FontWeight.Bold
            )
          }

          IconButton(
            onClick = onExitBlackScreen,
            modifier = Modifier
              .background(Color(0x33FFFFFF), CircleShape)
              .size(36.dp)
              .testTag("exit_black_screen_btn")
          ) {
            Icon(
              imageVector = Icons.Default.Close,
              contentDescription = "Wake Phone",
              tint = Color.White,
              modifier = Modifier.size(18.dp)
            )
          }
        }

        // Center Area: Remote Touchpad Surface
        Column(
          modifier = Modifier
            .align(Alignment.Center)
            .fillMaxWidth(0.9f)
            .height(280.dp)
            .background(Color(0x331C1B1F), RoundedCornerShape(20.dp))
            .border(1.dp, HighDensityPrimary.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(16.dp),
          horizontalAlignment = Alignment.CenterHorizontally,
          verticalArrangement = Arrangement.SpaceBetween
        ) {
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
          ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
              Icon(
                imageVector = Icons.Default.Mouse,
                contentDescription = null,
                tint = HighDensityPrimary.copy(alpha = 0.8f),
                modifier = Modifier.size(16.dp)
              )
              Spacer(modifier = Modifier.width(6.dp))
              Text(
                text = "REMOTE TOUCH TRACKPAD",
                color = Color(0xAAFFFFFF),
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
              )
            }
            Text(
              text = "Drag to move cursor",
              color = Color(0x77FFFFFF),
              fontSize = 10.sp
            )
          }

          Text(
            text = "Move finger on this surface to control presentation slides or highlight regions on the external monitor",
            color = Color(0x8894A3B8),
            fontSize = 12.sp,
            modifier = Modifier.padding(horizontal = 12.dp)
          )

          // Remote laser pointer coordinates
          Text(
            text = if (pointerOffset != null) {
              "Pointer: X=${pointerOffset!!.x.toInt()}, Y=${pointerOffset!!.y.toInt()}"
            } else "Pointer Idle (Touch to guide)",
            color = HighDensityPrimary.copy(alpha = 0.9f),
            fontSize = 11.sp,
            fontFamily = FontFamily.Monospace
          )
        }

        // Bottom Row: Stealth Quick Profile Switcher
        Column(
          modifier = Modifier
            .fillMaxWidth()
            .align(Alignment.BottomCenter)
            .background(Color(0x442B2930), RoundedCornerShape(16.dp))
            .border(1.dp, DisplayBorder, RoundedCornerShape(16.dp))
            .padding(12.dp)
        ) {
          Text(
            text = "SWITCH DISPLAY PROFILE",
            color = Color(0x88FFFFFF),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 1.sp
          )
          Spacer(modifier = Modifier.height(8.dp))
          Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
          ) {
            allProfiles.take(4).forEach { profile ->
              val isSelected = profile.id == activeProfile?.id
              Box(
                modifier = Modifier
                  .weight(1f)
                  .background(
                    if (isSelected) HighDensityPrimary.copy(alpha = 0.25f) else Color(0x22FFFFFF),
                    RoundedCornerShape(8.dp)
                  )
                  .border(
                    1.dp,
                    if (isSelected) HighDensityPrimary else Color.Transparent,
                    RoundedCornerShape(8.dp)
                  )
                  .pointerInput(profile.id) {
                    detectTapGestures {
                      onSelectProfile(profile)
                    }
                  }
                  .padding(vertical = 8.dp, horizontal = 4.dp),
                contentAlignment = Alignment.Center
              ) {
                Text(
                  text = profile.name.take(10),
                  color = if (isSelected) HighDensityPrimary else Color(0xAAFFFFFF),
                  fontSize = 11.sp,
                  fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                  maxLines = 1
                )
              }
            }
          }
        }
      }
    }
  }
}

