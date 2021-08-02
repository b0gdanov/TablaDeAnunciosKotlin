package ru.gamebreaker.tabladeanuncioskotlin.act

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityEditAdsBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialogs.DialogSpinnerHelper
import ru.gamebreaker.tabladeanuncioskotlin.utils.CityHelper
import android.content.pm.PackageManager
import android.graphics.Bitmap
import androidx.activity.result.ActivityResultLauncher
import com.fxn.utility.PermUtil
import ru.gamebreaker.tabladeanuncioskotlin.MainActivity
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImagePicker
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.adapters.ImageAdapter
import ru.gamebreaker.tabladeanuncioskotlin.model.DbManager
import ru.gamebreaker.tabladeanuncioskotlin.fragments.FragmentCloseInterface
import ru.gamebreaker.tabladeanuncioskotlin.fragments.ImageListFragment


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {

    var chooseImageFragment: ImageListFragment? = null
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    private val dbManager = DbManager()
    var editImagePos = 0
    var launcherMultiSelectImage: ActivityResultLauncher<Intent>? = null
    var launcherSingleSelectImage: ActivityResultLauncher<Intent>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()
        checkEditState()
    }

    private fun checkEditState(){
        if (isEditState()){
            fillViews(intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad)
        }
    }

    private fun isEditState(): Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillViews(ad: Ad) = with(rootElement) {
        tvFraction.text = ad.fraction
        tvHeroName.text = ad.heroName
        etTel.setText(ad.tel)
        etIndex.setText(ad.index)
        checkBoxWithSend.isChecked = ad.withSend.toBoolean()
        tvCategory.text = ad.category
        etTitle.setText(ad.title)
        etPrice.setText(ad.price)
        etDescription.setText(ad.description)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    //ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
                } else {

                    Toast.makeText(
                        this,
                        "Approve permissions to open Pix ImagePicker",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults) //добавил из-за подчеркивания
    }
    private fun init(){
        imageAdapter = ImageAdapter()
        rootElement.vpImages.adapter = imageAdapter
        launcherMultiSelectImage = ImagePicker.getLauncherForMultiSelectImages(this)
        launcherSingleSelectImage = ImagePicker.getLauncherForSingleImages(this)
    }

    //OnClicks
    fun onClickSelectFraction(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, rootElement.tvFraction)
        if(rootElement.tvHeroName.text.toString() != getString(R.string.select_hero_name)){
            rootElement.tvHeroName.text = getString(R.string.select_hero_name)
        }
    }

    fun onClickSelectHeroName(view: View){
        val selectedCountry = rootElement.tvFraction.text.toString()
        if (selectedCountry != getString(R.string.select_fraction)){
            val listCity = CityHelper.getAllCities(selectedCountry,this)
            dialog.showSpinnerDialog(this, listCity, rootElement.tvHeroName)

        }else{
            Toast.makeText(this, getString(R.string.warning_no_fraction_selected), Toast.LENGTH_LONG).show()
        }
    }

    fun onClickSelectCategory(view: View){

         val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
         dialog.showSpinnerDialog(this, listCategory, rootElement.tvCategory)


    }

    fun onClickGetImages(view: View){
        if(imageAdapter.mainArray.size == 0){
            ImagePicker.launcher(this, launcherMultiSelectImage, 3)

        } else {

            openChooseItemFragment(null)
            chooseImageFragment?.updateAdapterFromEdit(imageAdapter.mainArray)

        }

    }

    fun onClickPublish(view: View){

        dbManager.publishAd(fillAd())

    }

    private fun fillAd(): Ad{
        val ad: Ad
        rootElement.apply{

            ad = Ad(
                tvFraction.text.toString(),
                tvHeroName.text.toString(),
                etTel.text.toString(),
                etIndex.text.toString(),
                checkBoxWithSend.isChecked.toString(),
                tvCategory.text.toString(),
                etPrice.text.toString(),
                etTitle.text.toString(),
                etDescription.text.toString(),
                dbManager.db.push().key,
                dbManager.auth.uid
            )

        }
        return ad
    }

    override fun onFragmentClose(list : ArrayList<Bitmap>) {
        rootElement.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFragment = null
    }

    fun openChooseItemFragment(newList: ArrayList<String>?){

        chooseImageFragment = ImageListFragment(this, newList)
        rootElement.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()

    }
}