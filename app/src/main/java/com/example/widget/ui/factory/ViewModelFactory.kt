package com.example.widget.ui.factory

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.widget.data.local.AppDatabase
import com.example.widget.data.local.UserPreferencesDataStore
import com.example.widget.data.repository.CountdownRepository
import com.example.widget.data.repository.CountdownRepositoryImpl
import com.example.widget.ui.config.CountdownConfigViewModel
import com.example.widget.ui.dashboard.DashboardViewModel

class CountdownViewModelFactory(
    private val repository: CountdownRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardViewModel::class.java)) {
            return DashboardViewModel(repository) as T
        }
        if (modelClass.isAssignableFrom(CountdownConfigViewModel::class.java)) {
            return CountdownConfigViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}

object RepositoryProvider {
    @Volatile
    private var repository: CountdownRepository? = null

    fun getRepository(context: Context): CountdownRepository {
        return repository ?: synchronized(this) {
            val appCtx = context.applicationContext
            val db = AppDatabase.getDatabase(appCtx)
            val preferencesDataStore = UserPreferencesDataStore(appCtx)
            val instance = CountdownRepositoryImpl(db.countdownDao(), preferencesDataStore, appCtx)
            repository = instance
            instance
        }
    }
}
