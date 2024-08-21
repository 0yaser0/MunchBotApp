package com.munchbot.munchbot.data.repository

import android.net.Uri
import com.munchbot.munchbot.data.database.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class DataRepository(private val dataStoreManager: DataStoreManager) {

    fun getUsername(userID: String): Flow<String?> = dataStoreManager.getUsernameFlow(userID)
    fun getStatus(userID: String): Flow<String?> = dataStoreManager.getStatusFlow(userID)
    fun getBio(userID: String): Flow<String?> = dataStoreManager.getBioFlow(userID)
    fun getPetType(userID: String): Flow<String?> = dataStoreManager.getPetTypeFlow(userID)
    fun getPetName(userID: String): Flow<String?> = dataStoreManager.getPetNameFlow(userID)
    fun getPetGender(userID: String): Flow<String?> = dataStoreManager.getPetGenderFlow(userID)
    fun getPetWeight(userID: String): Flow<String?> = dataStoreManager.getPetWeightFlow(userID)
    fun getPetBreed(userID: String): Flow<String?> = dataStoreManager.getPetBreedFlow(userID)
    fun getPetDateOfBirth(userID: String): Flow<String?> = dataStoreManager.getPetDateOfBirthFlow(userID)
    fun getPetHeight(userID: String): Flow<String?> = dataStoreManager.getPetHeightFlow(userID)
    fun getUserProfileImage(userID: String): Flow<String?> { return dataStoreManager.getUserProfileImage()}
    fun getPetProfileImage(userID: String): Flow<String?> { return dataStoreManager.getPetProfileImage()}

    suspend fun setUsername(userID: String, username: String) = dataStoreManager.saveUsername(userID, username)
    suspend fun setStatus(userID: String, status: String) = dataStoreManager.saveStatus(userID, status)
    suspend fun setBio(userID: String, bio: String) = dataStoreManager.saveBio(userID, bio)
    suspend fun setPetType(userID: String, petType: String) = dataStoreManager.savePetType(userID, petType)
    suspend fun setPetName(userID: String, petName: String) = dataStoreManager.savePetName(userID, petName)
    suspend fun setPetGender(userID: String, petGender: String) = dataStoreManager.savePetGender(userID, petGender)
    suspend fun setPetWeight(userID: String, petWeight: String) = dataStoreManager.savePetWeight(userID, petWeight)
    suspend fun setPetBreed(userID: String, petBreed: String) = dataStoreManager.savePetBreed(userID, petBreed)
    suspend fun setPetDateOfBirth(userID: String, petDateOfBirth: String) = dataStoreManager.savePetDateOfBirth(userID, petDateOfBirth)
    suspend fun setPetHeight(userID: String, petHeight: String) = dataStoreManager.savePetHeight(userID, petHeight)
    suspend fun saveUserProfileImage(userID: String, uri: Uri) = dataStoreManager.saveUserProfileImage(uri)
    suspend fun savePetProfileImage(userID: String, uri: Uri) = dataStoreManager.savePetProfileImage(uri)
}
