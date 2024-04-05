package com.ardclient.esikap.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val okHttpClient: OkHttpClient? = OkHttpClient.Builder()
    .readTimeout(600, TimeUnit.SECONDS)
    .connectTimeout(600, TimeUnit.SECONDS)
    .build()
object RetrofitClient {
    private const val BASE_URL = "http://10.0.2.2:3001/"

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient)
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}