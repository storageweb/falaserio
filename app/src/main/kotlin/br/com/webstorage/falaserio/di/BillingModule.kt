package br.com.webstorage.falaserio.di

import android.content.Context
import br.com.webstorage.falaserio.data.repository.CreditsRepository
import br.com.webstorage.falaserio.domain.billing.BillingManager
import br.com.webstorage.falaserio.domain.billing.MonetizationManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção de dependências de billing e monetização.
 */
@Module
@InstallIn(SingletonComponent::class)
object BillingModule {

    @Provides
    @Singleton
    fun provideBillingManager(
        @ApplicationContext context: Context
    ): BillingManager {
        return BillingManager(context)
    }

    @Provides
    @Singleton
    fun provideMonetizationManager(
        creditsRepository: CreditsRepository
    ): MonetizationManager {
        return MonetizationManager(creditsRepository)
    }
}
