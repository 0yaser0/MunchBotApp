package com.munchbot.munchbot.data.database

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Singleton

@Singleton
class DataStoreManager(context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
    private val dataStore = context.dataStore

    private fun usernameKey(userID: String) = stringPreferencesKey("username_$userID")
    private fun statusKey(userID: String) = stringPreferencesKey("status_$userID")
    private fun bioKey(userID: String) = stringPreferencesKey("bio_$userID")
    private fun petTypeKey(userID: String) = stringPreferencesKey("pet_type_$userID")

    fun getUsernameFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[usernameKey(userID)] }

    fun getStatusFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[statusKey(userID)] }

    fun getBioFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[bioKey(userID)] }

    fun getPetTypeFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petTypeKey(userID)] }

    suspend fun saveUsername(userID: String, username: String) {
        dataStore.edit { preferences ->
            preferences[usernameKey(userID)] = username
        }
    }

    suspend fun saveStatus(userID: String, status: String) {
        dataStore.edit { preferences ->
            preferences[statusKey(userID)] = status
        }
    }

    suspend fun saveBio(userID: String, bio: String) {
        dataStore.edit { preferences ->
            preferences[bioKey(userID)] = bio
        }
    }

    suspend fun savePetType(userID: String, petType: String) {
        dataStore.edit { preferences ->
            preferences[petTypeKey(userID)] = petType
        }
    }

}

