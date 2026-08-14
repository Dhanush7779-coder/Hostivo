package com.example.hprams.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import com.example.hprams.theme.BackgroundGradEnd
import com.example.hprams.theme.BackgroundGradStart
import com.example.hprams.theme.isAppDarkTheme

@Composable
fun getAppTextColor(): Color {
    return if (isAppDarkTheme()) Color.White else Color(0xFF101415)
}

@Composable
fun getAppSubTextColor(): Color {
    return if (isAppDarkTheme()) Color(0xFFB9CAC8) else Color(0xFF4A5568)
}

@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isAppDarkTheme()
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(
                Brush.linearGradient(
                    colors = if (isDark) listOf(BackgroundGradStart, BackgroundGradEnd)
                             else listOf(Color(0xFFEAF5F4), Color(0xFFCFEBE8))
                )
            )
    ) {
        // Ambient Blob 1 (Top Left)
        Box(
            modifier = Modifier
                .offset(x = (-80).dp, y = (-80).dp)
                .size(320.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f)
                            else Color(0xFF00ACC1).copy(alpha = 0.15f),
                            Color.Transparent
                        )
                    )
                )
        )
        // Ambient Blob 2 (Bottom Right)
        Box(
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .offset(x = 100.dp, y = 100.dp)
                .size(420.dp)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            if (isDark) Color(0xFF027E3D).copy(alpha = 0.12f)
                            else Color(0xFF4CAF50).copy(alpha = 0.12f),
                            Color.Transparent
                        )
                    )
                )
        )
        content()

        // Floating Sun/Moon Theme Toggle Icon (Section 2)
        IconButton(
            onClick = {
                com.example.hprams.theme.ThemeManager.themeSetting = if (isDark) "Light" else "Dark"
            },
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 45.dp, end = 16.dp)
                .size(40.dp)
                .background(
                    if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.05f),
                    RoundedCornerShape(20.dp)
                )
        ) {
            Icon(
                imageVector = if (isDark) Icons.Default.WbSunny else Icons.Default.NightsStay,
                contentDescription = "Toggle Theme Preference",
                tint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isAppDarkTheme()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(
                if (isDark) Color.White.copy(alpha = 0.05f)
                else Color.White.copy(alpha = 0.6f)
            )
            .border(
                BorderStroke(
                    width = 1.dp,
                    brush = Brush.linearGradient(
                        colors = if (isDark) listOf(
                            Color.White.copy(alpha = 0.15f),
                            Color.White.copy(alpha = 0.02f)
                        ) else listOf(
                            Color.Black.copy(alpha = 0.15f),
                            Color.Black.copy(alpha = 0.02f)
                        )
                    )
                ),
                shape = RoundedCornerShape(24.dp)
            )
            .padding(24.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(16.dp),
            content = content
        )
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: @Composable (() -> Unit)? = null
) {
    val isDark = isAppDarkTheme()
    Button(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent
        ),
        contentPadding = PaddingValues(vertical = 16.dp, horizontal = 24.dp),
        modifier = modifier
            .fillMaxWidth()
            .background(
                Brush.linearGradient(
                    colors = if (isDark) listOf(
                        Color(0xFF29FCF3).copy(alpha = 0.8f),
                        Color(0xFF29FCF3).copy(alpha = 0.1f)
                    ) else listOf(
                        Color(0xFF00807A),
                        Color(0xFF006A66).copy(alpha = 0.7f)
                    )
                ),
                shape = RoundedCornerShape(16.dp)
            )
            .border(
                BorderStroke(1.dp, if (isDark) Color(0xFF29FCF3).copy(alpha = 0.5f) else Color(0xFF006A66).copy(alpha = 0.5f)),
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = text.uppercase(),
                color = if (isDark) Color(0xFF003735) else Color.White,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 2.sp
            )
            if (icon != null) {
                Spacer(modifier = Modifier.width(8.dp))
                icon()
            }
        }
    }
}

@Composable
fun GhostButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    leadingIcon: @Composable (() -> Unit)? = null,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    val isDark = isAppDarkTheme()
    val textColor = getAppTextColor()
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(Color.Transparent)
            .border(
                BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.15f)),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable(onClick = onClick)
            .padding(16.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth()
        ) {
            if (leadingIcon != null) {
                leadingIcon()
                Spacer(modifier = Modifier.width(12.dp))
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = text,
                    color = textColor,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
            if (trailingIcon != null) {
                trailingIcon()
            }
        }
    }
}

