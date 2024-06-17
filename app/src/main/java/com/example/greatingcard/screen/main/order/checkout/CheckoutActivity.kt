package com.example.greatingcard.screen.main.order.checkout

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.viewModels

import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.greatingcard.R
import com.example.greatingcard.databinding.ActivityCheckoutBinding
import com.example.greatingcard.model.MainViewModel
import com.google.android.libraries.places.api.Places
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class CheckoutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCheckoutBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val viewModel: MainViewModel by viewModels()

    companion object {
        const val MAP_REQUEST_CODE = 2
    }
    private var selectedPaymentMethod: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialize the Places API
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, getString(R.string.google_maps_api_key))
        }

        // Retrieve the total price from the Intent
        val totalPrice = intent.getStringExtra("TOTAL_PRICE") ?: "0"

        // Update the Total TextView
        binding.totalPriceTextView.text = "$totalPrice ₽"


        val backOneStepImageView = findViewById<ImageView>(R.id.backOneStep)
        // Set OnClickListener
        backOneStepImageView.setOnClickListener {
            finish() // This will perform the default "back" action
        }
        binding.btnOrder.setOnClickListener{
            finish()
        }
        binding.lyPaymentCash.setOnClickListener{
            binding.lyPaymentCash.setBackgroundResource(R.drawable.shape_active)
            binding.imgPaymentCash.setColorFilter(ContextCompat.getColor(this,R.color.yellow),android.graphics.PorterDuff.Mode.SRC_IN)
            binding.tvPaymentCash.setTextColor(ContextCompat.getColor(this, R.color.yellow))

            binding.lyPaymentCard.setBackgroundResource(R.drawable.shape_unactive)
            binding.imgPaymentCard.setColorFilter(ContextCompat.getColor(this,R.color.black),android.graphics.PorterDuff.Mode.SRC_IN)
            binding.tvPaymentCard.setTextColor(ContextCompat.getColor(this, R.color.black))
        }

        binding.lyPaymentCard.setOnClickListener{
            binding.lyPaymentCard.setBackgroundResource(R.drawable.shape_active)
            binding.imgPaymentCard.setColorFilter(ContextCompat.getColor(this,R.color.yellow),android.graphics.PorterDuff.Mode.SRC_IN)
            binding.tvPaymentCard.setTextColor(ContextCompat.getColor(this, R.color.yellow))

            binding.lyPaymentCash.setBackgroundResource(R.drawable.shape_unactive)
            binding.imgPaymentCash.setColorFilter(ContextCompat.getColor(this,R.color.black),android.graphics.PorterDuff.Mode.SRC_IN)
            binding.tvPaymentCash.setTextColor(ContextCompat.getColor(this, R.color.black))
        }
        // Set payment method click listeners
        binding.lyPaymentCash.setOnClickListener {
            setPaymentMethodActive(binding.lyPaymentCash, binding.imgPaymentCash, binding.tvPaymentCash)
            setPaymentMethodInactive(binding.lyPaymentCard, binding.imgPaymentCard, binding.tvPaymentCard)
            selectedPaymentMethod = "cash"
        }

        binding.lyPaymentCard.setOnClickListener {
            setPaymentMethodActive(binding.lyPaymentCard, binding.imgPaymentCard, binding.tvPaymentCard)
            setPaymentMethodInactive(binding.lyPaymentCash, binding.imgPaymentCash, binding.tvPaymentCash)
            selectedPaymentMethod = "card"
        }

        binding.imageViewLocation.setOnClickListener {
            openMapActivity()
        }
        binding.textInputEditTextAddress.setOnClickListener {
            openMapActivity()
        }

        binding.btnOrder.setOnClickListener {
            if (validateInputs()) {
              //  navigateToSuccessFragment()
             saveOrderData()
            }
        }

        /*binding.order.setOnClickListener {
            val intent = Intent(this, SuccessPaymentActivity::class.java)
            startActivity(intent)
        }*/







    }


    private fun openMapActivity() {
        val intent = Intent(this, MapActivity::class.java)
        startActivityForResult(intent, MAP_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == MAP_REQUEST_CODE && resultCode == RESULT_OK) {
            val address = data?.getStringExtra("selected_address")
            binding.textInputEditTextAddress.setText(address)
        }
    }


    private fun saveOrderData() {
        val address = binding.textInputEditTextAddress.text.toString().trim()
        val entrance = binding.editTextEntrance.text.toString().trim()
        val floor = binding.editTextFloor.text.toString().trim()
        val house = binding.editTextHouse.text.toString().trim()
        val totalPrice = binding.totalPriceTextView.text.toString()
        val phoneNumber = binding.textInputEditTextPhoneNumber.text.toString().trim()

        // Get the current user's ID from Firebase Authentication
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val orderData = hashMapOf(
                "address" to address,
                "entrance" to entrance,
                "floor" to floor,
                "house" to house,
                "phoneNumber" to phoneNumber,
                "totalPrice" to totalPrice,
                "paymentMethod" to selectedPaymentMethod,
                // Add more order details if needed
            )

            val userDocRef = firestore.collection("users").document(userId)
            val userOrdersCollection = userDocRef.collection("orders")

            // Save the order
            userOrdersCollection.add(orderData)
                .addOnSuccessListener { documentReference ->
                    // Get the order number assigned by Firestore
                    val orderId = documentReference.id

                    // Save each item in the cart with its quantity under the order number
                    viewModel.cartItems.value?.forEach { product ->
                        val itemData = hashMapOf(
                            "productId" to product.id,
                            "productName" to product.name,
                            "quantity" to product.quantity,
                            "price" to product.price
                        )
                        userOrdersCollection.document(orderId).collection("items").add(itemData)
                    }

                    // Clear the cart after saving the order
                    viewModel.clearCart()
                    navigateToSuccessFragment()

                    // Check and update user profile if necessary
                    userDocRef.get().addOnSuccessListener { documentSnapshot ->
                        if (documentSnapshot != null) {
                            val userProfileData = documentSnapshot.data
                            val currentAddress = userProfileData?.get("address") as? String
                            val currentPhoneNumber = userProfileData?.get("phone") as? String

                            val updates = hashMapOf<String, Any>()
                            if (currentAddress.isNullOrEmpty()) {
                                updates["address"] = address
                            }
                            if (currentPhoneNumber.isNullOrEmpty()) {
                                updates["phone"] = phoneNumber
                            }

                            if (updates.isNotEmpty()) {
                                userDocRef.update(updates).addOnSuccessListener {
                                    // Profile updated successfully
                                }.addOnFailureListener { e ->
                                    Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                                }
                            }
                        }
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save order: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }


    private fun navigateToSuccessFragment() {
        val intent = Intent(this, SuccessPaymentActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun setPaymentMethodActive(layout: LinearLayout, imageView: ImageView, textView: TextView) {
        layout.setBackgroundResource(R.drawable.shape_active)
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.yellow), android.graphics.PorterDuff.Mode.SRC_IN)
        textView.setTextColor(ContextCompat.getColor(this, R.color.yellow))
    }

    private fun setPaymentMethodInactive(layout: LinearLayout, imageView: ImageView, textView: TextView) {
        layout.setBackgroundResource(R.drawable.shape_unactive)
        imageView.setColorFilter(ContextCompat.getColor(this, R.color.black), android.graphics.PorterDuff.Mode.SRC_IN)
        textView.setTextColor(ContextCompat.getColor(this, R.color.black))
    }
    private fun validateInputs(): Boolean {
        val address = binding.textInputEditTextAddress.text.toString().trim()
        val entrance = binding.editTextEntrance.text.toString().trim()
        val floor = binding.editTextFloor.text.toString().trim()
        val house = binding.editTextHouse.text.toString().trim()
        val phoneNumber =binding.textInputEditTextPhoneNumber.text.toString().trim()

        if (address.isEmpty()) {
            binding.textInputEditTextAddress.error = "Address is required"
            return false
        }
        if (entrance.isEmpty()) {
            binding.editTextEntrance.error = "Entrance is required"
            return false
        }
        if (floor.isEmpty()) {
            binding.editTextFloor.error = "Floor is required"
            return false
        }
        if (house.isEmpty()) {
            binding.editTextHouse.error = "House Number is required"
            return false
        }
        if (phoneNumber.isEmpty()) {
            binding.textInputEditTextPhoneNumber.error = "Phone Number is required"
            return false
        }
        if (!isValidRussianPhoneNumber(phoneNumber)) {
            binding.textInputEditTextPhoneNumber.error = "Invalid Russian phone number"
            return false
        }
        if (selectedPaymentMethod == null) {
            // Show some error message
            Toast.makeText(this, "Please select a payment method", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun isValidRussianPhoneNumber(phoneNumber: String): Boolean {
        // Regex to validate Russian phone numbers
        val regex = "^\\+7\\d{10}$|^8\\d{10}$".toRegex()
        return regex.matches(phoneNumber)
    }


    }
