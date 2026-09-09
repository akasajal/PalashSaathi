package com.palashsaathi.app.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.palashsaathi.app.R
import kotlinx.coroutines.delay

@Composable
fun LandingSynopsisScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier
) {
    // Automatically fade out after 2.4 seconds
    LaunchedEffect(Unit) {
        delay(2400L)
        onDismiss()
    }

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.04f,
        animationSpec = infiniteRepeatable(
            animation = tween(1000, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseScale"
    )

    // Dark forest teal/green background matching the logo color palette
    val bgGradient = Brush.verticalGradient(
        colors = listOf(
            Color(0xFF041E17),
            Color(0xFF072C22),
            Color(0xFF0D3C30)
        )
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgGradient)
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null,
                onClick = onDismiss
            )
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            // Branded Logo with subtle glow and rounding
            Surface(
                shape = RoundedCornerShape(24.dp),
                color = Color.Transparent,
                modifier = Modifier
                    .scale(pulseScale)
                    .size(130.dp)
                    .shadow(16.dp, RoundedCornerShape(24.dp))
                    .border(2.dp, Color(0xFFE65100).copy(alpha = 0.8f), RoundedCornerShape(24.dp))
            ) {
                Image(
                    painter = painterResource(id = R.drawable.app_logo),
                    contentDescription = "PalashSaathi Logo",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(RoundedCornerShape(24.dp))
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // App Title
            Text(
                text = "PalashSaathi",
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = Color(0xFFFFFFFF),
                letterSpacing = 0.5.sp
            )

            // Ol Chiki Subtitle
            Text(
                text = "ᱯᱟᱞᱟᱥ ᱥᱟᱛᱷᱤ  •  पलाश साथी",
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFFFFB74D) // Warm golden amber
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Core Mission / Synopsis
            Text(
                text = "AI-Powered Vernacular Pedagogy & Offline Real-Time Translation",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFE0E0E0),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Key Synopsis Highlights Grid
            Column(
                verticalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                SynopsisPill(
                    icon = Icons.Default.Translate,
                    title = "Santali Primary Pedagogy",
                    subtitle = "Dual-script: Ol Chiki (ᱚᱞ ᱪᱤᱠᱤ) & Devanagari (देवनागरी)"
                )

                SynopsisPill(
                    icon = Icons.Default.Storage,
                    title = "20,000 Offline Sentences Indexed",
                    subtitle = "Fast bidirectional search across all screens & worksheets"
                )

                SynopsisPill(
                    icon = Icons.Default.GraphicEq,
                    title = "Acoustic Speech Synthesis",
                    subtitle = "Instant pronunciation with comparative Ho & Mundari subtitles"
                )

                SynopsisPill(
                    icon = Icons.Default.School,
                    title = "Bilingual FLN Worksheets",
                    subtitle = "Printable A4 exercises in numeracy, literacy & corpus reading"
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Footer Badge: Offline & Tap to enter
            Surface(
                color = Color(0xFF1B5E20).copy(alpha = 0.85f),
                shape = RoundedCornerShape(20.dp),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF4CAF50).copy(alpha = 0.6f))
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = Color(0xFF81C784),
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "100% Offline Edge Engine • Tap anywhere to start",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFFE8F5E9)
                    )
                }
            }
        }
    }
}

@Composable
private fun SynopsisPill(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Surface(
        color = Color(0xFF0F362C).copy(alpha = 0.75f),
        shape = RoundedCornerShape(12.dp),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFF2E7D32).copy(alpha = 0.4f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                color = Color(0xFFE65100).copy(alpha = 0.25f),
                shape = CircleShape,
                modifier = Modifier.size(34.dp)
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = Color(0xFFFFB74D),
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            Column {
                Text(
                    text = title,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = subtitle,
                    fontSize = 10.sp,
                    color = Color(0xFFB0BEC5)
                )
            }
        }
    }
}
