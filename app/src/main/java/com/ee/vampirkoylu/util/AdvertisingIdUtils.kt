package com.ee.vampirkoylu.util

import android.content.Context
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

object AdvertisingIdUtils {
    suspend fun getAdvertisingId(context: Context): String? = withContext(Dispatchers.IO) {
        try {
            val info = AdvertisingIdClient.getAdvertisingIdInfo(context)
            info?.id
        } catch (e: Exception) {
            null
        }
    }
}