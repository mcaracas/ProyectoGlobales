package com.mozama.impuestos.utils

import android.content.Context
import androidx.appcompat.app.AlertDialog

class DialogFragment {
    fun showDialogNeutral(context: Context, titulo: String, mensaje: String) {
        val builder = AlertDialog.Builder(context)
        builder.setTitle(titulo)
        builder.setMessage(mensaje)
        builder.setNeutralButton("Aceptar", null)

        val dialog = builder.create()
        dialog.show()
    }
}
