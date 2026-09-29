package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Badge
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.repository.QuerySource
import com.example.ui.AiViewModel
import com.example.ui.ChatMessage
import com.example.ui.components.SciFiWaveVisualizer
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.ErrorRed
import com.example.ui.theme.HologramCyan
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.SciFiBackground
import com.example.ui.theme.SciFiSurface
import com.example.ui.theme.SciFiSurfaceVariant
import com.example.ui.theme.TerminalGreen
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatAssistantScreen(
    viewModel: AiViewModel,
    modifier: Modifier = Modifier
) {
    val messages by viewModel.chatMessages.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val isThinking by viewModel.isAiThinking.collectAsState()
    val isTtsEnabled by viewModel.isTtsEnabled.collectAsState()
    val listState = rememberLazyListState()

    var inputQuery by remember { mutableStateOf("") }

    // Scroll to bottom when new messages arrive
    LaunchedEffect(messages.size) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val quickQuestions = listOf(
        "ทฤษฎีรูหนอนเป็นยังไง",
        "เด็กที่ฉลาดที่สุด",
        "อากิ้นฉลาดที่สุด",
        "มวลติดลบคืออะไร",
        "คุณคือใคร"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(SciFiBackground)
    ) {
        // Sci-Fi Header App Bar
        TopAppBar(
            title = {
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(10.dp)
                                .clip(CircleShape)
                                .background(if (isSpeaking) NeonPurple else TerminalGreen)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "AKIN AI ASSISTANT",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            fontFamily = FontFamily.Monospace,
                            color = CyanPrimary
                        )
                    }
                    Text(
                        text = if (isSpeaking) "กำลังตอบด้วยเสียง..." else "ระบบพร้อมใช้งาน • คำถามอิสระ",
                        fontSize = 11.sp,
                        color = TextMuted
                    )
                }
            },
            colors = TopAppBarDefaults.topAppBarColors(
                containerColor = SciFiSurface
            ),
            actions = {
                IconButton(
                    onClick = { viewModel.toggleTts() },
                    modifier = Modifier.testTag("toggle_tts_button")
                ) {
                    Icon(
                        imageVector = if (isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeMute,
                        contentDescription = "เปิด/ปิดเสียงพูด",
                        tint = if (isTtsEnabled) CyanPrimary else TextMuted
                    )
                }

                IconButton(
                    onClick = { viewModel.clearChat() },
                    modifier = Modifier.testTag("clear_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteSweep,
                        contentDescription = "ล้างแชท",
                        tint = TextSecondary
                    )
                }

                IconButton(
                    onClick = { viewModel.lockApp() },
                    modifier = Modifier.testTag("lock_app_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Lock,
                        contentDescription = "ล็อกความปลอดภัย",
                        tint = CyanPrimary
                    )
                }
            }
        )

        // Sci-Fi Waveform visualizer banner
        SciFiWaveVisualizer(
            isSpeaking = isSpeaking,
            modifier = Modifier
                .fillMaxWidth()
                .background(SciFiSurface.copy(alpha = 0.6f))
                .padding(vertical = 4.dp)
        )

        // Quick Suggestions Bar (Non-sequential prompt selection!)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState())
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            quickQuestions.forEach { prompt ->
                Surface(
                    onClick = {
                        viewModel.askQuestion(prompt)
                    },
                    shape = RoundedCornerShape(18.dp),
                    color = SciFiSurfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, CyanPrimary.copy(alpha = 0.35f)),
                    modifier = Modifier.testTag("quick_chip_${prompt.take(6)}")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 7.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = null,
                            tint = CyanPrimary,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = prompt,
                            fontSize = 12.sp,
                            color = TextPrimary,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }
        }

        // Chat Message History
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 8.dp)
        ) {
            items(messages, key = { it.id }) { msg ->
                ChatBubble(
                    message = msg,
                    onReplay = {
                        viewModel.ttsManager.speak(msg.text)
                    }
                )
            }

            if (isThinking) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            colors = CardDefaults.cardColors(containerColor = SciFiSurface),
                            shape = RoundedCornerShape(16.dp),
                            modifier = Modifier.padding(end = 64.dp)
                        ) {
                            Row(
                                modifier = Modifier.padding(12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = CyanPrimary,
                                    strokeWidth = 2.dp
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = "AI กำลังประมวลผลข้อมูล...",
                                    fontSize = 13.sp,
                                    color = TextSecondary
                                )
                            }
                        }
                    }
                }
            }
        }

        // Bottom Input Row
        Surface(
            color = SciFiSurface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = inputQuery,
                    onValueChange = { inputQuery = it },
                    placeholder = {
                        Text(
                            text = "พิมพ์คำถามใดก็ได้ (เช่น ทฤษฎีรูหนอน, เด็กที่ฉลาดที่สุด)...",
                            fontSize = 13.sp,
                            color = TextMuted
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_field"),
                    shape = RoundedCornerShape(24.dp),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SciFiSurfaceVariant,
                        unfocusedContainerColor = SciFiSurfaceVariant,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        focusedIndicatorColor = CyanPrimary,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                    keyboardActions = KeyboardActions(
                        onSend = {
                            if (inputQuery.isNotBlank()) {
                                viewModel.askQuestion(inputQuery)
                                inputQuery = ""
                            }
                        }
                    )
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (inputQuery.isNotBlank()) {
                            viewModel.askQuestion(inputQuery)
                            inputQuery = ""
                        }
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(if (inputQuery.isNotBlank()) CyanPrimary else SciFiSurfaceVariant)
                        .testTag("send_question_button"),
                    enabled = inputQuery.isNotBlank()
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "ส่งคำถาม",
                        tint = if (inputQuery.isNotBlank()) Color(0xFF00363D) else TextMuted
                    )
                }
            }
        }
    }
}

@Composable
fun ChatBubble(
    message: ChatMessage,
    onReplay: () -> Unit
) {
    val isUser = message.isFromUser

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 320.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) Color(0xFF004F58) else SciFiSurface
                ),
                border = if (!isUser) {
                    androidx.compose.foundation.BorderStroke(1.dp, SciFiSurfaceVariant)
                } else null
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Psychology,
                                contentDescription = null,
                                tint = CyanPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "AKIN AI",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = CyanPrimary,
                                fontFamily = FontFamily.Monospace
                            )
                            Spacer(modifier = Modifier.weight(1f))

                            // Source Badge
                            val sourceLabel = when (message.source) {
                                QuerySource.EXACT_LOCAL -> "คลังความรู้ตรง"
                                QuerySource.KEYWORD_LOCAL -> "วิเคราะห์คีย์เวิร์ด"
                                QuerySource.GEMINI_AI -> "Gemini AI"
                                QuerySource.NOT_FOUND -> "ไม่พบข้อมูล"
                            }
                            Text(
                                text = sourceLabel,
                                fontSize = 10.sp,
                                color = if (message.source == QuerySource.NOT_FOUND) ErrorRed else TerminalGreen
                            )
                        }
                    }

                    Text(
                        text = message.text,
                        color = if (isUser) Color(0xFFECFEFF) else TextPrimary,
                        fontSize = 14.sp,
                        lineHeight = 20.sp
                    )

                    if (!isUser) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 6.dp),
                            horizontalArrangement = Arrangement.End
                        ) {
                            IconButton(
                                onClick = onReplay,
                                modifier = Modifier.size(28.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.VolumeUp,
                                    contentDescription = "เล่นเสียงตอบซ้ำ",
                                    tint = TextSecondary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
