package com.example.widget.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Delete
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.material.icons.rounded.MoreVert
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Repeat
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.widget.data.model.CountdownEvent
import com.example.widget.ui.util.ColorUtils
import com.example.widget.ui.util.IconUtils
import com.example.widget.ui.util.TimeUtils
import coil.compose.AsyncImage
import android.net.Uri
import androidx.compose.ui.layout.ContentScale

@Composable
fun CountdownCard(
    event: CountdownEvent,
    currentTimestampMillis: Long,
    onEdit: () -> Unit,
    onDelete: () -> Unit,
    onTogglePin: () -> Unit,
    modifier: Modifier = Modifier,
    isCompact: Boolean = false
) {
    val backgroundBrush = remember(event.backgroundPresetId, event.customBackgroundColorHex) {
        ColorUtils.getBrushForEvent(event.backgroundPresetId, event.customBackgroundColorHex)
    }

    val textColor = remember(event.textColorHex) {
        ColorUtils.parseHexColor(event.textColorHex, Color.White)
    }

    val icon = remember(event.iconName) {
        IconUtils.getIconForName(event.iconName)
    }

    val timeRemaining = remember(event.targetTimestampMillis, currentTimestampMillis, event.countDirection, event.showMilliseconds) {
        TimeUtils.calculateTimeRemaining(
            targetTimestampMillis = event.targetTimestampMillis,
            currentTimestampMillis = currentTimestampMillis,
            countDirection = event.countDirection,
            showMilliseconds = event.showMilliseconds
        )
    }

    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .clickable { onEdit() },
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
        ) {
            if (!event.backgroundImageUri.isNullOrBlank()) {
                AsyncImage(
                    model = Uri.parse(event.backgroundImageUri),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.matchParentSize()
                )
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(Color.Black.copy(alpha = 0.4f))
                )
            } else {
                Box(
                    modifier = Modifier
                        .matchParentSize()
                        .background(backgroundBrush)
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(18.dp)
            ) {
                // Header: Category, Icon, Pin, Menu
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = CircleShape,
                            color = textColor.copy(alpha = 0.2f),
                            modifier = Modifier.size(36.dp)
                        ) {
                            Box(contentAlignment = Alignment.Center) {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = null,
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(12.dp),
                            color = textColor.copy(alpha = 0.18f),
                            modifier = Modifier.padding(vertical = 2.dp)
                        ) {
                            Text(
                                text = event.category,
                                color = textColor,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium,
                                modifier = Modifier.padding(horizontal = 10.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                            onClick = onTogglePin,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.PushPin,
                                contentDescription = if (event.isPinned) "Unpin" else "Pin",
                                tint = if (event.isPinned) textColor else textColor.copy(alpha = 0.4f),
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        Box {
                            IconButton(
                                onClick = { showMenu = true },
                                modifier = Modifier.size(36.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.MoreVert,
                                    contentDescription = "Options",
                                    tint = textColor,
                                    modifier = Modifier.size(20.dp)
                                )
                            }

                            DropdownMenu(
                                expanded = showMenu,
                                onDismissRequest = { showMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Edit Event") },
                                    leadingIcon = { Icon(Icons.Rounded.Edit, contentDescription = null) },
                                    onClick = {
                                        showMenu = false
                                        onEdit()
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Delete Event") },
                                    leadingIcon = { Icon(Icons.Rounded.Delete, contentDescription = null, tint = MaterialTheme.colorScheme.error) },
                                    onClick = {
                                        showMenu = false
                                        onDelete()
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Title
                Text(
                    text = event.title,
                    color = textColor,
                    fontSize = if (isCompact) 18.sp else 22.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )

                Text(
                    text = TimeUtils.formatDateTime(event.targetTimestampMillis),
                    color = textColor.copy(alpha = 0.75f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Normal
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Dynamic Live Ticker Section
                if (timeRemaining.isArrived) {
                    Surface(
                        shape = RoundedCornerShape(16.dp),
                        color = textColor.copy(alpha = 0.25f),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.padding(14.dp)
                        ) {
                            Text(
                                text = "🎉 Event Has Arrived! 🎉",
                                color = textColor,
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        TimeUnitBox(
                            value = timeRemaining.days.toString().padStart(2, '0'),
                            label = "DAYS",
                            textColor = textColor
                        )
                        TimeUnitBox(
                            value = timeRemaining.hours.toString().padStart(2, '0'),
                            label = "HOURS",
                            textColor = textColor
                        )
                        TimeUnitBox(
                            value = timeRemaining.minutes.toString().padStart(2, '0'),
                            label = "MINS",
                            textColor = textColor
                        )
                        TimeUnitBox(
                            value = timeRemaining.seconds.toString().padStart(2, '0'),
                            label = "SECS",
                            textColor = textColor
                        )
                        if (event.showMilliseconds) {
                            TimeUnitBox(
                                value = timeRemaining.millis.toString().padStart(2, '0'),
                                label = "MS",
                                textColor = textColor
                            )
                        }
                    }
                }

                if (event.repeatInterval != "NONE") {
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Repeat,
                            contentDescription = null,
                            tint = textColor.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Text(
                            text = "Repeats ${event.repeatInterval.lowercase()}",
                            color = textColor.copy(alpha = 0.7f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun TimeUnitBox(
    value: String,
    label: String,
    textColor: Color
) {
    Surface(
        shape = RoundedCornerShape(14.dp),
        color = textColor.copy(alpha = 0.18f),
        modifier = Modifier.padding(2.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)
        ) {
            Text(
                text = value,
                color = textColor,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Text(
                text = label,
                color = textColor.copy(alpha = 0.75f),
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
