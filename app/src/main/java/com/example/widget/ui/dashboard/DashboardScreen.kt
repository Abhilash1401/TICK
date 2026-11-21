package com.example.widget.ui.dashboard

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Add
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.FilterList
import androidx.compose.material.icons.rounded.HourglassEmpty
import androidx.compose.material.icons.rounded.PushPin
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material.icons.rounded.Sort
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.widget.data.model.CountdownEvent
import com.example.widget.data.model.SortOrder
import com.example.widget.data.model.StylePresets
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    viewModel: DashboardViewModel,
    onNavigateToCreate: () -> Unit,
    onNavigateToEdit: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.uiState.collectAsState()

    var currentTimestampMillis by remember { mutableLongStateOf(System.currentTimeMillis()) }
    var isSearching by remember { mutableStateOf(false) }
    var showSortMenu by remember { mutableStateOf(false) }
    var eventToDelete by remember { mutableStateOf<CountdownEvent?>(null) }

    // Dynamic Live Ticker Loop
    LaunchedEffect(Unit) {
        while (isActive) {
            currentTimestampMillis = System.currentTimeMillis()
            delay(100) // update every 100ms for smooth live timer & millisecond support
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    if (isSearching) {
                        OutlinedTextField(
                            value = uiState.searchQuery,
                            onValueChange = { viewModel.onSearchQueryChange(it) },
                            placeholder = { Text("Search countdowns...") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth(),
                            trailingIcon = {
                                IconButton(onClick = {
                                    viewModel.onSearchQueryChange("")
                                    isSearching = false
                                }) {
                                    Icon(Icons.Rounded.Close, contentDescription = "Close Search")
                                }
                            }
                        )
                    } else {
                        Column {
                            Text(
                                text = "Countdowns",
                                fontWeight = FontWeight.Bold,
                                fontSize = 22.sp
                            )
                            Text(
                                text = "${uiState.events.size} event(s) scheduled",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                },
                actions = {
                    if (!isSearching) {
                        IconButton(onClick = { isSearching = true }) {
                            Icon(Icons.Rounded.Search, contentDescription = "Search")
                        }

                        Box {
                            IconButton(onClick = { showSortMenu = true }) {
                                Icon(Icons.Rounded.Sort, contentDescription = "Sort")
                            }

                            DropdownMenu(
                                expanded = showSortMenu,
                                onDismissRequest = { showSortMenu = false }
                            ) {
                                DropdownMenuItem(
                                    text = { Text("Sort by Target Date") },
                                    onClick = {
                                        showSortMenu = false
                                        viewModel.onSortOrderChange(SortOrder.BY_TARGET_DATE)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort by Title") },
                                    onClick = {
                                        showSortMenu = false
                                        viewModel.onSortOrderChange(SortOrder.BY_TITLE)
                                    }
                                )
                                DropdownMenuItem(
                                    text = { Text("Sort by Date Created") },
                                    onClick = {
                                        showSortMenu = false
                                        viewModel.onSortOrderChange(SortOrder.BY_CREATED_DATE)
                                    }
                                )
                            }
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToCreate,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Rounded.Add,
                    contentDescription = "Add Countdown",
                    modifier = Modifier.size(28.dp)
                )
            }
        },
        modifier = modifier
    ) { innerPadding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                // Category Filter Chips
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    item {
                        FilterChip(
                            selected = uiState.selectedCategory == null || uiState.selectedCategory == "All",
                            onClick = { viewModel.onCategorySelect(null) },
                            label = { Text("All") },
                            colors = FilterChipDefaults.filterChipColors()
                        )
                    }
                    items(StylePresets.categoryList) { category ->
                        FilterChip(
                            selected = uiState.selectedCategory.equals(category, ignoreCase = true),
                            onClick = {
                                if (uiState.selectedCategory.equals(category, ignoreCase = true)) {
                                    viewModel.onCategorySelect(null)
                                } else {
                                    viewModel.onCategorySelect(category)
                                }
                            },
                            label = { Text(category) }
                        )
                    }
                }

                if (uiState.events.isEmpty()) {
                    // Empty State
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Rounded.HourglassEmpty,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.6f),
                                modifier = Modifier.size(72.dp)
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Text(
                                text = if (uiState.searchQuery.isNotBlank() || uiState.selectedCategory != null) {
                                    "No countdowns match your filter"
                                } else {
                                    "No Countdowns Yet"
                                },
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (uiState.searchQuery.isNotBlank() || uiState.selectedCategory != null) {
                                    "Try changing your search keywords or filter category."
                                } else {
                                    "Tap the + button below to create your first countdown event and widget!"
                                },
                                fontSize = 14.sp,
                                textAlign = TextAlign.Center,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                } else {
                    LazyColumn(
                        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        // If Pinned event exists and not searching/filtering out pinned
                        val pinned = uiState.pinnedEvent
                        if (pinned != null && uiState.events.contains(pinned) && uiState.searchQuery.isBlank() && uiState.selectedCategory == null) {
                            item(key = "pinned_header") {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.PushPin,
                                        contentDescription = null,
                                        tint = MaterialTheme.colorScheme.primary,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Text(
                                        text = "PINNED COUNTDOWN",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                            }
                            item(key = "pinned_${pinned.id}") {
                                CountdownCard(
                                    event = pinned,
                                    currentTimestampMillis = currentTimestampMillis,
                                    onEdit = { onNavigateToEdit(pinned.id) },
                                    onDelete = { eventToDelete = pinned },
                                    onTogglePin = { viewModel.onTogglePin(pinned) }
                                )
                            }
                            item(key = "all_header") {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "ALL COUNTDOWNS",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
                                )
                            }
                        }

                        val regularEvents = if (pinned != null && uiState.searchQuery.isBlank() && uiState.selectedCategory == null) {
                            uiState.events.filter { it.id != pinned.id }
                        } else {
                            uiState.events
                        }

                        items(
                            items = regularEvents,
                            key = { it.id }
                        ) { event ->
                            CountdownCard(
                                event = event,
                                currentTimestampMillis = currentTimestampMillis,
                                onEdit = { onNavigateToEdit(event.id) },
                                onDelete = { eventToDelete = event },
                                onTogglePin = { viewModel.onTogglePin(event) }
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(72.dp)) // bottom padding for FAB
                        }
                    }
                }
            }
        }
    }

    // Delete Confirmation Dialog
    eventToDelete?.let { event ->
        AlertDialog(
            onDismissRequest = { eventToDelete = null },
            title = { Text("Delete Countdown?") },
            text = { Text("Are you sure you want to delete '${event.title}'? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.onDeleteEvent(event)
                        eventToDelete = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(onClick = { eventToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
