package br.com.webstorage.falaserio.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Entidade Room para histórico de gravações e análises.
 */
@Entity(tableName = "history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,

    // Dados da gravação
    val filePath: String,
    val fileName: String,
    val durationMs: Long,
    val createdAt: Long = System.currentTimeMillis(),

    // Métricas VSA
    val microTremor: Float,
    val pitchVariation: Float,
    val jitter: Float,
    val shimmer: Float,
    val hnr: Float,
    val overallStressScore: Float,

    // Resultado textual
    val stressLevel: String,

    // Notas do usuário (opcional)
    val notes: String? = null
)
