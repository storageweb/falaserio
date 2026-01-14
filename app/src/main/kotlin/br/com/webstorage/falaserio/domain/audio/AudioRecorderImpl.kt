package br.com.webstorage.falaserio.domain.audio

import android.content.Context
import android.media.AudioFormat
import android.media.AudioRecord
import android.media.MediaRecorder
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.nio.ByteBuffer
import java.nio.ByteOrder
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementação otimizada do AudioRecorder.
 *
 * Correções aplicadas:
 * 1. Uso de CoroutineScope dedicado para evitar deadlock no start()
 * 2. Gravação direta em disco (FileOutputStream) para evitar OutOfMemory
 * 3. Header Patching para WAV (sem carregar áudio na RAM)
 */
@Singleton
class AudioRecorderImpl @Inject constructor(
    @param:ApplicationContext private val context: Context
) : AudioRecorder {

    companion object {
        const val SAMPLE_RATE = 44100
        const val CHANNEL_CONFIG = AudioFormat.CHANNEL_IN_MONO
        const val AUDIO_FORMAT = AudioFormat.ENCODING_PCM_16BIT
        const val BUFFER_SIZE_FACTOR = 2
    }

    private val recordingScope = CoroutineScope(Dispatchers.IO + SupervisorJob())

    private var audioRecord: AudioRecord? = null
    private var tempPcmFile: File? = null
    private var finalWavFile: File? = null

    private val _isRecording = MutableStateFlow(false)
    override val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDuration = MutableStateFlow(0L)
    override val recordingDuration: StateFlow<Long> = _recordingDuration.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0f)
    override val currentAmplitude: StateFlow<Float> = _currentAmplitude.asStateFlow()

    private val _audioSamples = MutableSharedFlow<FloatArray>(replay = 0, extraBufferCapacity = 10)
    override val audioSamples: Flow<FloatArray> = _audioSamples.asSharedFlow()

    private val bufferSize = AudioRecord.getMinBufferSize(
        SAMPLE_RATE, CHANNEL_CONFIG, AUDIO_FORMAT
    ) * BUFFER_SIZE_FACTOR

    private var startTime: Long = 0L

    override suspend fun start(): File? = withContext(Dispatchers.IO) {
        if (_isRecording.value) return@withContext finalWavFile

        try {
            val cacheDir = context.cacheDir
            tempPcmFile = File(cacheDir, "recording_temp.pcm")
            finalWavFile = File(cacheDir, "recording_${System.currentTimeMillis()}.wav")

            audioRecord = AudioRecord(
                MediaRecorder.AudioSource.MIC,
                SAMPLE_RATE,
                CHANNEL_CONFIG,
                AUDIO_FORMAT,
                bufferSize
            )

            if (audioRecord?.state != AudioRecord.STATE_INITIALIZED) {
                release()
                return@withContext null
            }

            audioRecord?.startRecording()
            _isRecording.value = true
            startTime = System.currentTimeMillis()

            // Lança os loops no escopo dedicado, sem bloquear o start()
            recordingScope.launch { recordAudioLoop() }
            recordingScope.launch { updateDurationLoop() }

            finalWavFile
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private suspend fun recordAudioLoop() {
        val buffer = ShortArray(bufferSize / 2)

        try {
            FileOutputStream(tempPcmFile).use { fos ->
                while (_isRecording.value) {
                    val readCount = audioRecord?.read(buffer, 0, buffer.size) ?: 0
                    if (readCount > 0) {
                        // 1. Calcular Amplitude (RMS)
                        var sum = 0.0
                        for (i in 0 until readCount) {
                            sum += buffer[i] * buffer[i]
                        }
                        val rms = kotlin.math.sqrt(sum / readCount)
                        _currentAmplitude.value = (rms / Short.MAX_VALUE).toFloat().coerceIn(0f, 1f)

                        // 2. Emitir samples para análise VSA (opcional/tempo real)
                        val floatSamples =
                            FloatArray(readCount) { i -> buffer[i].toFloat() / Short.MAX_VALUE }
                        _audioSamples.tryEmit(floatSamples)

                        // 3. Escrever no disco (PCM) - SEM BOXING!
                        val byteBuffer =
                            ByteBuffer.allocate(readCount * 2).order(ByteOrder.LITTLE_ENDIAN)
                        for (i in 0 until readCount) {
                            byteBuffer.putShort(buffer[i])
                        }
                        fos.write(byteBuffer.array())
                    }
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private suspend fun updateDurationLoop() {
        while (_isRecording.value) {
            _recordingDuration.value = System.currentTimeMillis() - startTime
            delay(100)
        }
    }

    override suspend fun stop(): File? = withContext(Dispatchers.IO) {
        if (!_isRecording.value) return@withContext null

        _isRecording.value = false

        audioRecord?.stop()
        audioRecord?.release()
        audioRecord = null

        // Converte PCM para WAV injetando o cabeçalho
        val resultFile = finalWavFile
        val pcmFile = tempPcmFile

        if (resultFile != null && pcmFile != null && pcmFile.exists()) {
            savePcmAsWav(pcmFile, resultFile)
            pcmFile.delete()
        }

        _currentAmplitude.value = 0f
        resultFile
    }

    override suspend fun cancel() {
        stop()
        finalWavFile?.delete()
        tempPcmFile?.delete()
    }

    override fun release() {
        _isRecording.value = false
        audioRecord?.release()
        audioRecord = null
        recordingScope.cancel() // Cancela todas as corrotinas pendentes
    }

    private fun savePcmAsWav(pcmFile: File, wavFile: File) {
        val pcmDataSize = pcmFile.length()
        val totalDataLen = pcmDataSize + 36
        val channels = 1
        val byteRate = SAMPLE_RATE * channels * 2

        FileOutputStream(wavFile).use { fos ->
            // WAV Header (44 bytes)
            fos.write("RIFF".toByteArray())
            fos.write(intToByteArray(totalDataLen.toInt()))
            fos.write("WAVE".toByteArray())
            fos.write("fmt ".toByteArray())
            fos.write(intToByteArray(16))
            fos.write(shortToByteArray(1)) // PCM
            fos.write(shortToByteArray(channels.toShort()))
            fos.write(intToByteArray(SAMPLE_RATE))
            fos.write(intToByteArray(byteRate))
            fos.write(shortToByteArray((channels * 2).toShort()))
            fos.write(shortToByteArray(16)) // 16 bits
            fos.write("data".toByteArray())
            fos.write(intToByteArray(pcmDataSize.toInt()))

            // Copia os dados PCM para o arquivo WAV em blocos (Streaming)
            FileInputStream(pcmFile).use { fis ->
                val buffer = ByteArray(8192)
                var bytesRead: Int
                while (fis.read(buffer).also { bytesRead = it } != -1) {
                    fos.write(buffer, 0, bytesRead)
                }
            }
        }
    }

    private fun intToByteArray(value: Int) = byteArrayOf(
        value.toByte(), (value shr 8).toByte(), (value shr 16).toByte(), (value shr 24).toByte()
    )

    private fun shortToByteArray(value: Short) = byteArrayOf(
        value.toByte(), (value.toInt() shr 8).toByte()
    )
}
