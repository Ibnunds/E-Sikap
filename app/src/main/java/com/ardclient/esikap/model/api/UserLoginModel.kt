package com.ardclient.esikap.model.api

import com.google.gson.annotations.SerializedName

data class UserLoginRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String
)

data class UserLoginResponse(
    @SerializedName("id")
    val id: Int,
    @SerializedName("username")
    val username: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("nama")
    val nama: String,
    @SerializedName("level")
    val level: String,
    @SerializedName("aktif")
    val aktif: Int,
    @SerializedName("avatar")
    val avatar: String,
)
