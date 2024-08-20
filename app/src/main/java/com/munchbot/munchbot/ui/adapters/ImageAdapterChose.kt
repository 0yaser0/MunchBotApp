package com.munchbot.munchbot.ui.adapters

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.munchbot.munchbot.R

class ImageAdapterChose(
    private val images: List<Int>,
    private val listener: OnImageClickListener
) : RecyclerView.Adapter<ImageAdapterChose.ImageViewHolder>(){

    private var selectedPosition = -1

    inner class ImageViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageView: ImageView = itemView.findViewById(R.id.dog)
        val background: View = itemView.findViewById(R.id.ifSelected)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition % images.size
                listener.onImageClick(position)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.image_container, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {
        val actualPosition = position % images.size
        holder.imageView.setImageResource(images[actualPosition])

        if (actualPosition == selectedPosition) {
            holder.background.setBackgroundResource(R.drawable.pet_chose_selected)
        } else {
            holder.background.setBackgroundResource(R.drawable.pet_chose)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setSelectedPosition(position: Int) {
        selectedPosition = position
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int = Int.MAX_VALUE

    interface OnImageClickListener {
        fun onImageClick(position: Int)
    }

}