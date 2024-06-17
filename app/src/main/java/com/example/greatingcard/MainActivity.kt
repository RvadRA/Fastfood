package com.example.greatingcard

import android.net.Uri
import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.example.greatingcard.databinding.ActivityMainBinding
import com.example.greatingcard.model.MainViewModel
import com.example.greatingcard.utils.ProfileSharedPreferences
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.firestore.FirebaseFirestore

class MainActivity : AppCompatActivity(){
    lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var profileSharedPreferences: ProfileSharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        profileSharedPreferences = ProfileSharedPreferences(this)

        // Load user profile data
        loadUserProfile()

        // Load Google user profile data if user signed in with Google
        if (auth.currentUser?.providerData?.any { it.providerId == GoogleAuthProvider.PROVIDER_ID } == true) {
            loadGoogleUserProfile()
        }
        val bottomNavView= findViewById<BottomNavigationView>(R.id.BottonNovigationView)
        val navigationView=findNavController(R.id.flContainer)

        bottomNavView.setupWithNavController(navigationView)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel.saveCartItems()
    }


    private fun loadUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            firestore.collection("users").document(currentUser.uid).get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val userData = document.data
                    if (userData != null) {
                        val username = userData["username"] as? String ?: ""
                        val imageUrl = userData["imageUrl"] as? String
                        viewModel.setUsername(username)
                        if (!imageUrl.isNullOrEmpty()) {
                            val uri = Uri.parse(imageUrl)
                            viewModel.setUserImageUri(uri)
                        }
                    }
                }
            }
        }
    }

    private fun loadGoogleUserProfile() {
        val currentUser = auth.currentUser
        if (currentUser != null) {
            val username = currentUser.displayName ?: ""
            val email = currentUser.email ?: ""
            val imageUrl = currentUser.photoUrl?.toString() ?: ""
            viewModel.setUsername(username)
            viewModel.setUserEmail(email)
            viewModel.setUserImageUri(Uri.parse(imageUrl))
        }
    }
}