@Composable
fun GlassTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    trailingIcon: @Composable (() -> Unit)? = null
) {
    var isFocused by remember { mutableStateOf(false) }
    val isDark = isAppDarkTheme()
    val labelColor = if (isFocused) {
        if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
    } else {
        if (isDark) Color(0xFFE0E3E5) else Color(0xFF4A5568)
    }
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 8.dp),
            fontFamily = FontFamily.Monospace
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder.isNotEmpty()) {
                {
                    Text(
                        text = placeholder,
                        color = if (isDark) Color(0xFFB9CAC8).copy(alpha = 0.5f) else Color(0xFF4A5568).copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else null,
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isFocused) {
                            if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
                        } else {
                            if (isDark) Color(0xFFB9CAC8).copy(alpha = 0.5f) else Color(0xFF4A5568).copy(alpha = 0.5f)
                        }
                    )
                }
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            shape = RoundedCornerShape(16.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = if (isDark) Color.White.copy(alpha = 0.08f) else Color.Black.copy(alpha = 0.03f),
                unfocusedContainerColor = if (isDark) Color.White.copy(alpha = 0.05f) else Color.Black.copy(alpha = 0.01f),
                focusedBorderColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                unfocusedBorderColor = if (isDark) Color.White.copy(alpha = 0.15f) else Color.Black.copy(alpha = 0.15f),
                cursorColor = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66),
                focusedTextColor = if (isDark) Color(0xFFE0E3E5) else Color(0xFF101415),
                unfocusedTextColor = if (isDark) Color(0xFFE0E3E5) else Color(0xFF101415)
            ),
            modifier = Modifier
                .fillMaxWidth()
                .onFocusChanged { isFocused = it.isFocused }
        )
    }
}

@Composable
fun StudentBottomBar(
    activeTab: String,
    onHomeClick: () -> Unit,
    onRoomsClick: () -> Unit,
    onFinanceClick: () -> Unit,
    onProfileClick: () -> Unit,
    onSupportClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val isDark = isAppDarkTheme()
    val subTextColor = getAppSubTextColor()
    val activeTint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)
    val activeBg = activeTint.copy(alpha = 0.1f)

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .background(if (isDark) Color.White.copy(alpha = 0.05f) else Color.White.copy(alpha = 0.9f))
            .border(
                BorderStroke(1.dp, if (isDark) Color.White.copy(alpha = 0.1f) else Color.Black.copy(alpha = 0.1f)),
                shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
            )
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val activeBg = if (isDark) Color(0xFF29FCF3).copy(alpha = 0.15f) else Color(0xFF006A66).copy(alpha = 0.15f)
        val activeTint = if (isDark) Color(0xFF29FCF3) else Color(0xFF006A66)

        // Home
        val homeActive = activeTab == "home"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onHomeClick() }
                .run {
                    if (homeActive) background(activeBg).padding(horizontal = 10.dp, vertical = 4.dp) else this
                }
        ) {
            Icon(
                imageVector = Icons.Default.Dashboard,
                contentDescription = "Home",
                tint = if (homeActive) activeTint else subTextColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Home",
                color = if (homeActive) activeTint else subTextColor,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
        }

        // Rooms
        val roomsActive = activeTab == "rooms"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onRoomsClick() }
                .run {
                    if (roomsActive) background(activeBg).padding(horizontal = 10.dp, vertical = 4.dp) else this
                }
        ) {
            Icon(
                imageVector = Icons.Default.Bed,
                contentDescription = "Rooms",
                tint = if (roomsActive) activeTint else subTextColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Rooms",
                color = if (roomsActive) activeTint else subTextColor,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
        }

        // Finance
        val financeActive = activeTab == "finance"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onFinanceClick() }
                .run {
                    if (financeActive) background(activeBg).padding(horizontal = 10.dp, vertical = 4.dp) else this
                }
        ) {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = "Finance",
                tint = if (financeActive) activeTint else subTextColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Finance",
                color = if (financeActive) activeTint else subTextColor,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
        }

        // Profile
        val profileActive = activeTab == "profile"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onProfileClick() }
                .run {
                    if (profileActive) background(activeBg).padding(horizontal = 10.dp, vertical = 4.dp) else this
                }
        ) {
            Icon(
                imageVector = Icons.Default.Person,
                contentDescription = "Profile",
                tint = if (profileActive) activeTint else subTextColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Profile",
                color = if (profileActive) activeTint else subTextColor,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
        }

        // Support
        val supportActive = activeTab == "support"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(12.dp))
                .clickable { onSupportClick() }
                .run {
                    if (supportActive) background(activeBg).padding(horizontal = 10.dp, vertical = 4.dp) else this
                }
        ) {
            Icon(
                imageVector = Icons.Default.SupportAgent,
                contentDescription = "Support",
                tint = if (supportActive) activeTint else subTextColor,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = "Support",
                color = if (supportActive) activeTint else subTextColor,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                fontSize = 9.sp
            )
        }
    }
}
