package com.ardclient.esikap.model

import com.google.gson.annotations.SerializedName

data class UploadResponse(
    @SerializedName("status")
    val status: String,
    @SerializedName("results")
    val results: Map<String, String>
)
