package ru.gamebreaker.tabladeanuncioskotlin.dialoghelper

import android.app.AlertDialog
import android.widget.Toast
import ru.gamebreaker.tabladeanuncioskotlin.MainActivity
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.accaunthelper.AccountHelper
import ru.gamebreaker.tabladeanuncioskotlin.databinding.SignDialogBinding

class DialogHelper(act:MainActivity) {
    private val activity = act
    private val accHelper = AccountHelper(act)

    fun createSignDialog(index:Int){
        val builder = AlertDialog.Builder(activity)
        val rootDialogElement = SignDialogBinding.inflate(activity.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        if (index == DialogConst.SIGN_UP_STATE){
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.sign_up)
            rootDialogElement.btSignUpIn.text = activity.resources.getString(R.string.sign_up_action)
        }else{
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.sign_in)
            rootDialogElement.btSignUpIn.text = activity.resources.getString(R.string.sign_in_action)
        }
        val dialog = builder.create()
        rootDialogElement.btSignUpIn.setOnClickListener {
            dialog.dismiss()
            if (index == DialogConst.SIGN_UP_STATE){
                accHelper.signUpWithEmail(rootDialogElement.edSignEmail.text.toString(), rootDialogElement.edSignPassword.text.toString())
            }else{
                accHelper.signInWithEmail(rootDialogElement.edSignEmail.text.toString(), rootDialogElement.edSignPassword.text.toString())
                Toast.makeText(activity, activity.resources.getString(R.string.sign_in_done), Toast.LENGTH_LONG).show()
            }
        }
        dialog.show()
    }
}