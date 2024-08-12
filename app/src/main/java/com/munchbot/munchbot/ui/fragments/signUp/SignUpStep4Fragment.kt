package com.munchbot.munchbot.ui.fragments.signUp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.databinding.SignUp4Binding
import java.util.Calendar

class SignUpStep4Fragment : Fragment() {
    private lateinit var binding: SignUp4Binding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUp4Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)
        SetupUI.setupUI(binding.root)

        binding.calendarEditText.setOnClickListener {
            showDatePickerDialog()
        }

        binding.calendarEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = binding.calendarEditText.text.toString()
                val regex = Regex("^(0?[1-9]|[12][0-9]|3[01])-(0?[1-9]|1[012])-[0-9]{4}\$")
                val dayMonthYear = input.split("-")

                if (dayMonthYear.size == 3) {
                    val day = dayMonthYear[0].toIntOrNull() ?: 0
                    val month = dayMonthYear[1].toIntOrNull() ?: 0

                    if (!input.matches(regex)) {
                        binding.calendarEditText.error = "Invalid date format. Please use DD-MM-YYYY."
                    } else if (day !in 1..31) {
                        binding.calendarEditText.error = "Invalid date. Day must be between 1 and 31."
                    } else if (month !in 1..12) {
                        binding.calendarEditText.error = "Invalid date. Month must be between 1 and 12."
                    } else {
                        binding.calendarEditText.error = null
                    }
                } else {
                    binding.calendarEditText.error = "Invalid date format. Please use DD-MM-YYYY."
                }
            }
        }

        val genderEditText = binding.genderEditText
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_dropdown_item_1line,
            arrayOf("Male", "Female")
        )
        genderEditText.setAdapter(adapter)

        genderEditText.onFocusChangeListener = View.OnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                val input = genderEditText.text.toString()
                if (input !in arrayOf("Male", "Female")) {
                    binding.genderTextInputLayout.error = "Please specify Male or Female"
                    genderEditText.error = "Please specify Male or Female"
                } else {
                    binding.genderTextInputLayout.error = null
                    genderEditText.error = null
                }
            }
        }
    }

    private fun showDatePickerDialog() {
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, day ->
                val selectedDate = "$day-${month + 1}-$year"
                binding.calendarEditText.setText(selectedDate)
            },
            Calendar.getInstance().get(Calendar.YEAR),
            Calendar.getInstance().get(Calendar.MONTH),
            Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }
}
