package com.ardclient.esikap.utils

import com.google.android.material.textfield.TextInputLayout

object InputValidation {
    fun isAllFieldComplete(vararg fields: TextInputLayout): Boolean{
        for (field in fields) {
            if (field.editText?.text!!.isEmpty()) {
                field.error = "${field.hint.toString()} tidak boleh kosong."
                return false
            }else{
                field.isErrorEnabled = false
                field.error = null
            }
        }
        return true
    }
}