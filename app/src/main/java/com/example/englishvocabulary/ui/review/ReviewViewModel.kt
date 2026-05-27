package com.example.englishvocabulary.ui.review

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.Review
import com.example.englishvocabulary.domain.usecase.GetDueReviewsUseCase
import com.example.englishvocabulary.domain.usecase.SubmitReviewAnswerUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ReviewViewModel(
    private val getDueReviewsUseCase: GetDueReviewsUseCase,
    private val submitReviewAnswerUseCase: SubmitReviewAnswerUseCase
) : ViewModel() {

    // Cached Review List from Room
    val cachedDueReviews: StateFlow<List<Review>> = getDueReviewsUseCase.getCachedDue()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _syncReviewsState = MutableStateFlow<Resource<List<Review>>?>(null)
    val syncReviewsState: StateFlow<Resource<List<Review>>?> = _syncReviewsState.asStateFlow()

    private val _answerState = MutableStateFlow<Resource<Review>?>(null)
    val answerState: StateFlow<Resource<Review>?> = _answerState.asStateFlow()

    fun syncReviewsFromServer() {
        viewModelScope.launch {
            getDueReviewsUseCase.refreshFromServer().collect {
                _syncReviewsState.value = it
            }
        }
    }

    fun submitAnswer(wordId: Int, scoreName: String) {
        viewModelScope.launch {
            submitReviewAnswerUseCase(wordId, scoreName).collect {
                _answerState.value = it
                if (it is Resource.Success) {
                    // Refresh due queue on successful answer record
                    syncReviewsFromServer()
                }
            }
        }
    }

    fun clearAnswerState() {
        _answerState.value = null
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val getDueReviewsUseCase: GetDueReviewsUseCase,
        private val submitReviewAnswerUseCase: SubmitReviewAnswerUseCase
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(ReviewViewModel::class.java)) {
                return ReviewViewModel(getDueReviewsUseCase, submitReviewAnswerUseCase) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
