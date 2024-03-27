package com.ardclient.esikap.model
import com.google.gson.annotations.SerializedName

data class ApiResponse<T>(
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val data: T?,
    @SerializedName("error")
    val error: List<Any>
)
