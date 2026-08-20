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
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
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
import com.example.hprams.theme.*

@Composable
fun getAppTextColor(): Color {
    return if (isAppDarkTheme()) DarkTextColor else LightTextColor
}

@Composable
fun getAppSubTextColor(): Color {
    return if (isAppDarkTheme()) DarkSubTextColor else LightSubTextColor
}

@Composable
fun GlassBackground(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val isDark = isAppDarkTheme()
    val bgColor = if (isDark) DarkBg else LightBg
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(bgColor)
    ) {
        content()
    }
}

// Raised neumorphic modifier
fun Modifier.neumorphicCard(isDark: Boolean, cornerRadius: Float = 48f): Modifier = this.drawBehind {
    val shadowColor = if (isDark) DarkShadow else LightShadow
    val highlightColor = if (isDark) DarkHighlight else LightHighlight
    
    // Bottom-right dark shadow
    drawRoundRect(
        color = shadowColor.copy(alpha = 0.8f),
        topLeft = Offset(12f, 12f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
    // Top-left light highlight
    drawRoundRect(
        color = highlightColor.copy(alpha = 0.9f),
        topLeft = Offset(-12f, -12f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
}

// Sunken neumorphic modifier (for fields)
fun Modifier.neumorphicSunken(isDark: Boolean, cornerRadius: Float = 36f): Modifier = this.drawBehind {
    val shadowColor = if (isDark) DarkShadow else LightShadow
    val highlightColor = if (isDark) DarkHighlight else LightHighlight
    
    // Top-left dark shadow
    drawRoundRect(
        color = shadowColor.copy(alpha = 0.5f),
        topLeft = Offset(4f, 4f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
    // Bottom-right light highlight
    drawRoundRect(
        color = highlightColor.copy(alpha = 0.6f),
        topLeft = Offset(-4f, -4f),
        size = size,
        cornerRadius = CornerRadius(cornerRadius, cornerRadius)
    )
}

@Composable
fun GlassCard(
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val isDark = isAppDarkTheme()
    val bgColor = if (isDark) DarkBg else LightBg
    Box(
        modifier = modifier
            .neumorphicCard(isDark)
            .clip(RoundedCornerShape(16.dp))
            .background(bgColor)
            .padding(16.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            content = content
        )
    }
}

@Composable
fun GlassButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: @Composable (() -> Unit)? = null
) {
    val isDark = isAppDarkTheme()
    val bgColor = if (isDark) DarkBg else LightBg
    val shadowColor = if (isDark) DarkShadow else LightShadow
    val highlightColor = if (isDark) DarkHighlight else LightHighlight
    val contentColor = if (enabled) AccentColor else getAppSubTextColor()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(52.dp)
            .drawBehind {
                val cornerRadiusPx = 12.dp.toPx()
                if (enabled) {
                    // Raised shadow
                    drawRoundRect(
                        color = shadowColor.copy(alpha = 0.8f),
                        topLeft = Offset(8f, 8f),
                        size = size,
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                    drawRoundRect(
                        color = highlightColor.copy(alpha = 0.9f),
                        topLeft = Offset(-8f, -8f),
                        size = size,
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                } else {
                    // Flat/sunken shadow
                    drawRoundRect(
                        color = shadowColor.copy(alpha = 0.3f),
                        topLeft = Offset(2f, 2f),
                        size = size,
                        cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                    )
                }
            }
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text(
                text = text.uppercase(),
                color = contentColor,
                style = MaterialTheme.typography.labelMedium,
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
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
    val bgColor = if (isDark) DarkBg else LightBg
    val shadowColor = if (isDark) DarkShadow else LightShadow
    val highlightColor = if (isDark) DarkHighlight else LightHighlight
    val textColor = getAppTextColor()

    Box(
        modifier = modifier
            .fillMaxWidth()
            .drawBehind {
                val cornerRadiusPx = 12.dp.toPx()
                drawRoundRect(
                    color = shadowColor.copy(alpha = 0.6f),
                    topLeft = Offset(6f, 6f),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
                drawRoundRect(
                    color = highlightColor.copy(alpha = 0.7f),
                    topLeft = Offset(-6f, -6f),
                    size = size,
                    cornerRadius = CornerRadius(cornerRadiusPx, cornerRadiusPx)
                )
            }
            .clip(RoundedCornerShape(12.dp))
            .background(bgColor)
            .clickable(onClick = onClick)
            .padding(14.dp)
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
    val bgColor = if (isDark) DarkBg else LightBg
    val labelColor = if (isFocused) AccentColor else getAppSubTextColor()
    
    Column(modifier = modifier.fillMaxWidth()) {
        Text(
            text = label,
            color = labelColor,
            style = MaterialTheme.typography.labelMedium,
            modifier = Modifier.padding(start = 4.dp, bottom = 4.dp),
            fontFamily = FontFamily.Monospace
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = if (placeholder.isNotEmpty()) {
                {
                    Text(
                        text = placeholder,
                        color = getAppSubTextColor().copy(alpha = 0.5f),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            } else null,
            leadingIcon = leadingIcon?.let { icon ->
                {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = if (isFocused) AccentColor else getAppSubTextColor().copy(alpha = 0.5f)
                    )
                }
            },
            trailingIcon = trailingIcon,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = bgColor,
                unfocusedContainerColor = bgColor,
                focusedBorderColor = AccentColor,
                unfocusedBorderColor = if (isDark) DarkShadow else LightShadow,
                cursorColor = AccentColor,
                focusedTextColor = getAppTextColor(),
                unfocusedTextColor = getAppTextColor()
            ),
            modifier = Modifier
                .fillMaxWidth()
                .neumorphicSunken(isDark)
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
    val activeTint = AccentColor
    val bgColor = if (isDark) DarkBg else LightBg

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(65.dp)
            .neumorphicCard(isDark)
            .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
            .background(bgColor)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceAround
    ) {
        val activeBg = if (isDark) DarkHighlight else LightShadow

        // Home
        val homeActive = activeTab == "home"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onHomeClick() }
                .run {
                    if (homeActive) background(activeBg).padding(horizontal = 14.dp, vertical = 6.dp) else this
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
                .clip(RoundedCornerShape(8.dp))
                .clickable { onRoomsClick() }
                .run {
                    if (roomsActive) background(activeBg).padding(horizontal = 14.dp, vertical = 6.dp) else this
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
                .clip(RoundedCornerShape(8.dp))
                .clickable { onFinanceClick() }
                .run {
                    if (financeActive) background(activeBg).padding(horizontal = 14.dp, vertical = 6.dp) else this
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

        // Support
        val supportActive = activeTab == "support"
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .clickable { onSupportClick() }
                .run {
                    if (supportActive) background(activeBg).padding(horizontal = 14.dp, vertical = 6.dp) else this
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
