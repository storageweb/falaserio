package br.com.webstorage.falaserio.data.repository

import android.media.MediaMetadataRetriever
import br.com.webstorage.falaserio.data.local.dao.HistoryDao
import br.com.webstorage.falaserio.data.local.entity.HistoryEntity
import br.com.webstorage.falaserio.domain.model.VsaMetrics
import kotlinx.coroutines.flow.Flow
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Repository para gerenciar histórico de gravações.
 */
@Singleton
class HistoryRepository @Inject constructor(
    private val historyDao: HistoryDao
) {

    fun getAllHistory(): Flow<List<HistoryEntity>> = historyDao.getAllHistory()

    fun getRecentHistory(limit: Int = 10): Flow<List<HistoryEntity>> =
        historyDao.getRecentHistory(limit)

    suspend fun getById(id: Long): HistoryEntity? = historyDao.getById(id)

    suspend fun saveAnalysis(file: File, metrics: VsaMetrics): Long {
        val duration = try {
            getDuration(file)
        } catch (e: Exception) {
            0L
        }

        val entity = HistoryEntity(
            filePath = file.absolutePath,
            fileName = file.name,
            durationMs = duration,
            microTremor = metrics.microTremor,
            pitchVariation = metrics.pitchVariation,
            jitter = metrics.jitter,
            shimmer = metrics.shimmer,
            hnr = metrics.hnr,
            overallStressScore = metrics.overallStressScore,
            stressLevel = metrics.getStressLevel()
        )
        return historyDao.insert(entity)
    }

    private fun getDuration(file: File): Long {
        return try {
            MediaMetadataRetriever().use { retriever ->
                retriever.setDataSource(file.absolutePath)
                val time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION)
                time?.toLongOrNull() ?: 0L
            }
        } catch (e: Exception) {
            0L
        }
    }

    suspend fun delete(history: HistoryEntity) {
        historyDao.delete(history)
        // Também deletar o arquivo de áudio
        try {
            File(history.filePath).delete()
        } catch (e: Exception) {
            // Ignorar se não conseguir deletar
        }
    }

    suspend fun deleteById(id: Long) {
        val history = historyDao.getById(id)
        history?.let { delete(it) }
    }

    suspend fun deleteAll() {
        historyDao.deleteAll()
    }

    suspend fun getCount(): Int = historyDao.getCount()
}
