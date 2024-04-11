package com.ardclient.esikap.utils

import android.content.Context
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
            .setTitle("Hapus Data")
            .setMessage("Apakah anda yakin ingin menghapus data ini?")
            .setNegativeButton("Batalkan") { dialog, _ -> dialog.dismiss()}
            .setPositiveButton("Konfirmasi") { _, _ -> listener.onDeleteConfirmed() }
            .show()
    }

    fun showUploadDialog(context: Context, listener: DialogListener) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Upload Data")
            .setMessage("Apakah anda yakin ingin mengupload data ini?")
            .setNegativeButton("Batalkan") { dialog, _ -> dialog.dismiss()}
            .setPositiveButton("Konfirmasi") { _, _ -> listener.onConfirmed() }
            .show()
    }

    fun showNotSavedDialog(context: Context, listener: DialogListener) {
        MaterialAlertDialogBuilder(context)
            .setTitle("Konfirmasi")
            .setMessage("Anda belum menyimpan data ini, apakah anda yakin ingin kembali?")
            .setNegativeButton("Batalkan") { dialog, _ -> dialog.dismiss()}
            .setPositiveButton("Konfirmasi") { _, _ -> listener.onConfirmed() }
            .show()
    }
}