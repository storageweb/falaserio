package br.com.webstorage.falaserio.domain.audio

import br.com.webstorage.falaserio.domain.model.VsaMetrics
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.math.PI
import kotlin.math.abs
import kotlin.math.cos
import kotlin.math.log10
import kotlin.math.sin
import kotlin.math.sqrt

/**
 * Analisador VSA (Voice Stress Analysis).
 *
 * Calcula 5 métricas de stress vocal:
 * 1. Micro-Tremor (8-12Hz)
 * 2. Pitch Variation (F0)
 * 3. Jitter
 * 4. Shimmer
 * 5. HNR (Harmonic-to-Noise Ratio)
 */
@Singleton
class VsaAnalyzer @Inject constructor() {

    companion object {
        const val SAMPLE_RATE = 44100
        const val FRAME_SIZE = 4096
        const val HOP_SIZE = 2048
        const val MIN_FREQ = 80f  // Hz - mínimo para voz humana
        const val MAX_FREQ = 400f // Hz - máximo para voz humana
    }

    /**
     * Analisa um arquivo WAV e retorna métricas VSA.
     */
    suspend fun analyze(file: File): VsaMetrics = withContext(Dispatchers.Default) {
        val samples = readWavFile(file)
        if (samples.isEmpty()) return@withContext VsaMetrics.empty()

        // Dividir em frames com overlap
        val frames = extractFrames(samples)
        if (frames.isEmpty()) return@withContext VsaMetrics.empty()

        // Calcular cada métrica
        val microTremor = calculateMicroTremor(frames)
        val pitchVariation = calculatePitchVariation(frames)
        val jitter = calculateJitter(frames)
        val shimmer = calculateShimmer(frames)
        val hnr = calculateHNR(frames)

        // Calcular score geral de stress (0-100)
        val overallStress = calculateOverallStress(
            microTremor, pitchVariation, jitter, shimmer, hnr
        )

        VsaMetrics(
            microTremor = microTremor,
            pitchVariation = pitchVariation,
            jitter = jitter,
            shimmer = shimmer,
            hnr = hnr,
            overallStressScore = overallStress
        )
    }

    /**
     * Lê arquivo WAV e retorna samples normalizados.
     */
    private fun readWavFile(file: File): FloatArray {
        return try {
            RandomAccessFile(file, "r").use { raf ->
                // Pular header WAV (44 bytes)
                raf.seek(44)

                val dataSize = (file.length() - 44).toInt()
                val buffer = ByteArray(dataSize)
                raf.readFully(buffer)

                // Converter bytes para shorts (16-bit PCM)
                val shortBuffer = ByteBuffer.wrap(buffer)
                    .order(ByteOrder.LITTLE_ENDIAN)
                    .asShortBuffer()

                val samples = FloatArray(shortBuffer.remaining())
                for (i in samples.indices) {
                    samples[i] = shortBuffer.get() / 32768f // Normalizar para -1..1
                }
                samples
            }
        } catch (e: Exception) {
            floatArrayOf()
        }
    }

    /**
     * Extrai frames com overlap do sinal.
     */
    private fun extractFrames(samples: FloatArray): List<FloatArray> {
        val frames = mutableListOf<FloatArray>()
        var offset = 0

        while (offset + FRAME_SIZE <= samples.size) {
            val frame = samples.copyOfRange(offset, offset + FRAME_SIZE)
            // Aplicar janela de Hamming
            applyHammingWindow(frame)
            frames.add(frame)
            offset += HOP_SIZE
        }

        return frames
    }

    /**
     * Aplica janela de Hamming ao frame.
     */
    private fun applyHammingWindow(frame: FloatArray) {
        for (i in frame.indices) {
            val multiplier = 0.54f - 0.46f * cos(2.0 * PI * i / (frame.size - 1)).toFloat()
            frame[i] *= multiplier
        }
    }

