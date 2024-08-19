package com.munchbot.munchbot.data.repository

import com.munchbot.munchbot.data.database.DataStoreManager
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(private val dataStoreManager: DataStoreManager) {

    fun getUsername(): Flow<String?> = dataStoreManager.usernameFlow
    fun getStatus(): Flow<String?> = dataStoreManager.statusFlow
    fun getBio(): Flow<String?> = dataStoreManager.bioFlow

    suspend fun setUsername(username: String) = dataStoreManager.saveUsername(username)
    suspend fun setStatus(status: String) = dataStoreManager.saveStatus(status)
    suspend fun setBio(bio: String) = dataStoreManager.saveBio(bio)
}
