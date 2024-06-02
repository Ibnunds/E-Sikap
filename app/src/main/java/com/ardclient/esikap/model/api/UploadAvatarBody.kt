package com.ardclient.esikap.model.api

import com.google.gson.annotations.SerializedName

data class UploadAvatarBody(
    @SerializedName("image") val image: String
)
