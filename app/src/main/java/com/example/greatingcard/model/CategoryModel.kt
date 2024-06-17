package com.example.greatingcard.model

import com.example.greatingcard.model.ProductModel
import java.io.Serializable


data class  CategoryModel(
    val id: Int,
    val name: String,
    val imageResourceId: Int,
    val products: List<ProductModel>
) : Serializable
