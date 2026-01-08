package br.com.webstorage.falaserio.domain.usecase

import br.com.webstorage.falaserio.domain.audio.VsaAnalyzer
import br.com.webstorage.falaserio.domain.model.VsaMetrics
import java.io.File
import javax.inject.Inject

/**
 * Use Case para analisar áudio gravado.
 *
 * Executa análise VSA e retorna métricas.
 * IMPORTANTE: Deve ser chamado em Dispatchers.Default (CPU intensive)
 */
class AnalyzeAudioUseCase @Inject constructor(
    private val vsaAnalyzer: VsaAnalyzer
) {

    /**
     * Analisa um arquivo de áudio e retorna métricas VSA.
     * @param file Arquivo WAV de áudio
     * @return VsaMetrics com as 5 métricas calculadas
     */
    suspend operator fun invoke(file: File): VsaMetrics {
        return vsaAnalyzer.analyze(file)
    }
}
