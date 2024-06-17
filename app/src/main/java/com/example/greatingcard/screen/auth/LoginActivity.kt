package com.example.greatingcard.screen.auth
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.greatingcard.MainActivity
import com.example.greatingcard.R
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        auth = FirebaseAuth.getInstance()

        val emailEditText: EditText = findViewById(R.id.email)
        val passwordEditText: EditText = findViewById(R.id.password)
        val loginButton: Button = findViewById(R.id.btnLoginIn)
        val forgotPasswordTextView: TextView = findViewById(R.id.forgotPassword)
        val signUpTextView: TextView = findViewById(R.id.signText)
        val signUpBtn: TextView= findViewById(R.id.signView)
        val loginWithGoogleButton: ImageView = findViewById(R.id.loginWithGoogle)
        val loginWithFacebookButton: ImageView = findViewById(R.id.loginWithFacebook)
        val loginWithGithubButton: ImageView = findViewById(R.id.loginWithGithub)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString()
            val password = passwordEditText.text.toString()
            // Check for empty fields
            if (email.isEmpty()) {
                Toast.makeText(this, "Email is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.isEmpty()) {
                Toast.makeText(this, "Password is required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            login(email, password)
        }

        forgotPasswordTextView.setOnClickListener {
            // Handle forgot password logic here
            val email = emailEditText.text.toString().trim()
            if (email.isEmpty()) {
                Toast.makeText(this, "Please enter your email to reset password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            auth.sendPasswordResetEmail(email)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(this, "Password reset email sent", Toast.LENGTH_SHORT).show()
                    } else {
                        Toast.makeText(this, "Failed to send password reset email", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        signUpTextView.setOnClickListener {
            startActivity(Intent(this, SignActivity::class.java))
        }
        signUpBtn.setOnClickListener {
            startActivity(Intent(this, SignActivity::class.java))
        }

        loginWithGoogleButton.setOnClickListener {
            // Handle login with Google
            loginWithGoogle()
        }

        loginWithFacebookButton.setOnClickListener {
            // Handle login with Facebook
            loginWithFacebook()
        }

        loginWithGithubButton.setOnClickListener {
            // Handle login with GitHub
            loginWithGithub()
        }
    }

    private fun login(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, MainActivity::class.java))
                    finish()
                } else {
                    Toast.makeText(this, "Login failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                }
            }
    }

    private fun loginWithGoogle() {
        // Implement Google login logic here
        Toast.makeText(this, "Google login not implemented", Toast.LENGTH_SHORT).show()
    }

    private fun loginWithFacebook() {
        // Implement Facebook login logic here
        Toast.makeText(this, "Facebook login not implemented", Toast.LENGTH_SHORT).show()
    }

    private fun loginWithGithub() {
        // Implement GitHub login logic here
        Toast.makeText(this, "GitHub login not implemented", Toast.LENGTH_SHORT).show()
    }
}