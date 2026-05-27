package com.example.englishvocabulary.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.englishvocabulary.domain.model.Review

@Entity(tableName = "cache_reviews")
data class ReviewEntity(
    @PrimaryKey val wordId: Int,
    val word: String,
    val partOfSpeech: String,
    val definition: String,
    val nextReviewDate: Long,
    val easeFactor: Double,
    val intervalDays: Int,
    val repetitions: Int,
    val isDue: Boolean
) {
    fun toDomain(): Review {
        return Review(
            wordId = wordId,
            word = word,
            partOfSpeech = partOfSpeech,
            definition = definition,
            nextReviewDate = nextReviewDate,
            easeFactor = easeFactor,
            intervalDays = intervalDays,
            repetitions = repetitions,
            isDue = isDue
        )
    }

    companion object {
        fun fromDomain(domain: Review): ReviewEntity {
            return ReviewEntity(
                wordId = domain.wordId,
                word = domain.word,
                partOfSpeech = domain.partOfSpeech,
                definition = domain.definition,
                nextReviewDate = domain.nextReviewDate,
                easeFactor = domain.easeFactor,
                intervalDays = domain.intervalDays,
                repetitions = domain.repetitions,
                isDue = domain.isDue
            )
        }
    }
}