    /**
     * Calcula micro-tremor (8-12Hz) via análise de modulação de amplitude.
     */
    private fun calculateMicroTremor(frames: List<FloatArray>): Float {
        // Calcular envelope de amplitude para cada frame
        val envelope = frames.map { frame ->
            sqrt(frame.map { it * it }.average().toFloat())
        }

        if (envelope.size < 10) return 9f // Valor neutro

        // FFT simplificada para detectar frequência dominante no envelope
        val fftSize = 256
        val paddedEnvelope = FloatArray(fftSize)
        for (i in 0 until minOf(envelope.size, fftSize)) {
            paddedEnvelope[i] = envelope[i]
        }

        val spectrum = fft(paddedEnvelope)

        // Taxa de frames por segundo
        val frameRate = SAMPLE_RATE.toFloat() / HOP_SIZE

        // Procurar pico na faixa 8-12Hz
        val minBin = (8f / frameRate * fftSize).toInt().coerceIn(1, fftSize / 2)
        val maxBin = (12f / frameRate * fftSize).toInt().coerceIn(1, fftSize / 2)

        var maxMagnitude = 0f
        var peakBin = minBin
        for (bin in minBin until maxBin) {
            if (spectrum[bin] > maxMagnitude) {
                maxMagnitude = spectrum[bin]
                peakBin = bin
            }
        }

        // Converter bin para frequência
        val frequency = peakBin * frameRate / fftSize
        return frequency.coerceIn(8f, 12f)
    }

    /**
     * Calcula variação de pitch (F0) via autocorrelação.
     */
    private fun calculatePitchVariation(frames: List<FloatArray>): Float {
        val pitches = mutableListOf<Float>()

        for (frame in frames) {
            val pitch = detectPitch(frame)
            if (pitch > 0) pitches.add(pitch)
        }

        if (pitches.size < 2) return 12f // Valor neutro

        // Calcular coeficiente de variação (CV = std / mean * 100)
        val mean = pitches.average().toFloat()
        val variance = pitches.map { (it - mean) * (it - mean) }.average().toFloat()
        val std = sqrt(variance)

        return (std / mean * 100f).coerceIn(0f, 50f)
    }

    /**
     * Detecta pitch via autocorrelação.
     */
    private fun detectPitch(frame: FloatArray): Float {
        val minLag = (SAMPLE_RATE / MAX_FREQ).toInt()
        val maxLag = (SAMPLE_RATE / MIN_FREQ).toInt().coerceAtMost(frame.size / 2)

        var maxCorr = 0f
        var bestLag = minLag

        for (lag in minLag until maxLag) {
            var corr = 0f
            for (i in 0 until frame.size - lag) {
                corr += frame[i] * frame[i + lag]
            }
            if (corr > maxCorr) {
                maxCorr = corr
                bestLag = lag
            }
        }

        return if (maxCorr > 0) SAMPLE_RATE.toFloat() / bestLag else 0f
    }

    /**
     * Calcula Jitter (variação ciclo-a-ciclo do período).
     */
    private fun calculateJitter(frames: List<FloatArray>): Float {
        val periods = mutableListOf<Float>()

        for (frame in frames) {
            val pitch = detectPitch(frame)
            if (pitch > 0) {
                periods.add(1000f / pitch) // Período em ms
            }
        }

        if (periods.size < 3) return 0.8f // Valor neutro

        // Calcular jitter como variação média entre períodos consecutivos
        var jitterSum = 0f
        for (i in 1 until periods.size) {
            jitterSum += abs(periods[i] - periods[i - 1])
        }

        val meanPeriod = periods.average().toFloat()
        val jitter = (jitterSum / (periods.size - 1)) / meanPeriod * 100f

        return jitter.coerceIn(0f, 10f)
    }

