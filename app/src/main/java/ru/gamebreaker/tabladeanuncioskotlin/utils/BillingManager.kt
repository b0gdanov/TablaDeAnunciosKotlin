package ru.gamebreaker.tabladeanuncioskotlin.utils

import androidx.appcompat.app.AppCompatActivity
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.PurchasesUpdatedListener

class BillingManager(val act: AppCompatActivity) {
    private var billingClient: BillingClient? = null

    init {

    }

    private fun setUpBillingClient(){
        billingClient = BillingClient.newBuilder(act).setListener(getPurchaseListener()).enablePendingPurchases().build()
    }

    private fun getPurchaseListener(): PurchasesUpdatedListener{
        return PurchasesUpdatedListener {
            result, list ->
            run {
                if (result.responseCode == BillingClient.BillingResponseCode.OK) {
                    list?.get(0).let {  }
                }
            }
        }
    }
}