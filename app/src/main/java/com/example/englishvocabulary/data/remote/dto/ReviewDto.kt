package com.example.englishvocabulary.data.remote.dto

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass
import com.example.englishvocabulary.domain.model.Review

@JsonClass(generateAdapter = true)
data class ReviewAnswerRequest(
    @Json(name = "answer") val answer: String // "again", "hard", "good", "easy"
)

@JsonClass(generateAdapter = true)
data class ReviewDto(
    @Json(name = "wordId") val wordId: Int,
    @Json(name = "word") val word: String,
    @Json(name = "partOfSpeech") val partOfSpeech: String,
    @Json(name = "definition") val definition: String,
    @Json(name = "nextReviewDate") val nextReviewDate: Long?,
    @Json(name = "easeFactor") val easeFactor: Double?,
    @Json(name = "intervalDays") val intervalDays: Int?,
    @Json(name = "repetitions") val repetitions: Int?,
    @Json(name = "isDue") val isDue: Boolean?
) {
    fun toDomain(): Review {
        return Review(
            wordId = wordId,
            word = word,
            partOfSpeech = partOfSpeech,
            definition = definition,
            nextReviewDate = nextReviewDate ?: System.currentTimeMillis(),
            easeFactor = easeFactor ?: 2.5,
            intervalDays = intervalDays ?: 1,
            repetitions = repetitions ?: 0,
            isDue = isDue ?: true
        )
    }
}
