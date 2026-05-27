package com.example.englishvocabulary.domain.model

data class Review(
    val wordId: Int,
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val nextReviewDate: Long = System.currentTimeMillis(),
    val easeFactor: Double = 2.5,
    val intervalDays: Int = 1,
    val repetitions: Int = 0,
    val isDue: Boolean = true
)
