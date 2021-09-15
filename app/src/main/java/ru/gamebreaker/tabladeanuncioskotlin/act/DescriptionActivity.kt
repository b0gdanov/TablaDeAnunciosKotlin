package ru.gamebreaker.tabladeanuncioskotlin.act

import android.graphics.Bitmap
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDescriptionBinding.inflate(layoutInflater)
        setContentView(binding.root)
        init()
    }

    private fun init(){
        adapter = ImageAdapter()
        binding.apply {
            viewPager.adapter = adapter
        }
        getIntentFromMainAct()
    }

    private fun getIntentFromMainAct(){
        val ad = intent.getSerializableExtra("AD") as Ad
        updateUI(ad)
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
        //tvEmailD.text = ad.
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
}