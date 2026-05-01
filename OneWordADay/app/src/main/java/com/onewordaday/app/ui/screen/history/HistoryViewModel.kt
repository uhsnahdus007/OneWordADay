package com.onewordaday.app.ui.screen.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    repository: WordRepository
) : ViewModel() {

    val historyWords: StateFlow<List<Word>> = repository.getHistoryWords()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())
}
