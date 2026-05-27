package com.example.englishvocabulary.data.local.dao

import androidx.room.*
import com.example.englishvocabulary.data.local.entity.WordEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WordDao {
    @Query("SELECT * FROM cache_words ORDER BY addedAt DESC")
    fun getAllWords(): Flow<List<WordEntity>>

    @Query("SELECT * FROM cache_words WHERE id = :id LIMIT 1")
    suspend fun getWordById(id: Int): WordEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWords(words: List<WordEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertWord(word: WordEntity)

    @Query("UPDATE cache_words SET isSaved = :isSaved WHERE id = :id")
    suspend fun updateSavedStatus(id: Int, isSaved: Boolean)

    @Query("DELETE FROM cache_words WHERE id = :id")
    suspend fun deleteWordById(id: Int)

    @Query("DELETE FROM cache_words")
    suspend fun clearAllWords()
}
