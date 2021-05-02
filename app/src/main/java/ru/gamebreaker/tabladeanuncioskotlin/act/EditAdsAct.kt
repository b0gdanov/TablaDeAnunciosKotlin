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
import com.fxn.utility.PermUtil
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImagePicker

import ru.gamebreaker.tabladeanuncioskotlin.adapters.ImageAdapter
import ru.gamebreaker.tabladeanuncioskotlin.database.DbManager
import ru.gamebreaker.tabladeanuncioskotlin.fragments.FragmentCloseInterface
import ru.gamebreaker.tabladeanuncioskotlin.fragments.ImageListFragment


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {

    var chooseImageFragment: ImageListFragment? = null
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    var editImagePos = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        ImagePicker.showSelectedImages(resultCode, requestCode, data, this)

    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS -> {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)
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
    }
    private fun init(){
        imageAdapter = ImageAdapter()
        rootElement.vpImages.adapter = imageAdapter
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

            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)

        } else {

            openChooseItemFragment(null)
            chooseImageFragment?.updateAdapterFromEdit(imageAdapter.mainArray)

        }

    }

    fun onClickPublish(view: View){
        val dbManager = DbManager()
        dbManager.publishAd()

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