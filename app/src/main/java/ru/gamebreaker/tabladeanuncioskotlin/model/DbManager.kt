package ru.gamebreaker.tabladeanuncioskotlin.model

import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage

class DbManager{
    val db = Firebase.database.getReference(MAIN_NODE)
    val dbStorage = Firebase.storage.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: Ad, finishListener: FinishWorkListener){
        if(auth.uid != null)db.child(ad.key ?: "empty").child(auth.uid!!).child(AD_NODE).setValue(ad).addOnCompleteListener {
            //if (it.isSuccessful) //Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
            val adFilter = AdFilter(ad.time, "${ad.category}_${ad.time}")
            db.child(ad.key ?: "empty").child(FILTER_NODE).setValue(adFilter).addOnCompleteListener {
                finishListener.onFinish()
            }
        }
    }

    fun publishClan(clan: Clan, finishListener: FinishWorkListener){
        if(auth.uid != null)db.child(clan.key ?: "empty")
            .child(auth.uid!!).child(CLAN_NODE).setValue(clan).addOnCompleteListener {
                finishListener.onFinish()
        }
    }

    fun adViewed(ad: Ad){
        var counter = ad.viewsCounter.toInt()
        counter++
        if(auth.uid != null)db.child(ad.key ?: "empty").child(INFO_NODE).setValue(InfoItem(counter.toString(), ad.emailsCounter,ad.callsCounter))
    }

    fun onFavClick(ad: Ad, listener: FinishWorkListener){
        if (ad.isFav){
            removeFromFavs(ad, listener)
        } else {
            addToFavs(ad, listener)
        }
    }

    private fun addToFavs(ad: Ad, listener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVS_NODE).child(uid).setValue(uid).addOnCompleteListener {
                    if (it.isSuccessful) listener.onFinish()
                }
            }
        }
    }

    private fun removeFromFavs(ad: Ad, listener: FinishWorkListener) {
        ad.key?.let {
            auth.uid?.let { uid ->
                db.child(it).child(FAVS_NODE).child(uid).removeValue().addOnCompleteListener {
                    if (it.isSuccessful) listener.onFinish()
                }
            }
        }
    }

    fun getMyAds(readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(true, query, readDataCallback, readDataClanCallback)
    }

    fun getMyFavs(readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild("/favs/${auth.uid}").equalTo(auth.uid)
        readDataFromDb(true, query, readDataCallback, readDataClanCallback)
    }

    fun getAllAdsFirstPage(readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild(GET_ALL_ADS).limitToLast(ADS_LIMIT)
        readDataFromDb(true, query, readDataCallback, readDataClanCallback)
    }

    fun getAllAdsNextPage(time: String, readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild(GET_ALL_ADS).endBefore(time).limitToLast(ADS_LIMIT)
        readDataFromDb(true, query, readDataCallback, readDataClanCallback)
    }

    fun getAllAdsFromCatFirstPage(cat: String, readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild(GET_ALL_CAT_ADS).startAt(cat).endAt(cat + "_\uf8ff").limitToLast(ADS_LIMIT)
        readDataFromDb(true, query, readDataCallback, readDataClanCallback)
    }

    fun getAllAdsFromCatNextPage(catTime: String, readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild(GET_ALL_CAT_ADS).endBefore(catTime).limitToLast(ADS_LIMIT)
        readDataFromDb(true, query, readDataCallback, readDataClanCallback)
    }

    fun getAllClansFirstPage(adOrClan: Boolean, readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild(CLAN_TIME).limitToLast(ADS_LIMIT)
        readDataFromDb(false, query, readDataCallback, readDataClanCallback)
    }

    fun getAllClansNextPage(adOrClan: Boolean, time: String, readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        val query = db.orderByChild(CLAN_TIME).endBefore(time).limitToLast(ADS_LIMIT)
        readDataFromDb(false, query, readDataCallback, readDataClanCallback)
    }

    fun deleteAd(ad: Ad, listener: FinishWorkListener){
        if (ad.key == null || ad.uid == null) return
        db.child(ad.key).child(ad.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) listener.onFinish()
            //else Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readDataFromDb(adOrClan: Boolean, query: Query, readDataCallback: ReadDataCallback?, readDataClanCallback: ReadDataClanCallback?){
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            val adArray = ArrayList<Ad>()
            val clanArray = ArrayList<Clan>()
            override fun onDataChange(snapshot: DataSnapshot) {
                if(adOrClan){
                    for (item in snapshot.children){

                        var ad: Ad? = null
                        item.children.forEach {
                            if (ad == null) ad = it.child(AD_NODE).getValue(Ad::class.java)
                        }
                        val infoItem = item.child(INFO_NODE).getValue(InfoItem::class.java)

                        val favCounter = item.child(FAVS_NODE).childrenCount
                        //Log.d("MyLog", "Counter favs: $favCounter")
                        val isFav = auth.uid?.let { item.child(FAVS_NODE).child(it).getValue(String::class.java) }
                        ad?.isFav = isFav != null
                        ad?.favCounter = favCounter.toString()

                        ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                        ad?.emailsCounter = infoItem?.emailsCounter ?: "0"
                        ad?.callsCounter = infoItem?.callsCounter ?: "0"
                        if (ad != null)adArray.add(ad!!)

                    }
                    readDataCallback?.readData(adArray)
                } else {
                    for (item in snapshot.children){

                        var clan: Clan? = null
                        item.children.forEach {
                            if (clan == null) clan = it.child(CLAN_NODE).getValue(Clan::class.java)
                        }

/*                        val infoItem = item.child(INFO_NODE).getValue(InfoItem::class.java)

                        val favCounter = item.child(FAVS_NODE).childrenCount
                        //Log.d("MyLog", "Counter favs: $favCounter")
                        val isFav = auth.uid?.let { item.child(FAVS_NODE).child(it).getValue(String::class.java) }
                        clan?.isFav = isFav != null
                        ad?.favCounter = favCounter.toString()

                        ad?.viewsCounter = infoItem?.viewsCounter ?: "0"
                        ad?.emailsCounter = infoItem?.emailsCounter ?: "0"
                        ad?.callsCounter = infoItem?.callsCounter ?: "0"*/

                        if (clan != null)clanArray.add(clan!!)
                    }
                    readDataClanCallback?.readDataClan(clanArray)
                }
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    interface ReadDataCallback {
        fun readData(list: ArrayList<Ad>)
    }

    interface ReadDataClanCallback{
        fun readDataClan(list: ArrayList<Clan>)
    }

    interface FinishWorkListener{
        fun onFinish()
    }

    companion object{
        const val AD_NODE = "ad"
        const val FILTER_NODE = "adFilter"
        const val INFO_NODE = "info"
        const val MAIN_NODE = "main"
        const val FAVS_NODE = "favs"
        const val ADS_LIMIT = 2
        const val GET_ALL_ADS = "/adFilter/time"
        const val GET_ALL_CAT_ADS = "/adFilter/catTime"

        const val CLAN_NODE = "clan"
        const val CLAN_TIME = "/clan/time"
    }
}