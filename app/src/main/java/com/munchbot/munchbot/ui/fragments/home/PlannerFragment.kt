package com.munchbot.munchbot.ui.fragments.home

import android.animation.ArgbEvaluator
import android.annotation.SuppressLint
import android.content.ContentValues.TAG
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.annotation.GlideModule
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.getPetId
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.viewmodel.PetViewModel
import com.munchbot.munchbot.data.viewmodel.PlannerViewModel
import com.munchbot.munchbot.data.viewmodel.UserViewModel
import com.munchbot.munchbot.databinding.FragmentPlannerBinding
import com.munchbot.munchbot.ui.adapters.PlannerAdapter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import kotlin.math.abs

@GlideModule
class PlannerFragment : MunchBotFragments() {
    private var _binding: FragmentPlannerBinding? = null
    private val binding get() = _binding!!
    private val userViewModel: UserViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()
    private val plannerViewModel: PlannerViewModel by viewModels()
    private lateinit var plannerAdapter: PlannerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPlannerBinding.inflate(inflater, container, false)

        plannerAdapter = PlannerAdapter(
            mutableListOf(),
            binding.DailyHabitsRecyclerView,
            requireContext(),
            plannerViewModel
        )

        binding.DailyHabitsRecyclerView.apply {
            layoutManager = LinearLayoutManager(context)
            adapter = plannerAdapter
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.black)

        daysLogic()
        setupObservers()
        setupListeners()
        deleteHabitWithSwipe()

        val userId = getUserId() ?: return
        plannerViewModel.loadHabits(userId)
    }

    fun daysLogic() {

        val dayFormat = SimpleDateFormat("dd", Locale.getDefault())
        val monthFormat = SimpleDateFormat("MMM", Locale.getDefault())

        val currentDate = Calendar.getInstance()

        val day3 = currentDate.clone() as Calendar
        day3.add(Calendar.DAY_OF_MONTH, -3)

        val day2 = currentDate.clone() as Calendar
        day2.add(Calendar.DAY_OF_MONTH, -2)

        val day1 = currentDate.clone() as Calendar
        day1.add(Calendar.DAY_OF_MONTH, -1)

        val dayPlus1 = currentDate.clone() as Calendar
        dayPlus1.add(Calendar.DAY_OF_MONTH, 1)

        val dayPlus2 = currentDate.clone() as Calendar
        dayPlus2.add(Calendar.DAY_OF_MONTH, 2)

        val dayPlus3 = currentDate.clone() as Calendar
        dayPlus3.add(Calendar.DAY_OF_MONTH, 3)

        binding.day3Text.text =
            getString(R.string.day,
            dayFormat.format(day3.time),
            monthFormat.format(day3.time)
        )

        binding.day2Text.text = getString(
            R.string.day,
            dayFormat.format(day2.time),
            monthFormat.format(day2.time)
        )

        binding.day1Text.text = getString(
            R.string.day,
            dayFormat.format(day1.time),
            monthFormat.format(day1.time)
        )

        binding.dayText.text = getString(
            R.string.day,
            dayFormat.format(currentDate.time),
            monthFormat.format(currentDate.time)
        )

        binding.dayPlus1Text.text = getString(
            R.string.day,
            dayFormat.format(dayPlus1.time),
            monthFormat.format(dayPlus1.time)
        )

        binding.dayPlus2Text.text = getString(
            R.string.day,
            dayFormat.format(dayPlus2.time),
            monthFormat.format(dayPlus2.time)
        )

        binding.dayPlus3Text.text = getString(
            R.string.day,
            dayFormat.format(dayPlus3.time),
            monthFormat.format(dayPlus3.time)
        )

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setupObservers() {
        petViewModel.petLiveData.observe(viewLifecycleOwner) { pet ->
            pet?.let {
                val petNameTextView = binding.TakeALookAtHowPetSDayWasPlanned
                val stringPetName =
                    getString(R.string.Take_a_look_at_how_pet_s_day_was_planned, it.petName)
                petNameTextView.text = stringPetName

                val petImageView = binding.petImagePlanner
                if (it.petProfileImage.isNotEmpty()) {
                    Log.d("PlannerFragment", "Loading image URL: ${it.petProfileImage}")
                    Glide.with(this)
                        .load(it.petProfileImage)
                        .error(R.drawable.ic_error)
                        .into(petImageView)
                } else {
                    petImageView.setImageResource(R.drawable.ic_error)
                }
            }
        }

        plannerViewModel.habitListLiveData.observe(viewLifecycleOwner) { habits ->
            habits?.let {
                plannerAdapter.habitList.clear()
                plannerAdapter.habitList.addAll(it)
                plannerAdapter.notifyDataSetChanged()
            }
        }
        setupGetter()
    }

    private fun setupGetter() {
        val userId = getUserId() ?: return
        Log.d(TAG, "User ID $userId")

        val petId = getPetId(userId)
        Log.d(TAG, "Pet ID $petId")

        petViewModel.loadPet(userId, petId)
        userViewModel.loadUser(userId)
        plannerViewModel.loadHabits(userId)
    }

    private fun setupListeners() {
        binding.AddDailyHabits.setOnClickListener {
            plannerAdapter.addHabit()
        }
    }

    private fun deleteHabitWithSwipe() {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.RIGHT) {

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val habit = plannerAdapter.habitList[position]
                    val userId = getUserId() ?: return
                    Log.d(TAG, "User ID $userId")
                    plannerViewModel.deleteHabit(userId, habit.id)
                    plannerAdapter.habitList.removeAt(position)
                    plannerAdapter.notifyItemRemoved(position)
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val backgroundColorStart = ContextCompat.getColor(
                        requireContext(),
                        com.firebase.ui.auth.R.color.fui_transparent
                    )
                    val backgroundColorEnd =
                        ContextCompat.getColor(requireContext(), R.color.firstColor)

                    val paint = Paint()
                    paint.color = if (dX > 0) {
                        backgroundColorEnd
                    } else {
                        val fraction = abs(dX) / itemView.width
                        val interpolatedColor = ArgbEvaluator().evaluate(
                            fraction,
                            backgroundColorStart,
                            backgroundColorEnd
                        ) as Int
                        interpolatedColor
                    }

                    paint.alpha = (abs(dX) / itemView.width * 255).toInt()

                    c.drawRect(
                        itemView.left.toFloat(),
                        itemView.top.toFloat(),
                        itemView.right.toFloat(),
                        itemView.bottom.toFloat(),
                        paint
                    )

                    super.onChildDraw(
                        c,
                        recyclerView,
                        viewHolder,
                        dX,
                        dY,
                        actionState,
                        isCurrentlyActive
                    )
                }

                override fun clearView(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder
                ) {
                    super.clearView(recyclerView, viewHolder)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.DailyHabitsRecyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
