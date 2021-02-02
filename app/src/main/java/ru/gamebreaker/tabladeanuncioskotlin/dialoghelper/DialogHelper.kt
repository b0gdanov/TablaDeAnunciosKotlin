package ru.gamebreaker.tabladeanuncioskotlin.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import ru.gamebreaker.tabladeanuncioskotlin.MainActivity
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.accaunthelper.AccountHelper
import ru.gamebreaker.tabladeanuncioskotlin.databinding.SignDialogBinding

class DialogHelper(act: MainActivity) {
    private val activity = act
    val accHelper = AccountHelper(act)

    fun createSignDialog(index: Int) {
        val builder = AlertDialog.Builder(activity)
        val rootDialogElement = SignDialogBinding.inflate(activity.layoutInflater)
        val view = rootDialogElement.root
        builder.setView(view)
        setDialogStare(index, rootDialogElement)
        val dialog = builder.create()
        rootDialogElement.btSignUpIn.setOnClickListener {
            setOnClickSignUpIn(index, rootDialogElement, dialog)
        }
        rootDialogElement.btForgetP.setOnClickListener {
            setOnClickResetPassword(rootDialogElement, dialog)
        }
        rootDialogElement.btGoogleSignIn.setOnClickListener {
            accHelper.signInWithGoogle()
        }
        dialog.show()
    }

    private fun setOnClickResetPassword(rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        if (rootDialogElement.edSignEmail.text.isNotEmpty()) {
            activity.mAuth.sendPasswordResetEmail(rootDialogElement.edSignEmail.text.toString()).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activity,R.string.email_reset_password_was_sent,Toast.LENGTH_LONG).show()
                    dialog?.dismiss()
                }
            }
        } else {
            rootDialogElement.btForgetP.text = activity.resources.getString(R.string.sent)
            rootDialogElement.tvDialogMessage.visibility = View.VISIBLE
            rootDialogElement.edSignPassword.visibility = View.GONE
            rootDialogElement.btSignUpIn.visibility = View.GONE
            rootDialogElement.btGoogleSignIn.visibility = View.GONE
        }
    }

    private fun setOnClickSignUpIn(index: Int, rootDialogElement: SignDialogBinding, dialog: AlertDialog?) {
        dialog?.dismiss()
        if (index == DialogConst.SIGN_UP_STATE) {
            accHelper.signUpWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
        } else {
            accHelper.signInWithEmail(
                rootDialogElement.edSignEmail.text.toString(),
                rootDialogElement.edSignPassword.text.toString()
            )
            Toast.makeText(activity,activity.resources.getString(R.string.sign_in_done),Toast.LENGTH_LONG).show()
            dialog?.dismiss()
        }
    }

    private fun setDialogStare(index: Int, rootDialogElement: SignDialogBinding) {
        if (index == DialogConst.SIGN_UP_STATE) {
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.sign_up)
            rootDialogElement.btSignUpIn.text =
                activity.resources.getString(R.string.sign_up_action)
        } else {
            rootDialogElement.tvSignTitle.text = activity.resources.getString(R.string.sign_in)
            rootDialogElement.btSignUpIn.text =
                activity.resources.getString(R.string.sign_in_action)
            rootDialogElement.btForgetP.visibility = View.VISIBLE
        }
    }

}