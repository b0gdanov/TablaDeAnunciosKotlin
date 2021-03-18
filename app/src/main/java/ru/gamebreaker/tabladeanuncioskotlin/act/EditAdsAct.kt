package ru.gamebreaker.tabladeanuncioskotlin.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityEditAdsBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialogs.DialogSpinnerHelper
import ru.gamebreaker.tabladeanuncioskotlin.utils.CityHelper

class EditAdsAct : AppCompatActivity() {
    private lateinit var rootElement: ActivityEditAdsBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        rootElement = ActivityEditAdsBinding.inflate(layoutInflater)
        val view = rootElement.root
        setContentView(view)
        val listCountry = CityHelper.getAllCountries(this)
        val dialog = DialogSpinnerHelper()
        dialog.showSpinnerDialog(this, listCountry)
    }
}