package com.example.widget.ui.widget

import android.content.Context
import android.os.Build
import android.os.SystemClock
import android.widget.RemoteViews
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.ImageProvider
import androidx.glance.LocalContext
import androidx.glance.action.actionStartActivity
import androidx.glance.action.clickable
import androidx.glance.appwidget.AndroidRemoteViews
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.provideContent
import androidx.glance.appwidget.state.getAppWidgetState
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.background
import androidx.glance.color.ColorProvider
import androidx.glance.currentState
import androidx.glance.layout.Alignment
import androidx.glance.layout.Box
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.fillMaxWidth
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.example.widget.MainActivity
import com.example.widget.data.model.CountdownEvent
import com.example.widget.ui.factory.RepositoryProvider
import com.example.widget.ui.util.TimeUtils
import kotlinx.coroutines.flow.firstOrNull
import java.util.Locale

class CountdownGlanceWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    companion object {
        val EVENT_ID_KEY = longPreferencesKey("widget_event_id")
        val LAST_TICK_KEY = longPreferencesKey("widget_last_tick")

        suspend fun updateAllWidgets(context: Context) {
            try {
                val manager = GlanceAppWidgetManager(context)
                val glanceIds = manager.getGlanceIds(CountdownGlanceWidget::class.java)
                val repository = RepositoryProvider.getRepository(context)

                for (glanceId in glanceIds) {
                    val appWidgetId = manager.getAppWidgetId(glanceId)

                    val glanceState = try {
                        getAppWidgetState(context, PreferencesGlanceStateDefinition, glanceId)
                    } catch (e: Exception) {
                        null
                    }
                    var eventId = glanceState?.get(EVENT_ID_KEY)

                    if (eventId == null || eventId <= 0L) {
                        val sp = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
                        val savedId = sp.getLong("widget_event_$appWidgetId", -1L)
                        if (savedId > 0L) {
                            eventId = savedId
                        }
                    }

                    var event = if (eventId != null && eventId > 0L) {
                        repository.getEventByIdDirect(eventId)
                    } else {
                        null
                    }

                    if (event == null) {
                        event = repository.getPinnedEvent().firstOrNull()
                    }
                    if (event == null) {
                        event = repository.getAllEvents().firstOrNull()?.firstOrNull()
                    }

                    updateAppWidgetState(context, glanceId) { prefs ->
                        if (event != null) {
                            prefs[EVENT_ID_KEY] = event.id
                        }
                        prefs[LAST_TICK_KEY] = System.currentTimeMillis()
                    }

                    CountdownGlanceWidget().update(context, glanceId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val appWidgetId = GlanceAppWidgetManager(context).getAppWidgetId(id)

        provideContent {
            val prefs = currentState<Preferences>()
            var eventId = prefs[EVENT_ID_KEY]
            val lastTick = prefs[LAST_TICK_KEY] ?: System.currentTimeMillis()

            if (eventId == null || eventId <= 0L) {
                val sp = context.getSharedPreferences("widget_prefs", Context.MODE_PRIVATE)
                val savedId = sp.getLong("widget_event_$appWidgetId", -1L)
                if (savedId > 0L) {
                    eventId = savedId
                }
            }

            var event by remember { mutableStateOf<CountdownEvent?>(null) }
            var isLoaded by remember { mutableStateOf(false) }

            LaunchedEffect(eventId, appWidgetId, lastTick) {
                val repository = RepositoryProvider.getRepository(context)
                if (eventId != null && eventId > 0L) {
                    event = repository.getEventByIdDirect(eventId)
                }
                if (event == null) {
                    event = repository.getPinnedEvent().firstOrNull()
                }
                if (event == null) {
                    event = repository.getAllEvents().firstOrNull()?.firstOrNull()
                }
                isLoaded = true
            }

            if (!isLoaded) {
                GlanceLoadingView()
            } else if (event == null) {
                GlanceEmptyView()
            } else {
                GlanceCountdownWidgetContent(
                    event = event!!,
                    currentTimestampMillis = lastTick
                )
            }
        }
    }
}

@Composable
private fun GlanceLoadingView() {
    val bgColor = ColorProvider(day = Color(0xFF1F1C2C), night = Color(0xFF1F1C2C))
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Loading Countdown...",
            style = TextStyle(
                color = ColorProvider(day = Color.White, night = Color.White),
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
        )
    }
}

@Composable
private fun GlanceEmptyView() {
    val bgColor = ColorProvider(day = Color(0xFF1F1C2C), night = Color(0xFF1F1C2C))
    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgColor)
            .padding(16.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = "No Event Configured",
                style = TextStyle(
                    color = ColorProvider(day = Color.White, night = Color.White),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Spacer(modifier = GlanceModifier.height(4.dp))
            Text(
                text = "Tap to open app and create an event",
                style = TextStyle(
                    color = ColorProvider(day = Color.LightGray, night = Color.LightGray),
                    fontSize = 12.sp
                )
            )
        }
    }
}

@Composable
private fun GlanceCountdownWidgetContent(
    event: CountdownEvent,
    currentTimestampMillis: Long
) {
    val context = LocalContext.current
    val timeRemaining = TimeUtils.calculateTimeRemaining(
        targetTimestampMillis = event.targetTimestampMillis,
        currentTimestampMillis = currentTimestampMillis,
        countDirection = event.countDirection,
        showMilliseconds = false
    )

    val textParsedColor = WidgetBackgroundUtils.parseColorSafely(event.textColorHex, android.graphics.Color.WHITE)
    val textColor = Color(textParsedColor)
    val textColorProvider = ColorProvider(day = textColor, night = textColor)

    val bgBitmap = WidgetBackgroundUtils.generateWidgetBackgroundBitmap(context = context, event = event)
    val bgImageProvider = ImageProvider(bgBitmap)

    val now = System.currentTimeMillis()
    val diff = event.targetTimestampMillis - now
    val isCountDown = event.countDirection == "COUNTDOWN"

    val chronometerRemoteViews = RemoteViews(context.packageName, com.example.widget.R.layout.widget_chronometer_layout).apply {
        if (diff > 0 && isCountDown) {
            val remainingInDay = if (timeRemaining.days > 0) {
                diff % (24 * 3600 * 1000L)
            } else {
                diff
            }
            val base = SystemClock.elapsedRealtime() + remainingInDay
            setChronometer(com.example.widget.R.id.live_chronometer, base, null, true)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                setChronometerCountDown(com.example.widget.R.id.live_chronometer, true)
            }
        }
        setTextColor(com.example.widget.R.id.live_chronometer, textParsedColor)
        setTextColor(com.example.widget.R.id.live_chronometer_label, textParsedColor)
    }

