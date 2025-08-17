package com.ee.vampirkoylu.ads

import android.app.Activity
import android.content.Context
import android.util.Log
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.OnUserEarnedRewardListener
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback

class RewardedAdManager(private val context: Context) {
    
    companion object {
        private const val TAG = "RewardedAdManager"
        // Test ID: ca-app-pub-3940256099942544/5224354917
        // Production ID: ca-app-pub-3681703552429739/8630643294
        private const val AD_UNIT_ID = "ca-app-pub-3681703552429739/8630643294" // Production ID
    }
    
    private var rewardedAd: RewardedAd? = null
    private var isLoading = false
    
    interface RewardedAdCallback {
        fun onAdLoaded()
        fun onAdFailedToLoad(error: String)
        fun onAdShown()
        fun onAdDismissed()
        fun onUserEarnedReward(rewardType: String, rewardAmount: Int)
        fun onAdFailedToShow(error: String)
    }
    
    fun loadRewardedAd(callback: RewardedAdCallback?) {
        if (isLoading) {
            Log.d(TAG, "Ad is already loading")
            return
        }
        
        if (rewardedAd != null) {
            Log.d(TAG, "Ad is already loaded")
            callback?.onAdLoaded()
            return
        }
        
        isLoading = true
        val adRequest = AdRequest.Builder().build()
        
        RewardedAd.load(
            context,
            AD_UNIT_ID,
            adRequest,
            object : RewardedAdLoadCallback() {
                override fun onAdLoaded(ad: RewardedAd) {
                    Log.d(TAG, "Rewarded ad loaded")
                    rewardedAd = ad
                    isLoading = false
                    callback?.onAdLoaded()
                }
                
                override fun onAdFailedToLoad(loadAdError: LoadAdError) {
                    Log.e(TAG, "Rewarded ad failed to load: ${loadAdError.message}")
                    rewardedAd = null
                    isLoading = false
                    callback?.onAdFailedToLoad(loadAdError.message)
                }
            }
        )
    }
    
    fun showRewardedAd(activity: Activity, callback: RewardedAdCallback?) {
        val ad = rewardedAd
        if (ad == null) {
            Log.e(TAG, "Rewarded ad is not loaded")
            callback?.onAdFailedToShow("Ad not loaded")
            return
        }
        
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdClicked() {
                Log.d(TAG, "Rewarded ad clicked")
            }
            
            override fun onAdDismissedFullScreenContent() {
                Log.d(TAG, "Rewarded ad dismissed")
                rewardedAd = null
                callback?.onAdDismissed()
                // Yeni reklam yükle
                loadRewardedAd(null)
            }
            
            override fun onAdFailedToShowFullScreenContent(adError: com.google.android.gms.ads.AdError) {
                Log.e(TAG, "Rewarded ad failed to show: ${adError.message}")
                rewardedAd = null
                callback?.onAdFailedToShow(adError.message)
            }
            
            override fun onAdImpression() {
                Log.d(TAG, "Rewarded ad impression")
            }
            
            override fun onAdShowedFullScreenContent() {
                Log.d(TAG, "Rewarded ad showed")
                callback?.onAdShown()
            }
        }
        
        ad.show(activity) { rewardItem ->
            val rewardType = rewardItem.type
            val rewardAmount = rewardItem.amount
            Log.d(TAG, "User earned reward: $rewardAmount $rewardType")
            callback?.onUserEarnedReward(rewardType, rewardAmount)
        }
    }
    
    fun isAdLoaded(): Boolean = rewardedAd != null
    
    fun destroy() {
        rewardedAd = null
        isLoading = false
    }
}
