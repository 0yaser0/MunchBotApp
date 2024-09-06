package com.munchbot.munchbot.ui.fragments.home.health

import android.animation.ArgbEvaluator
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.graphics.Canvas
import android.graphics.Paint
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.textfield.TextInputEditText
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.data.database.getPetId
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.viewmodel.MedicationsViewModel
import com.munchbot.munchbot.data.viewmodel.PetViewModel
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.data.viewmodel.UserViewModel
import com.munchbot.munchbot.databinding.FragmentHealth4Binding
import com.munchbot.munchbot.ui.adapters.MedicationsAdapter
import kotlin.math.abs

class Health4Fragment : MunchBotFragments() {
    private var _binding: FragmentHealth4Binding? = null
    private val binding get() = _binding!!
    private lateinit var medicationAdapter: MedicationsAdapter
    private lateinit var sharedViewModel: SignUpSharedViewModel
    private val medicationsViewModel: MedicationsViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val userId = getUserId()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHealth4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        setupGetter()
        setupObservers()
        deleteVetWithSwipe()

        medicationAdapter = MedicationsAdapter(mutableListOf())
        binding.health4RecyclerView.layoutManager = LinearLayoutManager(context)
        binding.health4RecyclerView.adapter = medicationAdapter

        observeMedicationsLiveData()

        binding.addRoundBrokenLine.setOnClickListener {
            showAddMedicationDialog()
        }

        binding.icBack.setOnClickListener {
            previousFragment()
        }
    }

    fun previousFragment() {
        Log.d("HealthFragment", "Going back to previous fragment")
        parentFragmentManager.popBackStack()
    }

    private fun setupViewModel() {
        sharedViewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]
    }

    private fun observeMedicationsLiveData() {
        medicationsViewModel.medicationsLiveData.observe(viewLifecycleOwner) { medications ->
            medications?.let {
                Log.d("Health4Fragment", "Medications received: $medications")
                medicationAdapter.setMedications(it)
            } ?: run {
                Log.d("Health4Fragment", "No medications found")
            }
        }
    }

    private fun setupObservers() {
        userId?.let {
            medicationsViewModel.loadAllMedications(it)
        }
    }

    private fun setupGetter() {
        if (userId != null) {
            val petId = getPetId(userId)
            Log.d(TAG, "Pet ID $petId")
            petViewModel.loadPet(userId, petId)
            userViewModel.loadUser(userId)
        }
    }

    private fun showAddMedicationDialog() {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.dialog_add_medications, null)
        val dialogBuilder = AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setTitle("Add Medicine")

        val alertDialog = dialogBuilder.create()
        alertDialog.show()

        val addButton = dialogView.findViewById<Button>(R.id.add_medicine_button)
        val nameEditText = dialogView.findViewById<TextInputEditText>(R.id.medicine_name)
        val dosageEditText = dialogView.findViewById<TextInputEditText>(R.id.medicine_dosage)
        val amountEditText = dialogView.findViewById<TextInputEditText>(R.id.medicine_amount)
        val durationEditText = dialogView.findViewById<TextInputEditText>(R.id.medicine_duration)
        val notifyCheckBox = dialogView.findViewById<CheckBox>(R.id.notifyCheckBoxDialog)

        addButton.setOnClickListener {
            val name = nameEditText.text.toString()
            val dosage = dosageEditText.text.toString()
            val amount = amountEditText.text.toString()
            val duration = durationEditText.text.toString()
            val notify = notifyCheckBox.isChecked

            if (name.isNotEmpty() && dosage.isNotEmpty() && amount.isNotEmpty() && duration.isNotEmpty()) {
                val userId = getUserId()
                if (userId != null) {
                    sharedViewModel.saveNewMedication(
                        userId,
                        name,
                        dosage,
                        amount,
                        duration,
                        notify,
                        medicationAdapter
                    )
                }

                alertDialog.dismiss()
            } else {
               Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun deleteVetWithSwipe() {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val medication = medicationAdapter.getMedicationAtPosition(position)

                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete Vet")
                        setMessage("Are you sure you want to delete this vet?")
                        setPositiveButton("Yes") { dialog, _ ->
                            medicationAdapter.removeMedicationAtPosition(position)
                            userId?.let {
                                medicationsViewModel.deleteMedication(userId, medication.medicationId)
                            }
                            dialog.dismiss()
                        }
                        setNegativeButton("No") { dialog, _ ->
                            medicationAdapter.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                        show()

                        val alertDialog = this.create()
                        alertDialog.setOnShowListener {
                            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                            positiveButton.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.firstColor
                                )
                            )
                            negativeButton.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.thirdColor
                                )
                            )
                        }
                    }
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
                    val backgroundColorStart = ContextCompat.getColor(requireContext(), com.firebase.ui.auth.R.color.fui_transparent)
                    val backgroundColorEnd = ContextCompat.getColor(requireContext(), R.color.red)

                    val paint = Paint()
                    paint.color = if (dX > 0) {
                        backgroundColorEnd
                    } else {
                        val fraction = abs(dX) / itemView.width
                        val interpolatedColor = ArgbEvaluator().evaluate(fraction, backgroundColorStart, backgroundColorEnd) as Int
                        interpolatedColor
                    }

                    paint.alpha = (abs(dX) / itemView.width * 255).toInt()

                    c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), paint)

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.health4RecyclerView)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
