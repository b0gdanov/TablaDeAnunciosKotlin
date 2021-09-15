package ru.gamebreaker.tabladeanuncioskotlin.model

import java.io.Serializable

data class Ad(
    val fraction: String? = null,
    val heroName: String? = null,
    val tel: String? = null,
    val email: String? = null,
    val index: String? = null,
    val withSend: String? = null,
    val category: String? = null,
    val price: String? = null,
    val title: String? = null,
    val description: String? = null,
    val mainImage: String? = null,
    val secondImage: String? = null,
    val thirdImage: String? = null,
    val key: String? = null,
    var favCounter: String = "0",
    val uid: String? = null,

    var isFav: Boolean = false,

    var viewsCounter: String = "0",
    var emailsCounter: String = "0",
    var callsCounter: String = "0"
): Serializable