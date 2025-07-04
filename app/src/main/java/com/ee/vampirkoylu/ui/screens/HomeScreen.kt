package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import android.app.Activity
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.Dp
import androidx.navigation.NavController
import androidx.compose.runtime.collectAsState
import com.ee.vampirkoylu.BillingClientWrapper
import com.ee.vampirkoylu.StoreManager
import com.ee.vampirkoylu.ui.theme.LocalWindowWidthSizeClass
import androidx.compose.material3.windowsizeclass.WindowWidthSizeClass
import androidx.navigation.compose.rememberNavController
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.navigation.Screen
import com.ee.vampirkoylu.ui.theme.PixelFont

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
    val containerModifier = modifier.fillMaxSize()
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Spacer(modifier = Modifier.height(30.dp))

        // Logo ve Başlık
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Row(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.vampir_logo),
                    contentDescription = "Vampir İkonu",
                    modifier = Modifier.size(140.dp)
                )

                Text(
                    text = stringResource(id = R.string.game_title),
                    fontSize = 32.sp,
                    fontFamily = PixelFont,
                    color = Color(0xFFF0E68C),
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 40.sp,
                    modifier = Modifier.weight(1f).align(Alignment.CenterVertically)
                )

                val products by billingClientWrapper.products.collectAsState()
                val isPlusUser by storeManager.isPlusUser.collectAsState(initial = false)
                var showPremiumDialog by remember { mutableStateOf(false) }

                if (!isPlusUser) {
                    PixelArtButton(
                        text = stringResource(id = R.string.premium),
                        onClick = { showPremiumDialog = true },
                        fontSize = 12.sp,
                        imageId = R.drawable.button_orange,
                        width = 110.dp,
                        height = 40.dp,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }

                if (showPremiumDialog) {
                    AlertDialog(
                        onDismissRequest = { showPremiumDialog = false },
                        confirmButton = {
                            TextButton(onClick = {
                                showPremiumDialog = false
                                products.find { it.productId == "plus_package" }?.let {
                                    billingClientWrapper.launchPurchaseFlow(activity, it)
                                }
                            }) {
                                Text(text = stringResource(id = R.string.upgrade))
                            }
                        },
                        dismissButton = {
                            TextButton(onClick = { showPremiumDialog = false }) {
                                Text(text = stringResource(id = R.string.not_now))
                            }
                        },
                        title = { Text(text = stringResource(id = R.string.premium_title)) },
                        text = { Text(text = stringResource(id = R.string.premium_description)) }
                    )
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
} 