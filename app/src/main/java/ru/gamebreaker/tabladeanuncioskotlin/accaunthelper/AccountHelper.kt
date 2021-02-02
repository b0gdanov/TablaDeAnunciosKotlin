package ru.gamebreaker.tabladeanuncioskotlin.accaunthelper

import android.util.Log
import android.widget.Toast
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import ru.gamebreaker.tabladeanuncioskotlin.MainActivity
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.constants.FirebaseAuthConstants
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.DialogHelper
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.GoogleAccConst
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.MyLogConst

class AccountHelper(act: MainActivity) {

    private val activity = act
    private lateinit var signInClient: GoogleSignInClient

    fun signUpWithEmail(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {

            activity.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    sendEmailVerification(task.result?.user!!)
                    activity.uiUpdate(task.result?.user)

                } else {
                    Toast.makeText(activity, activity.resources.getString(R.string.sign_up_error),Toast.LENGTH_LONG).show()

                    if (task.exception is FirebaseAuthUserCollisionException) {
                        val exception = task.exception as FirebaseAuthUserCollisionException

                        if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                            Toast.makeText(activity, FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE,Toast.LENGTH_LONG).show()
                        }
                    } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        val exception = task.exception as FirebaseAuthInvalidCredentialsException

                        if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                            Toast.makeText(activity, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG ).show()
                        }
                    }
                    if (task.exception is FirebaseAuthWeakPasswordException) {
                        val exception = task.exception as FirebaseAuthWeakPasswordException
                        Log.d(MyLogConst.MY_LOG, "Exception : ${exception.errorCode}") //1 эмулируем ошибку и смотрим лог
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                            Toast.makeText(activity, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG ).show() //сообщение пользователю (заменить на читаемое)
                        }
                    }
                }
            }
        }
    }

    fun signInWithEmail(email: String, password: String) {

        if (email.isNotEmpty() && password.isNotEmpty()) {

            activity.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->

                if (task.isSuccessful) {
                    activity.uiUpdate(task.result?.user)
                } else {
                    Toast.makeText(activity, activity.resources.getString(R.string.sign_in_error), Toast.LENGTH_LONG).show()
                    //Log.d(MyLogConst.MY_LOG, "sign In With Email Exception : ${task.exception}")

                    if (task.exception is FirebaseAuthUserCollisionException) {
                        //Log.d(MyLogConst.MY_LOG, "Exception : ${task.exception}")
                        val exception = task.exception as FirebaseAuthUserCollisionException
                        //Log.d(MyLogConst.MY_LOG, "sign In With Email Exception : ${exception.errorCode}")

                        if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                            Toast.makeText(activity, FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE, Toast.LENGTH_LONG).show()
                        } else if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                            Toast.makeText(activity, FirebaseAuthConstants.ERROR_WRONG_PASSWORD,Toast.LENGTH_LONG).show()
                        }
                    }
                }
            }
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activity.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(activity, gso)
    }

    fun signInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        activity.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
    }

    fun signInFirebaseWithGoogle(token:String){
        val credential = GoogleAuthProvider.getCredential(token, null)
        activity.mAuth.signInWithCredential(credential).addOnCompleteListener { task->
            if (task.isSuccessful){
                Toast.makeText(activity, R.string.sign_in_done, Toast.LENGTH_LONG).show()
                activity.uiUpdate(task.result?.user)
            } else{
                Log.d(MyLogConst.MY_LOG, "Google Sign in Exception : ${task.exception}")
            }
        }
    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(
                    activity,
                    activity.resources.getString(R.string.send_verification_email_done),
                    Toast.LENGTH_LONG
                ).show()
            } else {
                Toast.makeText(
                    activity,
                    activity.resources.getString(R.string.send_verification_email_error),
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

}