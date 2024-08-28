package com.munchbot.munchbot.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.munchbot.munchbot.R
import com.munchbot.munchbot.data.model.Vet

class VetAdapter(private var vetList: MutableList<Vet>) :
    RecyclerView.Adapter<VetAdapter.VetViewHolder>() {

    private var expandedPosition: Int = RecyclerView.NO_POSITION

    inner class VetViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val profileImage: ImageView = itemView.findViewById(R.id.vetProfileImage)
        val name: TextView = itemView.findViewById(R.id.name)
        val category: TextView = itemView.findViewById(R.id.category)
        val expandedView: View = itemView.findViewById(R.id.expandedView)
        val phoneNumber: TextView = itemView.findViewById(R.id.phoneNumber)
        val email: TextView = itemView.findViewById(R.id.email)

        fun bind(vet: Vet) {
            val requestOptions = RequestOptions()
                .placeholder(R.drawable.ic_camera)
                .error(R.drawable.ic_error)
                .transform(CircleCrop())  // Apply CircleCrop to make the image circular

            Glide.with(itemView.context)
                .load(vet.profileImage)
                .apply(requestOptions)
                .into(profileImage)

            name.text = vet.name
            category.text = vet.category
            phoneNumber.text = itemView.context.getString(R.string.phone_label, vet.phoneNumber)
            email.text = itemView.context.getString(R.string.email_label, vet.email)

            expandedView.visibility =
                if (adapterPosition == expandedPosition) View.VISIBLE else View.GONE

            itemView.setOnClickListener {
                val previousPosition = expandedPosition
                expandedPosition = if (expandedPosition == adapterPosition) {
                    RecyclerView.NO_POSITION
                } else {
                    adapterPosition
                }
                notifyItemChanged(previousPosition)
                notifyItemChanged(expandedPosition)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VetViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_vet, parent, false)
        return VetViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: VetViewHolder, position: Int) {
        holder.bind(vetList[position])
    }

    override fun getItemCount(): Int = vetList.size

    fun addVet(vet: Vet) {
        vetList.add(vet)
        notifyItemInserted(vetList.size - 1)
    }
}
