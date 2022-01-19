package ru.gamebreaker.tabladeanuncioskotlin.fragments

import android.app.Activity
import android.content.SharedPreferences
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.*
import com.google.android.gms.ads.interstitial.InterstitialAd
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback
import ru.gamebreaker.tabladeanuncioskotlin.R
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ListImageFragBinding
import ru.gamebreaker.tabladeanuncioskotlin.utils.BillingManager

open class BaseAdsFrag: Fragment(), InterstitialAdsClose {

    lateinit var adView: AdView
    var mInterstitialAd: InterstitialAd? = null
    private var pref: SharedPreferences? = null
    private var isPremiumUser = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        pref = activity?.getSharedPreferences(BillingManager.MAIN_PREF, AppCompatActivity.MODE_PRIVATE)
        isPremiumUser = pref?.getBoolean(BillingManager.REMOVE_ADS_PREF, false)!!
        isPremiumUser = true //убрать, это тест Премиум пользователя
        if (!isPremiumUser){
            initAds()
            loadInterAd()
        } else {
            adView.visibility = View.GONE
        }
    }


    override fun onResume() {
        super.onResume()
        adView.resume()
    }

    override fun onPause() {
        super.onPause()
        adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        adView.destroy()
    }

    private fun initAds(){
        MobileAds.initialize(activity as Activity)
        val adRequest = AdRequest.Builder().build()
        adView.loadAd(adRequest)
    }

    private fun loadInterAd(){
        val  adRequest = AdRequest.Builder().build()
        InterstitialAd.load(context as Activity, getString(R.string.ad_id_test_interstitial), adRequest, object : InterstitialAdLoadCallback(){
            override fun onAdLoaded(ad: InterstitialAd) {
                mInterstitialAd = ad
            }
        })
    }

    fun showInterstitialAd(){
        if (mInterstitialAd != null){
            mInterstitialAd?.fullScreenContentCallback = object : FullScreenContentCallback(){
                override fun onAdDismissedFullScreenContent() {
                    onClose()
                }

                override fun onAdFailedToShowFullScreenContent(p0: AdError) {
                    onClose()
                }
            }
            mInterstitialAd?.show(activity as Activity)
        } else {
            onClose()
        }
    }

    override fun onClose() {}
}