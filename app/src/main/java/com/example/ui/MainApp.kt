package com.example.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.ChatAssistantScreen
import com.example.ui.screens.KnowledgeBaseScreen
import com.example.ui.screens.PythonSimulatorScreen
import com.example.ui.screens.SecurityLockScreen
import com.example.ui.screens.SettingsScreen
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.SciFiSurface
import com.example.ui.theme.SciFiSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary

@Composable
fun MainApp(
    viewModel: AiViewModel = viewModel()
) {
    val isUnlocked by viewModel.isUnlocked.collectAsState()
    val activeTab by viewModel.activeTab.collectAsState()

    // Handle back button on subtabs
    BackHandler(enabled = isUnlocked && activeTab != AppTab.CHAT) {
        viewModel.setTab(AppTab.CHAT)
    }

    AnimatedContent(
        targetState = isUnlocked,
        transitionSpec = { fadeIn() togetherWith fadeOut() },
        label = "authTransition"
    ) { unlocked ->
        if (!unlocked) {
            SecurityLockScreen(viewModel = viewModel)
        } else {
            Scaffold(
                bottomBar = {
                    NavigationBar(
                        containerColor = SciFiSurface,
                        contentColor = TextPrimary,
                        modifier = Modifier
                            .navigationBarsPadding()
                            .testTag("main_bottom_nav")
                    ) {
                        val items = listOf(
                            Triple(AppTab.CHAT, Icons.Default.Chat, "แชท AI"),
                            Triple(AppTab.KNOWLEDGE, Icons.Default.MenuBook, "คลังความรู้"),
                            Triple(AppTab.PYTHON_LAB, Icons.Default.Terminal, "แล็บ Python"),
                            Triple(AppTab.SETTINGS, Icons.Default.Settings, "ตั้งค่า")
                        )

                        items.forEach { (tab, icon, title) ->
                            NavigationBarItem(
                                selected = activeTab == tab,
                                onClick = { viewModel.setTab(tab) },
                                icon = {
                                    Icon(
                                        imageVector = icon,
                                        contentDescription = title,
                                        modifier = Modifier.size(20.dp)
                                    )
                                },
                                label = {
                                    Text(
                                        text = title,
                                        fontSize = 11.sp
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = Color(0xFF00363D),
                                    selectedTextColor = CyanPrimary,
                                    indicatorColor = CyanPrimary,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                ),
                                modifier = Modifier.testTag("nav_tab_${tab.name.lowercase()}")
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
                    when (activeTab) {
                        AppTab.CHAT -> ChatAssistantScreen(viewModel = viewModel)
                        AppTab.KNOWLEDGE -> KnowledgeBaseScreen(viewModel = viewModel)
                        AppTab.PYTHON_LAB -> PythonSimulatorScreen(viewModel = viewModel)
                        AppTab.SETTINGS -> SettingsScreen(viewModel = viewModel)
                    }
                }
            }
        }
    }
}
