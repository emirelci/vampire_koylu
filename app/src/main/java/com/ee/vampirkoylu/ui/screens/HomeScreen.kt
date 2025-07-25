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
import com.ee.vampirkoylu.ui.theme.LocalWindowWidthSizeClass
import com.ee.vampirkoylu.util.WindowWidthSizeClass
import androidx.compose.ui.Alignment
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.window.Dialog
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
                var showPremiumDialog by remember { mutableStateOf(false) }

                if (isPlusUser == false) {
                    PixelArtButton(
                        text = stringResource(id = R.string.premium),
                        onClick = { showPremiumDialog = true },
                        fontSize = 10.sp,
                        imageId = R.drawable.button_plus_bg,
                        width = 180.dp,
                        height = 80.dp,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
                    )
                }else if (isPlusUser == true) {
                    Text(
                        text = stringResource(id = R.string.premium_active),
                        fontFamily = PixelFont,
                        color = Color(0xFFF0E68C),
                        fontSize = 14.sp,
                        modifier = Modifier.align(Alignment.CenterHorizontally).padding(bottom = 24.dp)
                    )
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
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .horizontalScroll(rememberScrollState()),
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

                                Spacer(modifier = Modifier.height(16.dp))

                                // Butonlar
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
                LanguageManager.setLanguage(context, newLanguage.lowercase())
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