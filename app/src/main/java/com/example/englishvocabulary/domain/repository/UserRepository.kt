package com.example.englishvocabulary.domain.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    fun getMyProfile(): Flow<Resource<User>>
    fun updateProfile(
        displayName: String,
        cefrLevel: String,
        interests: List<String>
    ): Flow<Resource<User>>
}
