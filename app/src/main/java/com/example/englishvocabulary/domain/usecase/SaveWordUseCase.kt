package com.example.englishvocabulary.domain.usecase

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow

class SaveWordUseCase(private val repository: WordRepository) {
    operator fun invoke(wordId: Int, isCurrentlySaved: Boolean): Flow<Resource<Boolean>> {
        return repository.toggleSaveWord(wordId, isCurrentlySaved)
    }
}
