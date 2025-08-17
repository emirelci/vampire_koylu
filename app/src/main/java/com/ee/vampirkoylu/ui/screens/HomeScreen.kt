package com.ee.vampirkoylu.ui.screens

import BillingClientWrapper
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import android.app.Activity
import android.content.Context
import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import com.ee.vampirkoylu.StoreManager
import com.ee.vampirkoylu.ads.RewardedAdManager
import com.ee.vampirkoylu.ui.theme.LocalWindowWidthSizeClass
import com.ee.vampirkoylu.util.WindowWidthSizeClass
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.launch
import java.util.Locale
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.component.RoleInfoItem
import com.ee.vampirkoylu.ui.navigation.Screen
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.shine_gold
import com.ee.vampirkoylu.util.LanguageManager

@Composable
fun MainScreenBackground(content: @Composable () -> Unit) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(id = R.drawable.main_screen_bg),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
        
        Surface(
            modifier = Modifier
                .fillMaxSize()
                .padding(6.dp),
            color = Color.Transparent
        ) {
            content()
        }
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    billingClientWrapper: BillingClientWrapper,
    storeManager: StoreManager,
    activity: Activity
) {
    val widthSizeClass = LocalWindowWidthSizeClass.current
    if (widthSizeClass == WindowWidthSizeClass.Compact) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            HomeScreenContent(
                navController = navController,
                billingClientWrapper = billingClientWrapper,
                storeManager = storeManager,
                activity = activity
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(32.dp),
            horizontalArrangement = Arrangement.spacedBy(32.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            HomeScreenContent(
                navController = navController,
                modifier = Modifier.weight(1f),
                billingClientWrapper = billingClientWrapper,
                storeManager = storeManager,
                activity = activity
            )
        }
    }
}

