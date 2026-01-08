package br.com.webstorage.falaserio.domain.model

/**
 * Métricas VSA (Voice Stress Analysis) calculadas durante a análise.
 *
 * As 5 métricas principais:
 * 1. Micro-Tremor: Oscilações de 8-12Hz nos músculos vocais
 * 2. Pitch Variation: Variação da frequência fundamental (F0)
 * 3. Jitter: Irregularidade ciclo-a-ciclo do período vocal
 * 4. Shimmer: Variação amplitude ciclo-a-ciclo
 * 5. HNR: Harmonic-to-Noise Ratio (clareza vocal)
 */
data class VsaMetrics(
    val microTremor: Float = 0f,      // Hz (8-12 normal, >11 stress)
    val pitchVariation: Float = 0f,   // % (10-15 normal, >20 stress)
    val jitter: Float = 0f,           // % (<1 normal, >2 stress)
    val shimmer: Float = 0f,          // % (<3 normal, >6 stress)
    val hnr: Float = 0f,              // dB (>20 normal, <15 stress)
    val overallStressScore: Float = 0f // 0-100%
) {
    companion object {
        fun empty() = VsaMetrics()

        // Thresholds para indicar stress
        const val MICRO_TREMOR_THRESHOLD = 11f
        const val PITCH_VARIATION_THRESHOLD = 20f
        const val JITTER_THRESHOLD = 2f
        const val SHIMMER_THRESHOLD = 6f
        const val HNR_THRESHOLD = 15f
    }

    /**
     * Calcula se cada métrica indica stress
     */
    val microTremorIndicatesStress: Boolean get() = microTremor > MICRO_TREMOR_THRESHOLD
    val pitchIndicatesStress: Boolean get() = pitchVariation > PITCH_VARIATION_THRESHOLD
    val jitterIndicatesStress: Boolean get() = jitter > JITTER_THRESHOLD
    val shimmerIndicatesStress: Boolean get() = shimmer > SHIMMER_THRESHOLD
    val hnrIndicatesStress: Boolean get() = hnr < HNR_THRESHOLD

    /**
     * Retorna descrição textual do nível de stress
     */
    fun getStressLevel(): String = when {
        overallStressScore >= 80 -> "Muito Alto"
        overallStressScore >= 60 -> "Alto"
        overallStressScore >= 40 -> "Moderado"
        overallStressScore >= 20 -> "Baixo"
        else -> "Muito Baixo"
    }
}
