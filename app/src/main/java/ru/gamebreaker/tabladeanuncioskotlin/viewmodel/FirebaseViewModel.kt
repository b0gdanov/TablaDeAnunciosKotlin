package ru.gamebreaker.tabladeanuncioskotlin.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.gamebreaker.tabladeanuncioskotlin.model.Ad
import ru.gamebreaker.tabladeanuncioskotlin.model.DbManager

class FirebaseViewModel: ViewModel() {
    private  val dbManager = DbManager()
    val liveAdsData = MutableLiveData<ArrayList<Ad>>()
    fun loadAllAds(){
        dbManager.getAllAds(object: DbManager.ReadDataCallback{
            override fun readData(list: ArrayList<Ad>) {
                liveAdsData.value = list
            }
        })
    }

    fun loadMyAds(){
        dbManager.getMyAds(object: DbManager.ReadDataCallback{
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
}