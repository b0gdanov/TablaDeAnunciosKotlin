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
        fillImageArray(ad)
    }

    private fun fillImageArray(ad: Ad){
        val listUris = listOf(ad.mainImage, ad.secondImage, ad.thirdImage)
        CoroutineScope(Dispatchers.Main).launch {
            val bitMapList = ImageManager.getBitmapFromUris(listUris)
            adapter.update(bitMapList as ArrayList<Bitmap>)
        }
    }
}