package ru.gamebreaker.tabladeanuncioskotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.model.Clan
import ru.gamebreaker.tabladeanuncioskotlin.model.DbManager

class FirebaseViewModel: ViewModel() {
    private  val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()
    val liveClansData = MutableLiveData<ArrayList<Clan>>()

    fun loadAllAdsFirstPage(){
        dbManager.getAllAdsFirstPage(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsNextPage(time: String){
        dbManager.getAllAdsNextPage(time, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCat(cat: String){
        dbManager.getAllAdsFromCatFirstPage(cat, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllAdsFromCatNextPage(catTime: String){
        dbManager.getAllAdsFromCatNextPage(catTime, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllClans(){
        dbManager.getAllClans(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadAllClansNext(time: String){
        dbManager.getAllClansNext(time, object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun onFavClick(ad: Ad){
        dbManager.onFavClick(ad, object: DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveAdsData.value
                val pos = updatedList?.indexOf(ad)
                if (pos != -1){
                    pos?.let {
                        val favCounter = if (ad.isFav) ad.favCounter.toInt() - 1 else ad.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(isFav = !ad.isFav, favCounter = favCounter.toString())
                    }
                }
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun onFavClickClan(clan: Clan){
        dbManager.onFavClickClan(clan, object: DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveClansData.value
                val pos = updatedList?.indexOf(clan)
                if (pos != -1){
                    pos?.let {
                        val favCounter = if (clan.isFav) clan.favCounter.toInt() - 1 else clan.favCounter.toInt() + 1
                        updatedList[pos] = updatedList[pos].copy(isFav = !clan.isFav, favCounter = favCounter.toString())
                    }
                }
                liveClansData.postValue(updatedList)
            }
        })
    }

    fun adViewed(ad: Ad){
        dbManager.adViewed(ad)
    }

    fun clanViewed(clan: Clan){
        dbManager.clanViewed(clan)
    }

    fun loadMyAds(){
        dbManager.getMyAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyFavs(){
        dbManager.getMyFavs(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun deleteItem(ad: Ad){
        dbManager.deleteAd(ad, object : DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveAdsData.value
                updatedList?.remove(ad)
                liveAdsData.postValue(updatedList)
            }
        })
    }

    fun deleteItemClan(clan: Clan){
        dbManager.deleteClan(clan, object : DbManager.FinishWorkListener{
            override fun onFinish() {
                val updatedList = liveClansData.value
                updatedList?.remove(clan)
                liveClansData.postValue(updatedList)
            }
        })
    }
}