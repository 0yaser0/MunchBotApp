package com.munchbot.munchbot.data.database

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

object FirebaseDatabase {
    private val database: FirebaseDatabase = FirebaseDatabase.getInstance()

    fun getDatabaseReference(): DatabaseReference {
        return database.reference
    }
}

fun getUserId(): String? {
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    return userId
}

fun getPetId(userId: String): String {
     val petId = "${userId}pet"
    return petId
}
