package com.ardclient.esikap.utils

import android.view.View
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.viewbinding.ViewBinding
import com.ardclient.esikap.R
import com.google.android.material.textfield.TextInputLayout

object InputValidation {
    fun isAllFieldComplete(vararg fields: TextInputLayout): Boolean{
        for (field in fields) {
            if (field.editText?.text!!.isEmpty()) {
                field.error = "${field.hint.toString()} error."
                return false
            }else{
                field.isErrorEnabled = false
                field.error = null
            }
        }
        return true
    }


    fun isAllRadioFilled(vararg radios: RadioGroup) : Boolean {
        return radios.all {
            it.checkedRadioButtonId != -1
        }
    }

    fun getSelectedRadioGroupValue(binding: ViewBinding, radioGroup: RadioGroup): String {
        val checkedRadioButtonId = radioGroup.checkedRadioButtonId
        val checkedRadioButton = binding.root.findViewById<RadioButton>(checkedRadioButtonId)
        return checkedRadioButton.text.toString()
    }

    fun disabledAllInput(vararg inputs: TextInputLayout) {
        inputs.forEach { input ->
            input.editText?.isEnabled = false
        }
    }

    fun disableRadioGroup(radioGroup: RadioGroup) {
        for (i in 0 until radioGroup.childCount) {
            val radioButton = radioGroup.getChildAt(i) as? RadioButton
            radioButton?.isEnabled = false
        }
    }

    fun disabledAllRadio(vararg radios: RadioGroup) {
        radios.forEach { radio ->
            disableRadioGroup(radio)
        }
    }
}