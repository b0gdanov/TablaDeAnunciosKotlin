package ru.gamebreaker.tabladeanuncioskotlin.database

import ru.gamebreaker.tabladeanuncioskotlin.data.Ad

interface ReadDataCallback {
    fun readData(list: List<Ad>)
}