package com.munchbot.munchbot.data.model

data class Pet(
    val petName: String = "",
    val petGender: String = "",
    val petType: String = "",
    val petBreed: String = "",
    val petDateOfBirth: String = "",
    val petWeight: Double = 0.0,
    val petHeight: Double = 0.0,
    val petProfileImage: String = ""
)