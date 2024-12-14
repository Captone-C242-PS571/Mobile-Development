package com.project.sleepwell.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.project.sleepwell.data.model.UserModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.userPreferencesStore: DataStore<Preferences> by preferencesDataStore(name = "user_session")

class UserPreferences private constructor(private val preferencesStore: DataStore<Preferences>) {

    suspend fun saveUserDetails(user: UserModel) {
        preferencesStore.edit { prefs ->
            prefs[NAME_KEY] = user.name
            prefs[EMAIL_KEY] = user.email
            prefs[TOKEN_KEY] = user.token
            prefs[IS_LOGGED_IN_KEY] = true
        }
    }

    fun fetchUser(): Flow<UserModel> {
        return preferencesStore.data.map { prefs ->
            UserModel(
                prefs[NAME_KEY] ?: "",
                prefs[EMAIL_KEY] ?: "",
                prefs[TOKEN_KEY] ?: "",
                prefs[IS_LOGGED_IN_KEY] ?: false
            )
        }
    }

    suspend fun clearUserData() {
        preferencesStore.edit { prefs ->
            prefs.clear()
        }
    }

    companion object {
        @Volatile
        private var instance: UserPreferences? = null

        private val EMAIL_KEY = stringPreferencesKey("user_email")
        private val NAME_KEY = stringPreferencesKey("user_name")
        private val TOKEN_KEY = stringPreferencesKey("access_token")
        private val IS_LOGGED_IN_KEY = booleanPreferencesKey("is_logged_in")

        fun getInstance(preferencesStore: DataStore<Preferences>): UserPreferences {
            return instance ?: synchronized(this) {
                instance ?: UserPreferences(preferencesStore).also { instance = it }
            }
        }
    }
}
