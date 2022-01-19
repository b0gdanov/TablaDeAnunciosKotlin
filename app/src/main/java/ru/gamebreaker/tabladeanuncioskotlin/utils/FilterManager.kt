package ru.gamebreaker.tabladeanuncioskotlin.utils

import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.model.AdFilter
import java.lang.StringBuilder

object FilterManager {
    fun createFilter(ad: Ad): AdFilter{
        return AdFilter(
            ad.time,
            "${ad.category}_${ad.time}",

            "${ad.category}_${ad.fraction}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.fraction}_${ad.heroName}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.fraction}_${ad.heroName}_${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.category}_${ad.withSend}_${ad.time}",

            "${ad.fraction}_${ad.withSend}_${ad.time}",
            "${ad.fraction}_${ad.heroName}_${ad.withSend}_${ad.time}",
            "${ad.fraction}_${ad.heroName}_${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.index}_${ad.withSend}_${ad.time}",
            "${ad.withSend}_${ad.time}"
        )
    }

    fun getFilter(filter: String): String{
        val sBuilder = StringBuilder()
        val tempArray = filter.split("_")
        if (tempArray[0] != "empty") sBuilder.append("fraction_")
        if (tempArray[1] != "empty") sBuilder.append("heroName_")
        if (tempArray[2] != "empty") sBuilder.append("index_")
        sBuilder.append("withSent_time")

        return sBuilder.toString()
    }
}