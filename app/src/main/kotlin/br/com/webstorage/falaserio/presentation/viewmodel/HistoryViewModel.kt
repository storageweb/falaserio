package br.com.webstorage.falaserio.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import br.com.webstorage.falaserio.data.local.entity.HistoryEntity
import br.com.webstorage.falaserio.data.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

/**
 * ViewModel para tela de histórico.
 */
@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    val historyList: StateFlow<List<HistoryEntity>> = historyRepository
        .getAllHistory()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun deleteHistory(id: Long) {
        viewModelScope.launch {
            historyRepository.deleteById(id)
        }
    }

    fun deleteAll() {
        viewModelScope.launch {
            historyRepository.deleteAll()
        }
    }
}
