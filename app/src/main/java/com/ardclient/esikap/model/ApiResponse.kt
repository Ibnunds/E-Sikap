package com.ardclient.esikap.model
import com.google.gson.annotations.SerializedName

data class ApiResponse(
    @SerializedName("status")
    val status: Int,
    @SerializedName("data")
    val data: Map<String, Any>?,
    @SerializedName("error")
    val error: List<Any>
)
