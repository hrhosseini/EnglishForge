package com.example.englishvocabulary.ui.word

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Word
import com.example.englishvocabulary.domain.usecase.AddCustomWordUseCase
import com.example.englishvocabulary.domain.usecase.GetDailyWordUseCase
import com.example.englishvocabulary.domain.usecase.GetPreviousWordsUseCase
import com.example.englishvocabulary.domain.usecase.SaveWordUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class WordViewModel(
    private val getDailyWordUseCase: GetDailyWordUseCase,
    private val addCustomWordUseCase: AddCustomWordUseCase,
    private val saveWordUseCase: SaveWordUseCase,
    private val getPreviousWordsUseCase: GetPreviousWordsUseCase
) : ViewModel() {

    // Daily Word State
    private val _dailyWordState = MutableStateFlow<Resource<Word>?>(null)
    val dailyWordState: StateFlow<Resource<Word>?> = _dailyWordState.asStateFlow()

    // Add Custom Word State
    private val _addCustomWordState = MutableStateFlow<Resource<Word>?>(null)
    val addCustomWordState: StateFlow<Resource<Word>?> = _addCustomWordState.asStateFlow()

    // Saved Words SQLite Cache lists
    val cachedWords: StateFlow<List<Word>> = getPreviousWordsUseCase.getCachedStream()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // Sync History network state
    private val _syncHistoryState = MutableStateFlow<Resource<List<Word>>?>(null)
    val syncHistoryState: StateFlow<Resource<List<Word>>?> = _syncHistoryState.asStateFlow()

    fun getDailyWord() {
        viewModelScope.launch {
            getDailyWordUseCase().collect {
                _dailyWordState.value = it
            }
        }
    }

    fun addCustomWord(wordText: String) {
        viewModelScope.launch {
            addCustomWordUseCase(wordText).collect {
                _addCustomWordState.value = it
            }
        }
    }

    fun toggleSave(wordId: Int, isCurrentlySaved: Boolean) {
        viewModelScope.launch {
            saveWordUseCase(wordId, isCurrentlySaved).collect { resource ->
                if (resource is Resource.Success) {
                    // Force refresh of states or let database flow handle updates
                }
            }
        }
    }

    fun syncWordsFromServer() {
        viewModelScope.launch {
            getPreviousWordsUseCase.refreshFromServer().collect {
                _syncHistoryState.value = it
            }
        }
    }

    fun clearAddWordState() {
        _addCustomWordState.value = null
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val getDailyWordUseCase: GetDailyWordUseCase,
        private val addCustomWordUseCase: AddCustomWordUseCase,
        private val saveWordUseCase: SaveWordUseCase,
        private val getPreviousWordsUseCase: GetPreviousWordsUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(WordViewModel::class.java)) {
                return WordViewModel(
                    getDailyWordUseCase,
                    addCustomWordUseCase,
                    saveWordUseCase,
                    getPreviousWordsUseCase
                ) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
