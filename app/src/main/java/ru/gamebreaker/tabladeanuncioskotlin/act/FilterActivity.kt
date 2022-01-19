package ru.gamebreaker.tabladeanuncioskotlin.act

import android.content.Intent
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
        onClickClear()
        actionBarSettings()
        getFilter()
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) finish()
        return super.onOptionsItemSelected(item)
    }

    private fun getFilter() = with(binding){
        val filter = intent.getStringExtra(FILTER_KEY)
        if (filter != null && filter != "empty"){
            val filterArray = filter.split("_")
            if (filterArray[0] != "empty") spFractionValue.text = filterArray[0]
            if (filterArray[1] != "empty") spHeroNameValue.text = filterArray[1]
            if (filterArray[2] != "empty") etIndexValue.setText(filterArray[2])
            cbWithSendValue.isChecked = filterArray[3].toBoolean()
        }
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
            val i = Intent().apply {
                putExtra(FILTER_KEY, createFilter())
            }
            setResult(RESULT_OK, i)
            Log.d("MyLog", "Filter: ${createFilter()}")
            finish()
        }
    }

    private fun onClickClear() = with(binding){
        btClear.setOnClickListener {
            spFractionValue.text = getString(R.string.select_fraction)
            spHeroNameValue.text = getString(R.string.select_hero_name)
            etIndexValue.setText("")
            cbWithSendValue.isChecked = false
            setResult(RESULT_CANCELED)
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
            } else {
                sBuilder.append("empty")
                if (i != arrayTempFilter.size - 1) sBuilder.append("_")
            }
        }
        return sBuilder.toString()
    }

    fun actionBarSettings(){
        val ab = supportActionBar
        ab?.setDisplayHomeAsUpEnabled(true)
    }

    companion object{
        const val FILTER_KEY = "filter_key"
    }
}