package ru.gamebreaker.tabladeanuncioskotlin.model

import java.io.Serializable

data class Clan(
    val name: String? = null,
    val level: String? = null,
    val tournament: String? = null,
    val description: String? = null,
    val key: String? = null,
    val uid: String? = null,
    val tel: String? = null,
    val email: String? = null,
    val time: String = "0",

    var isFav: Boolean = false,
    var favCounter: String = "0",

    var viewsCounter: String = "0",
    var emailsCounter: String = "0",
    var callsCounter: String = "0"
): Serializable
