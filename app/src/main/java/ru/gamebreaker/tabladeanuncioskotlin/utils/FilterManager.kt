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
        val sBuilderNode = StringBuilder()
        val sBuilderFilter = StringBuilder()
        val tempArray = filter.split("_")
        if (tempArray[0] != "empty"){
            sBuilderNode.append("fraction_")
            sBuilderFilter.append("${tempArray[0]}_")
        }
        if (tempArray[1] != "empty"){
            sBuilderNode.append("heroName_")
            sBuilderFilter.append("${tempArray[1]}_")
        }
        if (tempArray[2] != "empty"){
            sBuilderNode.append("index_")
            sBuilderFilter.append("${tempArray[2]}_")
        }
        sBuilderFilter.append(tempArray[3])
        sBuilderNode.append("withSent_time")

        return "$sBuilderNode|$sBuilderFilter"
    }
}