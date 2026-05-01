package com.onewordaday.app.domain.usecase

import com.onewordaday.app.data.model.Word
import com.onewordaday.app.data.repository.WordRepository
import javax.inject.Inject

class GetTodaysWordUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(): Word = repository.getTodaysWord()
}
