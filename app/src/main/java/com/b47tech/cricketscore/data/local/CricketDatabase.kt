package com.b47tech.cricketscore.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.b47tech.cricketscore.data.local.dao.MatchDao
import com.b47tech.cricketscore.data.local.dao.PlayerCareerStatsDao
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import com.b47tech.cricketscore.data.local.entity.PlayerCareerStatsEntity

@Database(
    entities = [
        MatchEntity::class,
        PlayerCareerStatsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class CricketDatabase : RoomDatabase() {
    abstract fun matchDao(): MatchDao
    abstract fun playerCareerStatsDao(): PlayerCareerStatsDao

    companion object {
        @Volatile
        private var INSTANCE: CricketDatabase? = null

        fun getDatabase(context: Context): CricketDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    CricketDatabase::class.java,
                    "b47_cricket_score.db"
                ).fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
