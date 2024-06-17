package com.example.greatingcard

import com.example.greatingcard.model.ProductModel
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.greatingcard.databinding.ActivityProductDetailBinding

class ProductDetailActivity : AppCompatActivity()/*, CartFragment.OnProductAddedListener*/ {
    private lateinit var binding: ActivityProductDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProductDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backOneStep.setOnClickListener {
            finish()
        }

        val foodImage = intent.getIntExtra("foodImage", 0)
        val foodName = intent.getStringExtra("foodName") ?: ""
        val foodPrice = intent.getStringExtra("foodPrice") ?: ""
        val foodTitle = intent.getStringExtra("foodTitle") ?: ""

        binding.imgProductDetail.setImageResource(foodImage)
        binding.tvNameProductDetail.text=foodName
        binding.tvPriceProductDetail.text=foodPrice
        binding.tvTitleProductDetail.text=foodTitle
        binding.btnAddToCartDetail.setOnClickListener{
            finish()
        }
    }
}
