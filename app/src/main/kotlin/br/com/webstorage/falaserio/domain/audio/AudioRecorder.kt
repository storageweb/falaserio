package br.com.webstorage.falaserio.domain.audio

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.StateFlow
import java.io.File

/**
 * Interface para gravação de áudio.
 *
 * IMPORTANTE: Esta interface existe para evitar vazamento de memória.
 * O ViewModel NÃO deve receber Context diretamente.
 * A implementação (AudioRecorderImpl) é injetada via Hilt.
 */
interface AudioRecorder {

    /** Estado atual da gravação */
    val isRecording: StateFlow<Boolean>

    /** Duração atual em milissegundos */
    val recordingDuration: StateFlow<Long>

    /** Amplitude atual para visualização (0.0 a 1.0) */
    val currentAmplitude: StateFlow<Float>

    /** Buffer de amostras para análise VSA */
    val audioSamples: Flow<FloatArray>

    /**
     * Inicia a gravação de áudio.
     * @return File onde o áudio será salvo, ou null se falhar
     */
    suspend fun start(): File?

    /**
     * Para a gravação.
     * @return File com o áudio gravado, ou null se não estava gravando
     */
    suspend fun stop(): File?

    /**
     * Cancela a gravação atual e descarta o arquivo.
     */
    suspend fun cancel()

    /**
     * Libera recursos. Chamar quando não precisar mais do recorder.
     */
    fun release()
}
