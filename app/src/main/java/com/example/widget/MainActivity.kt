package com.example.widget

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.ui.NavDisplay
import com.example.widget.ui.config.CountdownConfigScreen
import com.example.widget.ui.config.CountdownConfigViewModel
import com.example.widget.ui.dashboard.DashboardScreen
import com.example.widget.ui.dashboard.DashboardViewModel
import com.example.widget.ui.factory.CountdownViewModelFactory
import com.example.widget.ui.factory.RepositoryProvider
import com.example.widget.ui.navigation.AppRoute
import com.example.widget.ui.theme.WidgetTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            WidgetTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    val context = LocalContext.current
                    val repository = RepositoryProvider.getRepository(context)
                    val factory = CountdownViewModelFactory(repository)

                    val backStack = rememberNavBackStack(AppRoute.Dashboard)

                    NavDisplay(
                        backStack = backStack,
                        entryProvider = entryProvider {
                            entry<AppRoute.Dashboard> {
                                val dashboardViewModel: DashboardViewModel = viewModel(factory = factory)
                                DashboardScreen(
                                    viewModel = dashboardViewModel,
                                    onNavigateToCreate = {
                                        backStack.add(AppRoute.CountdownConfig(eventId = null))
                                    },
                                    onNavigateToEdit = { eventId ->
                                        backStack.add(AppRoute.CountdownConfig(eventId = eventId))
                                    }
                                )
                            }
                            entry<AppRoute.CountdownConfig> { key ->
                                val configViewModel: CountdownConfigViewModel = viewModel(factory = factory)
                                CountdownConfigScreen(
                                    viewModel = configViewModel,
                                    eventId = key.eventId,
                                    onNavigateBack = {
                                        if (backStack.size > 1) {
                                            backStack.removeAt(backStack.size - 1)
                                        }
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}
