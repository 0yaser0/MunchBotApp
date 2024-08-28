package com.munchbot.munchbot.data.repository

import android.net.Uri
import com.munchbot.munchbot.data.database.SignUpDataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SignUpDataRepository @Inject constructor(private val signUpDataStoreManager: SignUpDataStoreManager) {

    fun getUsername(userID: String): Flow<String?> = signUpDataStoreManager.getUsernameFlow(userID)
    fun getStatus(userID: String): Flow<String?> = signUpDataStoreManager.getStatusFlow(userID)
    fun getBio(userID: String): Flow<String?> = signUpDataStoreManager.getBioFlow(userID)
    fun getPetType(userID: String): Flow<String?> = signUpDataStoreManager.getPetTypeFlow(userID)
    fun getPetName(userID: String): Flow<String?> = signUpDataStoreManager.getPetNameFlow(userID)
    fun getPetGender(userID: String): Flow<String?> = signUpDataStoreManager.getPetGenderFlow(userID)
    fun getPetWeight(userID: String): Flow<String?> = signUpDataStoreManager.getPetWeightFlow(userID)
    fun getPetBreed(userID: String): Flow<String?> = signUpDataStoreManager.getPetBreedFlow(userID)
    fun getPetDateOfBirth(userID: String): Flow<String?> = signUpDataStoreManager.getPetDateOfBirthFlow(userID)
    fun getPetHeight(userID: String): Flow<String?> = signUpDataStoreManager.getPetHeightFlow(userID)
    fun getUserProfileImage(userID: String): Flow<String?> { return signUpDataStoreManager.getUserProfileImage()}
    fun getPetProfileImage(userID: String): Flow<String?> { return signUpDataStoreManager.getPetProfileImage()}

    suspend fun setUsername(userID: String, username: String) = signUpDataStoreManager.saveUsername(userID, username)
    suspend fun setStatus(userID: String, status: String) = signUpDataStoreManager.saveStatus(userID, status)
    suspend fun setBio(userID: String, bio: String) = signUpDataStoreManager.saveBio(userID, bio)
    suspend fun setPetType(userID: String, petType: String) = signUpDataStoreManager.savePetType(userID, petType)
    suspend fun setPetName(userID: String, petName: String) = signUpDataStoreManager.savePetName(userID, petName)
    suspend fun setPetGender(userID: String, petGender: String) = signUpDataStoreManager.savePetGender(userID, petGender)
    suspend fun setPetWeight(userID: String, petWeight: String) = signUpDataStoreManager.savePetWeight(userID, petWeight)
    suspend fun setPetBreed(userID: String, petBreed: String) = signUpDataStoreManager.savePetBreed(userID, petBreed)
    suspend fun setPetDateOfBirth(userID: String, petDateOfBirth: String) = signUpDataStoreManager.savePetDateOfBirth(userID, petDateOfBirth)
    suspend fun setPetHeight(userID: String, petHeight: String) = signUpDataStoreManager.savePetHeight(userID, petHeight)
    suspend fun saveUserProfileImage(userID: String, uri: Uri) = signUpDataStoreManager.saveUserProfileImage(uri)
    suspend fun savePetProfileImage(userID: String, uri: Uri) = signUpDataStoreManager.savePetProfileImage(uri)
}
