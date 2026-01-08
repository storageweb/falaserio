package br.com.webstorage.falaserio.di

import br.com.webstorage.falaserio.domain.audio.VsaAnalyzer
import br.com.webstorage.falaserio.domain.usecase.AnalyzeAudioUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para injeção do analisador VSA.
 */
@Module
@InstallIn(SingletonComponent::class)
object VsaModule {

    @Provides
    @Singleton
    fun provideVsaAnalyzer(): VsaAnalyzer {
        return VsaAnalyzer()
    }

    @Provides
    @Singleton
    fun provideAnalyzeAudioUseCase(
        vsaAnalyzer: VsaAnalyzer
    ): AnalyzeAudioUseCase {
        return AnalyzeAudioUseCase(vsaAnalyzer)
    }
}
