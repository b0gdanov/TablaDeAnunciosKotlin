package ru.gamebreaker.tabladeanuncioskotlin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import ru.gamebreaker.tabladeanuncioskotlin.act.EditAdsAct
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityMainBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.DialogConst
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.DialogHelper
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.GoogleAccConst
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.MyLogConst

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {
    private lateinit var tvAccount:TextView
    private lateinit var rootElement:ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu,menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE){
            //Log.d(MyLogConst.MY_LOG, MyLogConst.SIGN_IN_RESULT)
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                if (account != null){
                    Log.d(MyLogConst.MY_LOG, MyLogConst.API_ERROR)
                    dialogHelper.accHelper.signInFirebaseWithGoogle(account.idToken!!)
                }
            }catch (e:ApiException){
                Log.d(MyLogConst.MY_LOG, MyLogConst.API_ERROR + "${e.message}")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun init(){

        setSupportActionBar(rootElement.mainContent.toolbar) //указываем какой тулбар используется в активити (важно указать в начале)
        val toggle = ActionBarDrawerToggle(this, rootElement.drawerLayout, rootElement.mainContent.toolbar, R.string.open, R.string.close)
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener(this)
        tvAccount = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_new_ads){
            val i = Intent(this, EditAdsAct::class.java) //передаём контекст на котором находимся и активити на которое хотим перейти
            startActivity(i) //запускаем интент и новое активити
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {

        val length = Toast.LENGTH_SHORT
        val textAddToast = getString(R.string.textAddToast)

        when(item.itemId){

            R.id.id_my_ads ->{

                val text = textAddToast + getString(R.string.ad_my_ads)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_car ->{

                val text = textAddToast + getString(R.string.ad_car)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_pc ->{

                val text = textAddToast + getString(R.string.ad_pc)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_smart ->{

                val text = textAddToast + getString(R.string.ad_smartphone)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_dm ->{

                val text = textAddToast + getString(R.string.ad_dm)
                Toast.makeText(this, text, length).show()

            }
            R.id.id_sign_up ->{

                val text = textAddToast + getString(R.string.ac_sign_up)
                Toast.makeText(this, text, length).show()

                dialogHelper.createSignDialog(DialogConst.SIGN_UP_STATE)

            }
            R.id.id_sign_in ->{

                val text = textAddToast + getString(R.string.ac_sign_in)
                Toast.makeText(this, text, length).show()

                dialogHelper.createSignDialog(DialogConst.SIGN_IN_STATE)

            }
            R.id.id_sign_out ->{

                val text = getString(R.string.sign_out_done)
                Toast.makeText(this, text, length).show()

                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutGoogle()

            }

        }

        rootElement.drawerLayout.closeDrawer(GravityCompat.START)
        return true

    }

    fun uiUpdate(user:FirebaseUser?){
        tvAccount.text = if (user == null){
            resources.getString(R.string.not_reg)
        }else{
            user.email
        }
    }
}