    /**
     * Calcula Shimmer (variação ciclo-a-ciclo da amplitude).
     */
    private fun calculateShimmer(frames: List<FloatArray>): Float {
        val amplitudes = frames.map { frame ->
            frame.maxOrNull()?.let { abs(it) } ?: 0f
        }

        if (amplitudes.size < 3) return 2f // Valor neutro

        // Calcular shimmer como variação média entre amplitudes consecutivas
        var shimmerSum = 0f
        for (i in 1 until amplitudes.size) {
            shimmerSum += abs(amplitudes[i] - amplitudes[i - 1])
        }

        val meanAmplitude = amplitudes.average().toFloat()
        if (meanAmplitude == 0f) return 2f

        val shimmer = (shimmerSum / (amplitudes.size - 1)) / meanAmplitude * 100f

        return shimmer.coerceIn(0f, 20f)
    }

    /**
     * Calcula HNR (Harmonic-to-Noise Ratio) em dB.
     */
    private fun calculateHNR(frames: List<FloatArray>): Float {
        val hnrValues = mutableListOf<Float>()

        for (frame in frames) {
            val pitch = detectPitch(frame)
            if (pitch > 0) {
                val period = (SAMPLE_RATE / pitch).toInt()
                val hnr = calculateFrameHNR(frame, period)
                if (hnr.isFinite()) hnrValues.add(hnr)
            }
        }

        if (hnrValues.isEmpty()) return 18f // Valor neutro

        return hnrValues.average().toFloat().coerceIn(0f, 40f)
    }

    /**
     * Calcula HNR para um frame específico.
     */
    private fun calculateFrameHNR(frame: FloatArray, period: Int): Float {
        if (period <= 0 || period >= frame.size / 2) return 18f

        var harmonicEnergy = 0f
        var totalEnergy = 0f

        for (i in 0 until frame.size - period) {
            val harmonic = (frame[i] + frame[i + period]) / 2f
            harmonicEnergy += harmonic * harmonic
            totalEnergy += frame[i] * frame[i]
        }

        if (totalEnergy == 0f) return 18f

        val noiseEnergy = totalEnergy - harmonicEnergy
        if (noiseEnergy <= 0) return 30f

        return 10f * log10(harmonicEnergy / noiseEnergy)
    }

    /**
     * FFT simplificada (DFT para arrays pequenos).
     */
    private fun fft(signal: FloatArray): FloatArray {
        val n = signal.size
        val magnitude = FloatArray(n / 2)

        for (k in 0 until n / 2) {
            var real = 0f
            var imag = 0f
            for (t in signal.indices) {
                val angle = 2.0 * PI * k * t / n
                real += signal[t] * cos(angle).toFloat()
                imag -= signal[t] * sin(angle).toFloat()
            }
            magnitude[k] = sqrt(real * real + imag * imag)
        }

        return magnitude
    }

    /**
     * Calcula score geral de stress baseado nas 5 métricas.
     */
    private fun calculateOverallStress(
        microTremor: Float,
        pitchVariation: Float,
        jitter: Float,
        shimmer: Float,
        hnr: Float
    ): Float {
        // Normalizar cada métrica para 0-100
        val tremorScore = ((microTremor - 8f) / 4f * 100f).coerceIn(0f, 100f)
        val pitchScore = ((pitchVariation - 10f) / 30f * 100f).coerceIn(0f, 100f)
        val jitterScore = (jitter / 3f * 100f).coerceIn(0f, 100f)
        val shimmerScore = (shimmer / 10f * 100f).coerceIn(0f, 100f)
        val hnrScore = ((25f - hnr) / 15f * 100f).coerceIn(0f, 100f)

        // Média ponderada (micro-tremor tem mais peso)
        val weighted = (
                tremorScore * 0.30f +
                        pitchScore * 0.20f +
                        jitterScore * 0.20f +
                        shimmerScore * 0.15f +
                        hnrScore * 0.15f
                )

        // Adicionar fator aleatório sutil para entretenimento (±5%)
        val randomFactor = (Math.random() * 10 - 5).toFloat()

        return (weighted + randomFactor).coerceIn(0f, 100f)
    }
}
