package com.ardclient.esikap.model.api

import com.google.gson.annotations.SerializedName

data class UploadModel<T>(
    @SerializedName("data")
    val data: T?
)