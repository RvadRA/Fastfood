package com.example.greatingcard.model

import android.app.Application
import android.net.Uri
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.example.greatingcard.R
import com.example.greatingcard.utils.CartSharedPreferences
import com.example.greatingcard.utils.FavoriteSharedPreferences

class MainViewModel(application: Application)  : AndroidViewModel(application) {

    private val _allProducts = MutableLiveData<List<ProductModel>>()
    val allProducts: LiveData<List<ProductModel>> get() = _allProducts

    private val _categories = MutableLiveData<List<CategoryModel>>()
    val categories: LiveData<List<CategoryModel>> get() = _categories

    private val _cartItems = MutableLiveData<MutableList<ProductModel>>()
    val cartItems: LiveData<MutableList<ProductModel>> get() = _cartItems

    private val _favoriteItems = MutableLiveData<MutableList<ProductModel>>()
    val favoriteItems: LiveData<MutableList<ProductModel>> get() = _favoriteItems

    private val cartSharedPreferences = CartSharedPreferences(application)
    private val favoriteSharedPreferences = FavoriteSharedPreferences(application)

    private val _username = MutableLiveData<String>()
    val username: LiveData<String> get() = _username

    private val _userImageUri = MutableLiveData<Uri>()
    val userImageUri: LiveData<Uri> get() = _userImageUri

    private val _userEmail = MutableLiveData<String>()
    val userEmail: LiveData<String> get() = _userEmail




