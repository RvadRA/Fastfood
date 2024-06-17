package com.example.greatingcard.screen.main.cart

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button

import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.greatingcard.R
import com.example.greatingcard.adapter.CartAdapter
import com.example.greatingcard.databinding.FragmentCartBinding
import com.example.greatingcard.model.MainViewModel
import com.example.greatingcard.model.ProductModel
import com.example.greatingcard.screen.auth.SignActivity
import com.example.greatingcard.screen.main.order.checkout.CheckoutActivity
import java.math.BigDecimal

class CartFragment : Fragment() {

    private lateinit var binding: FragmentCartBinding
    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var totalPriceTextView: TextView
    private lateinit var totalItemsTextView: TextView
    private lateinit var emptyCartTextView: TextView
    private lateinit var btnCheckout: Button


    // Member variable to store the total price
    private var totalPrice: BigDecimal = BigDecimal.ZERO

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_cart, container, false)
        recyclerView = view.findViewById(R.id.cartRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        totalPriceTextView = view.findViewById(R.id.totalPriceTextView)
        totalItemsTextView = view.findViewById(R.id.totalItemsTextView)
        emptyCartTextView = view.findViewById(R.id.emptyCartTextView)
        btnCheckout=view.findViewById(R.id.btnCheckout)

        // In the btnCheckout click listener
        btnCheckout.setOnClickListener {
            val intent = Intent(requireContext(), CheckoutActivity::class.java)
            intent.putExtra("TOTAL_PRICE", totalPrice.toString())
            startActivityForResult(intent, CHECKOUT_REQUEST_CODE)
        }



        cartAdapter = CartAdapter(emptyList(), object : CartAdapter.CartItemListener {
            override fun onRemoveClicked(product: ProductModel) {
                viewModel.removeFromCart(product)
            }

            override fun onQuantityIncrease(product: ProductModel) {
                val newQuantity = product.quantity + 1
                viewModel.updateCartItemQuantity(product, newQuantity)
            }

            override fun onQuantityDecrease(product: ProductModel) {
                val newQuantity = product.quantity - 1
                if (newQuantity > 0) {
                    viewModel.updateCartItemQuantity(product, newQuantity)
                } else {
                    viewModel.removeFromCart(product)
                }
            }
        })

        recyclerView.adapter = cartAdapter

        viewModel.cartItems.observe(viewLifecycleOwner, Observer { cartItems ->
            cartAdapter.updateCartItems(cartItems)
            updateTotalPrice(cartItems)
            updateEmptyCartMessage(cartItems)
        })

        return view
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == CHECKOUT_REQUEST_CODE && resultCode == AppCompatActivity.RESULT_OK) {
            viewModel.clearCart() // Refresh cart after ordering food
        }
    }
    private fun updateTotalPrice(cartItems: List<ProductModel>) {
        var totalPrice = BigDecimal.ZERO
        var totalItems = 0
        for (item in cartItems) {
            totalPrice += BigDecimal(item.price.replace(" ₽", "")) * BigDecimal(item.quantity)
            totalItems += item.quantity

            // Store totalPrice in a member variable for later use
            this.totalPrice = totalPrice
        }
        totalPriceTextView.text = "$totalPrice ₽"
        totalItemsTextView.text = "Total items: $totalItems"
    }

    private fun updateEmptyCartMessage(cartItems: List<ProductModel>) {
        val isEmpty = cartItems.isEmpty()
        emptyCartTextView.visibility = if (cartItems.isEmpty()) View.VISIBLE else View.GONE
        recyclerView.visibility = if (cartItems.isEmpty()) View.GONE else View.VISIBLE

        // Disable or enable the checkout button based on whether the cart is empty
        btnCheckout.isEnabled = !isEmpty
        if (isEmpty) {
            btnCheckout.alpha = 0.5f // Make the button appear disabled
        } else {
            btnCheckout.alpha = 1.0f // Make the button appear enabled
        }
    }
    companion object {
        const val CHECKOUT_REQUEST_CODE = 1
    }
}
