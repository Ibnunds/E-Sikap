package com.ardclient.esikap.service

import com.ardclient.esikap.model.ApiResponse
import com.ardclient.esikap.model.COPModel
import com.ardclient.esikap.model.P3KModel
import com.ardclient.esikap.model.PHQCModel
import com.ardclient.esikap.model.SSCECModel
import com.ardclient.esikap.model.api.FileModel
import com.ardclient.esikap.model.api.UploadFileModel
import com.ardclient.esikap.model.api.UploadImageRequest
import com.ardclient.esikap.model.api.UploadModel
import com.ardclient.esikap.model.api.UserLoginRequest
import com.ardclient.esikap.model.api.UserLoginResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface ApiService {
    @POST("dev/upload")
    fun uploadImage(@Body requestBody: UploadImageRequest): Call<ApiResponse<Map<String, Any>>>

    // User
    @POST("user/login")
    fun userLogin(@Body body: UserLoginRequest) : Call<ApiResponse<UserLoginResponse>>

    // Upload PHQC
    @POST("upload/phqc")
    fun uploadPHQC(@Body body: UploadModel<PHQCModel>) : Call<ApiResponse<Any>>
    @POST("upload/phqc/single")
    fun uploadPHQCSingle(@Body body: UploadFileModel) : Call<ApiResponse<FileModel>>
    @POST("upload/phqc/delete/{id}")
    fun uploadPHQCDelete(@Path("id") id: String): Call<ApiResponse<Any>>

    // Upload P3K
    @POST("upload/p3k")
    fun uploadP3K(@Body body: UploadModel<P3KModel>) : Call<ApiResponse<Any>>
    @POST("upload/p3k/single")
    fun uploadP3KSingle(@Body body: UploadFileModel) : Call<ApiResponse<FileModel>>
    @POST("upload/p3k/delete/{id}")
    fun uploadP3KDelete(@Path("id") id: String): Call<ApiResponse<Any>>


    // Upload COP
    @POST("upload/cop")
    fun uploadCOP(@Body body: UploadModel<COPModel>) : Call<ApiResponse<Any>>
    @POST("upload/cop/single")
    fun uploadCOPSingle(@Body body: UploadFileModel) : Call<ApiResponse<FileModel>>
    @POST("upload/cop/delete/{id}")
    fun uploadCOPDelete(@Path("id") id: String): Call<ApiResponse<Any>>

    // Upload SCCEC
    @POST("upload/sscec")
    fun uploadSSCEC(@Body body: UploadModel<SSCECModel>) : Call<ApiResponse<Any>>
    @POST("upload/sscec/single")
    fun uploadSSCECSingle(@Body body: UploadFileModel) : Call<ApiResponse<FileModel>>
    @POST("upload/sscec/delete/{id}")
    fun uploadSSCECDelete(@Path("id") id: String): Call<ApiResponse<Any>>

    // Dev
    @POST("/dev")
    fun uploadDevCOP(@Body body: UploadModel<COPModel>) : Call<ApiResponse<Any>>
}