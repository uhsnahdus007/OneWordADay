package com.onewordaday.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.domain.usecase.GetTodaysWordUseCase
import com.onewordaday.app.domain.usecase.MarkWordAsSeenUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface HomeUiState {
    data object Loading : HomeUiState
    data class Success(val word: Word) : HomeUiState
    data class Error(val message: String) : HomeUiState
}

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodaysWord: GetTodaysWordUseCase,
    private val markWordAsSeen: MarkWordAsSeenUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadTodaysWord()
    }

    fun loadTodaysWord() {
        viewModelScope.launch {
            _uiState.value = HomeUiState.Loading
            try {
                val word = getTodaysWord()
                _uiState.value = HomeUiState.Success(word)
                markWordAsSeen(word.id)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Could not load today's word")
            }
        }
    }
}
