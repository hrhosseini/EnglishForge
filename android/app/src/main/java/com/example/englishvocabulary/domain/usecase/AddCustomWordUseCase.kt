package com.example.englishvocabulary.domain.usecase

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Word
import com.example.englishvocabulary.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow

class AddCustomWordUseCase(private val repository: WordRepository) {
    operator fun invoke(wordText: String): Flow<Resource<Word>> {
        return repository.createCustomWord(wordText)
    }
}
