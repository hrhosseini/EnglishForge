package com.example.englishvocabulary.data.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.data.local.AppDatabase
import com.example.englishvocabulary.data.local.entity.ReviewEntity
import com.example.englishvocabulary.data.remote.ApiService
import com.example.englishvocabulary.data.remote.dto.ReviewAnswerRequest
import com.example.englishvocabulary.domain.model.Review
import com.example.englishvocabulary.domain.repository.ReviewRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class ReviewRepositoryImpl(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : ReviewRepository {

    private val reviewDao = appDatabase.reviewDao()

    override fun getDueReviewsFromServer(): Flow<Resource<List<Review>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getDueReviews()
            val domains = response.map { it.toDomain() }
            
            // Re-sync local cache
            reviewDao.clearAllReviews()
            if (domains.isNotEmpty()) {
                reviewDao.insertReviews(domains.map { ReviewEntity.fromDomain(it) })
            }
            emit(Resource.Success(domains))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "FastAPI server error pulling review schedule."))
        } catch (e: IOException) {
            emit(Resource.Error(e, "Backend is offline. Fetching offline SM2 reviews from dynamic local SQLite cache..."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Failed loading review cycles."))
        }
    }

    override fun getCachedDueReviews(): Flow<List<Review>> {
        return reviewDao.getAllReviews().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun submitReviewAnswer(wordId: Int, answer: String): Flow<Resource<Review>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.submitReviewAnswer(wordId, ReviewAnswerRequest(answer))
            val domainReview = response.toDomain()
            
            // Delete word from local queue if it is no longer due, or update SM2 criteria
            if (domainReview.isDue) {
                reviewDao.insertReview(ReviewEntity.fromDomain(domainReview))
            } else {
                reviewDao.deleteReviewById(wordId)
            }
            emit(Resource.Success(domainReview))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "Review answer rejected by server (${e.code()})."))
        } catch (e: IOException) {
            emit(Resource.Error(e, "Cannot record score. Backend is currently offline."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Unknown answer execution failure."))
        }
    }
}
