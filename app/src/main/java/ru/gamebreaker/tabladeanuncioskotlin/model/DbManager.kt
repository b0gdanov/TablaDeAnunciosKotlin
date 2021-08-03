package ru.gamebreaker.tabladeanuncioskotlin.model

import android.widget.Toast
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.Query
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase

class DbManager{
    val db = Firebase.database.getReference("main")
    val auth = Firebase.auth

    fun publishAd(ad: Ad, finishListener: FinishWorkListener){
        if(auth.uid != null)db.child(ad.key ?: "empty").child(auth.uid!!).child("ad").setValue(ad).addOnCompleteListener {
            //if (it.isSuccessful) //Toast.makeText(this, "Welcome", Toast.LENGTH_SHORT).show()
            finishListener.onFinish()
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

    private fun readDataFromDb(query: Query, readDataCallback: ReadDataCallback?){
        query.addListenerForSingleValueEvent(object : ValueEventListener{
            val adArray = ArrayList<Ad>()
            override fun onDataChange(snapshot: DataSnapshot) {
                for (item in snapshot.children){
                    val ad = item.children.iterator().next().child("ad").getValue(Ad::class.java)
                    if (ad != null)adArray.add(ad)
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
}