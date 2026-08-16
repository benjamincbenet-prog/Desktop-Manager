package com.example.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProfileDao {
  @Query("SELECT * FROM display_profiles ORDER BY isPreset DESC, id ASC")
  fun getAllProfiles(): Flow<List<ProfileEntity>>

  @Query("SELECT * FROM display_profiles WHERE id = :id")
  suspend fun getProfileById(id: Long): ProfileEntity?

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertProfile(profile: ProfileEntity): Long

  @Insert(onConflict = OnConflictStrategy.REPLACE)
  suspend fun insertAll(profiles: List<ProfileEntity>)

  @Update
  suspend fun updateProfile(profile: ProfileEntity)

  @Delete
  suspend fun deleteProfile(profile: ProfileEntity)

  @Query("SELECT COUNT(*) FROM display_profiles")
  suspend fun getProfileCount(): Int
}
