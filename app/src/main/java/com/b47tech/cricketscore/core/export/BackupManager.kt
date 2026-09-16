package com.b47tech.cricketscore.core.export

import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.core.content.FileProvider
import com.b47tech.cricketscore.data.local.entity.MatchEntity
import com.b47tech.cricketscore.data.local.entity.PlayerCareerStatsEntity
import com.b47tech.cricketscore.data.repository.CricketRepository
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.io.File
import java.io.FileOutputStream

@Serializable
data class CricketBackupData(
    val app: String = "B47 Cricket Score",
    val schemaVersion: Int = 1,
    val exportTimestamp: Long = System.currentTimeMillis(),
    val matches: List<MatchEntity>,
    val playerStats: List<PlayerCareerStatsEntity>
)

object BackupManager {

    private val json = Json {
        ignoreUnknownKeys = true
        encodeDefaults = true
        prettyPrint = true
    }

    suspend fun createBackupFile(context: Context, repository: CricketRepository): File? {
        return try {
            val matches = repository.getAllMatchesList()
            val stats = repository.getAllPlayerStatsList()

            val backupData = CricketBackupData(
                matches = matches,
                playerStats = stats
            )

            val jsonContent = json.encodeToString(backupData)
            val backupDir = File(context.cacheDir, "backups").apply { mkdirs() }
            val backupFile = File(backupDir, "b47_cricket_backup_${System.currentTimeMillis()}.json")

            FileOutputStream(backupFile).use { out ->
                out.write(jsonContent.toByteArray(Charsets.UTF_8))
            }

            backupFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    fun shareBackupFile(context: Context, backupFile: File) {
        val uri = FileProvider.getUriForFile(
            context,
            "${context.packageName}.fileprovider",
            backupFile
        )

        val shareIntent = Intent(Intent.ACTION_SEND).apply {
            type = "application/json"
            putExtra(Intent.EXTRA_STREAM, uri)
            putExtra(Intent.EXTRA_SUBJECT, "B47 Cricket Score - Full Match & Stats Backup")
            putExtra(Intent.EXTRA_TEXT, "Full match and player database backup created with B47 Cricket Score.")
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val chooser = Intent.createChooser(shareIntent, "Share Backup File")
        chooser.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(chooser)
    }

    fun readBackupFromUri(context: Context, uri: Uri): String? {
        return try {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                stream.bufferedReader(Charsets.UTF_8).use { it.readText() }
            }
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    suspend fun restoreBackup(
        backupJson: String,
        repository: CricketRepository
    ): Result<Int> {
        return try {
            val backupData = json.decodeFromString<CricketBackupData>(backupJson)
            repository.restoreBackup(backupData.matches, backupData.playerStats)
            Result.success(backupData.matches.size)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)
        }
    }
}
