package ru.gamebreaker.tabladeanuncioskotlin.dialoghelper

import android.app.Activity
import android.app.AlertDialog
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ProgressDialogLayoutBinding
import ru.gamebreaker.tabladeanuncioskotlin.databinding.SignDialogBinding

object ProgressDialog {

    fun createProgressDialog(activity: Activity) : AlertDialog {

        val builder = AlertDialog.Builder(activity)
        val rootDialogElement = ProgressDialogLayoutBinding.inflate(activity.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }


}