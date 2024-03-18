package com.ardclient.esikap.service

import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.api.UploadImageRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("dev/upload")
    fun uploadImage(@Body requestBody: UploadImageRequest): Call<ApiResponse>
}