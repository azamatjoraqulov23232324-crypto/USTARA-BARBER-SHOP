package com.example.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookOnline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GoldPrimary
import com.example.ui.theme.SlateCard
import com.example.ui.theme.SlateDark
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextWhite
import com.example.ui.viewmodel.MainViewModel

@Composable
fun MainContainer(viewModel: MainViewModel) {
    val currentTab by viewModel.currentTab.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()
    val isOnboarded by viewModel.isOnboarded.collectAsState()

    if (!isOnboarded) {
        OnboardingScreen(viewModel = viewModel)
    } else {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            containerColor = SlateDark,
            bottomBar = {
                if (isAdminLoggedIn) {
                    NavigationBar(
                        containerColor = SlateCard,
                        tonalElevation = 8.dp,
                        modifier = Modifier.testTag("main_navigation_bar")
                    ) {
                        NavigationBarItem(
                            selected = currentTab == 0,
                            onClick = { viewModel.setTab(0) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.BookOnline,
                                    contentDescription = "Qabul"
                                )
                            },
                            label = { Text("Yozilish", color = if (currentTab == 0) GoldPrimary else TextMuted) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SlateDark,
                                unselectedIconColor = TextMuted,
                                selectedTextColor = GoldPrimary,
                                unselectedTextColor = TextMuted,
                                indicatorColor = GoldPrimary
                            ),
                            modifier = Modifier.testTag("tab_booking")
                        )

                        NavigationBarItem(
                            selected = currentTab == 1,
                            onClick = { viewModel.setTab(1) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.History,
                                    contentDescription = "Buyurtmalar"
                                )
                            },
                            label = { Text("Navbatlar", color = if (currentTab == 1) GoldPrimary else TextMuted) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SlateDark,
                                unselectedIconColor = TextMuted,
                                selectedTextColor = GoldPrimary,
                                unselectedTextColor = TextMuted,
                                indicatorColor = GoldPrimary
                            ),
                            modifier = Modifier.testTag("tab_bookings")
                        )

                        NavigationBarItem(
                            selected = currentTab == 2,
                            onClick = { viewModel.setTab(2) },
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Settings,
                                    contentDescription = "Sozlamalar"
                                )
                            },
                            label = { Text("Sozlamalar", color = if (currentTab == 2) GoldPrimary else TextMuted) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = SlateDark,
                                unselectedIconColor = TextMuted,
                                selectedTextColor = GoldPrimary,
                                unselectedTextColor = TextMuted,
                                indicatorColor = GoldPrimary
                            ),
                            modifier = Modifier.testTag("tab_settings")
                        )
                    }
                }
            }
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                when (currentTab) {
                    0 -> BookingScreen(viewModel = viewModel)
                    1 -> if (isAdminLoggedIn) BookingsListScreen(viewModel = viewModel) else BookingScreen(viewModel = viewModel)
                    2 -> if (isAdminLoggedIn) SettingsScreen(viewModel = viewModel) else BookingScreen(viewModel = viewModel)
                    else -> BookingScreen(viewModel = viewModel)
                }
            }
        }
    }
}
