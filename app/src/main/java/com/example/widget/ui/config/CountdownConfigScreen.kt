package com.example.widget.ui.config

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material.icons.rounded.AccessTime
import androidx.compose.material.icons.rounded.CalendarToday
import androidx.compose.material.icons.rounded.Check
import androidx.compose.material.icons.rounded.ColorLens
import androidx.compose.material.icons.rounded.FormatPaint
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.Palette
import androidx.compose.material.icons.rounded.Style
import androidx.compose.material.icons.rounded.AddPhotoAlternate
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import coil.compose.AsyncImage
import androidx.compose.ui.layout.ContentScale
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.InputChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePicker
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.StylePresets
import com.example.widget.ui.dashboard.CountdownCard
import com.example.widget.ui.util.ColorUtils
import com.example.widget.ui.util.IconUtils
import com.example.widget.ui.util.TimeUtils
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import java.util.Calendar
import java.util.TimeZone

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun CountdownConfigScreen(
    viewModel: CountdownConfigViewModel,
    eventId: Long?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(eventId) {
        viewModel.loadEvent(eventId)
    }

    var currentTimestampMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var showCategoryDropdown by remember { mutableStateOf(false) }
    var showRepeatDropdown by remember { mutableStateOf(false) }
    var customHexInput by remember(uiState.customBackgroundColorHex) {
        mutableStateOf(uiState.customBackgroundColorHex ?: "")
    }

    val context = androidx.compose.ui.platform.LocalContext.current
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            try {
                val fileName = "bg_${System.currentTimeMillis()}.jpg"
                val destFile = java.io.File(context.filesDir, fileName)
                context.contentResolver.openInputStream(uri)?.use { input ->
                    destFile.outputStream().use { output ->
                        input.copyTo(output)
                    }
                }
                viewModel.onBackgroundImageChange(Uri.fromFile(destFile).toString())
            } catch (e: Exception) {
                e.printStackTrace()
                viewModel.onBackgroundImageChange(uri.toString())
            }
        }
    }

    // Dynamic Live Ticker Loop for Widget Preview
    LaunchedEffect(Unit) {
        while (isActive) {
            currentTimestampMillis = System.currentTimeMillis()
            delay(100)
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = if (eventId == null || eventId == 0L) "New Countdown" else "Edit Countdown",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Rounded.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    Button(
                        onClick = { viewModel.saveEvent(onSuccess = onNavigateBack) },
                        enabled = !uiState.isSaving && uiState.title.isNotBlank(),
                        modifier = Modifier.padding(end = 8.dp)
                    ) {
                        Icon(Icons.Rounded.Check, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Save")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {

            // Error Banner if validation fails
            uiState.errorMessage?.let { errorMsg ->
                Surface(
                    color = MaterialTheme.colorScheme.errorContainer,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.Info, contentDescription = null, tint = MaterialTheme.colorScheme.onErrorContainer)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(errorMsg, color = MaterialTheme.colorScheme.onErrorContainer, fontSize = 14.sp)
                    }
                }
            }

            // 1. DYNAMIC REAL-TIME LIVE WIDGET PREVIEW
            Column {
                Text(
                    text = "LIVE WIDGET PREVIEW",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 4.dp, bottom = 6.dp)
                )

                val previewEvent = remember(
                    uiState.title,
                    uiState.targetTimestampMillis,
                    uiState.category,
                    uiState.backgroundPresetId,
                    uiState.customBackgroundColorHex,
                    uiState.textColorHex,
                    uiState.iconName,
                    uiState.showMilliseconds,
                    uiState.countDirection,
                    uiState.repeatInterval,
                    uiState.backgroundImageUri
                ) {
                    CountdownEvent(
                        id = uiState.eventId ?: 0L,
                        title = if (uiState.title.isBlank()) "Event Title Preview" else uiState.title,
                        targetTimestampMillis = uiState.targetTimestampMillis,
                        category = uiState.category,
                        backgroundPresetId = uiState.backgroundPresetId,
                        customBackgroundColorHex = uiState.customBackgroundColorHex,
                        textColorHex = uiState.textColorHex,
                        iconName = uiState.iconName,
                        isPinned = uiState.isPinned,
                        showMilliseconds = uiState.showMilliseconds,
                        countDirection = uiState.countDirection,
                        repeatInterval = uiState.repeatInterval,
                        backgroundImageUri = uiState.backgroundImageUri
                    )
                }

                CountdownCard(
                    event = previewEvent,
                    currentTimestampMillis = currentTimestampMillis,
                    onEdit = {},
                    onDelete = {},
                    onTogglePin = {}
                )
            }

            // 2. EVENT DETAILS SECTION
            SectionHeader(title = "Event Details", icon = Icons.Rounded.Info)

            OutlinedTextField(
                value = uiState.title,
                onValueChange = { viewModel.onTitleChange(it) },
                label = { Text("Event Title") },
                placeholder = { Text("e.g., Vacation to Paris, Birthday, New Year") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            // Category Selector
            Text("Category", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StylePresets.categoryList.forEach { category ->
                    FilterChip(
                        selected = uiState.category.equals(category, ignoreCase = true),
                        onClick = { viewModel.onCategoryChange(category) },
                        label = { Text(category) }
                    )
                }
            }

            // 3. TARGET DATE & TIME SECTION
            SectionHeader(title = "Target Date & Time", icon = Icons.Rounded.CalendarToday)

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Date picker trigger card
                Card(
                    onClick = { showDatePicker = true },
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.CalendarToday, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Date", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = TimeUtils.formatDateOnly(uiState.targetTimestampMillis),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                // Time picker trigger card
                Card(
                    onClick = { showTimePicker = true },
                    modifier = Modifier.weight(1f),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainerHigh)
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Rounded.AccessTime, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text("Time", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            Text(
                                text = TimeUtils.formatTimeOnly(uiState.targetTimestampMillis),
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }

            // Options: Direction, Milliseconds, Repeat
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Count Direction", fontWeight = FontWeight.Medium, fontSize = 14.sp)

                SingleChoiceSegmentedButtonRow {
                    SegmentedButton(
                        selected = uiState.countDirection == "COUNTDOWN",
                        onClick = { viewModel.onCountDirectionChange("COUNTDOWN") },
                        shape = SegmentedButtonDefaults.itemShape(index = 0, count = 2)
                    ) {
                        Text("Countdown")
                    }
                    SegmentedButton(
                        selected = uiState.countDirection == "COUNTUP",
                        onClick = { viewModel.onCountDirectionChange("COUNTUP") },
                        shape = SegmentedButtonDefaults.itemShape(index = 1, count = 2)
                    ) {
                        Text("Count Up")
                    }
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text("Show Milliseconds", fontWeight = FontWeight.Medium, fontSize = 14.sp)
                    Text("Displays live .MS ticker", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                Switch(
                    checked = uiState.showMilliseconds,
                    onCheckedChange = { viewModel.onShowMillisecondsChange(it) }
                )
            }

            // Repeat Interval
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Repeat", fontWeight = FontWeight.Medium, fontSize = 14.sp)

                ExposedDropdownMenuBox(
                    expanded = showRepeatDropdown,
                    onExpandedChange = { showRepeatDropdown = !showRepeatDropdown }
                ) {
                    OutlinedTextField(
                        value = uiState.repeatInterval,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = showRepeatDropdown) },
                        modifier = Modifier
                            .width(160.dp)
                            .menuAnchor(),
                        textStyle = MaterialTheme.typography.bodyMedium
                    )

                    ExposedDropdownMenu(
                        expanded = showRepeatDropdown,
                        onDismissRequest = { showRepeatDropdown = false }
                    ) {
                        listOf("NONE", "DAILY", "WEEKLY", "MONTHLY", "YEARLY").forEach { interval ->
                            DropdownMenuItem(
                                text = { Text(interval) },
                                onClick = {
                                    viewModel.onRepeatIntervalChange(interval)
                                    showRepeatDropdown = false
                                }
                            )
                        }
                    }
                }
            }

            // 4. BACKGROUND STYLING SECTION
            SectionHeader(title = "Background Styling", icon = Icons.Rounded.Palette)

            Text("Gradient Presets", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(StylePresets.backgroundPresets) { preset ->
                    val isSelected = uiState.customBackgroundColorHex == null && uiState.backgroundPresetId == preset.id
                    val startColor = ColorUtils.parseHexColor(preset.startColorHex)
                    val endColor = ColorUtils.parseHexColor(preset.endColorHex)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable {
                            viewModel.onBackgroundPresetChange(preset.id)
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(RoundedCornerShape(16.dp))
                                .background(ColorUtils.getBrushForEvent(preset.id, null))
                                .border(
                                    width = if (isSelected) 3.dp else 0.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else Color.Transparent,
                                    shape = RoundedCornerShape(16.dp)
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White)
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(preset.name, fontSize = 11.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }

            Text("Solid Custom Color", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            val solidSwatches = listOf("#121212", "#1A237E", "#0D47A1", "#1B5E20", "#B71C1C", "#4A148C", "#333333")
            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                solidSwatches.forEach { hex ->
                    val color = ColorUtils.parseHexColor(hex)
                    val isSelected = uiState.customBackgroundColorHex == hex

                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(CircleShape)
                            .background(color)
                            .border(
                                width = if (isSelected) 3.dp else 1.dp,
                                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outlineVariant,
                                shape = CircleShape
                            )
                            .clickable { viewModel.onCustomColorChange(hex) },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isSelected) {
                            Icon(Icons.Rounded.Check, contentDescription = null, tint = Color.White, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }

            OutlinedTextField(
                value = customHexInput,
                onValueChange = {
                    customHexInput = it
                    if (it.isBlank()) {
                        viewModel.onCustomColorChange(null)
                    } else if (it.startsWith("#") && (it.length == 7 || it.length == 9)) {
                        viewModel.onCustomColorChange(it)
                    }
                },
                label = { Text("Custom Hex Color (e.g. #FF5722)") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(4.dp))
            Text("Background Image", fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurfaceVariant)

            if (uiState.backgroundImageUri.isNullOrBlank()) {
                OutlinedButton(
                    onClick = { imagePickerLauncher.launch("image/*") },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Rounded.AddPhotoAlternate, contentDescription = null, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Select Background Image from Gallery")
                }
            } else {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        AsyncImage(
                            model = Uri.parse(uiState.backgroundImageUri),
                            contentDescription = "Selected Background Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Row(
                            modifier = Modifier
                                .align(Alignment.BottomEnd)
                                .padding(8.dp),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Button(
                                onClick = { imagePickerLauncher.launch("image/*") }
                            ) {
                                Text("Change")
                            }
                            OutlinedButton(
                                onClick = { viewModel.onBackgroundImageChange(null) }
                            ) {
                                Text("Remove")
                            }
                        }
                    }
                }
            }

            // 5. TEXT COLOR SECTION
            SectionHeader(title = "Text Color", icon = Icons.Rounded.FormatPaint)

            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StylePresets.textColorOptions.forEach { option ->
                    val color = ColorUtils.parseHexColor(option.hex)
                    val isSelected = uiState.textColorHex.equals(option.hex, ignoreCase = true)

                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.clickable { viewModel.onTextColorChange(option.hex) }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(color)
                                .border(
                                    width = if (isSelected) 3.dp else 1.dp,
                                    color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.outline,
                                    shape = CircleShape
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (isSelected) {
                                Icon(
                                    imageVector = Icons.Rounded.Check,
                                    contentDescription = null,
                                    tint = if (option.hex == "#FFFFFF" || option.hex == "#FFFDD0") Color.Black else Color.White,
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(option.name, fontSize = 10.sp)
                    }
                }
            }

            // 6. ICON PICKER SECTION
            SectionHeader(title = "Event Icon", icon = Icons.Rounded.Style)

            Row(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                StylePresets.iconOptions.forEach { iconName ->
                    val iconVector = IconUtils.getIconForName(iconName)
                    val isSelected = uiState.iconName.equals(iconName, ignoreCase = true)

                    Surface(
                        shape = CircleShape,
                        color = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceContainerHigh,
                        modifier = Modifier
                            .size(44.dp)
                            .clickable { viewModel.onIconChange(iconName) }
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = iconVector,
                                contentDescription = iconName,
                                tint = if (isSelected) MaterialTheme.colorScheme.onPrimaryContainer else MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    // DatePicker Dialog
    if (showDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = uiState.targetTimestampMillis
        )

        DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        datePickerState.selectedDateMillis?.let { selectedMillis ->
                            val updatedTimestamp = combineDateAndTime(selectedMillis, uiState.targetTimestampMillis)
                            viewModel.onTargetTimestampChange(updatedTimestamp)
                        }
                        showDatePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DatePicker(state = datePickerState)
        }
    }

    // TimePicker Dialog
    if (showTimePicker) {
        val calendar = remember(uiState.targetTimestampMillis) {
            Calendar.getInstance().apply { timeInMillis = uiState.targetTimestampMillis }
        }
        val timePickerState = rememberTimePickerState(
            initialHour = calendar.get(Calendar.HOUR_OF_DAY),
            initialMinute = calendar.get(Calendar.MINUTE),
            is24Hour = false
        )

        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val updatedTimestamp = updateHourAndMinute(
                            uiState.targetTimestampMillis,
                            timePickerState.hour,
                            timePickerState.minute
                        )
                        viewModel.onTargetTimestampChange(updatedTimestamp)
                        showTimePicker = false
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) {
                    Text("Cancel")
                }
            },
            text = {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    TimePicker(state = timePickerState)
                }
            }
        )
    }
}

@Composable
private fun SectionHeader(title: String, icon: ImageVector) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(20.dp)
        )
        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

private fun combineDateAndTime(dateMillis: Long, existingTimestampMillis: Long): Long {
    val dateCal = Calendar.getInstance(TimeZone.getTimeZone("UTC")).apply { timeInMillis = dateMillis }
    val existingCal = Calendar.getInstance().apply { timeInMillis = existingTimestampMillis }

    val resultCal = Calendar.getInstance().apply {
        set(Calendar.YEAR, dateCal.get(Calendar.YEAR))
        set(Calendar.MONTH, dateCal.get(Calendar.MONTH))
        set(Calendar.DAY_OF_MONTH, dateCal.get(Calendar.DAY_OF_MONTH))
        set(Calendar.HOUR_OF_DAY, existingCal.get(Calendar.HOUR_OF_DAY))
        set(Calendar.MINUTE, existingCal.get(Calendar.MINUTE))
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return resultCal.timeInMillis
}

private fun updateHourAndMinute(existingTimestampMillis: Long, hour: Int, minute: Int): Long {
    val resultCal = Calendar.getInstance().apply {
        timeInMillis = existingTimestampMillis
        set(Calendar.HOUR_OF_DAY, hour)
        set(Calendar.MINUTE, minute)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return resultCal.timeInMillis
}
