package com.munchbot.munchbot.ui.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.munchbot.munchbot.R
import com.munchbot.munchbot.data.model.Medications

class MedicationsAdapter(private var medicationList: MutableList<Medications>) :
    RecyclerView.Adapter<MedicationsAdapter.MedicationViewHolder>() {

    inner class MedicationViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val name: TextView = itemView.findViewById(R.id.medicine_name)
        val dosage: TextView = itemView.findViewById(R.id.dosage)
        val amount: TextView = itemView.findViewById(R.id.amount)
        val duration: TextView = itemView.findViewById(R.id.duration)

        fun bind(medication: Medications) {
            name.text = medication.name
            dosage.text = medication.dosage
            amount.text = medication.amount
            duration.text = medication.duration
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MedicationViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_medication, parent, false)
        return MedicationViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: MedicationViewHolder, position: Int) {
        holder.bind(medicationList[position])
    }

    override fun getItemCount(): Int = medicationList.size

    fun addMedication(medication: Medications) {
        medicationList.add(medication)
        notifyItemInserted(medicationList.size - 1)
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setMedications(medications: List<Medications>) {
        medicationList.clear()
        medicationList.addAll(medications)
        Log.d("MedicationsAdapter", "Adapter list updated with ${medications.size} items")
        notifyDataSetChanged()
    }


    fun getMedicationAtPosition(position: Int): Medications {
        return medicationList[position]
    }

    fun removeMedicationAtPosition(position: Int) {
        medicationList.removeAt(position)
        notifyItemRemoved(position)
    }

    fun updateMedicationAtPosition(position: Int, medication: Medications) {
        medicationList[position] = medication
        notifyItemChanged(position)
    }

}