    private val initialListOfProducts = listOf(
        ProductModel(1, R.drawable.chickenfoot, "Chicken Foods", "Cluck 'n' Chow: Succulent Chicken Delights Await Your Taste Buds!", "400 ₽"),
        ProductModel(2, R.drawable.uzbekpilafsilde, "Pilaf", "Pilaf Paradise: Indulge in the Exotic Flavors of the East, Infused with Fragrant Spices", "350 ₽"),
        ProductModel(3, R.drawable.uzbeksambusasaladwithsalad, "Sambu with Salad", "Samba Sensation: Dive into Brazilian Inspired Delights, Complete with a Fresh Salad Twist", "500 ₽"),
        ProductModel(4, R.drawable.cappuccino, "Cappuccino", "Cappuccino Craze: Treat Yourself to a Daily Dose of Rich, Frothy Java Joy and Whipped Cream Bliss", "250 ₽"),
        ProductModel(5, R.drawable.friesfrench, "French Fries", "French Fry Fiesta: Crispy Delights Straight from the Fryer to Your Plate, Served with a Dash of Seasoned Salt", "200 ₽"),
        ProductModel(6, R.drawable.pizzauzbek, "Uzbek Pizza", "Uzbek Pizza Perfection: Experience a Slice of Central Asian Flavor Explosion in Every Bite, Freshly Baked with Love", "450 ₽"),
        ProductModel(7, R.drawable.steake, "Grilled Barbecue Ribs with Coleslaw and Green Salad", "Ingredients:\n" +
                "- Grilled Barbecue Ribs:\n" +
                "- Pork ribs\n" +
                "- Barbecue sauce (tomato paste, vinegar, brown sugar, honey, mustard, Worcestershire sauce, garlic, onion, spices)\n" +
                "- Salt and pepper\n" +
                "- Coleslaw:\n" +
                "- Cabbage (green and red)\n" +
                "- Carrots\n" +
                "- Mayonnaise\n" +
                "- Apple cider vinegar\n" +
                "- Sugar\n" +
                "- Salt and pepper\n" +
                "- Green Salad:\n" +
                "- Mixed greens (lettuce, arugula, spinach)\n" +
                "- Cucumber\n" +
                "- Avocado\n" +
                "- Olive oil\n" +
                "- Lemon juice\n" +
                "- Salt and pepper", "700 ₽"),
        ProductModel(8, R.drawable.sushi, "Assorted Sushi Platter with Green Tea", "Ingredients:\n" +
                "- Assorted Sushi Platter:\n" +
                "- Sushi rice (vinegared rice)\n" +
                "- Nori (seaweed)\n" +
                "- Fresh salmon\n" +
                "- Avocado\n" +
                "- Cucumber\n" +
                "- Eel (unagi)\n" +
                "- Fish roe\n" +
                "- Pickled ginger\n" +
                "- Wasabi\n" +
                "- Soy sauce\n" +
                "- Green Tea:\n" +
                "- Green tea leaves\n" +
                "- Hot water", "1200 ₽"),
        ProductModel(9, R.drawable.cocacola, "Coca-Cola Classic Can", "Coca-Cola in Classic Can\n"+
            "- 330ml", "450 ₽"),

        ProductModel(10, R.drawable.pizzalarge, "Pepperoni Pizza", "Ingredients:\n" +
                "- Pizza dough\n" +
                "- Tomato sauce\n" +
                "- Mozzarella cheese\n" +
                "- Pepperoni slices\n" +
                "- Fresh basil or parsley (garnish)", "750 ₽"),
        ProductModel(11, R.drawable.combo, "Fried Chicken Sandwiches", "Ingredients:\n" +
                "- Burger buns (sesame seed buns)\n" +
                "- Fried chicken fillets\n" +
                "- Lettuce\n" +
                "- Mayonnaise or special sauce\n" +
                "- Pickles (optional)Potatoes\n" +
                "- Salt\n" +
                "- Vegetable oil (for frying)", "250 ₽"),
        ProductModel(12, R.drawable.forbreakfast, "Vegetable Sandwiches", "Ingredients:\n" +
                "- Baguette or whole grain bread\n" +
                "- Lettuce\n" +
                "- Tomato slices\n" +
                "- Cucumber slices\n" +
                "- Cheese (such as mozzarella or provolone)\n" +
                "- Fresh basil or spinach leaves\n" +
                "- Mayonnaise or hummus (optional)\n" +
                "- Green Smoothies\n" +
                    "   Ingredients:\n" +
                "- Spinach or kale\n" +
                "- Green apple\n" +
                "- Cucumber\n" +
                "- Lemon juice\n" +
                "- Ginger\n" +
                "- Water or coconut water", "200 ₽"),
        ProductModel(13, R.drawable.hotdog2, "Hot Dog Meal", "Ingredients:\n" +
                "- Hot dog sausage\n" +
                "- Hot dog bun\n" +
                "- Lettuce\n" +
                "- Ketchup\n" +
                "- Mustard\n" +
                "- Mayonnaise\n" +
                "- Garlic bread or mini calzones\n" +
                "- Two glasses of cola", "450 ₽"),

        ProductModel(14, R.drawable.pizzawithcheese, "Pizza and Pasta Meal", "Ingredients:\n" +
                "- Pizza dough\n" +
                "- Tomato sauce\n" +
                "- Mozzarella cheese\n" +
                "- Pepperoni slices\n" +
                "- Cherry tomatoes\n" +
                "- Sliced cucumber\n" +
                "- Fresh basil\n" +
                "   Pasta:\n" +
                "- Spaghetti\n" +
                "- Olives\n" +
                "- Olive oil or light sauce", "450 ₽"),

        ProductModel(14, R.drawable.samsawithsous, "Samosas", "Ingredients:\n" +
                "- Dough: Flour, water, salt, oil\n" +
                "- Filling: Potatoes, peas, spices (cumin, coriander, garam masala), onions, garlic, ginger, green chilies", "470 ₽"),
        ProductModel(15, R.drawable.shavurmamedium, "Chicken Shawarma", "Ingredients:\n" +
                "- Wrap: Flatbread or pita\n" +
                "- Filling: Grilled chicken, lettuce, tomatoes, cucumbers, onions, sauce (tahini or garlic sauce)", "300 ₽"),
        ProductModel(16, R.drawable.showrmacombo, "Lamb Shawarma", "Ingredients:\n" +
                "- Wrap: Flatbread or pita\n" +
                "- Filling: Grilled lamb, lettuce, tomatoes, cucumbers, onions, sauce (tahini or garlic sauce)", "300 ₽"),

    )

    private val initialListOfCategories = listOf(
        CategoryModel(0, "All Food", R.drawable.combsilde, initialListOfProducts),
        CategoryModel(1, "Chicken", R.drawable.combochickenburger, initialListOfProducts.filter { it.id == 1 }),
        CategoryModel(2, "Pilaf", R.drawable.uzbeksoupsilde, initialListOfProducts.filter { it.id == 2 }),
        CategoryModel(3, "Desserts", R.drawable.meringue, initialListOfProducts.filter { it.id == 3 }),
        CategoryModel(4, "Drinks", R.drawable.cappuccino, initialListOfProducts.filter { it.id == 4 }),
        CategoryModel(5, "Snacks", R.drawable.friesfrench, initialListOfProducts.filter { it.id == 5 }),
        CategoryModel(6, "Pizza", R.drawable.pizzauzbek, initialListOfProducts.filter { it.id == 6 })
    )

