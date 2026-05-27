package com.example.englishvocabulary.data.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.data.datastore.TokenDataStore
import com.example.englishvocabulary.data.local.AppDatabase
import com.example.englishvocabulary.data.remote.ApiService
import com.example.englishvocabulary.data.remote.dto.LoginRequest
import com.example.englishvocabulary.data.remote.dto.RegisterRequest
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class AuthRepositoryImpl(
    private val apiService: ApiService,
    private val tokenDataStore: TokenDataStore,
    private val appDatabase: AppDatabase
) : AuthRepository {

    override val isLoggedIn: Flow<Boolean> = tokenDataStore.accessToken.map { !it.isNullOrEmpty() }

    override fun login(email: String, password: String): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.login(LoginRequest(email, password))
            tokenDataStore.saveToken(response.accessToken)
            emit(Resource.Success(response.user.toDomain()))
        } catch (e: HttpException) {
            val errMsg = when (e.code()) {
                401 -> "Invalid email or password."
                404 -> "API path not found."
                500 -> "Internal FastAPI server error."
                else -> "Login failed. Code: ${e.code()}"
            }
            emit(Resource.Error(e, errMsg))
        } catch (e: IOException) {
            emit(Resource.Error(e, "FastAPI backend unavailable! Please check your IP/Port configurations."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Unknown authentication error."))
        }
    }

    override fun register(
        email: String,
        password: String,
        displayName: String,
        cefrLevel: String,
        interests: List<String>
    ): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val request = RegisterRequest(email, password, displayName, cefrLevel, interests)
            val response = apiService.register(request)
            tokenDataStore.saveToken(response.accessToken)
            emit(Resource.Success(response.user.toDomain()))
        } catch (e: HttpException) {
            val msg = if (e.code() == 400) "Email address is already registered." else "Registration failed (${e.code()})."
            emit(Resource.Error(e, msg))
        } catch (e: IOException) {
            emit(Resource.Error(e, "FastAPI backend unavailable! Check computer network connection."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Unknown registration failure."))
        }
    }

    override fun getMe(): Flow<Resource<User>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getAuthMe()
            emit(Resource.Success(response.toDomain()))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "Authentication credentials expired or invalid. Please login again."))
        } catch (e: IOException) {
            emit(Resource.Error(e, "Backend offline. Fetching from cache..."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Could not verify account identity."))
        }
    }

    override suspend fun logout() {
        tokenDataStore.clearToken()
        appDatabase.wordDao().clearAllWords()
        appDatabase.reviewDao().clearAllReviews()
    }
}
