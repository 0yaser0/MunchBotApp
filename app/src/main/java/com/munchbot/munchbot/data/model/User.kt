package com.munchbot.munchbot.data.model

data class User(
    val username: String = "",
    val status: String = "",
    val bio: String = "",
    val userProfileImage: String = "",
    val pets: Map<String, Pet> = mapOf()
)
