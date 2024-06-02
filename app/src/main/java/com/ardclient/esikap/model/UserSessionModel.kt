package com.ardclient.esikap.model

import com.google.gson.annotations.SerializedName

data class UserSessionModel(
    @SerializedName("userId")
    val userId: Int?,
    @SerializedName("userName")
    val userName: String?,
    @SerializedName("userPassword")
    val userPassword: String?,
    @SerializedName("name")
    val name: String?,
    @SerializedName("userLevel")
    val userLevel: String?,
    @SerializedName("userAktif")
    val userAktif: Int?,
    @SerializedName("userAvatar")
    var userAvatar: String?,
)
