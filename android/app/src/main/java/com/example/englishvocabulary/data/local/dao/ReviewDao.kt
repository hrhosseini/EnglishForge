package com.example.englishvocabulary.data.local.dao

import androidx.room.*
import com.example.englishvocabulary.data.local.entity.ReviewEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ReviewDao {
    @Query("SELECT * FROM cache_reviews ORDER BY nextReviewDate ASC")
    fun getAllReviews(): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReviews(reviews: List<ReviewEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    @Query("DELETE FROM cache_reviews WHERE wordId = :wordId")
    suspend fun deleteReviewById(wordId: Int)

    @Query("DELETE FROM cache_reviews")
    suspend fun clearAllReviews()
}
