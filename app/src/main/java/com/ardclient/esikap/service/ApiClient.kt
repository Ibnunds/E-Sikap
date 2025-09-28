package com.ardclient.esikap.service

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit


val okHttpClient: OkHttpClient? = OkHttpClient.Builder()
    .readTimeout(300, TimeUnit.SECONDS)
    .connectTimeout(300, TimeUnit.SECONDS)
    .build()
object RetrofitClient {
    private const val PROD_URL = "http://103.103.23.36/"
    private const val LOCAL_URL = "http://10.0.2.2:3001/"

    private const val BASE_URL = PROD_URL

    val retrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(okHttpClient!!)
            .build()
    }
}

object ApiClient {
    val apiService: ApiService by lazy {
        RetrofitClient.retrofit.create(ApiService::class.java)
    }
}