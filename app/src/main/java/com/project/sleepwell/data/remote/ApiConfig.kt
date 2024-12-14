package com.project.sleepwell.data.remote

import com.project.sleepwell.BuildConfig
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ApiConfig {
    companion object {
        fun getApiService(token: String): ApiServices {
            val client = buildClient(token)
            val retrofit = Retrofit.Builder()
                .baseUrl(BuildConfig.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build()
            return retrofit.create(ApiServices::class.java)
        }

        private fun buildClient(token: String): OkHttpClient {
            val loggingInterceptor = HttpLoggingInterceptor().apply {
                level = if (BuildConfig.DEBUG) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE
            }
            val authInterceptor = Interceptor { chain ->
                println("Token: $token") // Log token untuk debug
                val newRequest = chain.request().newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()

                // Cetak log untuk memastikan header Authorization benar
                println("Request URL: ${newRequest.url}")
                println("Authorization Header: ${newRequest.header("Authorization")}")
                chain.proceed(newRequest)
            }
            return OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .addInterceptor(authInterceptor)
                .build()
        }
    }
}