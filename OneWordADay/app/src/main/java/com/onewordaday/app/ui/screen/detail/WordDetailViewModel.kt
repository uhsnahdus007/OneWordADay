package com.onewordaday.app.ui.screen.detail

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

sealed interface WordDetailUiState {
    data object Loading : WordDetailUiState
    data class Success(val word: Word) : WordDetailUiState
    data class Error(val message: String) : WordDetailUiState
}

@HiltViewModel
class WordDetailViewModel @Inject constructor(
    savedStateHandle: SavedStateHandle,
    private val repository: WordRepository
) : ViewModel() {

    private val wordId: Long = checkNotNull(savedStateHandle["wordId"])

    private val _uiState = MutableStateFlow<WordDetailUiState>(WordDetailUiState.Loading)
    val uiState: StateFlow<WordDetailUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            val word = repository.getWordById(wordId)
            _uiState.value = if (word != null) WordDetailUiState.Success(word)
            else WordDetailUiState.Error("Word not found")
        }
    }

    fun toggleFavourite() {
        val state = _uiState.value as? WordDetailUiState.Success ?: return
        viewModelScope.launch {
            repository.toggleFavourite(state.word.id, state.word.isFavourited)
            val updated = repository.getWordById(state.word.id) ?: return@launch
            _uiState.value = WordDetailUiState.Success(updated)
        }
    }
}
