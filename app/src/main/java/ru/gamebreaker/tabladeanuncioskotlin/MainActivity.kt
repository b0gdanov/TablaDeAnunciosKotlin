package ru.gamebreaker.tabladeanuncioskotlin
import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.core.content.ContextCompat
import androidx.core.view.GravityCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.squareup.picasso.Picasso
import ru.gamebreaker.tabladeanuncioskotlin.accaunthelper.AccountHelper
import ru.gamebreaker.tabladeanuncioskotlin.act.DescriptionActivity
import ru.gamebreaker.tabladeanuncioskotlin.act.EditAdsAct
import ru.gamebreaker.tabladeanuncioskotlin.adapters.AdsRcAdapter
import ru.gamebreaker.tabladeanuncioskotlin.act.FilterActivity
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityMainBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.DialogConst
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.DialogHelper
import ru.gamebreaker.tabladeanuncioskotlin.dialoghelper.MyLogConst
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.utils.FilterManager
import ru.gamebreaker.tabladeanuncioskotlin.viewmodel.FirebaseViewModel

class MainActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener, AdsRcAdapter.Listener {
    private lateinit var tvAccount: TextView
    private lateinit var imAccount: ImageView
    private lateinit var binding :ActivityMainBinding
    private val dialogHelper = DialogHelper(this)
    val mAuth = Firebase.auth
    val adapter = AdsRcAdapter(this)
    lateinit var googleSignInLauncher: ActivityResultLauncher<Intent>
    lateinit var filterLauncher: ActivityResultLauncher<Intent>
    private val firebaseViewModel: FirebaseViewModel by viewModels()
    private var clearUpdate: Boolean = true
    private var currentCategory: String? = null
    private var filter: String? = "empty"
    private var filterDb: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
        initAds()
        initRecyclerView()
        initViewModel()
        bottomMenuOnClick()
        scrollListener()
        onActivityResultFilter()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.id_filter) {
            val i = Intent(this@MainActivity, FilterActivity::class.java).apply {
                putExtra(FilterActivity.FILTER_KEY, filter)
            }
            filterLauncher.launch(i)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onResume() {
        super.onResume()
        binding.mainContent.botNavView.selectedItemId = R.id.id_home
        binding.mainContent.adView2.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.mainContent.adView2.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.mainContent.adView2.destroy()
    }

    private fun initAds(){
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.mainContent.adView2.loadAd(adRequest)
    }

    private fun onActivityResult() {
        googleSignInLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
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
    }

    private fun onActivityResultFilter(){
        filterLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            if (it.resultCode == RESULT_OK){
                filter = it.data?.getStringExtra(FilterActivity.FILTER_KEY)!!
                //Log.d("MyLog", "Filter: $filter")
                //Log.d("MyLog", "getFilter: ${FilterManager.getFilter(filter!!)}")
                filterDb = FilterManager.getFilter(filter!!)
            } else if (it.resultCode == RESULT_CANCELED){
                filterDb = ""
                filter = "empty"
            }
        }
    }

    override fun onStart() {
        super.onStart()
        uiUpdate(mAuth.currentUser)
    }

    private fun initViewModel(){
        firebaseViewModel.liveAdsData.observe(this, {
            val list = getAdsByCategory(it)
            if (!clearUpdate){
                adapter.updateAdapter(list)
            } else {
                adapter.updateAdapterWithClear(list)
            }
            binding.mainContent.tvEmpty.visibility = if (adapter.itemCount == 0) View.VISIBLE else View.GONE
        })
    }

    private fun getAdsByCategory(list: ArrayList<Ad>): ArrayList<Ad>{
        val tempList = ArrayList<Ad>()
        tempList.addAll(list)
        if (currentCategory != getString(R.string.def)){
            tempList.clear()
            list.forEach {
                if (currentCategory == it.category)tempList.add(it)
            }
        }
        tempList.reverse()
        return tempList
    }

    private fun init(){
        currentCategory = getString(R.string.def)
        setSupportActionBar(binding.mainContent.toolbar) //указываем какой тулбар используется в активити (важно указать в начале)
        onActivityResult()
        navViewSettings()
        val toggle = ActionBarDrawerToggle(this, binding.drawerLayout, binding.mainContent.toolbar, R.string.open, R.string.close)
        binding.drawerLayout.addDrawerListener(toggle)
        toggle.syncState()
        binding.navView.setNavigationItemSelectedListener(this)
        tvAccount = binding.navView.getHeaderView(0).findViewById(R.id.tvAccountEmail)
        imAccount = binding.navView.getHeaderView(0).findViewById(R.id.imAccountImage)

    }

    private fun bottomMenuOnClick() = with(binding){
        mainContent.botNavView.setOnNavigationItemSelectedListener { item ->
            clearUpdate = true
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
                    currentCategory = getString(R.string.def)
                    filterDb?.let { firebaseViewModel.loadAllAdsFirstPage(it) }
                    mainContent.toolbar.title = getString(R.string.def)
                }
            }
            true
        }
    }

    private fun initRecyclerView(){
        binding.apply {
            mainContent.rcView.layoutManager = LinearLayoutManager(this@MainActivity)
            mainContent.rcView.adapter = adapter
        }
    }

    /*
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return super.onOptionsItemSelected(item)
    }
     */

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        clearUpdate = true
        val length = Toast.LENGTH_SHORT
        val textAddToast = getString(R.string.textAddToast)
        when(item.itemId){
            R.id.id_my_ads ->{
                val text = textAddToast + getString(R.string.ad_my_ads)
                Toast.makeText(this, text, length).show()
            }
            R.id.id_heroes ->{
                getAdsFromCat(getString(R.string.ad_heroes))

                //val catTime = "${getString(R.string.ad_heroes)}_0"
                //firebaseViewModel.loadAllAdsFromCat(catTime)

                //val text = textAddToast + getString(R.string.ad_heroes)
                //Toast.makeText(this, text, length).show()
            }
            R.id.id_dungeons ->{
                getAdsFromCat(getString(R.string.ad_dungeons))
            }
            R.id.id_faction_war ->{
                getAdsFromCat(getString(R.string.ad_faction_war))
            }
            R.id.id_arena ->{
                getAdsFromCat(getString(R.string.ad_arena))
            }
            R.id.id_cb ->{
                getAdsFromCat(getString(R.string.ad_cb))
            }
            R.id.id_tower ->{
                getAdsFromCat(getString(R.string.ad_tower))
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
                    binding.drawerLayout.closeDrawer(GravityCompat.START)
                    return true
                }
                val text = getString(R.string.sign_out_done)
                Toast.makeText(this, text, length).show()
                uiUpdate(null)
                mAuth.signOut()
                dialogHelper.accHelper.signOutGoogle()
            }
        }
        binding.drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun getAdsFromCat(cat: String){
        currentCategory = cat
        filterDb?.let { firebaseViewModel.loadAllAdsFromCat(cat, it) }
    }

    fun uiUpdate(user: FirebaseUser?) {
        if (user == null) {
            dialogHelper.accHelper.signInAnonymously(object : AccountHelper.Listener {
                override fun onComplete() {
                    tvAccount.text = getString(R.string.the_guest) // tvAccount.setText(R.string.text) или tvAccount.text = getString(R.string.text)
                    imAccount.setImageResource(R.drawable.ic_account_default)
                }
            })
        } else if (user.isAnonymous) {
            tvAccount.text = getString(R.string.the_guest)
            imAccount.setImageResource(R.drawable.ic_account_default)
        } else if (!user.isAnonymous) {
            tvAccount.text = user.email
            Picasso.get().load(user.photoUrl).into(imAccount)
        }
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

    //изменение цвета текста категорий из выдвижного меню
    private fun navViewSettings() = with(binding){
        val menu = navView.menu
        val adsCategory = menu.findItem(R.id.adsCat)
        val accCategory = menu.findItem(R.id.accCat)
        val spanAdsCat = SpannableString(adsCategory.title)
        val spanAccCat = SpannableString(accCategory.title)
        spanAdsCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.ic_main)), 0, adsCategory.title.length, 0)
        spanAccCat.setSpan(ForegroundColorSpan(ContextCompat.getColor(this@MainActivity, R.color.ic_main)), 0, accCategory.title.length, 0)
        adsCategory.title = spanAdsCat
        accCategory.title = spanAccCat
    }

    private fun scrollListener() = with(binding.mainContent){
        rcView.addOnScrollListener(object: RecyclerView.OnScrollListener(){
            override fun onScrollStateChanged(recView: RecyclerView, newState: Int) {
                super.onScrollStateChanged(recView, newState)
                if (!recView.canScrollVertically(SCROLL_DOWN) && newState == RecyclerView.SCROLL_STATE_IDLE){
                    clearUpdate = false
                    val adsList = firebaseViewModel.liveAdsData.value!!
                    if (adsList.isNotEmpty()){
                        getAdsFromCat(adsList)
                    }
                }
            }
        })
    }

    private fun getAdsFromCat(adsList: ArrayList<Ad>) {
        adsList[0].let {
            if (currentCategory == getString(R.string.def)) {
                filterDb?.let { it1 -> firebaseViewModel.loadAllAdsNextPage(it.time, it1) }
            } else {
                filterDb?.let { it1 ->
                    firebaseViewModel.loadAllAdsFromCatNextPage(it.category!!, it.time,
                        it1
                    )
                }
            }
        }
    }

    companion object{
        const val EDIT_STATE = "edit_state"
        const val ADS_DATA = "ads_data"
        const val SCROLL_DOWN = 1
    }
}