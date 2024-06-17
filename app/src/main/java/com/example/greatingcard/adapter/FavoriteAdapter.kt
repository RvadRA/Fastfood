package com.example.greatingcard.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greatingcard.R
import com.example.greatingcard.model.ProductModel

class FavoriteAdapter(
    private val context: Context,
    private var favoriteItems: List<ProductModel>,
    private val listener: FavoriteItemListener,
) : RecyclerView.Adapter<FavoriteAdapter.FavoriteViewHolder>() {

    interface FavoriteItemListener {
        fun onRemoveClicked(product: ProductModel)
        fun onAddToCartClicked(product: ProductModel)
        fun onRemoveFromCartClicked(product: ProductModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FavoriteViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.favorite_item_layout, parent, false)
        return FavoriteViewHolder(view)
    }

    override fun onBindViewHolder(holder: FavoriteViewHolder, position: Int) {
        val currentItem = favoriteItems[position]
        holder.bind(currentItem)
    }

    override fun getItemCount(): Int = favoriteItems.size

    fun updateFavoriteItems(items: List<ProductModel>) {
        favoriteItems = items
        notifyDataSetChanged()
    }

    inner class FavoriteViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val favoriteProductName: TextView = itemView.findViewById(R.id.favoriteProductName)
        private val favoriteProductPrice: TextView = itemView.findViewById(R.id.favoriteProductPrice)
        private val favoriteProductImage: ImageView = itemView.findViewById(R.id.favoriteProductImage)
        private val btnRemoveFavorite: ImageView = itemView.findViewById(R.id.removeFavoriteButton)
        private val addToCartButton: Button = itemView.findViewById(R.id.btnAddToCart)
        fun bind(product: ProductModel) {
            with(itemView) {
                favoriteProductName.text = product.name
                favoriteProductPrice.text = product.price
                favoriteProductImage.setImageResource(product.imageResourceId)
                btnRemoveFavorite.setOnClickListener {
                    listener.onRemoveClicked(product)
                }

                /*addToCartButton.setOnClickListener {
                    listener.onAddToCartClicked(product)
                }*/
                addToCartButton.setOnClickListener {
                    if (product.isInCart) {
                        listener.onRemoveFromCartClicked(product)
                    } else {
                        listener.onAddToCartClicked(product)
                    }
                    product.isInCart = !product.isInCart
                    updateButtonState(product)
                    notifyItemChanged(adapterPosition) // Ensure the item is updated in the adapter
                }

                updateButtonState(product)
            }
        }

        private fun updateButtonState(product: ProductModel) {
            if (product.isInCart) {
                addToCartButton.text = context.getString(R.string.add_to_cart)
            } else {
                addToCartButton.text = context.getString(R.string.delete_from_cart)
            }
        }
    }
}
