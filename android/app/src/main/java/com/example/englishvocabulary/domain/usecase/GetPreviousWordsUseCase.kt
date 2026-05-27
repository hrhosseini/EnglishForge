package com.example.englishvocabulary.domain.usecase

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Word
import com.example.englishvocabulary.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow

class GetPreviousWordsUseCase(private val repository: WordRepository) {
    // Return standard stream
    fun getCachedStream(): Flow<List<Word>> {
        return repository.getCachedWords()
    }

    // Return refresh triggers
    fun refreshFromServer(): Flow<Resource<List<Word>>> {
        return repository.refreshMyWordsFromServer()
    }
}
