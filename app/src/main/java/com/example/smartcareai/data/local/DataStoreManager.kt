package com.example.smartcareai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "smartcare_prefs")

class DataStoreManager(private val context: Context) {

    companion object {
        val LARGE_TEXT_KEY = booleanPreferencesKey("large_text_enabled")
        val NOTIFICATIONS_KEY = booleanPreferencesKey("notifications_enabled")
        val PREFERRED_VISIT_TIME_KEY = stringPreferencesKey("preferred_visit_time")
        val MOBILITY_ASSIST_KEY = booleanPreferencesKey("mobility_assistance")
        val PATIENT_NAME_KEY = stringPreferencesKey("patient_name")
        val PATIENT_PHONE_KEY = stringPreferencesKey("patient_phone")
    }

    val largeTextFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[LARGE_TEXT_KEY] ?: false
    }

    val notificationsFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[NOTIFICATIONS_KEY] ?: true
    }

    val preferredVisitTimeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PREFERRED_VISIT_TIME_KEY] ?: "Morning (9 AM - 12 PM)"
    }

    val mobilityAssistFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[MOBILITY_ASSIST_KEY] ?: false
    }

    val patientNameFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PATIENT_NAME_KEY] ?: "Sarah Jenkins"
    }

    val patientPhoneFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PATIENT_PHONE_KEY] ?: "+1 (555) 234-5678"
    }

    suspend fun setLargeText(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[LARGE_TEXT_KEY] = enabled
        }
    }

    suspend fun setNotifications(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[NOTIFICATIONS_KEY] = enabled
        }
    }

    suspend fun setPreferredVisitTime(time: String) {
        context.dataStore.edit { prefs ->
            prefs[PREFERRED_VISIT_TIME_KEY] = time
        }
    }

    suspend fun setMobilityAssist(enabled: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[MOBILITY_ASSIST_KEY] = enabled
        }
    }

    suspend fun updatePatientInfo(name: String, phone: String) {
        context.dataStore.edit { prefs ->
            prefs[PATIENT_NAME_KEY] = name
            prefs[PATIENT_PHONE_KEY] = phone
        }
    }
}
