package com.example.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [ProfileEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
  abstract fun profileDao(): ProfileDao

  companion object {
    @Volatile
    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context, scope: CoroutineScope): AppDatabase {
      return INSTANCE ?: synchronized(this) {
        val instance = Room.databaseBuilder(
          context.applicationContext,
          AppDatabase::class.java,
          "display_master_db"
        )
          .addCallback(DatabaseCallback(scope))
          .fallbackToDestructiveMigration()
          .build()
        INSTANCE = instance
        instance
      }
    }

    private class DatabaseCallback(
      private val scope: CoroutineScope
    ) : RoomDatabase.Callback() {
      override fun onCreate(db: SupportSQLiteDatabase) {
        super.onCreate(db)
        INSTANCE?.let { database ->
          scope.launch(Dispatchers.IO) {
            populateInitialProfiles(database.profileDao())
          }
        }
      }
    }

    suspend fun populateInitialProfiles(dao: ProfileDao) {
      if (dao.getProfileCount() > 0) return

      val presets = listOf(
        ProfileEntity(
          name = "Cinema 4K HDR",
          description = "Mastered for 4K film viewing with HDR10 dynamic range and D65 white point.",
          resolutionWidth = 3840,
          resolutionHeight = 2160,
          refreshRate = 24f,
          isHdrEnabled = true,
          hdrFormat = "HDR10",
          colorGamut = "DCI-P3",
          colorTemperatureK = 6500,
          brightnessNits = 850,
          contrastRatio = 1.2f,
          keepAwake = true,
          blackScreenPhone = true,
          iconName = "movie",
          isPreset = true
        ),
        ProfileEntity(
          name = "High-FPS Gaming",
          description = "Ultra smooth 120Hz/144Hz high refresh rate with low-latency SDR response.",
          resolutionWidth = 2560,
          resolutionHeight = 1440,
          refreshRate = 120f,
          isHdrEnabled = false,
          hdrFormat = "SDR",
          colorGamut = "DCI-P3",
          colorTemperatureK = 6500,
          brightnessNits = 450,
          contrastRatio = 1.0f,
          keepAwake = true,
          blackScreenPhone = false,
          iconName = "sports_esports",
          isPreset = true
        ),
        ProfileEntity(
          name = "Color Accurate Studio",
          description = "100% sRGB Rec.709 color reference profile for photo, video, and design work.",
          resolutionWidth = 1920,
          resolutionHeight = 1080,
          refreshRate = 60f,
          isHdrEnabled = false,
          hdrFormat = "SDR",
          colorGamut = "sRGB",
          colorTemperatureK = 6500,
          brightnessNits = 300,
          contrastRatio = 1.0f,
          keepAwake = true,
          blackScreenPhone = false,
          iconName = "palette",
          isPreset = true
        ),
        ProfileEntity(
          name = "Keynote Presentation",
          description = "High contrast crisp text mode with permanent display keep-awake & phone blackout.",
          resolutionWidth = 1920,
          resolutionHeight = 1080,
          refreshRate = 60f,
          isHdrEnabled = true,
          hdrFormat = "HLG",
          colorGamut = "sRGB",
          colorTemperatureK = 7500,
          brightnessNits = 600,
          contrastRatio = 1.3f,
          keepAwake = true,
          blackScreenPhone = true,
          iconName = "co_present",
          isPreset = true
        ),
        ProfileEntity(
          name = "Dolby Vision Master",
          description = "12-bit wide color gamut Rec.2020 with peak HDR luminescence.",
          resolutionWidth = 3840,
          resolutionHeight = 2160,
          refreshRate = 60f,
          isHdrEnabled = true,
          hdrFormat = "Dolby Vision",
          colorGamut = "Rec.2020",
          colorTemperatureK = 6500,
          brightnessNits = 1000,
          contrastRatio = 1.5f,
          keepAwake = true,
          blackScreenPhone = true,
          iconName = "hdr_on",
          isPreset = true
        ),
        ProfileEntity(
          name = "Eco Energy Saver",
          description = "Reduced 30Hz refresh rate and standard HD resolution for low power sessions.",
          resolutionWidth = 1280,
          resolutionHeight = 720,
          refreshRate = 30f,
          isHdrEnabled = false,
          hdrFormat = "SDR",
          colorGamut = "sRGB",
          colorTemperatureK = 5000,
          brightnessNits = 200,
          contrastRatio = 0.9f,
          keepAwake = true,
          blackScreenPhone = true,
          iconName = "eco",
          isPreset = true
        )
      )
      dao.insertAll(presets)
    }
  }
}