    Box(
        modifier = GlanceModifier
            .fillMaxSize()
            .background(bgImageProvider)
            .padding(12.dp)
            .clickable(actionStartActivity<MainActivity>()),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = GlanceModifier.fillMaxSize(),
            verticalAlignment = Alignment.Vertical.CenterVertically,
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally
        ) {
            // Header Row: Event Title & Category
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.Start
            ) {
                Text(
                    text = event.title,
                    style = TextStyle(
                        color = textColorProvider,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    maxLines = 1
                )
                Spacer(modifier = GlanceModifier.defaultWeight())
                Text(
                    text = event.category.uppercase(),
                    style = TextStyle(
                        color = ColorProvider(day = textColor.copy(alpha = 0.8f), night = textColor.copy(alpha = 0.8f)),
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Medium
                    )
                )
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Rectangular Countdown Units Row
            // Combines Days block with native 1-second live ticking Chronometer (HRS : MINS : SECS)
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                if (timeRemaining.days > 0) {
                    CountdownUnitBox(value = timeRemaining.days, label = "DAYS", textColor = textColor)
                    Spacer(modifier = GlanceModifier.width(8.dp))
                    AndroidRemoteViews(remoteViews = chronometerRemoteViews)
                } else if (!timeRemaining.isArrived) {
                    AndroidRemoteViews(remoteViews = chronometerRemoteViews)
                } else {
                    CountdownUnitBox(value = 0, label = "DAYS", textColor = textColor)
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    CountdownUnitBox(value = 0, label = "HRS", textColor = textColor)
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    CountdownUnitBox(value = 0, label = "MINS", textColor = textColor)
                    Spacer(modifier = GlanceModifier.width(6.dp))
                    CountdownUnitBox(value = 0, label = "SECS", textColor = textColor)
                }
            }

            Spacer(modifier = GlanceModifier.height(6.dp))

            // Target Date Footer Row
            Row(
                modifier = GlanceModifier.fillMaxWidth(),
                verticalAlignment = Alignment.Vertical.CenterVertically,
                horizontalAlignment = Alignment.Horizontal.CenterHorizontally
            ) {
                val dateStr = TimeUtils.formatDateTime(event.targetTimestampMillis)
                Text(
                    text = if (timeRemaining.isArrived) "🎉 Event Arrived!" else "Target: $dateStr",
                    style = TextStyle(
                        color = ColorProvider(day = textColor.copy(alpha = 0.9f), night = textColor.copy(alpha = 0.9f)),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Normal
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun CountdownUnitBox(
    value: Long,
    label: String,
    textColor: Color
) {
    val formattedValue = String.format(Locale.getDefault(), "%02d", value)
    Box(
        modifier = GlanceModifier
            .background(ColorProvider(day = Color.Black.copy(alpha = 0.25f), night = Color.Black.copy(alpha = 0.25f)))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.Horizontal.CenterHorizontally,
            verticalAlignment = Alignment.Vertical.CenterVertically
        ) {
            Text(
                text = formattedValue,
                style = TextStyle(
                    color = ColorProvider(day = textColor, night = textColor),
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )
            )
            Text(
                text = label,
                style = TextStyle(
                    color = ColorProvider(day = textColor.copy(alpha = 0.75f), night = textColor.copy(alpha = 0.75f)),
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Medium
                )
            )
        }
    }
}
