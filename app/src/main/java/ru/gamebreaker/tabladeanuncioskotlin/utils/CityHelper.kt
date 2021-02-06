package ru.gamebreaker.tabladeanuncioskotlin.utils

import android.content.Context
import org.json.JSONObject
import java.io.IOException
import java.io.InputStream

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
}