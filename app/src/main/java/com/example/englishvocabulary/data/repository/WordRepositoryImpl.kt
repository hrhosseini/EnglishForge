package com.example.englishvocabulary.data.repository

import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.data.local.AppDatabase
import com.example.englishvocabulary.data.local.entity.WordEntity
import com.example.englishvocabulary.data.remote.ApiService
import com.example.englishvocabulary.data.remote.dto.CustomWordRequest
import com.example.englishvocabulary.domain.model.Word
import com.example.englishvocabulary.domain.repository.WordRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException

class WordRepositoryImpl(
    private val apiService: ApiService,
    private val appDatabase: AppDatabase
) : WordRepository {

    private val wordDao = appDatabase.wordDao()

    override fun getSuggestedWord(): Flow<Resource<Word>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getDailyWordSuggestion()
            val domainWord = response.toDomain(isSaved = false, isSuggested = true)
            // Save in cache
            wordDao.insertWord(WordEntity.fromDomain(domainWord))
            emit(Resource.Success(domainWord))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "Error code ${e.code()} when proposing suggestion."))
        } catch (e: IOException) {
            emit(Resource.Error(e, "FastAPI backend is offline. Suggested word could not be fetched."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Unknown suggestion fetching error."))
        }
    }

    override fun createCustomWord(wordText: String): Flow<Resource<Word>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.createCustomWord(CustomWordRequest(wordText))
            // Save custom in local database marked as saved
            val domainWord = response.toDomain(isSaved = true, isSuggested = false)
            wordDao.insertWord(WordEntity.fromDomain(domainWord))
            emit(Resource.Success(domainWord))
        } catch (e: HttpException) {
            val msg = if (e.code() == 422) "The word format or definition was rejected. Please try another." else "Error creating word: ${e.code()}"
            emit(Resource.Error(e, msg))
        } catch (e: IOException) {
            emit(Resource.Error(e, "Backend is unreachable. Cannot process custom word request."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Failed to record custom word."))
        }
    }

    override fun toggleSaveWord(wordId: Int, isCurrentlySaved: Boolean): Flow<Resource<Boolean>> = flow {
        emit(Resource.Loading)
        val targetSaved = !isCurrentlySaved
        try {
            val result = apiService.toggleSaveWord(wordId)
            val serverSaved = result["saved"] ?: targetSaved
            // Update SQLite cache
            val existing = wordDao.getWordById(wordId)
            if (existing != null) {
                wordDao.updateSavedStatus(wordId, serverSaved)
            }
            emit(Resource.Success(serverSaved))
        } catch (e: HttpException) {
            // Local fallback if server fails
            wordDao.updateSavedStatus(wordId, targetSaved)
            emit(Resource.Success(targetSaved))
        } catch (e: IOException) {
            // Local fallback when offline
            wordDao.updateSavedStatus(wordId, targetSaved)
            emit(Resource.Success(targetSaved))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Toggle bookmark failed."))
        }
    }

    override fun getCachedWords(): Flow<List<Word>> {
        return wordDao.getAllWords().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun refreshMyWordsFromServer(): Flow<Resource<List<Word>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getMyWords()
            val domains = response.map { it.toDomain(isSaved = true) }
            // Populate/Insert SQLite cache bulk
            wordDao.insertWords(domains.map { WordEntity.fromDomain(it) })
            emit(Resource.Success(domains))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "Server issue while accessing vocabulary history: ${e.code()}"))
        } catch (e: IOException) {
            emit(Resource.Error(e, "Could not reach FastAPI. Displaying local SQLite offline index..."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Unhandled history fetching issue."))
        }
    }

    override fun getSuggestionHistoryFromServer(): Flow<Resource<List<Word>>> = flow {
        emit(Resource.Loading)
        try {
            val response = apiService.getMySuggestionHistory()
            val domains = response.map { it.toDomain(isSuggested = true) }
            wordDao.insertWords(domains.map { WordEntity.fromDomain(it) })
            emit(Resource.Success(domains))
        } catch (e: HttpException) {
            emit(Resource.Error(e, "Server denied access to proposals: ${e.code()}"))
        } catch (e: IOException) {
            emit(Resource.Error(e, "Offline. Utilizing local cache index."))
        } catch (e: Exception) {
            emit(Resource.Error(e, e.localizedMessage ?: "Failed to list suggestions."))
        }
    }
}
