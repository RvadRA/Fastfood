package com.example.greatingcard.adapter
import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greatingcard.R
import com.example.greatingcard.model.ProductModel
import com.example.greatingcard.ProductDetailActivity
import androidx.appcompat.widget.AppCompatImageButton

import java.util.*
class ProductAdapter(
    private var productList: List<ProductModel>,
    private val context: Context,
    private val addToCartListener: AddToCartListener
) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>(), Filterable {

    interface AddToCartListener {
        fun onAddToCartClicked(product: ProductModel)
        fun onRemoveFromCartClicked(product: ProductModel)
        fun onAddToFavoriteClicked(product: ProductModel)
        fun onRemoveFromFavoriteClicked(product: ProductModel)
    }

    private var filteredProductList: List<ProductModel> = productList

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item_layout, parent, false)
        return ProductViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val currentItem = filteredProductList[position]
        holder.bind(currentItem)
    }

    override fun getItemCount() = filteredProductList.size

    fun submitList(products: List<ProductModel>) {
        productList = products
        filteredProductList = products
        notifyDataSetChanged()
    }

    inner class ProductViewHolder(itemView: View) :
        RecyclerView.ViewHolder(itemView), View.OnClickListener {
        private val productNameTextView: TextView = itemView.findViewById(R.id.productNameTextView)
        private val productPriceTextView: TextView = itemView.findViewById(R.id.productPriceTextView)
        private val productImageView: ImageView = itemView.findViewById(R.id.imgProduct)
        private val addToCartButton: Button = itemView.findViewById(R.id.btnAddToCart)
        private val addToFavoriteButton: ImageView  = itemView.findViewById(R.id.btnAddToFavorite)

        init {
            itemView.setOnClickListener(this)
            addToCartButton.setOnClickListener(this)
            addToFavoriteButton.setOnClickListener(this)
        }

        fun bind(product: ProductModel) {
            productNameTextView.text = product.name
            productPriceTextView.text = product.price
            productImageView.setImageResource(product.imageResourceId)

            updateButtonState(product)

            addToCartButton.setOnClickListener {
                if (product.isInCart) {
                    addToCartListener.onRemoveFromCartClicked(product)
                } else {
                    addToCartListener.onAddToCartClicked(product)
                }
                product.isInCart = !product.isInCart
                updateButtonState(product)
            }

            addToFavoriteButton.setOnClickListener {
                if (product.isFavorite) {
                    addToCartListener.onRemoveFromFavoriteClicked(product)
                } else {
                    addToCartListener.onAddToFavoriteClicked(product)
                }
                product.isFavorite = !product.isFavorite
                updateButtonState(product)
            }

        }

        private fun updateButtonState(product: ProductModel) {
            if (product.isInCart) {
                addToCartButton.text = context.getString(R.string.add_to_cart)
            } else {
                addToCartButton.text = context.getString(R.string.delete_from_cart)
            }
            if (product.isFavorite) {
                addToFavoriteButton.setImageResource(R.drawable.favorite_filled_svgrepo_com) // Change to unfilled favorite icon
            } else {
                addToFavoriteButton.setImageResource(R.drawable.favorite_svgrepo_com) // Change to filled favorite icon
            }
        }

        override fun onClick(v: View?) {
            val position = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                val product = filteredProductList[position]
                when (v?.id) {
                    R.id.btnAddToCart -> addToCartListener.onAddToCartClicked(product)
                    R.id.btnAddToFavorite -> addToCartListener.onAddToFavoriteClicked(product)
                    else -> {
                        val intent = Intent(context, ProductDetailActivity::class.java).apply {
                            putExtra("productId", product.id)
                            putExtra("foodImage", product.imageResourceId)
                            putExtra("foodName", product.name)
                            putExtra("foodPrice", product.price)
                            putExtra("foodTitle", product.title)
                        }
                        context.startActivity(intent)
                    }
                }
            }
        }
    }

    override fun getFilter(): Filter {
        return object : Filter() {
            override fun performFiltering(constraint: CharSequence?): FilterResults {
                val filteredList = mutableListOf<ProductModel>()
                if (constraint.isNullOrEmpty()) {
                    filteredList.addAll(productList)
                } else {
                    val filterPattern =
                        constraint.toString().toLowerCase(Locale.getDefault()).trim()
                    for (product in productList) {
                        if (product.name.toLowerCase(Locale.getDefault())
                                .contains(filterPattern)
                        ) {
                            filteredList.add(product)
                        }
                    }
                }
                val results = FilterResults()
                results.values = filteredList
                return results
            }

            override fun publishResults(constraint: CharSequence?, results: FilterResults?) {
                filteredProductList = results?.values as List<ProductModel>
                notifyDataSetChanged()
            }
        }
    }
}