@Composable
private fun HomeScreenContent(
    navController: NavController,
    modifier: Modifier = Modifier,
    billingClientWrapper: BillingClientWrapper,
    storeManager: StoreManager,
    activity: Activity
) {
    val context = LocalContext.current
    var currentLanguage by remember { 
        mutableStateOf(LanguageManager.getCurrentLanguage(context).uppercase())
    }
    
    // Ödüllü reklam manager'ı
    val rewardedAdManager = remember { RewardedAdManager(context) }
    var showRewardDialog by remember { mutableStateOf(false) }
    var adError by remember { mutableStateOf<String?>(null) }
    var shouldGrantTrial by remember { mutableStateOf(false) }
    
    // Trial hakkı verme effect'i
    if (shouldGrantTrial) {
        LaunchedEffect(shouldGrantTrial) {
            storeManager.grantTrialRight()
            shouldGrantTrial = false
        }
    }
    
    // Reklam yükleme - sadece trial teklifi gösterilebiliyorsa
    LaunchedEffect(Unit) {
        if (storeManager.canShowTrialOffer()) {
            rewardedAdManager.loadRewardedAd(null)
        }
    }
    
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(30.dp))

            // Logo ve Başlık
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                val isPlusUserState = storeManager.isPlusUser.collectAsState(initial = null)
                val isPlusUser = isPlusUserState.value
                val isTrialAvailableState = storeManager.isTrialAvailable.collectAsState(initial = false)
                val isTrialAvailable = isTrialAvailableState.value
                var showPremiumDialog by remember { mutableStateOf(false) }

                when {
                    isPlusUser == true -> {
                        // Plus user aktif
                        Text(
                            text = stringResource(id = R.string.premium_active),
                            fontFamily = PixelFont,
                            color = Color(0xFFF0E68C),
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
                        )
                    }
                    isTrialAvailable -> {
                        // Trial hakkı var
                        Text(
                            text = stringResource(id = R.string.trial_available), // Yeni string eklenecek
                            fontFamily = PixelFont,
                            color = Color(0xFF4CAF50),
                            fontSize = 14.sp,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
                        )
                    }
                    else -> {
                        // Plus user değil ve trial hakkı yok
                        PixelArtButton(
                            text = stringResource(id = R.string.premium),
                            onClick = { showPremiumDialog = true },
                            fontSize = 10.sp,
                            imageId = R.drawable.button_plus_bg,
                            width = 180.dp,
                            height = 80.dp,
                            modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
                        )
                    }
                }

            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.vampir_logo),
                    contentDescription = "Vampir İkonu",
                    modifier = Modifier.size(140.dp)
                )
                BasicText(
                    text = stringResource(R.string.game_title),
                    autoSize = TextAutoSize.StepBased(),
                    maxLines = 2,
                    style = TextStyle(
                        fontFamily = PixelFont,
                        color = Color(0xFFF0E68C),
                        textAlign = TextAlign.Center
                    ),
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(4.dp)
                )

                val products by billingClientWrapper.products.collectAsState()

                if (showPremiumDialog) {
                    Dialog(onDismissRequest = { showPremiumDialog = false }) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.alert_bg),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxWidth().heightIn(350.dp,450.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.premium_title), // "PLUS PAKETİ"
                                    fontFamily = PixelFont,
                                    color = Color(0xFFF0E68C),
                                    fontSize = 20.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                // Rol kaydırmalı alan
                                Column {
                                    val scrollState = rememberScrollState()
                                    
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .horizontalScroll(scrollState),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RoleInfoItem(
                                            R.drawable.sahtekar,
                                            R.string.vote_saboteur,
                                            R.string.vote_saboteur_info
                                        )
                                        RoleInfoItem(
                                            R.drawable.kahin,
                                            R.string.autopsir,
                                            R.string.autopsir_info
                                        )
                                        RoleInfoItem(
                                            R.drawable.nobetci,
                                            R.string.veteran,
                                            R.string.veteran_info
                                        )
                                        RoleInfoItem(
                                            R.drawable.deli,
                                            R.string.madman,
                                            R.string.madman_info
                                        )
                                        RoleInfoItem(
                                            R.drawable.transporter,
                                            R.string.wizard,
                                            R.string.wizard_info
                                        )
                                    }
                                    
                                    // Yatay scroll indicator - her zaman görünür
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(6.dp)
                                            .padding(horizontal = 24.dp)
                                            .padding(top = 8.dp)
                                    ) {
                                        // Arka plan bar - daha belirgin
                                        Box(
                                            modifier = Modifier
                                                .fillMaxSize()
                                                .background(
                                                    Color.White.copy(alpha = 0.4f),
                                                    RoundedCornerShape(3.dp)
                                                )
                                        )
                                        
                                        // Scroll thumb
                                        if (scrollState.maxValue > 0) {
                                            val scrollProgress = scrollState.value.toFloat() / scrollState.maxValue
                                            val thumbWidth = 0.3f // Thumb genişliği (bar'ın %30'u)
                                            val thumbPosition = scrollProgress * (1f - thumbWidth)
                                            
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(thumbWidth)
                                                    .absoluteOffset(x = (thumbPosition * 150).dp) // Scroll pozisyonu
                                                    .background(
                                                        Color(0xFFF0E68C),
                                                        RoundedCornerShape(3.dp)
                                                    )
                                            )
                                        } else {
                                            // Scroll edilecek içerik yoksa statik thumb
                                            Box(
                                                modifier = Modifier
                                                    .fillMaxHeight()
                                                    .fillMaxWidth(0.8f)
                                                    .background(
                                                        Color(0xFFF0E68C).copy(alpha = 0.6f),
                                                        RoundedCornerShape(3.dp)
                                                    )
                                            )
                                        }
                                    }
                                }

                                Spacer(modifier = Modifier.height(16.dp))

                                // Butonlar
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    // Üst sıra - Satın alma butonları
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        PixelArtButton(
                                            text = stringResource(id = R.string.upgrade),
                                            onClick = {
                                                showPremiumDialog = false
                                                products.find { it.productId == "plus_package" }?.let {
                                                    billingClientWrapper.launchPurchaseFlow(activity, it)
                                                }
                                            },
                                            imageId = R.drawable.button_red,
                                            fontSize = 10.sp,
                                            width = 140.dp,
                                            height = 56.dp
                                        )

                                        PixelArtButton(
                                            text = stringResource(id = R.string.not_now),
                                            onClick = { showPremiumDialog = false },
                                            imageId = R.drawable.button_gray,
                                            fontSize = 10.sp,
                                            width = 140.dp,
                                            height = 56.dp
                                        )
                                    }
                                    
                                    Spacer(modifier = Modifier.height(8.dp))
                                    
                                    // Alt sıra - Ödüllü reklam butonu (sadece trial teklifi yapılabiliyorsa)
                                    var canShowTrial by remember { mutableStateOf(false) }
                                    
                                    LaunchedEffect(Unit) {
                                        canShowTrial = storeManager.canShowTrialOffer()
                                    }
                                    
                                    if (canShowTrial) {
                                        PixelArtButton(
                                            text = stringResource(id = R.string.try_for_free), // String güncellendi
                                            onClick = {
                                                if (rewardedAdManager.isAdLoaded()) {
                                                    rewardedAdManager.showRewardedAd(activity, object : RewardedAdManager.RewardedAdCallback {
                                                        override fun onAdLoaded() {}
                                                        override fun onAdFailedToLoad(error: String) {
                                                            adError = error
                                                        }
                                                        override fun onAdShown() {}
                                                        override fun onAdDismissed() {
                                                            showPremiumDialog = false
                                                        }
                                                        override fun onUserEarnedReward(rewardType: String, rewardAmount: Int) {
                                                            // Trial hakkı ver
                                                            showPremiumDialog = false
                                                            showRewardDialog = true
                                                            // StoreManager'da trial hakkı ver
                                                            shouldGrantTrial = true
                                                        }
                                                        override fun onAdFailedToShow(error: String) {
                                                            adError = error
                                                        }
                                                    })
                                                } else {
                                                    adError = "Reklam henüz yüklenmedi"
                                                }
                                            },
                                            imageId = R.drawable.button_brown,
                                            fontSize = 9.sp,
                                            width = 200.dp,
                                            height = 48.dp
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
                
                // Ödül başarı dialog'ı
                if (showRewardDialog) {
                    Dialog(onDismissRequest = { showRewardDialog = false }) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.alert_bg),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxWidth().heightIn(250.dp, 350.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.reward_title),
                                    fontFamily = PixelFont,
                                    color = Color(0xFF4CAF50),
                                    fontSize = 18.sp
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                Text(
                                    text = stringResource(id = R.string.reward_message),
                                    fontFamily = PixelFont,
                                    color = Color.White,
                                    fontSize = 12.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                PixelArtButton(
                                    text = stringResource(id = R.string.awesome),
                                    onClick = { showRewardDialog = false },
                                    imageId = R.drawable.button_red,
                                    fontSize = 12.sp,
                                    width = 120.dp,
                                    height = 48.dp
                                )
                            }
                        }
                    }
                }
                
                // Error dialog
                adError?.let { error ->
                    Dialog(onDismissRequest = { adError = null }) {
                        Box(contentAlignment = Alignment.Center) {
                            Image(
                                painter = painterResource(id = R.drawable.alert_bg),
                                contentDescription = null,
                                contentScale = ContentScale.FillBounds,
                                modifier = Modifier.fillMaxWidth().heightIn(200.dp, 300.dp)
                            )
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                modifier = Modifier.padding(16.dp)
                            ) {
                                Text(
                                    text = stringResource(id = R.string.error_title),
                                    fontFamily = PixelFont,
                                    color = Color(0xFFFF5722),
                                    fontSize = 16.sp
                                )

                                Spacer(modifier = Modifier.height(8.dp))

                                Text(
                                    text = error,
                                    fontFamily = PixelFont,
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    textAlign = TextAlign.Center
                                )

                                Spacer(modifier = Modifier.height(12.dp))

                                PixelArtButton(
                                    text = stringResource(id = R.string.ok),
                                    onClick = { adError = null },
                                    imageId = R.drawable.button_gray,
                                    fontSize = 10.sp,
                                    width = 100.dp,
                                    height = 40.dp
                                )
                            }
                        }
                    }
                }
            }
        }

        // Butonlar
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(top = 160.dp)
        ) {
            PixelArtButton(
                text = stringResource(id = R.string.start_game),
                onClick = { navController.navigate(Screen.Setup.route) },
                modifier = Modifier,
                fontSize = 17.sp,
                imageId = R.drawable.button_red
            )

            PixelArtButton(
                text = stringResource(id = R.string.rules),
                onClick = { navController.navigate(Screen.Rules.route) },
                modifier = Modifier,
                fontSize = 23.sp,
                imageId = R.drawable.button_gray
            )
        }

        // Geliştirici bilgisi
        Text(
            text = stringResource(id = R.string.developer),
            color = Color.Gray,
            fontSize = 12.sp,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        }
        
        // Dil seçici - sağ üst köşe (DEBUG - daha belirgin)
        LanguageSelector(
            currentLanguage = currentLanguage,
            onLanguageChange = { newLanguage ->
                Log.d("HomeScreenContent", "Language change requested: $newLanguage")
                currentLanguage = newLanguage
                LanguageManager.setLanguage(activity, newLanguage.lowercase())
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(8.dp) // Daha küçük padding
        )
    }
}

@Composable
fun LanguageSelector(
    currentLanguage: String,
    onLanguageChange: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(Color.Black.copy(alpha = 0.8f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Turkish
        Text(
            text = "TR",
            color = if (currentLanguage == "TR") Color(0xFFF0E68C) else Color.White,
            fontFamily = PixelFont,
            fontSize = 12.sp,
            modifier = Modifier
                .clickable { 
                    Log.d("LanguageSelector", "TR clicked, current: $currentLanguage")
                    onLanguageChange("TR") 
                }
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )
        
        Text(
            text = "|",
            color = Color.White,
            fontFamily = PixelFont,
            fontSize = 14.sp
        )
        
        // English
        Text(
            text = "EN",
            color = if (currentLanguage == "EN") Color(0xFFF0E68C) else Color.White,
            fontFamily = PixelFont,
            fontSize = 12.sp,
            modifier = Modifier
                .clickable { 
                    Log.d("LanguageSelector", "EN clicked, current: $currentLanguage")
                    onLanguageChange("EN") 
                }
                .padding(horizontal = 8.dp, vertical = 6.dp)
        )
    }
}