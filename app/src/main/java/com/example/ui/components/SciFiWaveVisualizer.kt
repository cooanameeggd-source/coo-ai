package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import com.example.ui.theme.CyanPrimary
import com.example.ui.theme.NeonPurple
import kotlin.math.sin

@Composable
fun SciFiWaveVisualizer(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier,
    primaryColor: Color = CyanPrimary,
    accentColor: Color = NeonPurple
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveTransition")

    val phase by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (2 * Math.PI).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 800 else 3000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "phaseAnim"
    )

    val amplitudeMultiplier by infiniteTransition.animateFloat(
        initialValue = if (isSpeaking) 0.8f else 0.2f,
        targetValue = if (isSpeaking) 1.2f else 0.35f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ampAnim"
    )

    Canvas(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
    ) {
        val width = size.width
        val height = size.height
        val centerY = height / 2f
        val points = 60
        val step = width / points

        // Draw background faint glow lines
        for (waveIndex in 0..2) {
            val wavePhase = phase + (waveIndex * 0.8f)
            val currentAmp = (height * 0.35f) * amplitudeMultiplier * (1f - (waveIndex * 0.25f))
            val waveColor = if (waveIndex == 0) primaryColor else accentColor
            val alpha = if (isSpeaking) 0.85f - (waveIndex * 0.25f) else 0.3f - (waveIndex * 0.08f)

            var prevX = 0f
            var prevY = centerY

            for (i in 0..points) {
                val x = i * step
                // Bell curve attenuation on edges
                val envelope = sin((i.toFloat() / points) * Math.PI).toFloat()
                val y = centerY + sin((x * 0.04f) + wavePhase).toFloat() * currentAmp * envelope

                if (i > 0) {
                    drawLine(
                        color = waveColor.copy(alpha = alpha.coerceIn(0f, 1f)),
                        start = Offset(prevX, prevY),
                        end = Offset(x, y),
                        strokeWidth = if (waveIndex == 0) 3.5f else 2.0f
                    )
                }
                prevX = x
                prevY = y
            }
        }
    }
}

@Composable
fun HologramOrb(
    isSpeaking: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "orbTransition")

    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 0.92f,
        targetValue = if (isSpeaking) 1.15f else 1.02f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = if (isSpeaking) 500 else 1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAnim"
    )

    val rotationAngle by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 8000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "rotAnim"
    )

    Box(
        modifier = modifier.size(100.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.size(90.dp)) {
            val center = Offset(size.width / 2, size.height / 2)
            val radius = (size.width / 2.5f) * pulseScale

            // Glowing Outer Ring
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        CyanPrimary.copy(alpha = if (isSpeaking) 0.5f else 0.25f),
                        NeonPurple.copy(alpha = if (isSpeaking) 0.3f else 0.1f),
                        Color.Transparent
                    ),
                    center = center,
                    radius = radius * 1.4f
                ),
                radius = radius * 1.4f
            )

            // Inner Ring
            drawCircle(
                color = CyanPrimary.copy(alpha = if (isSpeaking) 0.9f else 0.6f),
                radius = radius,
                style = Stroke(width = if (isSpeaking) 4f else 2f)
            )

            // Core
            drawCircle(
                brush = Brush.radialGradient(
                    colors = listOf(
                        Color.White,
                        CyanPrimary,
                        NeonPurple
                    ),
                    center = center,
                    radius = radius * 0.65f
                ),
                radius = radius * 0.65f
            )
        }
    }
}
