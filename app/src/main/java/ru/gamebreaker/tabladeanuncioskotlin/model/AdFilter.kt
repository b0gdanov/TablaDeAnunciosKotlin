package ru.gamebreaker.tabladeanuncioskotlin.model

data class AdFilter(
    val time: String? = null,
    val cat_time: String? = null,
    //Filter with category
    val cat_fraction_withSent_time: String? = null,
    val cat_fraction_heroName_withSent_time: String? = null,
    val cat_fraction_heroName_index_withSent_time: String? = null,
    val cat_index_withSent_time: String? = null,
    val cat_withSent_time: String? = null,
    //Filter without category
    val fraction_withSent_time: String? = null,
    val fraction_heroName_withSent_time: String? = null,
    val fraction_heroName_index_withSent_time: String? = null,
    val index_withSent_time: String? = null,
    val withSent_time: String? = null
)
