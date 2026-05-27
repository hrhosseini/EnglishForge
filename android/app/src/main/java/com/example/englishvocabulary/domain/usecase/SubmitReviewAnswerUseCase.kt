package com.example.englishvocabulary.domain.usecase

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Review
import com.example.englishvocabulary.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow

class SubmitReviewAnswerUseCase(private val repository: ReviewRepository) {
    operator fun invoke(wordId: Int, answer: String): Flow<Resource<Review>> {
        return repository.submitReviewAnswer(wordId, answer)
    }
}
