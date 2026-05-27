package com.example.englishvocabulary.domain.usecase

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow

class LoginUseCase(private val repository: AuthRepository) {
    operator fun invoke(email: String, password: String): Flow<Resource<User>> {
        return repository.login(email, password)
    }
}
