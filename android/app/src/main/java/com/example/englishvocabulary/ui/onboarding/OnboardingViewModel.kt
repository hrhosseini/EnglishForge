package com.example.englishvocabulary.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OnboardingViewModel(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _onboardingState = MutableStateFlow<Resource<User>?>(null)
    val onboardingState: StateFlow<Resource<User>?> = _onboardingState.asStateFlow()

    fun completeOnboarding(
        displayName: String,
        cefrLevel: String,
        interests: List<String>
    ) {
        viewModelScope.launch {
            userRepository.updateProfile(displayName, cefrLevel, interests).collect {
                _onboardingState.value = it
            }
        }
    }

    fun resetState() {
        _onboardingState.value = null
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val userRepository: UserRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(OnboardingViewModel::class.java)) {
                return OnboardingViewModel(userRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
