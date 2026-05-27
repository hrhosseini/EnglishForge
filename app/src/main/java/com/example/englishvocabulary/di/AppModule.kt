package com.example.englishvocabulary.di

import android.content.Context
import com.example.englishvocabulary.core.NetworkMonitor
import com.example.englishvocabulary.data.datastore.SettingsDataStore
import com.example.englishvocabulary.data.datastore.TokenDataStore
import com.example.englishvocabulary.data.local.AppDatabase
import com.example.englishvocabulary.data.remote.ApiService
import com.example.englishvocabulary.data.remote.AuthInterceptor
import com.example.englishvocabulary.data.remote.RetrofitModule
import com.example.englishvocabulary.data.repository.AuthRepositoryImpl
import com.example.englishvocabulary.data.repository.ReviewRepositoryImpl
import com.example.englishvocabulary.data.repository.UserRepositoryImpl
import com.example.englishvocabulary.data.repository.WordRepositoryImpl
import com.example.englishvocabulary.domain.repository.AuthRepository
import com.example.englishvocabulary.domain.repository.ReviewRepository
import com.example.englishvocabulary.domain.repository.UserRepository
import com.example.englishvocabulary.domain.repository.WordRepository
import com.example.englishvocabulary.domain.usecase.*

class AppModule(private val context: Context) {

    val appDatabase: AppDatabase by lazy {
        AppDatabase.getDatabase(context)
    }

    val tokenDataStore: TokenDataStore by lazy {
        TokenDataStore(context)
    }

    val settingsDataStore: SettingsDataStore by lazy {
        SettingsDataStore(context)
    }

    val authInterceptor: AuthInterceptor by lazy {
        AuthInterceptor(tokenDataStore, settingsDataStore)
    }

    val okHttpClient by lazy {
        RetrofitModule.provideOkHttpClient(authInterceptor)
    }

    val apiService: ApiService by lazy {
        RetrofitModule.provideApiService(okHttpClient)
    }

    // Repositories
    val authRepository: AuthRepository by lazy {
        AuthRepositoryImpl(apiService, tokenDataStore, appDatabase)
    }

    val wordRepository: WordRepository by lazy {
        WordRepositoryImpl(apiService, appDatabase)
    }

    val reviewRepository: ReviewRepository by lazy {
        ReviewRepositoryImpl(apiService, appDatabase)
    }

    val userRepository: UserRepository by lazy {
        UserRepositoryImpl(apiService)
    }

    // Use cases
    val loginUseCase by lazy { LoginUseCase(authRepository) }
    val registerUseCase by lazy { RegisterUseCase(authRepository) }
    val getDailyWordUseCase by lazy { GetDailyWordUseCase(wordRepository) }
    val addCustomWordUseCase by lazy { AddCustomWordUseCase(wordRepository) }
    val saveWordUseCase by lazy { SaveWordUseCase(wordRepository) }
    val getPreviousWordsUseCase by lazy { GetPreviousWordsUseCase(wordRepository) }
    val getDueReviewsUseCase by lazy { GetDueReviewsUseCase(reviewRepository) }
    val submitReviewAnswerUseCase by lazy { SubmitReviewAnswerUseCase(reviewRepository) }

    val networkMonitor by lazy {
        NetworkMonitor(context)
    }
}
