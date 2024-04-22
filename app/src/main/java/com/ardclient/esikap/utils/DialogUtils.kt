package com.ardclient.esikap.utils

import android.content.Context
import com.ardclient.esikap.R
import com.google.android.material.dialog.MaterialAlertDialogBuilder

object DialogUtils {

    interface OnDeleteConfirmListener {
        fun onDeleteConfirmed()
    }

    interface DialogListener {
        fun onConfirmed()
    }
    fun showDeleteDialog(context: Context, listener: OnDeleteConfirmListener) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.delete_data_title))
            .setMessage(context.getString(R.string.dialog_delete_message))
            .setNegativeButton(context.getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss()}
            .setPositiveButton(context.getString(R.string.confirm_button)) { _, _ -> listener.onDeleteConfirmed() }
            .show()
    }

    fun showUploadDialog(context: Context, listener: DialogListener) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.upload_data_title))
            .setMessage(context.getString(R.string.dialog_upload_message))
            .setNegativeButton(context.getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss()}
            .setPositiveButton(context.getString(R.string.confirm_button)) { _, _ -> listener.onConfirmed() }
            .show()
    }

    fun showNotSavedDialog(context: Context, listener: DialogListener) {
        MaterialAlertDialogBuilder(context)
            .setTitle(context.getString(R.string.confirm_button))
            .setMessage(context.getString(R.string.dialog_back_confirm_message))
            .setNegativeButton(context.getString(R.string.cancel_button)) { dialog, _ -> dialog.dismiss()}
            .setPositiveButton(context.getString(R.string.confirm_button)) { _, _ -> listener.onConfirmed() }
            .show()
    }
}