package com.munchbot.munchbot.data.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.model.Planner
import com.munchbot.munchbot.data.repository.PlannerRepository

class PlannerViewModel : ViewModel() {
    private val plannerRepository = PlannerRepository()
    val habitListLiveData = MutableLiveData<List<Planner>?>()

    fun loadHabits(userId: String) {
        plannerRepository.getHabits(userId) { habits ->
            habitListLiveData.postValue(habits)
        }
    }

    fun addHabit(userId: String, habitId: String, habit: Planner) {
        plannerRepository.saveHabit(userId, habitId, habit)
    }

    fun updateHabit(userId: String, habitId: String, habit: Planner) {
        plannerRepository.updateHabit(userId, habitId, habit)
    }

    fun deleteHabit(userId: String, habitId: String) {
        plannerRepository.deleteHabit(userId, habitId)
    }

    fun getHabitId(): String {
        val uid = getUserId() ?: "defaultUid"
        return "${uid}habits${System.currentTimeMillis()}"
    }
}
