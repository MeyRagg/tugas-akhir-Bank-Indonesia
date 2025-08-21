package com.lancar.tugasakhir.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val IS_ONBOARDING_COMPLETED = booleanPreferencesKey("is_onboarding_completed")
    private val AUTH_TOKEN = stringPreferencesKey("auth_token")

    val isOnboardingCompleted: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[IS_ONBOARDING_COMPLETED] ?: false
        }

    val authToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[AUTH_TOKEN]
        }

    suspend fun setOnboardingCompleted(isCompleted: Boolean) {
        context.dataStore.edit { settings ->
            settings[IS_ONBOARDING_COMPLETED] = isCompleted
        }
    }

    suspend fun saveAuthToken(token: String) {
        context.dataStore.edit { settings ->
            settings[AUTH_TOKEN] = token
        }
    }

    suspend fun clearAuthToken() {
        context.dataStore.edit { settings ->
            settings.remove(AUTH_TOKEN)
        }
    }
}