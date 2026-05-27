package com.example.englishvocabulary.domain.model

data class User(
    val id: Int,
    val email: String,
    val displayName: String,
    val cefrLevel: String, // "A1", "A2", "B1", "B2", "C1", "C2"
    val interests: List<String>
)
