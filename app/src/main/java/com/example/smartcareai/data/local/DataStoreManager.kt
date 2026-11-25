package com.example.smartcareai.data.local

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
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
        val PATIENT_EMAIL_KEY = stringPreferencesKey("patient_email")
        val PATIENT_BLOOD_TYPE_KEY = stringPreferencesKey("patient_blood_type")
        val PATIENT_EMERGENCY_KEY = stringPreferencesKey("patient_emergency")
        val PATIENT_AGE_KEY = intPreferencesKey("patient_age")
        val SELECTED_LANGUAGE_KEY = stringPreferencesKey("selected_language")
        val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")
        val AUTH_TOKEN_KEY = stringPreferencesKey("auth_token")
        val VISIT_HISTORY_JSON_KEY = stringPreferencesKey("visit_history_json")
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

    val patientEmailFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PATIENT_EMAIL_KEY] ?: "sarah.jenkins@example.com"
    }

    val patientBloodTypeFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PATIENT_BLOOD_TYPE_KEY] ?: "O+"
    }

    val patientEmergencyFlow: Flow<String> = context.dataStore.data.map { prefs ->
        prefs[PATIENT_EMERGENCY_KEY] ?: "+1 (555) 911-0022"
    }

    val patientAgeFlow: Flow<Int> = context.dataStore.data.map { prefs ->
        prefs[PATIENT_AGE_KEY] ?: 34
    }

    val selectedLanguageFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[SELECTED_LANGUAGE_KEY]
    }

    val isLoggedInFlow: Flow<Boolean> = context.dataStore.data.map { prefs ->
        prefs[IS_LOGGED_IN_KEY] ?: false
    }

    val authTokenFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[AUTH_TOKEN_KEY]
    }

    val visitHistoryJsonFlow: Flow<String?> = context.dataStore.data.map { prefs ->
        prefs[VISIT_HISTORY_JSON_KEY]
    }

    suspend fun saveVisitHistoryJson(json: String) {
        context.dataStore.edit { prefs ->
            prefs[VISIT_HISTORY_JSON_KEY] = json
        }
    }

    suspend fun setSelectedLanguage(language: String) {
        context.dataStore.edit { prefs ->
            prefs[SELECTED_LANGUAGE_KEY] = language
        }
    }

    suspend fun setLoggedIn(isLoggedIn: Boolean) {
        context.dataStore.edit { prefs ->
            prefs[IS_LOGGED_IN_KEY] = isLoggedIn
        }
    }

    suspend fun setAuthToken(token: String?) {
        context.dataStore.edit { prefs ->
            if (token != null) {
                prefs[AUTH_TOKEN_KEY] = token
                prefs[IS_LOGGED_IN_KEY] = true
            } else {
                prefs.remove(AUTH_TOKEN_KEY)
                prefs[IS_LOGGED_IN_KEY] = false
            }
        }
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

    suspend fun updatePatientProfile(name: String, phone: String, email: String, emergency: String, bloodType: String, age: Int) {
        context.dataStore.edit { prefs ->
            prefs[PATIENT_NAME_KEY] = name
            prefs[PATIENT_PHONE_KEY] = phone
            prefs[PATIENT_EMAIL_KEY] = email
            prefs[PATIENT_EMERGENCY_KEY] = emergency
            prefs[PATIENT_BLOOD_TYPE_KEY] = bloodType
            prefs[PATIENT_AGE_KEY] = age
        }
    }
}
