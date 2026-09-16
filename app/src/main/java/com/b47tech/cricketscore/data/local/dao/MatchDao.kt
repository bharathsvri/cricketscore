package com.b47tech.cricketscore.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface MatchDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdateMatch(match: MatchEntity)

    @Query("SELECT * FROM matches ORDER BY updatedAt DESC")
    fun getAllMatchesFlow(): Flow<List<MatchEntity>>

    @Query("SELECT * FROM matches WHERE status != 'COMPLETED' AND status != 'ABANDONED' ORDER BY updatedAt DESC LIMIT 1")
    fun getActiveMatchFlow(): Flow<MatchEntity?>

    @Query("SELECT * FROM matches WHERE id = :matchId")
    suspend fun getMatchById(matchId: String): MatchEntity?

    @Query("SELECT * FROM matches WHERE id = :matchId")
    fun getMatchFlowById(matchId: String): Flow<MatchEntity?>

    @Query("DELETE FROM matches WHERE id = :matchId")
    suspend fun deleteMatchById(matchId: String)

    @Query("DELETE FROM matches")
    suspend fun deleteAllMatches()
}
