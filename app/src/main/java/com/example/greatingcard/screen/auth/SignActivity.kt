package com.example.greatingcard.screen.auth

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.greatingcard.MainActivity
import com.example.greatingcard.R
import com.google.android.gms.auth.api.Auth
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.auth.api.signin.GoogleSignInResult
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import de.hdodenhof.circleimageview.CircleImageView

class SignActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private val RC_SIGN_IN = 9001 // Request code for Google Sign-In

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign)

        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()

        // Configure Google Sign-In
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        val loginBtn = findViewById<TextView>(R.id.loginView)
        val usernameEditText = findViewById<EditText>(R.id.username)
        val emailEditText = findViewById<EditText>(R.id.email)
        val passwordEditText = findViewById<EditText>(R.id.password)
        val confirmPasswordEditText = findViewById<EditText>(R.id.confirmPassword)
        val btnSignUp = findViewById<Button>(R.id.btnSignUp)

        // Social login buttons
        val loginWithGoogle = findViewById<CircleImageView>(R.id.loginWithGoogle)
        val loginWithFacebook = findViewById<CircleImageView>(R.id.loginWithFacebook)
        val loginWithGithub = findViewById<CircleImageView>(R.id.loginWithGithub)

        loginBtn.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        btnSignUp.setOnClickListener {
            val username = usernameEditText.text.toString()
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            val confirmPassword = confirmPasswordEditText.text.toString()

            // Check each field one by one and provide feedback
            if (username.isEmpty()) {
                Toast.makeText(this, "Username is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (email.isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (confirmPassword.isEmpty()) {
                Toast.makeText(this, "Confirm Password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password != confirmPassword) {
                Toast.makeText(this, "Passwords do not match", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userMap = hashMapOf(
                            "username" to username,
                            "email" to email,
                            "password" to password,
                            "phone" to "",
                            "address" to "",
                            "imageUrl" to ""
                        )

                        if (user != null) {
                            firestore.collection("users").document(user.uid).set(userMap)
                        }

                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(baseContext, "Authentication failed.", Toast.LENGTH_SHORT).show()
                    }
                }
        }
        // Google Sign-In button click listener
        loginWithGoogle.setOnClickListener {
            val signInIntent = mGoogleSignInClient.signInIntent
            startActivityForResult(signInIntent, RC_SIGN_IN)
        }

        // GitHub Sign-In button click listener
        loginWithGithub.setOnClickListener {
            // Implement GitHub Sign-In logic here
            // This could involve opening a WebView to GitHub OAuth page
            // or using a library like AppAuth for OAuth flow
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Handle Google Sign-In result
        if (requestCode == RC_SIGN_IN) {
            val task = data?.let { Auth.GoogleSignInApi.getSignInResultFromIntent(it) }
            handleGoogleSignInResult(task)
        }
    }

    private fun handleGoogleSignInResult(completedTask: GoogleSignInResult?) {
        try {
            val account = completedTask?.signInAccount
            // Signed in successfully, show authenticated UI.
            val googleIdToken = account?.idToken // You can use this token for Firebase authentication

            // Start MainActivity after successful sign-in
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        } catch (e: ApiException) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w(TAG, "signInResult:failed code=" + e.statusCode)
            Toast.makeText(baseContext, "Google Sign-In failed.", Toast.LENGTH_SHORT).show()
        }
    }



}
