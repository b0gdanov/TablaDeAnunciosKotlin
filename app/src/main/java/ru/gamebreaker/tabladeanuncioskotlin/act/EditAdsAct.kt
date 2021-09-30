package ru.gamebreaker.tabladeanuncioskotlin.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityEditAdsBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialogs.DialogSpinnerHelper
import ru.gamebreaker.tabladeanuncioskotlin.utils.CityHelper
import android.graphics.Bitmap
import android.net.Uri
import androidx.viewpager2.widget.ViewPager2
import com.google.android.gms.tasks.OnCompleteListener
import ru.gamebreaker.tabladeanuncioskotlin.MainActivity
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImagePicker
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.adapters.ImageAdapter
import ru.gamebreaker.tabladeanuncioskotlin.model.DbManager
import ru.gamebreaker.tabladeanuncioskotlin.fragments.FragmentCloseInterface
import ru.gamebreaker.tabladeanuncioskotlin.fragments.ImageListFragment
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImageManager
import java.io.ByteArrayOutputStream


class EditAdsAct : AppCompatActivity(), FragmentCloseInterface {

    var chooseImageFragment: ImageListFragment? = null
    lateinit var binding: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()
    lateinit var imageAdapter: ImageAdapter
    private val dbManager = DbManager()
    var editImagePos = 0
    private var imageIndex = 0
    private var isEditState = false
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        init()
        checkEditState()
        imageChangeCounter()
    }

    private fun checkEditState(){
        isEditState = isEditState()
        if (isEditState){
            ad = intent.getSerializableExtra(MainActivity.ADS_DATA) as Ad
            if(ad != null) fillViews(ad!!)
        }
    }

    private fun isEditState(): Boolean{
        return intent.getBooleanExtra(MainActivity.EDIT_STATE, false)
    }

    private fun fillViews(ad: Ad) = with(binding) {
        spFractionValue.text = ad.fraction
        spHeroNameValue.text = ad.heroName
        etTelValue.setText(ad.tel)
        etIndexValue.setText(ad.index)
        cbWithSendValue.isChecked = ad.withSend.toBoolean()
        spCategoryValue.text = ad.category
        etTitleValue.setText(ad.title)
        etPriceValue.setText(ad.price)
        etDescriptionValue.setText(ad.description)
        ImageManager.fillImageArray(ad, imageAdapter)
    }

    private fun init(){
        imageAdapter = ImageAdapter()
        binding.vpImages.adapter = imageAdapter
    }

    //OnClicks
    fun onClickSelectFraction(view: View){
        val listCountry = CityHelper.getAllCountries(this)
        dialog.showSpinnerDialog(this, listCountry, binding.spFractionValue)
        if(binding.spHeroNameValue.text.toString() != getString(R.string.select_hero_name)){
            binding.spHeroNameValue.text = getString(R.string.select_hero_name)
        }
    }

    fun onClickSelectHeroName(view: View){
        val selectedCountry = binding.spFractionValue.text.toString()
        if (selectedCountry != getString(R.string.select_fraction)){
            val listCity = CityHelper.getAllCities(selectedCountry,this)
            dialog.showSpinnerDialog(this, listCity, binding.spHeroNameValue)

        }else{
            Toast.makeText(this, getString(R.string.warning_no_fraction_selected), Toast.LENGTH_LONG).show()
        }
    }

    fun onClickSelectCategory(view: View){

         val listCategory = resources.getStringArray(R.array.category).toMutableList() as ArrayList
         dialog.showSpinnerDialog(this, listCategory, binding.spCategoryValue)


    }

    fun onClickGetImages(view: View){
        if(imageAdapter.mainArray.size == 0){
            ImagePicker.getMultiImages(this, 3)

        } else {

            openChooseItemFragment(null)
            chooseImageFragment?.updateAdapterFromEdit(imageAdapter.mainArray)

        }

    }

    fun onClickPublish(view: View){
        ad = fillAd()
        if(isEditState){
            ad?.copy(key = ad?.key)?.let {
                dbManager.publishAd(it, onPublishFinish()) } //обновляем существующее объявление
        } else {
            //dbManager.publishAd(adTemp, onPublishFinish()) //публикуем новое объявление
            uploadImages()
        }
    }

    private fun  onPublishFinish(): DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish() {
                finish()
            }
        }
    }

    private fun fillAd(): Ad{
        val ad: Ad
        binding.apply{
            ad = Ad(
                spFractionValue.text.toString(),
                spHeroNameValue.text.toString(),
                etTelValue.text.toString(),
                etMailValue.text.toString(),
                etIndexValue.text.toString(),
                cbWithSendValue.isChecked.toString(),
                spCategoryValue.text.toString(),
                etPriceValue.text.toString(),
                etTitleValue.text.toString(),
                etDescriptionValue.text.toString(),
                "empty",
                "empty",
                "empty",
                dbManager.db.push().key,
                "0",
                dbManager.auth.uid
            )
        }
        return ad
    }

    override fun onFragmentClose(list : ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFragment = null
    }

    fun openChooseItemFragment(newList: ArrayList<Uri>?){
        chooseImageFragment = ImageListFragment(this)
        if(newList != null)chooseImageFragment?.resizeSelectedImages(newList, true, this)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()
    }

    private fun uploadImages(){
        if(imageAdapter.mainArray.size == imageIndex){
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])
        uploadImage(byteArray){
            dbManager
                .publishAd(
                    ad!!,
                    onPublishFinish()
                )
            nextImage(it.result.toString())
        }
    }

    private fun nextImage(uri: String){
        setImageUriToAd(uri)
        imageIndex++
        uploadImages()
    }

    private fun setImageUriToAd(uri: String){
        when(imageIndex){
            0 -> ad = ad?.copy(mainImage = uri)
            1 -> ad = ad?.copy(secondImage = uri)
            2 -> ad = ad?.copy(thirdImage = uri)
        }
    }

    private fun prepareImageByteArray(bitmap: Bitmap):ByteArray{
        val outStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 20, outStream)
        return outStream.toByteArray()
    }

    private fun uploadImage(byteArray: ByteArray, listener: OnCompleteListener<Uri>){
        val imStorageRef = dbManager.dbStorage
            .child(dbManager.auth.uid!!)
            .child("image_${System.currentTimeMillis()}")
        val upTask = imStorageRef.putBytes(byteArray)
        upTask.continueWithTask {
            task -> imStorageRef.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun imageChangeCounter(){
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                val imageCounter = "${position +1}/${binding.vpImages.adapter?.itemCount}"
                binding.tvImageCounter.text = imageCounter
            }
        })
    }
}