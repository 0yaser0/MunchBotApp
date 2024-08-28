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
    return FirebaseAuth.getInstance().currentUser?.uid
}

fun getPetId(userId: String, callback: (String?) -> Unit) {
    val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("users/$userId/pets")

    database.get().addOnSuccessListener { snapshot ->
        val petId = snapshot.children.firstOrNull()?.key
        callback(petId)
    }.addOnFailureListener {
        callback(null)
    }
}
