package com.example.widget.ui.widget

import android.appwidget.AppWidgetManager
import android.content.Context
import android.content.Intent
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver

class CountdownWidgetReceiver : GlanceAppWidgetReceiver() {

    override val glanceAppWidget: GlanceAppWidget = CountdownGlanceWidget()

    override fun onEnabled(context: Context) {
        super.onEnabled(context)
        CountdownWidgetWorker.enqueuePeriodic(context)
        WidgetTickReceiver.scheduleTicks(context)
    }

    override fun onDisabled(context: Context) {
        super.onDisabled(context)
        CountdownWidgetWorker.cancelPeriodic(context)
        WidgetTickReceiver.cancelTicks(context)
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        super.onUpdate(context, appWidgetManager, appWidgetIds)
        CountdownWidgetWorker.enqueuePeriodic(context)
        WidgetTickReceiver.scheduleTicks(context)
        CountdownWidgetWorker.triggerImmediate(context)
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED ||
            action == Intent.ACTION_MY_PACKAGE_REPLACED ||
            action == AppWidgetManager.ACTION_APPWIDGET_UPDATE ||
            action == WidgetTickReceiver.ACTION_TICK ||
            action == Intent.ACTION_TIME_TICK ||
            action == Intent.ACTION_TIME_CHANGED ||
            action == Intent.ACTION_TIMEZONE_CHANGED
        ) {
            CountdownWidgetWorker.enqueuePeriodic(context)
            WidgetTickReceiver.scheduleTicks(context)
            CountdownWidgetWorker.triggerImmediate(context)
        }
    }
}
