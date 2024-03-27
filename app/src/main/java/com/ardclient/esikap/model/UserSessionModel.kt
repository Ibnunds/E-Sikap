package com.ardclient.esikap.model

import com.google.gson.annotations.SerializedName

data class UserSessionModel(
    @SerializedName("userId")
    val userId: Int?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("userWilayah")
    val userWilayah: String?,
    @SerializedName("userLevel")
    val userLevel: String?,
)
