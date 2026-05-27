package com.example.englishvocabulary.domain.usecase

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class RegisterUseCase(private val repository: AuthRepository) {
    operator fun invoke(
        email: String,
        password: String,
        displayName: String,
        cefrLevel: String,
        interests: List<String>
    ): Flow<Resource<User>> {
        return repository.register(email, password, displayName, cefrLevel, interests)
    }
}
