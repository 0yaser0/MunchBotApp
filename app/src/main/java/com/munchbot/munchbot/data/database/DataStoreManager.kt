package com.munchbot.munchbot.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataStoreManager @Inject constructor(context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
    private val dataStore = context.dataStore

    private val USERNAME_KEY = stringPreferencesKey("username")
    private val STATUS_KEY = stringPreferencesKey("status")
    private val BIO_KEY = stringPreferencesKey("bio")

    val usernameFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[USERNAME_KEY] }

    val statusFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[STATUS_KEY] }

    val bioFlow: Flow<String?> = dataStore.data
        .map { preferences -> preferences[BIO_KEY] }

    suspend fun saveUsername(username: String) {
        dataStore.edit { preferences ->
            preferences[USERNAME_KEY] = username
        }
    }

    suspend fun saveStatus(status: String) {
        dataStore.edit { preferences ->
            preferences[STATUS_KEY] = status
        }
    }

    suspend fun saveBio(bio: String) {
        dataStore.edit { preferences ->
            preferences[BIO_KEY] = bio
        }
    }
}
