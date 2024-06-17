package com.example.greatingcard.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.greatingcard.databinding.SlideItemLayoutBinding
import com.example.greatingcard.model.SlideModel

class SlideAdapter(val items: List<SlideModel>) : RecyclerView.Adapter<SlideAdapter.ItemHolder>() {

    inner class ItemHolder(val binding: SlideItemLayoutBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ItemHolder {
        return ItemHolder(
            SlideItemLayoutBinding.inflate(
                LayoutInflater.from(parent.context),
                parent,
                false
            )
        )
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun onBindViewHolder(holder: ItemHolder, position: Int) {
        val item = items[position]
        with(holder.binding) {
            // Assuming you have ImageView, TextView for title, and TextView for subtitle in your layout
            imgSliderHome.setImageResource(item.image)
            titleSliderHome.text = item.title
            subTitleSliderHome.text = item.subtitle
        }

    }
}
