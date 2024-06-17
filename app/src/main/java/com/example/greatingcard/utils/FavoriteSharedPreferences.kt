package com.example.greatingcard.utils
import android.content.Context
import com.example.greatingcard.model.ProductModel
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class FavoriteSharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("FavoriteSharedPreferences", Context.MODE_PRIVATE)
    private val gson = Gson()

    fun saveFavoriteItems(favoriteItems: List<ProductModel>) {
        val json = gson.toJson(favoriteItems)
        sharedPreferences.edit().putString("favoriteItems", json).apply()
    }

    fun getFavoriteItems(): List<ProductModel> {
        val json = sharedPreferences.getString("favoriteItems", null)
        return if (json != null) {
            val type = object : TypeToken<List<ProductModel>>() {}.type
            gson.fromJson(json, type)
        } else {
            emptyList()
        }
    }
}
