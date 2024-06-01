package com.ardclient.esikap.model.api

import com.google.gson.annotations.SerializedName

data class KapalStatusResponse(
    @SerializedName("status") var status : Int? = null
)
