package com.ee.vampirkoylu.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.component.RoleInfoItem
import com.ee.vampirkoylu.ui.theme.PixelFont

@Composable
fun RuleScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Text(
            text = stringResource(id = R.string.rules_title),
            fontSize = 36.sp,
            fontFamily = PixelFont,
            color = Color(0xFFF0E68C),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 24.dp)
        )

        Box(contentAlignment = Alignment.Center) {
            Image(
                painter = painterResource(id = R.drawable.alert_bg),
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxWidth()
                    .heightIn(300.dp, 250.dp)
                    .padding(12.dp),
                alpha = 0.4f
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.padding(16.dp)
            ) {

                Text(
                    text = stringResource(id = R.string.rules_roles_info),
                    fontFamily = PixelFont,
                    textAlign = TextAlign.Center,
                    color = Color(0xFFF0E68C),
                    fontSize = 16.sp,
                    modifier = Modifier.padding(top = 16.dp)
                )

                // Temel Roller kaydırmalı alan
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(rememberScrollState()),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Vampir
                    RoleInfoItem(
                        R.drawable.vampir_logo,
                        R.string.vampire,
                        R.string.vampire_info
                    )

                    // Köylü
                    RoleInfoItem(
                        R.drawable.villager1,
                        R.string.villager,
                        R.string.villager_info
                    )

                    // Şerif
                    RoleInfoItem(
                        R.drawable.sheriff1,
                        R.string.sheriff,
                        R.string.sheriff_info
                    )

                    // Gözcü
                    RoleInfoItem(
                        R.drawable.watcher1,
                        R.string.watcher,
                        R.string.watcher_info
                    )

                    // Seri Katil
                    RoleInfoItem(
                        R.drawable.serial_killer1,
                        R.string.serial_killer,
                        R.string.serial_killer_info
                    )

                    // Doktor
                    RoleInfoItem(
                        R.drawable.doctor1,
                        R.string.doctor,
                        R.string.doctor_info
                    )

                    // Kahin
                    RoleInfoItem(
                        R.drawable.kahin,
                        R.string.seer,
                        R.string.seer_info
                    )

                    // Sahtekar
                    RoleInfoItem(
                        R.drawable.sahtekar,
                        R.string.vote_saboteur,
                        R.string.vote_saboteur_info
                    )

                    // Otopsir
                    RoleInfoItem(
                        R.drawable.otopsier,
                        R.string.autopsir,
                        R.string.autopsir_info
                    )

                    // Nöbetçi
                    RoleInfoItem(
                        R.drawable.nobetci,
                        R.string.veteran,
                        R.string.veteran_info
                    )

                    // Deli
                    RoleInfoItem(
                        R.drawable.deli,
                        R.string.madman,
                        R.string.madman_info
                    )

                    // Büyücü
                    RoleInfoItem(
                        R.drawable.transporter,
                        R.string.wizard,
                        R.string.wizard_info
                    )
                }
            }
            Spacer(modifier = Modifier.height(16.dp))
        }



    Card(
        modifier = Modifier
            .fillMaxWidth()
            .weight(1f)
            .padding(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A1A2E).copy(alpha = 0.85f)
        )
    ) {
        Text(
            text = stringResource(id = R.string.rules_gameplay),
            fontSize = 24.sp,
            fontFamily = PixelFont,
            color = Color(0xFFF0E68C),
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(vertical = 12.dp).align(Alignment.CenterHorizontally)
        )

        Text(
            text = stringResource(id = R.string.rules_text),
            fontSize = 16.sp,
            fontFamily = PixelFont,
            color = Color(0xFFF0E68C),
            overflow = TextOverflow.Visible,
            modifier = Modifier
                .verticalScroll(rememberScrollState())
                .padding(16.dp)
        )
    }

    PixelArtButton(
        text = stringResource(id = R.string.back_to_menu),
        onClick = { navController.navigateUp() },
        imageId = R.drawable.button_brown,
        modifier = Modifier.padding(vertical = 16.dp),
        fontSize = 14.sp
    )
}
}

@Preview(showBackground = true)
@Composable
fun previewRules() {
    val navController = rememberNavController()

    RuleScreen(navController)
}

