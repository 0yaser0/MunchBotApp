package com.munchbot.munchbot.ui.adapters

import android.annotation.SuppressLint
import android.content.Context
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.munchbot.munchbot.R
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.model.Planner
import com.munchbot.munchbot.data.viewmodel.PlannerViewModel

class PlannerAdapter(
    var habitList: MutableList<Planner>,
    private val recyclerView: RecyclerView,
    private val context: Context,
    private val plannerViewModel: PlannerViewModel
) : RecyclerView.Adapter<PlannerAdapter.HabitViewHolder>() {

    private var currentFocusedPosition: Int = -1
    private var isEditing: Boolean = false
    private var isEditModeEnabled: Boolean = false



    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val editText: EditText = itemView.findViewById(R.id.task)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_planner, parent, false)
        return HabitViewHolder(view)
    }

    @SuppressLint("NotifyDataSetChanged")
    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        val habit = habitList[position]
        holder.editText.setText(habit.task)

        holder.editText.isFocusableInTouchMode = false
        holder.editText.isFocusable = false
        holder.editText.isEnabled = true


        holder.itemView.setOnLongClickListener {
            if (!isEditModeEnabled) {
                enableEditingForItem(holder, position)
                true
            } else {
                false
            }
        }

        holder.editText.setOnKeyListener { _, keyCode, event ->
            if (event.action == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_ENTER) {
                updateHabit(holder, position)
                true
            } else {
                false
            }
        }

        holder.editText.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                updateHabit(holder, position)
                true
            } else {
                false
            }
        }
    }

    override fun getItemCount(): Int = habitList.size

    private fun enableEditingForItem(holder: HabitViewHolder, position: Int) {
        holder.editText.isFocusableInTouchMode = true
        holder.editText.isFocusable = true
        holder.editText.requestFocus()
        holder.editText.setSelection(holder.editText.text.length)
        showSoftKeyboard(holder.editText)
        currentFocusedPosition = position
        isEditing = true
    }

    private fun updateHabit(holder: HabitViewHolder, position: Int) {
        val updatedTask = holder.editText.text.toString()
        val updatedHabit = habitList[position].copy(task = updatedTask)
        plannerViewModel.updateHabit(getUserId()!!, updatedHabit.id, updatedHabit)

        holder.editText.clearFocus()
        holder.editText.isFocusableInTouchMode = false
        holder.editText.isFocusable = false
        hideSoftKeyboard(holder.editText)
        currentFocusedPosition = -1
        isEditing = false

        habitList[position] = updatedHabit
        notifyItemChanged(position)
    }

    private fun showSoftKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(view, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun hideSoftKeyboard(view: View) {
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun addHabit() {
        if (!isEditing) {
            if (habitList.isEmpty() || habitList.last().task.isNotBlank()) {
                val newHabit = Planner(id = plannerViewModel.getHabitId(), task = "")
                habitList.add(newHabit)
                notifyItemInserted(habitList.size - 1)
//                updateGuidVisibility()

                val userId = getUserId()!!
                plannerViewModel.addHabit(userId, newHabit.id, newHabit)
                recyclerView.post {
                    recyclerView.scrollToPosition(habitList.size - 1)
                    enableEditingForItem(
                        recyclerView.findViewHolderForAdapterPosition(habitList.size - 1) as HabitViewHolder,
                        habitList.size - 1
                    )
                }
            } else {
                Toast.makeText(
                    context,
                    "Please finish editing the current habit before adding a new one.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        } else {
            Toast.makeText(
                context,
                "Please finish editing the current habit before adding a new one.",
                Toast.LENGTH_SHORT
            ).show()
        }
    }

//    fun updateGuidVisibility() {
//        val guidTextView = recyclerView.rootView.findViewById<TextView>(R.id.Guid)
//        if (habitList.isEmpty()) {
//            guidTextView.visibility = View.VISIBLE
//        } else {
//            guidTextView.visibility = View.GONE
//        }
//    }
}