package com.example.englishvocabulary.core

sealed interface Resource<out T> {
    data class Success<out T>(val data: T) : Resource<T>
    data class Error(val exception: Throwable, val message: String? = null) : Resource<Nothing>
    object Loading : Resource<Nothing>
}
