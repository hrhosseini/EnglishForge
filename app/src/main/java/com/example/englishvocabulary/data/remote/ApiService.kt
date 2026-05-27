package com.example.englishvocabulary.data.remote

import com.example.englishvocabulary.data.remote.dto.*
import retrofit2.http.*

interface ApiService {

    // Health Check
    @GET("health")
    suspend fun checkHealth(): Map<String, String>

    // Authentication Endpoints
    @POST("api/v1/auth/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/v1/auth/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/v1/auth/me")
    suspend fun getAuthMe(): UserDto

    // User Profile
    @GET("api/v1/users/me")
    suspend fun getUserProfile(): UserDto

    @PUT("api/v1/users/me")
    suspend fun updateUserProfile(@Body request: ProfileUpdateRequest): UserDto

    // Word Suggestions and Customs
    @GET("api/v1/words/suggest")
    suspend fun getDailyWordSuggestion(): WordDto

    @GET("api/v1/words/{word_id}")
    suspend fun getWordById(@Path("word_id") wordId: Int): WordDto

    @POST("api/v1/words/custom")
    suspend fun createCustomWord(@Body request: CustomWordRequest): WordDto

    @POST("api/v1/words/{word_id}/save")
    suspend fun toggleSaveWord(@Path("word_id") wordId: Int): Map<String, Boolean>

    @GET("api/v1/users/me/words")
    suspend fun getMyWords(): List<WordDto>

    @GET("api/v1/users/me/suggestions")
    suspend fun getMySuggestionHistory(): List<WordDto>

    // Review System
    @GET("api/v1/reviews/due")
    suspend fun getDueReviews(): List<ReviewDto>

    @POST("api/v1/reviews/{word_id}/answer")
    suspend fun submitReviewAnswer(
        @Path("word_id") wordId: Int,
        @Body request: ReviewAnswerRequest
    ): ReviewDto
}
