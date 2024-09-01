package com.munchbot.munchbot.data.repository

import android.util.Log
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.munchbot.munchbot.data.model.Planner

class PlannerRepository {
    private val database: DatabaseReference = FirebaseDatabase.getInstance().getReference()

    fun saveHabit(userId: String, habitId: String, habit: Planner) {
        database.child("users").child(userId).child("habits").child(habitId).setValue(habit)
            .addOnSuccessListener {
                Log.wtf("Firebase", "Planner added successfully to RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to add habit to RTDB", e)
            }
    }

    fun getHabits(userId: String, callback: (List<Planner>?) -> Unit) {
        database.child("users").child(userId).child("habits")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val habits = mutableListOf<Planner>()
                    for (habitSnapshot in snapshot.children) {
                        val habit = habitSnapshot.getValue(Planner::class.java)
                        if (habit != null) {
                            habits.add(habit)
                        }
                    }
                    callback(habits)
                }

                override fun onCancelled(error: DatabaseError) {
                    callback(null)
                }
            })
    }

    fun getHabit(userId: String, habitId: String, callback: (Planner?) -> Unit) {
        database.child("users").child(userId).child("habits").child(habitId).get()
            .addOnSuccessListener {
                callback(it.getValue(Planner::class.java))
            }.addOnFailureListener {
                callback(null)
            }
    }

    fun updateHabit(userId: String, habitId: String, habit: Planner) {
        database.child("users").child(userId).child("habits").child(habitId).setValue(habit)
            .addOnSuccessListener {
                Log.wtf("Firebase", "Planner updated successfully in RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to update habit in RTDB", e)
            }
    }

    fun deleteHabit(userId: String, habitId: String) {
        database.child("users").child(userId).child("habits").child(habitId).removeValue()
            .addOnSuccessListener {
                Log.wtf("Firebase", "Planner deleted successfully from RTDB")
            }
            .addOnFailureListener { e ->
                Log.e("Firebase", "Failed to delete habit from RTDB", e)
            }
    }
}
