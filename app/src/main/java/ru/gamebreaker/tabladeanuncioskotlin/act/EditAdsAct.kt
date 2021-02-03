package ru.gamebreaker.tabladeanuncioskotlin.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.sax.RootElement
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityEditAdsBinding

class EditAdsAct : AppCompatActivity() {
    private lateinit var rootElement: ActivityEditAdsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
    }
}