package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "display_profiles")
data class ProfileEntity(
  @PrimaryKey(autoGenerate = true)
  val id: Long = 0,
  val name: String,
  val description: String,
  val resolutionWidth: Int,
  val resolutionHeight: Int,
  val refreshRate: Float,
  val isHdrEnabled: Boolean,
  val hdrFormat: String, // "HDR10", "HLG", "Dolby Vision", "SDR"
  val colorGamut: String, // "sRGB", "DCI-P3", "Rec.2020"
  val colorTemperatureK: Int, // 5000, 6500, 7500, 9300
  val brightnessNits: Int,
  val contrastRatio: Float,
  val keepAwake: Boolean,
  val blackScreenPhone: Boolean,
  val iconName: String,
  val isPreset: Boolean = false
)
