package br.com.webstorage.falaserio.di

import android.content.Context
import androidx.room.Room
import br.com.webstorage.falaserio.data.local.AppDatabase
import br.com.webstorage.falaserio.data.local.dao.CreditsDao
import br.com.webstorage.falaserio.data.local.dao.HistoryDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção do banco de dados Room.
 */
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(
        @ApplicationContext context: Context
    ): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration() // Para desenvolvimento
            .build()
    }

    @Provides
    @Singleton
    fun provideHistoryDao(database: AppDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    @Singleton
    fun provideCreditsDao(database: AppDatabase): CreditsDao {
        return database.creditsDao()
    }
}
