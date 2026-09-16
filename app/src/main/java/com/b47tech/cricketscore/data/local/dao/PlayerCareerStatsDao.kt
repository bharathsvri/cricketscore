package com.b47tech.cricketscore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.b47tech.cricketscore.data.local.entity.PlayerCareerStatsEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface PlayerCareerStatsDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stats: PlayerCareerStatsEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(stats: List<PlayerCareerStatsEntity>)

    @Query("SELECT * FROM player_career_stats ORDER BY totalRuns DESC")
    fun getAllPlayerStatsByRunsFlow(): Flow<List<PlayerCareerStatsEntity>>

    @Query("SELECT * FROM player_career_stats ORDER BY wicketsTaken DESC")
    fun getAllPlayerStatsByWicketsFlow(): Flow<List<PlayerCareerStatsEntity>>

    @Query("SELECT * FROM player_career_stats WHERE playerName = :name LIMIT 1")
    suspend fun getPlayerByName(name: String): PlayerCareerStatsEntity?
}
