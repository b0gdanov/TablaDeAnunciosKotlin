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
import ru.gamebreaker.tabladeanuncioskotlin.dialogs.RcViewDialogSpinnerAdapter
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
        updateImageCounter(0)
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

    fun onClickSelectCategory(view: View) {
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
        if(isFieldsEmpty()){
            showToast("Внимание! Все поля * должны быть заполнены!")
            return
        }
        binding.progressLayout.visibility = View.VISIBLE
        ad = fillAd()
        uploadImages()
    }

    private fun isFieldsEmpty(): Boolean = with(binding){
        return      spFractionValue.text.toString() == getString(R.string.select_fraction)
                ||  spHeroNameValue.text.toString() == getString(R.string.select_hero_name)
                ||  spCategoryValue.text.toString() == getString(R.string.select_category)
                ||  etTelValue.text.isEmpty()
                ||  etIndexValue.text.isEmpty()
                ||  etTitleValue.text.isEmpty()
                ||  etPriceValue.text.isEmpty()
                ||  etDescriptionValue.text.isEmpty()
                ||  imageAdapter.mainArray.size == 0
    }

    private fun  onPublishFinish(): DbManager.FinishWorkListener{
        return object : DbManager.FinishWorkListener{
            override fun onFinish(isDone: Boolean) {
                binding.progressLayout.visibility = View.GONE
                if (isDone)finish()
            }
        }
    }

    private fun fillAd(): Ad{
        val adTemp: Ad
        binding.apply{
            adTemp = Ad(
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
                ad?.mainImage ?: "empty",
                ad?.secondImage ?: "empty",
                ad?.thirdImage ?: "empty",
                ad?.key ?: dbManager.db.push().key,
                "0",
                dbManager.auth.uid,
                ad?.time ?: System.currentTimeMillis().toString()
            )
        }
        return adTemp
    }

    override fun onFragmentClose(list : ArrayList<Bitmap>) {
        binding.scrollViewMain.visibility = View.VISIBLE
        imageAdapter.update(list)
        chooseImageFragment = null
        updateImageCounter(binding.vpImages.currentItem)
    }

    fun openChooseItemFragment(newList: ArrayList<Uri>?){
        chooseImageFragment = ImageListFragment(this)
        if(newList != null)chooseImageFragment?.resizeSelectedImages(newList, true, this)
        binding.scrollViewMain.visibility = View.GONE
        val fm = supportFragmentManager.beginTransaction()
        fm.replace(R.id.place_holder, chooseImageFragment!!)
        fm.commit()
    }

    private fun uploadImages() {
        if (imageIndex == 3) {
            dbManager.publishAd(ad!!, onPublishFinish())
            return
        }
        val oldUrl = getUelFromAd()
        if (imageAdapter.mainArray.size > imageIndex) {
            val byteArray = prepareImageByteArray(imageAdapter.mainArray[imageIndex])
            if (oldUrl.startsWith("http")) {
                updateImage(byteArray, oldUrl) {
                    nextImage(it.result.toString())
                }
            } else {
                uploadImage(byteArray) {
                    //dbManager.publishAd(ad!!, onPublishFinish())
                    nextImage(it.result.toString())
                }
            }
        } else {
            if (oldUrl.startsWith("http")) {
                deleteImageByUrl(oldUrl) {
                    nextImage("empty")
                }
            } else {
                nextImage("empty")
            }
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

    private fun getUelFromAd(): String{
        return listOf(ad?.mainImage!!, ad?.secondImage!!, ad?.thirdImage!!)[imageIndex]
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

    private fun deleteImageByUrl(oldUrl: String, listener: OnCompleteListener<Void>) {
        dbManager.dbStorage.storage.getReferenceFromUrl(oldUrl).delete().addOnCompleteListener(listener)
    }
    private fun updateImage(byteArray: ByteArray, url: String, listener: OnCompleteListener<Uri>){
        val imStorageRef = dbManager.dbStorage.storage.getReferenceFromUrl(url)
        val upTask = imStorageRef.putBytes(byteArray)
        upTask.continueWithTask {
                task -> imStorageRef.downloadUrl
        }.addOnCompleteListener(listener)
    }

    private fun imageChangeCounter(){
        binding.vpImages.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateImageCounter(position)
            }
        })
    }

    private fun updateImageCounter(counter: Int){
        var index = 1
        val itemCount = binding.vpImages.adapter?.itemCount
        if (itemCount == 0) index == 0
        val imageCounter = "${counter + index}/$itemCount"
        binding.tvImageCounter.text = imageCounter
    }
}