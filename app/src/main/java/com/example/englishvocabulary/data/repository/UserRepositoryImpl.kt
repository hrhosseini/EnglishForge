package com.example.englishvocabulary.data.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.data.remote.ApiService
import com.example.englishvocabulary.data.remote.dto.ProfileUpdateRequest
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.HttpException
import java.io.IOException

class UserRepositoryImpl(
    private val apiService: ApiService
) : UserRepository {

    override fun getMyProfile(): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getUserProfile()
            emit(Resource.Success(response.toDomain()))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "Forbidden server access: login credentials invalidated."))
        } catch (e: IOException) {
            emit(Resource.Error(e, "Connection fault fetching current profile details."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Failed loading profile summary."))
        }
    }

    override fun updateProfile(
        displayName: String,
        cefrLevel: String,
        interests: List<String>
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.updateUserProfile(
                ProfileUpdateRequest(displayName, cefrLevel, interests)
            )
            emit(Resource.Success(response.toDomain()))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "Server rejected profile parameters (${e.code()})."))
        } catch (e: java.io.IOException) {
            emit(Resource.Error(e, "Network disconnected. Cannot save profile data online."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Failed profile synchronization."))
        }
    }
}
