package com.onewordaday.app.ui.screen.favourites

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.repository.WordRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FavouritesViewModel @Inject constructor(
    private val repository: WordRepository
) : ViewModel() {

    val favourites: StateFlow<List<Word>> = repository.getFavourites()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5_000), emptyList())

    fun toggleFavourite(word: Word) {
        viewModelScope.launch {
            repository.toggleFavourite(word.id, word.isFavourited)
        }
    }
}
