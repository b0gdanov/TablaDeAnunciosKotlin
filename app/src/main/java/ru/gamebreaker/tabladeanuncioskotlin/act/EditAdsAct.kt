package ru.gamebreaker.tabladeanuncioskotlin.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityEditAdsBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialogs.DialogSpinnerHelper
import ru.gamebreaker.tabladeanuncioskotlin.utils.CityHelper

class EditAdsAct : AppCompatActivity() {
    lateinit var rootElement: ActivityEditAdsBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        init()

    }
    private fun init(){

    }

    //OnClicks
    fun onClickSelectCountry(view: View){
        val listCountry = CityHelper.getAllCountries(this)

        dialog.showSpinnerDialog(this, listCountry)
    }
}