    init {
        _categories.value = initialListOfCategories
        filterProductsByCategoryId(0)  // Set default category products initially
        _cartItems.value = cartSharedPreferences.getCartItems().toMutableList()
        _favoriteItems.value = favoriteSharedPreferences.getFavoriteItems().toMutableList()
    }
    fun clearCart() {
        _cartItems.value?.clear()
        _cartItems.value = _cartItems.value // This will notify observers
        saveCartItems()
    }

    fun setUsername(username: String) {
        _username.value = username
    }

    fun setUserImageUri(uri: Uri) {
        _userImageUri.value = uri
    }

    fun setUserEmail(email: String) {
        _userEmail.value = email
    }

    fun filterProductsByCategoryId(categoryId: Int) {
        val filteredProducts = initialListOfCategories.find { it.id == categoryId }?.products ?: emptyList()
        _allProducts.value = filteredProducts
    }

    fun addToCart(product: ProductModel) {
        val updatedCartItems = _cartItems.value ?: mutableListOf()
        val existingItem = updatedCartItems.find { it.id == product.id }

        if (existingItem != null) {
            existingItem.quantity = 1 // Reset quantity to 1
        } else {
            product.isInCart = true
            updatedCartItems.add(product)
        }

        _cartItems.value = updatedCartItems
        saveCartItems()
        updateAllProducts(product)

    }

    fun removeFromCart(product: ProductModel) {
        val updatedCartItems = _cartItems.value ?: return
        updatedCartItems.removeAll { it.id == product.id }
        product.isInCart = false // Ensure the state is updated
        _cartItems.value = updatedCartItems
        saveCartItems()
        updateAllProducts(product)
    }

    fun updateCartItemQuantity(product: ProductModel, quantity: Int) {
        val updatedCartItems = _cartItems.value ?: return
        val existingItem = updatedCartItems.find { it.id == product.id }

        existingItem?.quantity = quantity

        _cartItems.value = updatedCartItems
        saveCartItems()
        updateAllProducts(product)
    }

    private fun updateAllProducts(updatedProduct: ProductModel) {
        _allProducts.value = _allProducts.value?.map {
            if (it.id == updatedProduct.id) {
                it.copy(isInCart = updatedProduct.isInCart)
            } else {
                it
            }
        }
        _favoriteItems.value = _favoriteItems.value?.map {
            if (it.id == updatedProduct.id) {
                it.copy(isInCart = updatedProduct.isInCart)
            } else {
                it
            }
        }?.toMutableList()
    }
    fun addToFavorite(product: ProductModel) {
        val updatedFavoriteItems = _favoriteItems.value ?: mutableListOf()
        val existingItem = updatedFavoriteItems.find { it.id == product.id }

        if (existingItem == null) {
            product.isFavorite = true
            updatedFavoriteItems.add(product)
        }

        _favoriteItems.value = updatedFavoriteItems
        saveFavoriteItems()
        updateAllProductsFavorite(product)
    }

    fun removeFromFavorite(product: ProductModel) {
        val updatedFavoriteItems = _favoriteItems.value ?: return
        updatedFavoriteItems.removeAll { it.id == product.id }
        product.isFavorite = false // Ensure the state is updated
        _favoriteItems.value = updatedFavoriteItems
        saveFavoriteItems()
        updateAllProductsFavorite(product)
    }





    private fun updateAllProductsFavorite(updatedProduct: ProductModel) {
        _allProducts.value = _allProducts.value?.map {
            if (it.id == updatedProduct.id) {
                it.copy(isFavorite = updatedProduct.isFavorite)
            } else {
                it
            }
        }
        _favoriteItems.value = _favoriteItems.value?.map {
            if (it.id == updatedProduct.id) {
                it.copy(isFavorite = updatedProduct.isFavorite)
            } else {
                it
            }
        }?.toMutableList()
    }
    fun saveCartItems() {
        cartSharedPreferences.saveCartItems(_cartItems.value ?: emptyList())
    }
    private fun saveFavoriteItems() {
        favoriteSharedPreferences.saveFavoriteItems(_favoriteItems.value ?: emptyList())
    }
}