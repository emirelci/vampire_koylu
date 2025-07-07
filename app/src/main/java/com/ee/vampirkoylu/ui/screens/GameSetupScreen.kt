package com.ee.vampirkoylu.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.ee.vampirkoylu.R
import com.ee.vampirkoylu.model.GameSettings
import com.ee.vampirkoylu.ui.component.IncreaseDecreaseCount
import com.ee.vampirkoylu.ui.component.PixelArtButton
import com.ee.vampirkoylu.ui.component.RoleCountSelector
import com.ee.vampirkoylu.ui.component.VerticalScrollbar
import com.ee.vampirkoylu.ui.component.WarningBanner
import com.ee.vampirkoylu.ui.component.ModeSelector
import com.ee.vampirkoylu.model.GameMode
import com.ee.vampirkoylu.ui.navigation.Screen
import com.ee.vampirkoylu.ui.theme.Beige
import com.ee.vampirkoylu.ui.theme.Gold
import com.ee.vampirkoylu.ui.theme.PixelFont
import com.ee.vampirkoylu.ui.theme.dark_gold
import com.ee.vampirkoylu.viewmodel.GameViewModel
import kotlinx.coroutines.delay

@Composable
fun GameSetupScreen(
    settings: GameSettings,
    navController: NavController,
    isPlusUser: Boolean,
    onSettingsChange: (Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int, Int) -> Unit,
    onStartGame: (List<String>) -> Unit,
    viewModel: GameViewModel = viewModel()
) {
    // State'leri viewModel değerlerine göre başlat
    val gameModes = remember(isPlusUser) {
        GameMode.values().filter { isPlusUser || !it.plusOnly }.toList()
    }
    var selectedMode by remember { mutableStateOf(GameMode.CUSTOM) }
    if (selectedMode.plusOnly && !isPlusUser) {
        selectedMode = GameMode.CUSTOM
    }

    var playerCount by remember { mutableStateOf(settings.playerCount) }
    var vampireCount by remember { mutableStateOf(settings.vampireCount) }
    var sheriffCount by remember { mutableStateOf(settings.sheriffCount) }
    var watcherCount by remember { mutableStateOf(settings.watcherCount) }
    var serialKillerCount by remember { mutableStateOf(settings.serialKillerCount) }
    var doctorCount by remember { mutableStateOf(settings.doctorCount) }
    var seerCount by remember { mutableStateOf(settings.seerCount) }
    var saboteurCount by remember { mutableStateOf(settings.voteSaboteurCount) }
    var autopsirCount by remember { mutableStateOf(settings.autopsirCount) }
    var veteranCount by remember { mutableStateOf(settings.veteranCount) }
    var madmanCount by remember { mutableStateOf(settings.madmanCount) }
    var wizardCount by remember { mutableStateOf(settings.wizardCount) }

    val playerNames = remember {
        mutableStateListOf<String>().apply {
            repeat(settings.playerCount) { add("") }
        }
    }

    fun applyMode(mode: GameMode) {
        selectedMode = mode
        val s = mode.settings
        playerCount = s.playerCount
        vampireCount = s.vampireCount
        sheriffCount = s.sheriffCount
        watcherCount = s.watcherCount
        serialKillerCount = s.serialKillerCount
        doctorCount = s.doctorCount
        seerCount = s.seerCount
        saboteurCount = s.voteSaboteurCount
        autopsirCount = s.autopsirCount
        veteranCount = s.veteranCount
        madmanCount = s.madmanCount
        wizardCount = s.wizardCount
        playerNames.clear()
        repeat(playerCount) { playerNames.add("") }
        onSettingsChange(
            playerCount,
            vampireCount,
            sheriffCount,
            watcherCount,
            serialKillerCount,
            doctorCount,
            seerCount,
            saboteurCount,
            autopsirCount,
            veteranCount,
            madmanCount,
            wizardCount
        )
    }

    LaunchedEffect(Unit) {
        applyMode(selectedMode)
    }

    // Maksimum vampir sayısı hesapla
    val maxVampireCount = calculateMaxVampires(playerCount)

    // Özel rol sayısı hesapla
    val specialRoleCount =
        vampireCount + sheriffCount + watcherCount + serialKillerCount + doctorCount + seerCount +
                saboteurCount + autopsirCount + veteranCount + madmanCount + wizardCount
    val maxRoleCount = playerCount - 1 // En az 1 normal köylü olmalı

    var showWarning by remember { mutableStateOf(false) }
    var warningMessage by remember { mutableStateOf("") }
    val editNotAllowedMessage = "Rol ayarları sadece Özel modda değiştirilebilir!"

    Box(modifier = Modifier.fillMaxSize()) {
        // Arka plan resmi
        Image(
            painter = painterResource(id = R.drawable.night_bg),
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

            // İçerik
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(14.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 36.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = stringResource(id = R.string.new_game),
                        fontSize = 28.sp,
                        fontFamily = PixelFont,
                        color = dark_gold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .align(Alignment.CenterVertically)
                    )
                }
                // Başlık


                // Oyun ayarları
                Card(
                    modifier = Modifier
                        .fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.05f) // Hafif şeffaf
                    )
                ) {
                    Column(
                        modifier = Modifier.padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        // Oyun modu
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 8.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.game_mode),
                                fontSize = 14.sp,
                                fontFamily = PixelFont,
                                color = Beige,
                                modifier = Modifier.weight(1f)
                            )

                            ModeSelector(
                                modes = gameModes.map { it.nameRes },
                                selectedIndex = gameModes.indexOf(selectedMode),
                                onIndexChange = { applyMode(gameModes[it]) }
                            )
                        }

                        // Oyuncu sayısı
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 16.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = stringResource(id = R.string.player_count),
                                fontSize = 14.sp,
                                fontFamily = PixelFont,
                                color = Beige,
                                modifier = Modifier.weight(1f)
                            )

                            IncreaseDecreaseCount(
                                count = playerCount,
                                onIncrease = {
                                    val newPlayerCount = playerCount + 1
                                    playerCount = newPlayerCount
                                    onSettingsChange(
                                        newPlayerCount,
                                        vampireCount,
                                        sheriffCount,
                                        watcherCount,
                                        serialKillerCount,
                                        doctorCount,
                                        seerCount,
                                        saboteurCount,
                                        autopsirCount,
                                        veteranCount,
                                        madmanCount,
                                        wizardCount
                                    )
                                    if (playerNames.size < newPlayerCount) {
                                        playerNames.add("")
                                    }
                                },
                                onDecrease = {
                                    val newPlayerCount = playerCount - 1
                                    val newMaxVampires = calculateMaxVampires(newPlayerCount)
                                    val newVampireCount = minOf(vampireCount, newMaxVampires)
                                    val newMaxRoleCount = newPlayerCount - 1

                                    if (specialRoleCount > newMaxRoleCount) {
                                        warningMessage = "Önce özel rollerin sayısını azaltmalısınız!"
                                        showWarning = true
                                    } else {
                                        playerCount = newPlayerCount
                                        vampireCount = newVampireCount
                                        onSettingsChange(
                                            newPlayerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )

                                        while (playerNames.size > newPlayerCount) {
                                            playerNames.removeAt(playerNames.lastIndex)
                                        }
                                    }
                                },
                                canIncrease = selectedMode == GameMode.CUSTOM && playerCount < 15,
                                canDecrease = selectedMode == GameMode.CUSTOM && playerCount > 4,
                                showWarningOnIncrease = {
                                    warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                        "Oyuncu sayısı en fazla 15 olabilir!"
                                    } else {
                                        editNotAllowedMessage
                                    }
                                    showWarning = true
                                },
                                showWarningOnDecrease = {
                                    warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                        "Oyuncu sayısı en az 4 olmalıdır!"
                                    } else {
                                        editNotAllowedMessage
                                    }
                                    showWarning = true
                                },
                                modifier = Modifier.padding(start = 6.dp)
                            )


                        }

                    }
                }


                // Özel Roller bölümü
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFF1A1A2E).copy(alpha = 0.05f)
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(4.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = stringResource(id = R.string.role_settings),
                            fontSize = 16.sp,
                            fontFamily = PixelFont,
                            color = Beige,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )

                        Box(
                            modifier = Modifier
                                .padding(4.dp)
                                .heightIn(max = 110.dp, min = 100.dp)
                        ) {
                            val roleScrollState = rememberScrollState()

                            Column(
                                modifier = Modifier
                                    .padding(4.dp)
                                    .padding(end = 12.dp)
                                    .verticalScroll(roleScrollState),
                                horizontalAlignment = Alignment.CenterHorizontally,
                            ) {
                                // Vampir sayısı
                                RoleCountSelector(
                                    title = stringResource(id = R.string.vampire_count),
                                    count = vampireCount,
                                    maxCount = maxVampireCount,
                                    onIncrease = {
                                        val newVampireCount = vampireCount + 1
                                        vampireCount = newVampireCount
                                        onSettingsChange(
                                            playerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )
                                    },
                                    onDecrease = {
                                        val newVampireCount = vampireCount - 1
                                        vampireCount = newVampireCount
                                        onSettingsChange(
                                            playerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )
                                    },
                                    editable = selectedMode == GameMode.CUSTOM,
                                    showWarningOnIncrease = {
                                        warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                            "Bu rolden en fazla $maxVampireCount tane olabilir!"
                                        } else {
                                            editNotAllowedMessage
                                        }
                                    }
                                )

                                // Şerif sayısı
                                RoleCountSelector(
                                    title = stringResource(id = R.string.sheriff_count),
                                    count = sheriffCount,
                                    maxCount = 1,
                                    onIncrease = {
                                        if (specialRoleCount < maxRoleCount) {
                                            sheriffCount = 1
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )

                                        } else {
                                            warningMessage =
                                                "En fazla ${maxRoleCount} özel rol olabilir!"
                                            showWarning = true
                                        }
                                    },
                                    onDecrease = {
                                        sheriffCount = 0
                                        onSettingsChange(
                                            playerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )

                                    },
                                    editable = selectedMode == GameMode.CUSTOM,
                                    showWarningOnIncrease = {
                                        warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                            "Bu rolden en fazla 1 tane olabilir!"
                                        } else {
                                            editNotAllowedMessage
                                        }
                                        showWarning = true
                                    }
                                )

                                // Gözcü sayısı
                                RoleCountSelector(
                                    title = stringResource(id = R.string.watcher_count),
                                    count = watcherCount,
                                    maxCount = playerCount,
                                    onIncrease = {
                                        if (specialRoleCount < maxRoleCount) {
                                            watcherCount += 1
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )

                                        } else {
                                            warningMessage =
                                                "En fazla ${maxRoleCount} özel rol olabilir!"
                                            showWarning = true
                                        }
                                    },
                                    onDecrease = {
                                        watcherCount = (watcherCount - 1).coerceAtLeast(0)
                                        onSettingsChange(
                                            playerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )

                                    },
                                    editable = selectedMode == GameMode.CUSTOM,
                                    showWarningOnIncrease = {
                                        warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                            "Bu rolden en fazla $playerCount tane olabilir!"
                                        } else {
                                            editNotAllowedMessage
                                        }
                                        showWarning = true
                                    }
                                )

                                // Seri Katil sayısı
                                RoleCountSelector(
                                    title = stringResource(id = R.string.serial_killer_count),
                                    count = serialKillerCount,
                                    maxCount = 1,
                                    onIncrease = {
                                        if (specialRoleCount < maxRoleCount) {
                                            serialKillerCount = 1
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )

                                        } else {
                                            warningMessage =
                                                "En fazla ${maxRoleCount} özel rol olabilir!"
                                            showWarning = true
                                        }
                                    },
                                    onDecrease = {
                                        serialKillerCount = 0
                                        onSettingsChange(
                                            playerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )

                                    },
                                    editable = selectedMode == GameMode.CUSTOM,
                                    showWarningOnIncrease = {
                                        warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                            "Bu rolden en fazla 1 tane olabilir!"
                                        } else {
                                            editNotAllowedMessage
                                        }
                                        showWarning = true
                                    }
                                )

                                // Doktor sayısı
                                RoleCountSelector(
                                    title = stringResource(id = R.string.doctor_count),
                                    count = doctorCount,
                                    maxCount = playerCount,
                                    onIncrease = {
                                        if (specialRoleCount < maxRoleCount) {
                                            doctorCount += 1
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )

                                        } else {
                                            warningMessage =
                                                "En fazla ${maxRoleCount} özel rol olabilir!"
                                            showWarning = true
                                        }
                                    },
                                    onDecrease = {
                                        doctorCount = (doctorCount - 1).coerceAtLeast(0)
                                        onSettingsChange(
                                            playerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )

                                    },
                                    editable = selectedMode == GameMode.CUSTOM,
                                    showWarningOnIncrease = {
                                        warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                            "Bu rolden en fazla $playerCount tane olabilir!"
                                        } else {
                                            editNotAllowedMessage
                                        }
                                        showWarning = true
                                    }
                                )

                                RoleCountSelector(
                                    title = stringResource(id = R.string.seer_count),
                                    count = seerCount,
                                    maxCount = playerCount,
                                    onIncrease = {
                                        if (specialRoleCount < maxRoleCount) {
                                            seerCount += 1
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )
                                        } else {
                                            warningMessage =
                                                "En fazla ${maxRoleCount} özel rol olabilir!"
                                            showWarning = true
                                        }
                                    },
                                    onDecrease = {
                                        seerCount = (seerCount - 1).coerceAtLeast(0)
                                        onSettingsChange(
                                            playerCount,
                                            vampireCount,
                                            sheriffCount,
                                            watcherCount,
                                            serialKillerCount,
                                            doctorCount,
                                            seerCount,
                                            saboteurCount,
                                            autopsirCount,
                                            veteranCount,
                                            madmanCount,
                                            wizardCount
                                        )

                                    },
                                    editable = selectedMode == GameMode.CUSTOM,
                                    showWarningOnIncrease = {
                                        warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                            "Bu rolden en fazla $playerCount tane olabilir!"
                                        } else {
                                            editNotAllowedMessage
                                        }
                                        showWarning = true
                                    }
                                )

                                if (isPlusUser) {
                                RoleCountSelector(
                                    title = stringResource(id = R.string.vote_saboteur_count),
                                    count = saboteurCount,
                                    maxCount = 1,
                                        onIncrease = {
                                            if (specialRoleCount < maxRoleCount) {
                                                saboteurCount = 1
                                                onSettingsChange(
                                                    playerCount,
                                                    vampireCount,
                                                    sheriffCount,
                                                    watcherCount,
                                                    serialKillerCount,
                                                    doctorCount,
                                                    seerCount,
                                                    saboteurCount,
                                                    autopsirCount,
                                                    veteranCount,
                                                    madmanCount,
                                                    wizardCount
                                                )
                                            } else {
                                                warningMessage =
                                                    "En fazla ${maxRoleCount} özel rol olabilir!"
                                                showWarning = true
                                            }
                                        },
                                        onDecrease = {
                                            saboteurCount = 0
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )
                                        },
                                        editable = selectedMode == GameMode.CUSTOM,
                                        showWarningOnIncrease = {
                                            warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                                "Bu rolden en fazla 1 tane olabilir!"
                                            } else {
                                                editNotAllowedMessage
                                            }
                                            showWarning = true
                                        }
                                    )

                                    RoleCountSelector(
                                        title = stringResource(id = R.string.autopsir_count),
                                        count = autopsirCount,
                                        maxCount = playerCount,
                                        onIncrease = {
                                            if (specialRoleCount < maxRoleCount) {
                                                autopsirCount += 1
                                                onSettingsChange(
                                                    playerCount,
                                                    vampireCount,
                                                    sheriffCount,
                                                    watcherCount,
                                                    serialKillerCount,
                                                    doctorCount,
                                                    seerCount,
                                                    saboteurCount,
                                                    autopsirCount,
                                                    veteranCount,
                                                    madmanCount,
                                                    wizardCount
                                                )
                                            } else {
                                                warningMessage =
                                                    "En fazla ${maxRoleCount} özel rol olabilir!"
                                                showWarning = true
                                            }
                                        },
                                        onDecrease = {
                                            autopsirCount = (autopsirCount - 1).coerceAtLeast(0)
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )
                                        },
                                        editable = selectedMode == GameMode.CUSTOM,
                                        showWarningOnIncrease = {
                                            warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                                "Bu rolden en fazla $playerCount tane olabilir!"
                                            } else {
                                                editNotAllowedMessage
                                            }
                                            showWarning = true
                                        }
                                    )

                                    RoleCountSelector(
                                        title = stringResource(id = R.string.veteran_count),
                                        count = veteranCount,
                                        maxCount = 1,
                                        onIncrease = {
                                            if (specialRoleCount < maxRoleCount) {
                                                veteranCount = 1
                                                onSettingsChange(
                                                    playerCount,
                                                    vampireCount,
                                                    sheriffCount,
                                                    watcherCount,
                                                    serialKillerCount,
                                                    doctorCount,
                                                    seerCount,
                                                    saboteurCount,
                                                    autopsirCount,
                                                    veteranCount,
                                                    madmanCount,
                                                    wizardCount
                                                )
                                            } else {
                                                warningMessage =
                                                    "En fazla ${maxRoleCount} özel rol olabilir!"
                                                showWarning = true
                                            }
                                        },
                                        onDecrease = {
                                            veteranCount = 0
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )
                                        },
                                        editable = selectedMode == GameMode.CUSTOM,
                                        showWarningOnIncrease = {
                                            warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                                "Bu rolden en fazla 1 tane olabilir!"
                                            } else {
                                                editNotAllowedMessage
                                            }
                                            showWarning = true
                                        }
                                    )

                                    RoleCountSelector(
                                        title = stringResource(id = R.string.madman_count),
                                        count = madmanCount,
                                        maxCount = 1,
                                        onIncrease = {
                                            if (specialRoleCount < maxRoleCount) {
                                                madmanCount = 1
                                                onSettingsChange(
                                                    playerCount,
                                                    vampireCount,
                                                    sheriffCount,
                                                    watcherCount,
                                                    serialKillerCount,
                                                    doctorCount,
                                                    seerCount,
                                                    saboteurCount,
                                                    autopsirCount,
                                                    veteranCount,
                                                    madmanCount,
                                                    wizardCount
                                                )
                                            } else {
                                                warningMessage =
                                                    "En fazla ${maxRoleCount} özel rol olabilir!"
                                                showWarning = true
                                            }
                                        },
                                        onDecrease = {
                                            madmanCount = 0
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )
                                        },
                                        editable = selectedMode == GameMode.CUSTOM,
                                        showWarningOnIncrease = {
                                            warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                                "Bu rolden en fazla 1 tane olabilir!"
                                            } else {
                                                editNotAllowedMessage
                                            }
                                            showWarning = true
                                        }
                                    )

                                    RoleCountSelector(
                                        title = stringResource(id = R.string.wizard_count),
                                        count = wizardCount,
                                        maxCount = playerCount,
                                        onIncrease = {
                                            if (specialRoleCount < maxRoleCount) {
                                                wizardCount += 1
                                                onSettingsChange(
                                                    playerCount,
                                                    vampireCount,
                                                    sheriffCount,
                                                    watcherCount,
                                                    serialKillerCount,
                                                    doctorCount,
                                                    seerCount,
                                                    saboteurCount,
                                                    autopsirCount,
                                                    veteranCount,
                                                    madmanCount,
                                                    wizardCount
                                                )
                                            } else {
                                                warningMessage =
                                                    "En fazla ${maxRoleCount} özel rol olabilir!"
                                                showWarning = true
                                            }
                                        },
                                        onDecrease = {
                                            wizardCount = (wizardCount - 1).coerceAtLeast(0)
                                            onSettingsChange(
                                                playerCount,
                                                vampireCount,
                                                sheriffCount,
                                                watcherCount,
                                                serialKillerCount,
                                                doctorCount,
                                                seerCount,
                                                saboteurCount,
                                                autopsirCount,
                                                veteranCount,
                                                madmanCount,
                                                wizardCount
                                            )
                                        },
                                        editable = selectedMode == GameMode.CUSTOM,
                                        showWarningOnIncrease = {
                                            warningMessage = if (selectedMode == GameMode.CUSTOM) {
                                                "Bu rolden en fazla $playerCount tane olabilir!"
                                            } else {
                                                editNotAllowedMessage
                                            }
                                            showWarning = true
                                        }
                                    )
                                }

                            }

                            VerticalScrollbar(
                                scrollState = roleScrollState,
                                modifier = Modifier.align(Alignment.CenterEnd)
                            )
                        }
                    }
                }

                // Oyuncu isimleri girişi
                Text(
                    text = stringResource(id = R.string.enter_names),
                    fontSize = 20.sp,
                    fontFamily = PixelFont,
                    color = dark_gold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(vertical = 4.dp, horizontal = 4.dp)
                )

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(
                            Color(0xFF1A1A2E).copy(alpha = 0.01f),
                            shape = MaterialTheme.shapes.medium
                        )
                        .padding(horizontal = 8.dp, vertical = 8.dp),
                    contentPadding = PaddingValues(vertical = 4.dp)
                ) {
                    itemsIndexed(playerNames) { index, name ->
                        PlayerNameInput(
                            name = name,
                            index = index + 1,
                            onNameChange = { newName ->
                                playerNames[index] = newName
                            }
                        )
                    }
                }

                // Butonlar
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    // Geri butonu
                    PixelArtButton(
                        text = stringResource(id = R.string.back_to_menu),
                        onClick = {
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        },
                        color = Beige,
                        imageId = R.drawable.button_brown,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 8.dp),
                        fontSize = 12.sp
                    )

                    // Başlat butonu
                    val allNamesEntered = playerNames.all { it.isNotBlank() }

                    PixelArtButton(
                        text = stringResource(id = R.string.start),
                        onClick = {
                            if (!allNamesEntered) {
                                warningMessage = "Tüm oyuncuların isimlerini girmelisiniz!"
                                showWarning = true
                            } else if (selectedMode == GameMode.CUSTOM && vampireCount == 0 && serialKillerCount == 0) {
                                warningMessage = "En az 1 tane vampir veya seri katil rolü bulunmalı!"
                                showWarning = true
                            } else {
                                onStartGame(playerNames.toList())
                            }
                        },
                        color = Beige,
                        imageId = R.drawable.button_brown,
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp),
                        fontSize = 16.sp
                    )
                }

                AnimatedVisibility(
                    visible = showWarning,
                    enter = slideInVertically(initialOffsetY = { it }) + fadeIn(),
                    exit = slideOutVertically(targetOffsetY = { it }) + fadeOut()
                ) {
                    WarningBanner(warningMessage)
                }

                if (showWarning) {
                    LaunchedEffect(Unit) {
                        delay(3000)
                        showWarning = false
                    }
                }
            }

        }

    }
}

