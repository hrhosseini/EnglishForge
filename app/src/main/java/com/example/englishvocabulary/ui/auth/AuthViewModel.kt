package com.example.englishvocabulary.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.englishvocabulary.core.Resource
import com.example.englishvocabulary.domain.model.User
import com.example.englishvocabulary.domain.usecase.LoginUseCase
import com.example.englishvocabulary.domain.usecase.RegisterUseCase
import com.example.englishvocabulary.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AuthViewModel(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _loginState = MutableStateFlow<Resource<User>?>(null)
    val loginState: StateFlow<Resource<User>?> = _loginState.asStateFlow()

    private val _registerState = MutableStateFlow<Resource<User>?>(null)
    val registerState: StateFlow<Resource<User>?> = _registerState.asStateFlow()

    val isLoggedIn: StateFlow<Boolean> = MutableStateFlow(false).apply {
        viewModelScope.launch {
            authRepository.isLoggedIn.collect { value = it }
        }
    }

    fun login(email: String, password: String) {
        viewModelScope.launch {
            loginUseCase(email, password).collect {
                _loginState.value = it
            }
        }
    }

    fun register(
        email: String,
        password: String,
        displayName: String,
        cefrLevel: String,
        interests: List<String>
    ) {
        viewModelScope.launch {
            registerUseCase(email, password, displayName, cefrLevel, interests).collect {
                _registerState.value = it
            }
        }
    }

    fun resetStates() {
        _loginState.value = null
        _registerState.value = null
    }

    fun logout() {
        viewModelScope.launch {
            authRepository.logout()
        }
    }

    @Suppress("UNCHECKED_CAST")
    class Factory(
        private val loginUseCase: LoginUseCase,
        private val registerUseCase: RegisterUseCase,
        private val authRepository: AuthRepository
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(AuthViewModel::class.java)) {
                return AuthViewModel(loginUseCase, registerUseCase, authRepository) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
}
