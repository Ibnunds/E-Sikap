package com.ardclient.esikap.service

import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.model.api.UploadImageRequest
import com.ardclient.esikap.model.api.UploadModel
import com.ardclient.esikap.model.api.UserLoginRequest
import com.ardclient.esikap.model.api.UserLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("dev/upload")
    fun uploadImage(@Body requestBody: UploadImageRequest): Call<ApiResponse<Map<String, Any>>>

    // User
    @POST("user/login")
    fun userLogin(@Body body: UserLoginRequest) : Call<ApiResponse<UserLoginResponse>>

    // Upload
    @POST("upload/phqc")
    fun uploadPHQC(@Body body: UploadModel<PHQCModel>) : Call<ApiResponse<Any>>
}