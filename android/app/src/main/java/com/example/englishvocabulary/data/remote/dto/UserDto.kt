package com.example.englishvocabulary.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.englishvocabulary.domain.model.User

@JsonClass(generateAdapter = true)
data class UserDto(
    @Json(name = "id") val id: Int,
    @Json(name = "email") val email: String,
    @Json(name = "displayName") val displayName: String,
    @Json(name = "cefrLevel") val cefrLevel: String,
    @Json(name = "interests") val interests: List<String>? = emptyList()
) {
    fun toDomain(): User {
        return User(
            id = id,
            email = email,
            displayName = displayName,
            cefrLevel = cefrLevel,
            interests = interests ?: emptyList()
        )
    }
}

@JsonClass(generateAdapter = true)
data class ProfileUpdateRequest(
    @Json(name = "displayName") val displayName: String,
    @Json(name = "cefrLevel") val cefrLevel: String,
    @Json(name = "interests") val interests: List<String>
)
