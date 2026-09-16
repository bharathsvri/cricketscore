package com.b47tech.cricketscore

import android.app.Application
import com.b47tech.cricketscore.core.ads.AdManager
import com.b47tech.cricketscore.data.local.CricketDatabase
import com.b47tech.cricketscore.data.local.SettingsRepository
import com.b47tech.cricketscore.data.repository.CricketRepository
import com.b47tech.cricketscore.data.repository.CricketRepositoryImpl

class CricketApplication : Application() {
    lateinit var database: CricketDatabase
        private set
    lateinit var repository: CricketRepository
        private set
    lateinit var settingsRepository: SettingsRepository
        private set

    override fun onCreate() {
        super.onCreate()
        database = CricketDatabase.getDatabase(this)
        repository = CricketRepositoryImpl(database.matchDao(), database.playerCareerStatsDao())
        settingsRepository = SettingsRepository(this)

        // Initialize centralized AdManager safely
        AdManager.getInstance().initialize(this)
    }
}
