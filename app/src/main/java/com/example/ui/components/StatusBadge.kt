package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.GhanaGoldAccent
import com.example.ui.theme.StatusClosingSoon
import com.example.ui.theme.StatusOpen
import com.example.ui.theme.StatusUpcoming

@Composable
fun StatusBadge(
    status: String,
    modifier: Modifier = Modifier
) {
    val (bgColor, textColor, label) = when (status.lowercase()) {
        "open" -> Triple(StatusOpen.copy(alpha = 0.15f), StatusOpen, "OPEN")
        "upcoming" -> Triple(StatusUpcoming.copy(alpha = 0.15f), StatusUpcoming, "UPCOMING")
        "closing_soon" -> Triple(StatusClosingSoon.copy(alpha = 0.18f), StatusClosingSoon, "CLOSING SOON")
        "closed" -> Triple(Color(0xFF64748B).copy(alpha = 0.15f), Color(0xFF64748B), "CLOSED")
        else -> Triple(Color(0xFF8B5CF6).copy(alpha = 0.15f), Color(0xFF8B5CF6), status.uppercase())
    }

    Box(
        modifier = modifier
            .background(bgColor, RoundedCornerShape(6.dp))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun VerificationPill(
    verificationStatus: String,
    modifier: Modifier = Modifier
) {
    val isDemo = verificationStatus.equals("demo", ignoreCase = true)
    val text = if (isDemo) "DEMO DATA" else "VERIFIED"
    val color = if (isDemo) Color(0xFFEA580C) else GhanaGoldAccent

    Box(
        modifier = modifier
            .border(1.dp, color.copy(alpha = 0.6f), RoundedCornerShape(6.dp))
            .background(color.copy(alpha = 0.12f), RoundedCornerShape(6.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
    ) {
        Text(
            text = text,
            color = color,
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.4.sp
        )
    }
}
