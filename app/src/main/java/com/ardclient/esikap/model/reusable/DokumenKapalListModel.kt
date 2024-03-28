package com.ardclient.esikap.model.reusable

data class DokumenKapalListModel(
    var title: String,
    var key: String,
    var checkedVal: String?,
    var docImage: String?,
    var note: String?,
    var needNote: Boolean = false
)
