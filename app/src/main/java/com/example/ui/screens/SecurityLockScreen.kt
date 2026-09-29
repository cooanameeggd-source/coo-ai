package com.example.ui.screens

import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Backspace
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.AiViewModel
import com.example.ui.components.HologramOrb
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

@Composable
fun SecurityLockScreen(
    viewModel: AiViewModel,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val enteredCode by viewModel.enteredPasscode.collectAsState()
    val errorCode by viewModel.passcodeError.collectAsState()
    val isSpeaking by viewModel.ttsManager.isSpeaking.collectAsState()
    val defaultCode = viewModel.currentPasscode.collectAsState().value

    fun triggerVibration() {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
                vibratorManager.defaultVibrator.vibrate(VibrationEffect.createOneShot(50, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
                @Suppress("DEPRECATION")
                vibrator.vibrate(50)
            }
        } catch (_: Exception) {}
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(
                        SciFiBackground,
                        Color(0xFF0F172A),
                        Color(0xFF070B14)
                    )
                )
            )
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .widthIn(max = 440.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Sci-Fi AI Orb Header
            HologramOrb(isSpeaking = isSpeaking)

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = "AKIN AI SECURITY SYSTEM",
                color = CyanPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp,
                fontFamily = FontFamily.Monospace
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "โปรดป้อนหมายเลขรหัสความปลอดภัย",
                color = TextPrimary,
                fontSize = 20.sp,
                fontWeight = FontWeight.SemiBold,
                textAlign = TextAlign.Center
            )

            Text(
                text = "d = int(input(\"โปรดป้อนหมายเลข\"))  # A = $defaultCode",
                color = TextMuted,
                fontSize = 12.sp,
                fontFamily = FontFamily.Monospace,
                modifier = Modifier.padding(top = 4.dp)
            )

            Spacer(modifier = Modifier.height(20.dp))

            // PIN Display Field
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .border(
                        width = 1.5.dp,
                        color = if (errorCode != null) ErrorRed else CyanPrimary.copy(alpha = 0.6f),
                        shape = RoundedCornerShape(16.dp)
                    ),
                colors = CardDefaults.cardColors(
                    containerColor = SciFiSurface
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp, horizontal = 20.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    if (enteredCode.isEmpty()) {
                        Text(
                            text = "ใส่รหัส 7 หลัก...",
                            color = TextMuted,
                            fontSize = 18.sp,
                            fontFamily = FontFamily.Monospace
                        )
                    } else {
                        enteredCode.forEach { char ->
                            Text(
                                text = char.toString(),
                                color = if (errorCode != null) ErrorRed else CyanPrimary,
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                fontFamily = FontFamily.Monospace,
                                modifier = Modifier.padding(horizontal = 4.dp)
                            )
                        }
                    }
                }
            }

            // Error or Status text
            AnimatedVisibility(
                visible = errorCode != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Text(
                    text = errorCode ?: "",
                    color = ErrorRed,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .testTag("passcode_error_text")
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Quick Auto-fill button for test convenience
            OutlinedButton(
                onClick = {
                    triggerVibration()
                    viewModel.onPasscodeSetText(defaultCode)
                },
                modifier = Modifier.testTag("fill_default_code_button"),
                shape = RoundedCornerShape(20.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = HologramCyan
                )
            ) {
                Icon(
                    imageVector = Icons.Default.Key,
                    contentDescription = "ใส่รหัสตัวอย่าง",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = "ใส่รหัสของฉัน: $defaultCode", fontSize = 13.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Numeric Keypad (1 to 9, C, 0, Backspace)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                val rows = listOf(
                    listOf("1", "2", "3"),
                    listOf("4", "5", "6"),
                    listOf("7", "8", "9"),
                    listOf("C", "0", "BACK")
                )

                for (row in rows) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        for (key in row) {
                            KeypadButton(
                                text = key,
                                modifier = Modifier.weight(1f),
                                onClick = {
                                    triggerVibration()
                                    when (key) {
                                        "C" -> viewModel.onPasscodeClear()
                                        "BACK" -> viewModel.onPasscodeBackspace()
                                        else -> viewModel.onPasscodeDigit(key)
                                    }
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Unlock Button
            Button(
                onClick = {
                    triggerVibration()
                    viewModel.verifyPasscode()
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .padding(horizontal = 24.dp)
                    .testTag("unlock_ai_button"),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = CyanPrimary,
                    contentColor = Color(0xFF00363D)
                ),
                enabled = enteredCode.isNotBlank()
            ) {
                Icon(
                    imageVector = Icons.Default.Shield,
                    contentDescription = "ยืนยันรหัส",
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "ตรวจสอบและปลดล็อก AI",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

@Composable
fun KeypadButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier
            .height(54.dp)
            .testTag("keypad_btn_$text"),
        shape = RoundedCornerShape(14.dp),
        color = when (text) {
            "C" -> SciFiSurfaceVariant.copy(alpha = 0.8f)
            "BACK" -> SciFiSurfaceVariant.copy(alpha = 0.8f)
            else -> SciFiSurface
        },
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            SciFiSurfaceVariant
        )
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            when (text) {
                "BACK" -> {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Backspace,
                        contentDescription = "ลบตัวเลข",
                        tint = TextSecondary,
                        modifier = Modifier.size(22.dp)
                    )
                }
                "C" -> {
                    Text(
                        text = "ล้าง",
                        color = ErrorRed.copy(alpha = 0.85f),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
                else -> {
                    Text(
                        text = text,
                        color = TextPrimary,
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Medium,
                        fontFamily = FontFamily.Monospace
                    )
                }
            }
        }
    }
}
