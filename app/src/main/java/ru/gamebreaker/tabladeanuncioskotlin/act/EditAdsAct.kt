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
import com.fxn.pix.Pix
import android.content.pm.PackageManager
import android.util.Log
import com.fxn.utility.PermUtil
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImagePicker

import ru.gamebreaker.tabladeanuncioskotlin.adapters.ImageAdapter
import ru.gamebreaker.tabladeanuncioskotlin.fragments.FragmentCloseInterface
import ru.gamebreaker.tabladeanuncioskotlin.fragments.ImageListFragment
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImageManager


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {

    private var chooseImageFragment: ImageListFragment? = null
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    private var isImagesPermissionGranted = false
    private lateinit var imageAdapter: ImageAdapter
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

        if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_IMAGES) {

            if (data != null) {

                val returnValues = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)

                if (returnValues?.size!! > 1 && chooseImageFragment == null) {

                    openChooseItemFragment(returnValues)

                } else if (returnValues.size == 1 && chooseImageFragment == null) {

                    //imageAdapter.update(returnValues)
                    val tempList = ImageManager.getImageSize(returnValues[0])
                    Log.d("MyLog", "Image width : ${tempList[0]}")
                    Log.d("MyLog", "Image height : ${tempList[1]}")

                } else if (chooseImageFragment != null) {

                    chooseImageFragment?.updateAdapter(returnValues)

                }
            }
        } else if (resultCode == RESULT_OK && requestCode == ImagePicker.REQUEST_CODE_GET_SINGLE_IMAGE){

            if (data != null) {

                val uris = data.getStringArrayListExtra(Pix.IMAGE_RESULTS)
                chooseImageFragment?.setSingleImage(uris?.get(0)!!, editImagePos)
            }

        }
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
    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, rootElement.tvCountry)
        if(rootElement.tvCity.text.toString() != getString(R.string.select_city)){
            rootElement.tvCity.text = getString(R.string.select_city)
        }
    }

    fun onClickSelectCity(view: View){
        val selectedCountry = rootElement.tvCountry.text.toString()
        if (selectedCountry != getString(R.string.select_country)){
            val listCity = CityHelper.getAllCities(selectedCountry,this)
            dialog.showSpinnerDialog(this, listCity, rootElement.tvCity)

        }else{
            Toast.makeText(this, "No country selected", Toast.LENGTH_LONG).show()
        }
    }

    fun onClickGetImages(view: View){

        if(imageAdapter.mainArray.size == 0){

            ImagePicker.getImages(this, 3, ImagePicker.REQUEST_CODE_GET_IMAGES)

        } else {

            openChooseItemFragment(imageAdapter.mainArray)

        }

    }

    override fun onFragmentClose(list : ArrayList<String>) {
        rootElement.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFragment = null
    }

    private fun openChooseItemFragment(newList: ArrayList<String>){

        chooseImageFragment = ImageListFragment(this, newList)
        rootElement.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()

    }
}