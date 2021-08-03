package ru.gamebreaker.tabladeanuncioskotlin.model

import java.io.Serializable

data class Ad(
    val fraction: String? = null,
    val heroName: String? = null,
    val tel: String? = null,
    val index: String? = null,
    val withSend: String? = null,
    val category: String? = null,
    val price: String? = null,
    val title: String? = null,
    val description: String? = null,
    val key: String? = null,
    val uid: String? = null
): Serializable