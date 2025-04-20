package com.example.salamtech_gp

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class PillsAdapter(private val pillList: List<Pill>) :
    RecyclerView.Adapter<PillsAdapter.PillViewHolder>() {

    class PillViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title = itemView.findViewById<TextView>(R.id.pillTitle)
        val brand = itemView.findViewById<TextView>(R.id.pillBrand)
        val image = itemView.findViewById<ImageView>(R.id.pillImage)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PillViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_pill, parent, false)
        return PillViewHolder(view)
    }

    override fun onBindViewHolder(holder: PillViewHolder, position: Int) {
        val pill = pillList[position]
        holder.title.text = pill.title
        holder.brand.text = pill.brand

        // Glide to load image from URL
        Glide.with(holder.itemView.context)
            .load(pill.imageUrl)
            .placeholder(R.drawable.ic_launcher_background) // Replace with your placeholder
            .into(holder.image)
    }

    override fun getItemCount() = pillList.size
}
