package com.example.greatingcard.screen.main.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.greatingcard.R
import com.example.greatingcard.adapter.CategoryAdapter
import com.example.greatingcard.adapter.ProductAdapter
import com.example.greatingcard.model.CategoryModel
import com.example.greatingcard.model.MainViewModel
import com.example.greatingcard.model.ProductModel

class HomeFragment : Fragment(), ProductAdapter.AddToCartListener  {

    private val viewModel: MainViewModel by activityViewModels()
    private lateinit var categoryAdapter: CategoryAdapter
    private lateinit var productAdapter: ProductAdapter
    private lateinit var userImageView: ImageView
    private lateinit var usernameTextView: TextView
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home, container, false)

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        userImageView = view.findViewById(R.id.userImageView)
        usernameTextView = view.findViewById(R.id.usernameTextView)

        // Observe the SharedViewModel for user data updates
        viewModel.username.observe(viewLifecycleOwner, { username ->
            usernameTextView.text = username
        })

        viewModel.userImageUri.observe(viewLifecycleOwner, { uri ->
            if (uri != null) {
                Glide.with(requireContext())
                    .load(uri)
                    .into(userImageView)
            } else {
                userImageView.setImageResource(R.drawable.user)
            }
        })
        val searchView: SearchView = view.findViewById(R.id.search_menu_item1)
        searchView.setOnQueryTextFocusChangeListener { _, hasFocus ->
            setSearchViewVisibility(hasFocus)
        }
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                newText?.let {
                    productAdapter.filter.filter(it)
                }
                return false
            }
        })

        val categoryRecyclerView: RecyclerView = view.findViewById(R.id.categoryRecyclerView)
        categoryAdapter = CategoryAdapter { category -> onCategoryClicked(category) }
        categoryRecyclerView.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        categoryRecyclerView.adapter = categoryAdapter

        val productRecyclerView: RecyclerView = view.findViewById(R.id.resysclerCategoriesProducts)
        productAdapter = ProductAdapter(emptyList(), requireContext(), this)
        productRecyclerView.layoutManager = GridLayoutManager(context, 2)
        productRecyclerView.adapter = productAdapter

        viewModel.categories.observe(viewLifecycleOwner, Observer { categories ->
            categoryAdapter.submitList(categories)
            onCategoryClicked(categories.first { it.id == 0 })  // Select default category
        })

        viewModel.allProducts.observe(viewLifecycleOwner, Observer { products ->
            productAdapter.submitList(products)
        })

        viewModel.cartItems.observe(viewLifecycleOwner, Observer { cartItems ->
            updateProductCartStatus(cartItems)
        })
        viewModel.favoriteItems.observe(viewLifecycleOwner, Observer { favoriteItems ->
            updateProductFavoriteStatus(favoriteItems)
        })
    }
    override fun onResume() {
        super.onResume()
        viewModel.filterProductsByCategoryId(0)  // Ensure default category is selected
    }
    private fun onCategoryClicked(category: CategoryModel) {
        viewModel.filterProductsByCategoryId(category.id)
    }

    private fun setSearchViewVisibility(hasFocus: Boolean) {
        val categoryRecyclerView: RecyclerView? = view?.findViewById(R.id.categoryRecyclerView)
        val nameCategory: LinearLayout? = view?.findViewById(R.id.nameCategory)

        categoryRecyclerView?.visibility = if (hasFocus) View.GONE else View.VISIBLE
        nameCategory?.visibility = if (hasFocus) View.GONE else View.VISIBLE
    }

    override fun onAddToCartClicked(product: ProductModel) {
        viewModel.addToCart(product)
    }

    override fun onRemoveFromCartClicked(product: ProductModel) {
        viewModel.removeFromCart(product)
    }

    override fun onAddToFavoriteClicked(product: ProductModel) {
        viewModel.addToFavorite(product)
    }

    override fun onRemoveFromFavoriteClicked(product: ProductModel) {
        viewModel.removeFromFavorite(product)
    }

    private fun updateProductCartStatus(cartItems: List<ProductModel>) {
        val allProducts = viewModel.allProducts.value ?: return
        allProducts.forEach { product ->
            product.isInCart = cartItems.any { it.id == product.id }
        }
        productAdapter.submitList(allProducts)
    }
    private fun updateProductFavoriteStatus(favoriteItems: List<ProductModel>) {
        val allProducts = viewModel.allProducts.value ?: return
        allProducts.forEach { product ->
            product.isFavorite = favoriteItems.any { it.id == product.id }
        }
        productAdapter.submitList(allProducts)
    }

}
