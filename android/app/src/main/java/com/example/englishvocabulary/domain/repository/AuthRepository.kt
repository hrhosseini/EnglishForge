package com.example.englishvocabulary.domain.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    fun login(email: String, password: String): Flow<Resource<User>>
    fun register(
        email: String, 
        password: String, 
        displayName: String, 
        cefrLevel: String, 
        interests: List<String>
    ): Flow<Resource<User>>
    fun getMe(): Flow<Resource<User>>
    suspend fun logout()
    val isLoggedIn: Flow<Boolean>
}