// Maksimum vampir sayısı hesaplama fonksiyonu
private fun calculateMaxVampires(playerCount: Int): Int {
    return maxOf(1, playerCount / 3)
}

@Composable
fun PlayerNameInput(
    name: String,
    index: Int,
    onNameChange: (String) -> Unit
) {
    OutlinedTextField(
        value = name,
        onValueChange = onNameChange,
        label = {
            Text(
                text = "Oyuncu $index",
                fontFamily = PixelFont,
                fontSize = 16.sp,
                color = Beige
            )
        },
        singleLine = true,
        keyboardOptions = KeyboardOptions(
            capitalization = KeyboardCapitalization.Words,
            imeAction = ImeAction.Next
        ),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Gold,
            unfocusedTextColor = Gold.copy(alpha = 0.8f),
            cursorColor = Gold,
            focusedBorderColor = Gold,
            unfocusedBorderColor = Gold.copy(alpha = 0.7f),
            focusedContainerColor = Color(0xFF1A1A2E),
            unfocusedContainerColor = Color(0xFF1A1A2E),
            unfocusedLabelColor = Gold.copy(alpha = 0.7f),
            focusedLabelColor = Gold
        ),
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    )
}

@Preview(showBackground = true,showSystemUi = true)
@Composable
fun GameSetupScreenPreview() {
    val navController = rememberNavController()
    GameSetupScreen(
        settings = GameSettings(6, 1),
        navController = navController,
        isPlusUser = true,
        onSettingsChange = { _, _, _, _, _, _, _, _, _, _, _ , _-> },
        onStartGame = { _ -> }
    )
}