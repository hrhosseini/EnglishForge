package com.example.englishvocabulary.data.remote

import com.example.englishvocabulary.data.datastore.SettingsDataStore
import com.example.englishvocabulary.data.datastore.TokenDataStore
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.HttpUrl
import okhttp3.HttpUrl.Companion.toHttpUrlOrNull
import okhttp3.Interceptor
import okhttp3.Response

class AuthInterceptor(
    private val tokenDataStore: TokenDataStore,
    private val settingsDataStore: SettingsDataStore
) : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()
        
        // 1. Swap host dynamic base url selection
        val dynamicBaseUrl = runBlocking { settingsDataStore.baseUrl.first() }
        val finalRequest = originalRequest.newBuilder().let { builder ->
            dynamicBaseUrl.toHttpUrlOrNull()?.let { dynamicUrl ->
                val newUrl = originalRequest.url.newBuilder()
                    .scheme(dynamicUrl.scheme)
                    .host(dynamicUrl.host)
                    .port(dynamicUrl.port)
                    .build()
                builder.url(newUrl)
            }
            
            // 2. Fetch authenticated Token header
            val token = runBlocking { tokenDataStore.accessToken.first() }
            if (!token.isNullOrEmpty()) {
                builder.addHeader("Authorization", "Bearer $token")
            }
            builder.build()
        }

        return chain.proceed(finalRequest)
    }
}
