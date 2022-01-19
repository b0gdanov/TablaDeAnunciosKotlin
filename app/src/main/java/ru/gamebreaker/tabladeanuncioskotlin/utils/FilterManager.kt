package ru.gamebreaker.tabladeanuncioskotlin.utils

import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.model.AdFilter

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
}