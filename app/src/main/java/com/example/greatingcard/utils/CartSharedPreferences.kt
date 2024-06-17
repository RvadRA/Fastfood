package com.example.greatingcard.utils
import android.content.Context
import com.example.greatingcard.model.ProductModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class CartSharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("CartSharedPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveCartItems(cartItems: List<ProductModel>) {
        val json = gson.toJson(cartItems)
        sharedPreferences.edit().putString("cartItems", json).apply()
    }

    fun getCartItems(): List<ProductModel> {
        val json = sharedPreferences.getString("cartItems", null)
        return if (json != null) {
            val type = object : TypeToken<List<ProductModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
