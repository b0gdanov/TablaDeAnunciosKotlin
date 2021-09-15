package ru.gamebreaker.tabladeanuncioskotlin.act

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.net.toUri
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import ru.gamebreaker.tabladeanuncioskotlin.adapters.ImageAdapter
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityDescriptionBinding
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.utils.ImageManager

class DescriptionActivity : AppCompatActivity() {
    lateinit var binding: ActivityDescriptionBinding
    lateinit var adapter: ImageAdapter
    private var ad: Ad? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
        binding.fbTel.setOnClickListener { call() }
        binding.fbEmail.setOnClickListener { sendEmail() }
    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter = adapter
        }
        getIntentFromMainAct()
    }

    private fun getIntentFromMainAct(){
        ad = intent.getSerializableExtra("AD") as Ad
        if (ad != null) updateUI(ad!!)
    }

    private fun updateUI(ad: Ad) {
        fillImageArray(ad)
        fillTextViews(ad)
    }

    private fun fillTextViews(ad: Ad) = with(binding){
        tvTitle.text = ad.title
        tvDescription.text = ad.description
        tvFractionD.text = ad.fraction
        tvHeroNameD.text = ad.heroName
        tvEmailD.text = ad.email
        tvTelD.text = ad.tel
        tvIndexD.text = ad.index
        checkBoxWithSendD.text = isWithSent(ad.withSend.toBoolean())
        tvCategoryD.text = ad.category
        tvPriceD.text = ad.price
    }

    private fun  isWithSent(withSent: Boolean): String{
        return if (withSent) "Да" else "Нет"
    }

    private fun fillImageArray(ad: Ad){
        val listUris = listOf(ad.mainImage, ad.secondImage, ad.thirdImage)
        CoroutineScope(Dispatchers.Main).launch {
            val bitMapList = ImageManager.getBitmapFromUris(listUris)
            adapter.update(bitMapList as ArrayList<Bitmap>)
        }
    }

    private fun call(){
        val callUri = "tel:${ad?.tel}"
        val iCall = Intent(Intent.ACTION_DIAL)
        iCall.data = callUri.toUri()
        startActivity(iCall)
    }

    private fun sendEmail(){
        val iSendEmail = Intent(Intent.ACTION_SEND)
        iSendEmail.type = "message/rfc822"
        iSendEmail.apply {
            putExtra(Intent.EXTRA_EMAIL, arrayOf(ad?.email))
            putExtra(Intent.EXTRA_SUBJECT, "Объявление")
            putExtra(Intent.EXTRA_TEXT, "Меня интересует Ваше объявление!")
        }
        try {
            startActivity(Intent.createChooser(iSendEmail, "Открыть с помощью"))
        }catch (e: ActivityNotFoundException){

        }
    }
}