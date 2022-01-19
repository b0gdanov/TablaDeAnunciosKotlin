package ru.gamebreaker.tabladeanuncioskotlin.act

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ActivityFilterBinding
import ru.gamebreaker.tabladeanuncioskotlin.dialogs.DialogSpinnerHelper
import ru.gamebreaker.tabladeanuncioskotlin.utils.CityHelper
import java.lang.StringBuilder

class FilterActivity : AppCompatActivity() {
    lateinit var binding: ActivityFilterBinding
    private val dialog = DialogSpinnerHelper()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFilterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        onClickSelectFraction()
        onClickSelectHeroName()
        onClickDone()
        actionBarSettings()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    //OnClicks
    private fun onClickSelectFraction() = with(binding){
        spFractionValue.setOnClickListener {
            val listCountry = CityHelper.getAllCountries(this@FilterActivity)
            dialog.showSpinnerDialog(this@FilterActivity, listCountry, spFractionValue)
            if(spHeroNameValue.text.toString() != getString(R.string.select_hero_name)){
                spHeroNameValue.text = getString(R.string.select_hero_name)
            }
        }
    }

    private fun onClickSelectHeroName() = with(binding){
        spHeroNameValue.setOnClickListener {
            val selectedCountry = spFractionValue.text.toString()
            if (selectedCountry != getString(R.string.select_fraction)){
                val listCity = CityHelper.getAllCities(selectedCountry,this@FilterActivity)
                dialog.showSpinnerDialog(this@FilterActivity, listCity, spHeroNameValue)

            }else{
                Toast.makeText(this@FilterActivity, getString(R.string.warning_no_fraction_selected), Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun onClickDone() = with(binding){
        btDone.setOnClickListener {
            Log.d("MyLog", "Filter: ${createFilter()}")
        }
    }

    private fun createFilter(): String = with(binding){
        val sBuilder = StringBuilder()
        val arrayTempFilter = listOf(
            spFractionValue.text,
            spHeroNameValue.text,
            etIndexValue.text,
            cbWithSendValue.isChecked.toString()
        )
        for ((i, s) in arrayTempFilter.withIndex()){
            if (s != getString(R.string.select_fraction) && s != getString(R.string.select_hero_name) && s.isNotEmpty()){
                sBuilder.append(s)
                if (i != arrayTempFilter.size - 1) sBuilder.append("_")
            }
        }
        return sBuilder.toString()
    }

    fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }
}