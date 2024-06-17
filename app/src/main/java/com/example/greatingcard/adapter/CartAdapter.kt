package com.example.greatingcard.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.greatingcard.R
import com.example.greatingcard.model.ProductModel

class CartAdapter(
    private var items: List<ProductModel>,

    private val listener: CartItemListener
) : RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    interface CartItemListener {
        fun onRemoveClicked(product: ProductModel)
        fun onQuantityIncrease(product: ProductModel)
        fun onQuantityDecrease(product: ProductModel)
    }

    inner class CartViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val productImage: ImageView = itemView.findViewById(R.id.productImage)
        val productName: TextView = itemView.findViewById(R.id.productName)
        val productPrice: TextView = itemView.findViewById(R.id.productPrice)
        val productQuantity: TextView = itemView.findViewById(R.id.productQuantity)
        val increaseButton: Button = itemView.findViewById(R.id.btnPlusQuantity)
        val decreaseButton: Button = itemView.findViewById(R.id.btnMinusQuantity)
        val removeButton: Button = itemView.findViewById(R.id.removeButton)

        fun bind(product: ProductModel) {
            productImage.setImageResource(product.imageResourceId)
            productName.text = product.name
            productPrice.text = product.price
            productQuantity.text = product.quantity.toString()


            increaseButton.setOnClickListener {
                listener.onQuantityIncrease(product)
            }

            decreaseButton.setOnClickListener {
                listener.onQuantityDecrease(product)
            }

            removeButton.setOnClickListener {
                listener.onRemoveClicked(product)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.cart_prodact_item_layout, parent, false)
        return CartViewHolder(view)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int = items.size

    fun updateCartItems(newItems: List<ProductModel>) {
        items = newItems
        notifyDataSetChanged()
    }
}
