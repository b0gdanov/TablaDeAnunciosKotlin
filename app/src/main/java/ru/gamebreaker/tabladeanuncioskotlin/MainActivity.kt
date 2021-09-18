package ru.gamebreaker.tabladeanuncioskotlin
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import ru.gamebreaker.tabladeanuncioskotlin.accaunthelper.AccountHelper
import ru.gamebreaker.tabladeanuncioskotlin.act.DescriptionActivity
import ru.gamebreaker.tabladeanuncioskotlin.act.EditAdsAct
import ru.gamebreaker.tabladeanuncioskotlin.adapters.AdsRcAdapter
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityMainBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.DialogConst
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.DialogHelper
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.GoogleAccConst
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.MyLogConst
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.viewmodel.FirebaseViewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdsRcAdapter.Listener {
    private lateinit var tvAccount:TextView
    private lateinit var rootElement:ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
    val adapter = AdsRcAdapter(this)
    private val firebaseViewModel: FirebaseViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityMainBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
        initRecyclerView()
        initViewModel()
        firebaseViewModel.loadAllAds()
        bottomMenuOnClick()
        //Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
    }

    override fun onResume() {
        super.onResume()
        rootElement.mainContent.botNavView.selectedItemId = R.id.id_home
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

    private fun initViewModel(){
        firebaseViewModel.liveAdsData.observe(this, {
            adapter.updateAdapter(it)
            rootElement.mainContent.tvEmpty.visibility = if (it.isEmpty()) View.VISIBLE else View.GONE
        })
    }

    private fun init(){

        setSupportActionBar(rootElement.mainContent.toolbar) //указываем какой тулбар используется в активити (важно указать в начале)
        val toggle = ActionBarDrawerToggle(this, rootElement.drawerLayout, rootElement.mainContent.toolbar, R.string.open, R.string.close)
        rootElement.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        rootElement.navView.setNavigationItemSelectedListener(this)
        tvAccount = rootElement.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)

    }

    private fun bottomMenuOnClick() = with(rootElement){
        mainContent.botNavView.setOnNavigationItemSelectedListener { item ->
            when(item.itemId){
                R.id.id_new_ad -> {
                    val i = Intent(this@MainActivity, EditAdsAct::class.java) //передаём контекст на котором находимся и активити на которое хотим перейти
                    startActivity(i) //запускаем интент и новое активити
                }
                R.id.id_my_ads -> {
                    firebaseViewModel.loadMyAds()
                    mainContent.toolbar.title = getString(R.string.ad_my_ads)
                }
                R.id.id_favorites -> {
                    //Toast.makeText(this@MainActivity, "favs", Toast.LENGTH_SHORT).show()
                    firebaseViewModel.loadMyFavs()
                    mainContent.toolbar.title = "Избранное"
                }
                R.id.id_home -> {
                    firebaseViewModel.loadAllAds()
                    mainContent.toolbar.title = getString(R.string.def)
                }
            }
            true
        }
    }

    private fun initRecyclerView(){
        rootElement.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
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
            R.id.id_heroes ->{
                val text = textAddToast + getString(R.string.ad_heroes)
                Toast.makeText(this, text, length).show()
            }
            R.id.id_dungeons ->{
                val text = textAddToast + getString(R.string.ad_dungeons)
                Toast.makeText(this, text, length).show()
            }
            R.id.id_faction_war ->{
                val text = textAddToast + getString(R.string.ad_faction_war)
                Toast.makeText(this, text, length).show()
            }
            R.id.id_arena ->{
                val text = textAddToast + getString(R.string.ad_arena)
                Toast.makeText(this, text, length).show()
            }
            R.id.id_cb ->{
                val text = textAddToast + getString(R.string.ad_cb)
                Toast.makeText(this, text, length).show()
            }
            R.id.id_tower ->{
                val text = textAddToast + getString(R.string.ad_tower)
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
                if(mAuth.currentUser?.isAnonymous == true){
                    rootElement.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
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

    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            dialogHelper.accHelper.signInAnonymously(object : AccountHelper.Listener {
                override fun onComplete() {
                    tvAccount.text =
                        "Гость" // tvAccount.setText(R.string.text) или tvAccount.text = getString(R.string.text)
                }
            })
        } else if (user.isAnonymous) {
            tvAccount.text = "Гость"
        } else if (!user.isAnonymous) {
            tvAccount.text = user.email
        }
    }

    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
    }

    override fun onDeleteItem(ad: Ad) {
        firebaseViewModel.deleteItem(ad)
    }

    override fun onAdViewed(ad: Ad) {
        firebaseViewModel.adViewed(ad)
        val i = Intent(this, DescriptionActivity::class.java)
        i.putExtra("AD", ad)
        startActivity(i)
    }

    override fun onFavClicked(ad: Ad) {
        firebaseViewModel.onFavClick(ad)
    }
}