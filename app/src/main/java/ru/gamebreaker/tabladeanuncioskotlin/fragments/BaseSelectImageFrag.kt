package ru.gamebreaker.tabladeanuncioskotlin.fragments

import android.app.Activity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import ru.gamebreaker.tabladeanuncioskotlin.databinding.ListImageFragBinding

open class BaseSelectImageFrag : Fragment() {

    lateinit var binding : ListImageFragBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        binding = ListImageFragBinding.inflate(layoutInflater)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initAds()
    }

    override fun onResume() {
        super.onResume()
        binding.adView.resume()
    }

    override fun onPause() {
        super.onPause()
        binding.adView.pause()
    }

    override fun onDestroy() {
        super.onDestroy()
        binding.adView.destroy()
    }

    private fun initAds(){

        MobileAds.initialize(activity as Activity)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)

    }

}