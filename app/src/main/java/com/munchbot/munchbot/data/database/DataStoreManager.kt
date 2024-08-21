package com.munchbot.munchbot.data.database

import android.content.Context
import android.net.Uri
import android.util.Base64
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.io.ByteArrayOutputStream
import javax.inject.Singleton

@Singleton
class DataStoreManager(private val context: Context) {

    private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "user_prefs")
    private val dataStore = context.dataStore

    companion object {
        private val USER_PROFILE_IMAGE_KEY = stringPreferencesKey("user_profile_image")
        private val PET_PROFILE_IMAGE_KEY = stringPreferencesKey("pet_profile_image")
    }

    private fun uriToBase64(uri: Uri): String {
        val inputStream = context.contentResolver.openInputStream(uri)
        val byteArrayOutputStream = ByteArrayOutputStream()
        inputStream?.copyTo(byteArrayOutputStream)
        return Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT)
    }

    private fun usernameKey(userID: String) = stringPreferencesKey("username_$userID")
    private fun statusKey(userID: String) = stringPreferencesKey("status_$userID")
    private fun bioKey(userID: String) = stringPreferencesKey("bio_$userID")
    private fun petTypeKey(userID: String) = stringPreferencesKey("pet_type_$userID")
    private fun petNameKey(userID: String) = stringPreferencesKey("pet_name_$userID")
    private fun petGenderKey(userID: String) = stringPreferencesKey("pet_gender_$userID")
    private fun petWeightKey(userID: String) = stringPreferencesKey("pet_weight_$userID")
    private fun petBreedKey(userID: String) = stringPreferencesKey("pet_breed_$userID")
    private fun petDateOfBirthKey(userID: String) = stringPreferencesKey("pet_date_of_birth_$userID")
    private fun petHeightKey(userID: String) = stringPreferencesKey("pet_height_$userID")

    fun getUsernameFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[usernameKey(userID)] }

    fun getStatusFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[statusKey(userID)] }

    fun getBioFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[bioKey(userID)] }

    fun getPetTypeFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petTypeKey(userID)] }

    fun getPetNameFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petNameKey(userID)] }

    fun getPetGenderFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petGenderKey(userID)] }

    fun getPetWeightFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petWeightKey(userID)] }

    fun getPetBreedFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petBreedKey(userID)] }

    fun getPetDateOfBirthFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petDateOfBirthKey(userID)] }

    fun getPetHeightFlow(userID: String): Flow<String?> = dataStore.data
        .map { preferences -> preferences[petHeightKey(userID)] }


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

    suspend fun savePetName(userID: String, petName: String) {
        dataStore.edit { preferences ->
            preferences[petNameKey(userID)] = petName
        }
    }

    suspend fun savePetGender(userID: String, petGender: String) {
        dataStore.edit { preferences ->
            preferences[petGenderKey(userID)] = petGender
        }
    }

    suspend fun savePetWeight(userID: String, petWeight: String) {
        dataStore.edit { preferences ->
            preferences[petWeightKey(userID)] = petWeight
        }
    }

    suspend fun savePetBreed(userID: String, petBreed: String) {
        dataStore.edit { preferences ->
            preferences[petBreedKey(userID)] = petBreed
        }
    }

    suspend fun savePetDateOfBirth(userID: String, petDateOfBirth: String) {
        dataStore.edit { preferences ->
            preferences[petDateOfBirthKey(userID)] = petDateOfBirth
        }
    }

    suspend fun savePetHeight(userID: String, petHeight: String) {
        dataStore.edit { preferences ->
            preferences[petHeightKey(userID)] = petHeight
        }
    }

    suspend fun saveUserProfileImage(uri: Uri) {
        val base64Image = uriToBase64(uri)
        context.dataStore.edit { preferences ->
            preferences[USER_PROFILE_IMAGE_KEY] = base64Image
        }
    }

    fun getUserProfileImage(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[USER_PROFILE_IMAGE_KEY]
        }
    }

    suspend fun savePetProfileImage(uri: Uri) {
        val base64Image = uriToBase64(uri)
        context.dataStore.edit { preferences ->
            preferences[PET_PROFILE_IMAGE_KEY] = base64Image
        }
    }

    fun getPetProfileImage(): Flow<String?> {
        return context.dataStore.data.map { preferences ->
            preferences[PET_PROFILE_IMAGE_KEY]
        }
    }
}

