package ru.gamebreaker.tabladeanuncioskotlin.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream
import java.util.*
import kotlin.collections.ArrayList
import android.R

import android.widget.TextView

import android.view.LayoutInflater

import android.view.ViewGroup

object CityHelper {
    fun getAllCountries(context: Context):ArrayList<String>{
        var tempArray = ArrayList<String>()
        try {
            val inputStream : InputStream = context.assets.open("countriesToCites.json")
            val size:Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val countiesNames = jsonObject.names()
            if (countiesNames != null){
                for (n in 0 until countiesNames.length()){
                    tempArray.add(countiesNames.getString(n))
                }
            }

        }catch (e:IOException){

        }
        return tempArray
    }

    fun getAllCities(country:String, context:Context):ArrayList<String>{
        var tempArray = ArrayList<String>()
        try {
            val inputStream : InputStream = context.assets.open("countriesToCites.json")
            val size:Int = inputStream.available()
            val bytesArray = ByteArray(size)
            inputStream.read(bytesArray)
            val jsonFile = String(bytesArray)
            val jsonObject = JSONObject(jsonFile)
            val cityNames = jsonObject.getJSONArray(country)

            for (n in 0 until cityNames.length()){
                tempArray.add(cityNames.getString(n))
            }


        }catch (e:IOException){

        }
        return tempArray
    }

    fun filterListData(list : ArrayList<String>, searchText : String?) : ArrayList<String>{
        val tempList = ArrayList<String>()
        tempList.clear()
        if (searchText == null){
            tempList.add("Безрезультатно")
            return tempList
        }
        for (selection: String in list) {
            if (selection.toLowerCase(Locale.ROOT).startsWith(searchText.toLowerCase(Locale.ROOT)))
                tempList.add(selection)
        }
        if (tempList.size == 0)tempList.add("Безрезультатно")
        return tempList
    }

}