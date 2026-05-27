package com.example.englishvocabulary.domain.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Review
import kotlinx.coroutines.flow.Flow

interface ReviewRepository {
    fun getDueReviewsFromServer(): Flow<Resource<List<Review>>>
    fun getCachedDueReviews(): Flow<List<Review>>
    fun submitReviewAnswer(wordId: Int, answer: String): Flow<Resource<Review>>
}
