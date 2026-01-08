package br.com.webstorage.falaserio.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import br.com.webstorage.falaserio.data.local.dao.CreditsDao
import br.com.webstorage.falaserio.data.local.dao.HistoryDao
import br.com.webstorage.falaserio.data.local.entity.CreditsEntity
import br.com.webstorage.falaserio.data.local.entity.HistoryEntity

/**
 * Banco de dados Room do FalaSério.
 *
 * Entidades:
 * - HistoryEntity: Histórico de gravações e análises
 * - CreditsEntity: Créditos do usuário
 */
@Database(
    entities = [
        HistoryEntity::class,
        CreditsEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun historyDao(): HistoryDao
    abstract fun creditsDao(): CreditsDao

    companion object {
        const val DATABASE_NAME = "falaserio_db"
    }
}
