package com.example.greatingcard.model

import java.io.Serializable

data class ProductModel(
    val id: Int,
    val imageResourceId: Int,
    val name: String,
    val title: String,
    val price: String,
    var quantity: Int = 1, // Default quantity is 1
    var isInCart: Boolean = false, // Indicates whether the product is already in the cart
    var isFavorite: Boolean = false,  // Add this field
) : Serializable
