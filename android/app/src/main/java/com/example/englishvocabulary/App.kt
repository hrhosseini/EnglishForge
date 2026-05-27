package com.example.englishvocabulary

import android.app.Application
import com.example.englishvocabulary.di.AppModule

class App : Application() {

    lateinit var appModule: AppModule
        private set

    override fun onCreate() {
        super.onCreate()
        appModule = AppModule(this)
    }
}
