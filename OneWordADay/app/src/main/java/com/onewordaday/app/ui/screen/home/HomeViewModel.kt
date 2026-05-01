package com.onewordaday.app.ui.screen.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.repository.WordRepository
import com.onewordaday.app.domain.usecase.GetTodaysWordUseCase
import com.onewordaday.app.domain.usecase.MarkWordAsSeenUseCase
import com.onewordaday.app.util.PreferencesManager
import com.onewordaday.app.util.ShareImageGenerator
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

private val MILESTONES = setOf(3, 7, 14, 30, 60, 100)

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val getTodaysWord: GetTodaysWordUseCase,
    private val markWordAsSeen: MarkWordAsSeenUseCase,
    private val repository: WordRepository,
    private val preferencesManager: PreferencesManager,
    private val shareImageGenerator: ShareImageGenerator
) : ViewModel() {

    private val _uiState = MutableStateFlow<HomeUiState>(HomeUiState.Loading)
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    private val _streak = MutableStateFlow(0)
    val streak: StateFlow<Int> = _streak.asStateFlow()

    private val _milestoneStreak = MutableStateFlow(0)
    val milestoneStreak: StateFlow<Int> = _milestoneStreak.asStateFlow()

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
                val streak = repository.getCurrentStreak()
                _streak.value = streak
                checkMilestone(streak)
            } catch (e: Exception) {
                _uiState.value = HomeUiState.Error(e.message ?: "Could not load today's word")
            }
        }
    }

    fun toggleFavourite() {
        val state = _uiState.value as? HomeUiState.Success ?: return
        viewModelScope.launch {
            repository.toggleFavourite(state.word.id, state.word.isFavourited)
            val updated = repository.getWordById(state.word.id) ?: return@launch
            _uiState.value = HomeUiState.Success(updated)
        }
    }

    fun shareCurrentWord() {
        val state = _uiState.value as? HomeUiState.Success ?: return
        shareImageGenerator.shareWordAsImage(state.word)
    }

    fun dismissMilestone() {
        viewModelScope.launch {
            preferencesManager.setLastMilestoneShown(_streak.value)
            _milestoneStreak.value = 0
        }
    }

    private suspend fun checkMilestone(streak: Int) {
        if (streak !in MILESTONES) return
        val lastShown = preferencesManager.getLastMilestoneShown()
        if (streak > lastShown) {
            _milestoneStreak.value = streak
        }
    }
}
