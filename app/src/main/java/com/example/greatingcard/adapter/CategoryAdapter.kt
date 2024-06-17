package com.example.greatingcard.adapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greatingcard.R
import com.example.greatingcard.model.CategoryModel

class CategoryAdapter(private val onItemClick: (CategoryModel) -> Unit) :
    RecyclerView.Adapter<CategoryAdapter.CategoryViewHolder>() {

    private var categoryList: List<CategoryModel> = listOf()
    private var selectedCategoryPosition: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CategoryViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.category_item_layout, parent, false)
        return CategoryViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: CategoryViewHolder, position: Int) {
        val currentItem = categoryList[position]
        holder.bind(currentItem, position == selectedCategoryPosition)
    }

    override fun getItemCount() = categoryList.size

    fun submitList(categories: List<CategoryModel>) {
        categoryList = categories
        notifyDataSetChanged()
    }

    inner class CategoryViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val categoryName: TextView = itemView.findViewById(R.id.categoryName)
        private val categoryImage: ImageView = itemView.findViewById(R.id.categoryImage)

        init {
            itemView.setOnClickListener {
                val position = adapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    selectedCategoryPosition = position
                    notifyDataSetChanged()
                    val category = categoryList[position]
                    onItemClick(category)
                }
            }
        }

        fun bind(category: CategoryModel, isSelected: Boolean) {
            categoryName.text = category.name
            categoryImage.setImageResource(category.imageResourceId)
            if (isSelected) {
                categoryName.setTextColor(itemView.context.resources.getColor(R.color.yellow))
            } else {
                categoryName.setTextColor(itemView.context.resources.getColor(R.color.white))
            }
        }
    }
}
