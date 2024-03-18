package com.ardclient.esikap.model.api

import com.google.gson.annotations.SerializedName

data class UploadImageRequest(
    @SerializedName("image")
    val image: String
)
