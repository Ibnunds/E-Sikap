package com.ardclient.esikap.model.api

import com.google.gson.annotations.SerializedName

data class UploadModel<T>(
    @SerializedName("data")
    val data: T?,
    @SerializedName("file")
    val file: List<FileModel>?
)

data class UploadFileModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("image")
    val image: String?,
    @SerializedName("docId")
    val id: Int,
)

data class FileModel(
    @SerializedName("key")
    val key: String,
    @SerializedName("image")
    val image: String
)