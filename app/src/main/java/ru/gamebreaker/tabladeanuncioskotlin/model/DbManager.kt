package ru.gamebreaker.tabladeanuncioskotlin.model

import android.util.Log
import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager{
    val db = Firebase.database.getReference(MAIN_NODE)
    val auth = Firebase.auth

    fun publishAd(ad: Ad, finishListener: FinishWorkListener){
        if(auth.uid != null)db.child(ad.key ?: "empty").child(auth.uid!!).child(AD_NODE).setValue(ad).addOnCompleteListener {
            //if (it.isSuccessful) //Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
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

    fun getMyAds(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/ad/uid").equalTo(auth.uid)
        readDataFromDb(query, readDataCallback)
    }

    fun getAllAds(readDataCallback: ReadDataCallback?){
        val query = db.orderByChild(auth.uid + "/ad/price")
        readDataFromDb(query, readDataCallback)
    }

    fun deleteAd(ad: Ad, listener: FinishWorkListener){
        if (ad.key == null || ad.uid == null) return
        db.child(ad.key).child(ad.uid).removeValue().addOnCompleteListener {
            if (it.isSuccessful) listener.onFinish()
            //else Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
        }
    }

    private fun readDataFromDb(query: Query, readDataCallback: ReadDataCallback?){
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            val adArray = ArrayList<Ad>()
            override fun onDataChange(snapshot: DataSnapshot) {
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
            }
            override fun onCancelled(error: DatabaseError) {}
        })
    }
    interface ReadDataCallback {
        fun readData(list: ArrayList<Ad>)
    }

    interface FinishWorkListener{
        fun onFinish()
    }

    companion object{
        const val AD_NODE = "ad"
        const val INFO_NODE = "info"
        const val MAIN_NODE = "main"
        const val FAVS_NODE = "favs"
    }
}