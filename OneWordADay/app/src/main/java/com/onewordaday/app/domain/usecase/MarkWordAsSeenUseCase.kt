package com.onewordaday.app.domain.usecase

import com.onewordaday.app.data.repository.WordRepository
import com.onewordaday.app.util.DateUtils
import javax.inject.Inject

class MarkWordAsSeenUseCase @Inject constructor(
    private val repository: WordRepository
) {
    suspend operator fun invoke(wordId: Long) {
        repository.markWordAsSeen(wordId, DateUtils.today())
    }
}
