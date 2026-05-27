package com.example.englishvocabulary.domain.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Word
import kotlinx.coroutines.flow.Flow

interface WordRepository {
    fun getSuggestedWord(): Flow<Resource<Word>>
    fun createCustomWord(wordText: String): Flow<Resource<Word>>
    fun toggleSaveWord(wordId: Int, isCurrentlySaved: Boolean): Flow<Resource<Boolean>>
    fun getCachedWords(): Flow<List<Word>>
    fun refreshMyWordsFromServer(): Flow<Resource<List<Word>>>
    fun getSuggestionHistoryFromServer(): Flow<Resource<List<Word>>>
}
