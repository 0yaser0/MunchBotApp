package com.munchbot.munchbot.data.repository

import com.munchbot.munchbot.data.database.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Singleton

@Singleton
class DataRepository(private val dataStoreManager: DataStoreManager) {

    fun getUsername(userID: String): Flow<String?> = dataStoreManager.getUsernameFlow(userID)
    fun getStatus(userID: String): Flow<String?> = dataStoreManager.getStatusFlow(userID)
    fun getBio(userID: String): Flow<String?> = dataStoreManager.getBioFlow(userID)
    fun getPetType(userID: String): Flow<String?> = dataStoreManager.getPetTypeFlow(userID)

    suspend fun setUsername(userID: String, username: String) = dataStoreManager.saveUsername(userID, username)
    suspend fun setStatus(userID: String, status: String) = dataStoreManager.saveStatus(userID, status)
    suspend fun setBio(userID: String, bio: String) = dataStoreManager.saveBio(userID, bio)
    suspend fun setPetType(userID: String, petType: String) = dataStoreManager.savePetType(userID, petType)

}
