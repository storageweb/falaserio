package br.com.webstorage.falaserio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.webstorage.falaserio.data.repository.CreditsRepository
import br.com.webstorage.falaserio.data.repository.HistoryRepository
import br.com.webstorage.falaserio.domain.audio.AudioRecorder
import br.com.webstorage.falaserio.domain.model.VsaMetrics
import br.com.webstorage.falaserio.domain.usecase.AnalyzeAudioUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.File
import javax.inject.Inject

/**
 * ViewModel principal do FalaSério.
 *
 * Melhora aplicada: Verificação de créditos ANTES de iniciar a gravação.
 */
@HiltViewModel
class MainViewModel @Inject constructor(
    private val audioRecorder: AudioRecorder,
    private val analyzeAudioUseCase: AnalyzeAudioUseCase,
    private val creditsRepository: CreditsRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    // ========== UI STATE ==========
    private val _uiState = MutableStateFlow(UiState())
    val uiState: StateFlow<UiState> = _uiState.asStateFlow()

    // ========== RECORDING STATE ==========
    val isRecording: StateFlow<Boolean> = audioRecorder.isRecording
    val recordingDuration: StateFlow<Long> = audioRecorder.recordingDuration
    val currentAmplitude: StateFlow<Float> = audioRecorder.currentAmplitude

    // ========== CREDITS ==========
    private val _credits = MutableStateFlow(0)
    val credits: StateFlow<Int> = _credits.asStateFlow()

    init {
        loadCredits()
    }

    private fun loadCredits() {
        viewModelScope.launch {
            creditsRepository.getCredits().collect { entity ->
                entity?.let {
                    _credits.value = if (it.isUnlimited) Int.MAX_VALUE else it.available
                }
            }
        }
    }

    // ========== RECORDING ACTIONS ==========

    fun startRecording() {
        viewModelScope.launch {
            // VERIFICAÇÃO PREVENTIVA: Se não tem crédito, nem começa.
            if (_credits.value <= 0) {
                _uiState.update { it.copy(error = "Você não possui créditos suficientes.") }
                return@launch
            }

            _uiState.update { it.copy(isAnalyzing = false, error = null, metrics = null) }
            audioRecorder.start()
        }
    }

    fun stopRecording() {
        viewModelScope.launch {
            val file = audioRecorder.stop()
            file?.let { analyzeRecording(it) }
        }
    }

    fun cancelRecording() {
        viewModelScope.launch {
            audioRecorder.cancel()
            _uiState.update { it.copy(isAnalyzing = false, error = null) }
        }
    }

    // ========== ANALYSIS ==========

    private fun analyzeRecording(file: File) {
        viewModelScope.launch(Dispatchers.Default) {
            _uiState.update { it.copy(isAnalyzing = true, error = null) }

            // Tenta usar o crédito de fato agora
            val success = creditsRepository.useCredit()
            if (!success) {
                _uiState.update {
                    it.copy(isAnalyzing = false, error = "Erro ao processar crédito.")
                }
                return@launch
            }

            try {
                val metrics = analyzeAudioUseCase(file)
                historyRepository.saveAnalysis(file, metrics)

                _uiState.update {
                    it.copy(isAnalyzing = false, metrics = metrics, error = null)
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(isAnalyzing = false, error = "Erro na análise: ${e.message}")
                }
            }
        }
    }

    fun onAdWatched() {
        viewModelScope.launch {
            creditsRepository.addCredits(1)
        }
    }

    override fun onCleared() {
        super.onCleared()
        audioRecorder.release()
    }
}

data class UiState(
    val isAnalyzing: Boolean = false,
    val metrics: VsaMetrics? = null,
    val error: String? = null
)
