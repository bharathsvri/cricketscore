package com.b47tech.cricketscore.data.repository

import com.b47tech.cricketscore.core.engine.CricketEngine
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import com.b47tech.cricketscore.data.local.entity.PlayerCareerStatsEntity
import kotlinx.coroutines.flow.Flow

interface CricketRepository {
    fun getAllMatchesFlow(): Flow<List<MatchEntity>>
    fun getActiveMatchFlow(): Flow<MatchEntity?>
    suspend fun getMatchById(id: String): MatchEntity?
    fun getMatchFlowById(id: String): Flow<MatchEntity?>
    suspend fun saveMatchFromEngine(engine: CricketEngine)
    suspend fun loadEngineFromMatchEntity(entity: MatchEntity): CricketEngine
    suspend fun deleteMatch(id: String)
    fun getAllPlayerCareerStatsFlow(): Flow<List<PlayerCareerStatsEntity>>
}
