package com.example.englishvocabulary.domain.usecase

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Review
import com.example.englishvocabulary.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow

class GetDueReviewsUseCase(private val repository: ReviewRepository) {
    fun getCachedDue(): Flow<List<Review>> {
        return repository.getCachedDueReviews()
    }

    fun refreshFromServer(): Flow<Resource<List<Review>>> {
        return repository.getDueReviewsFromServer()
    }
}
