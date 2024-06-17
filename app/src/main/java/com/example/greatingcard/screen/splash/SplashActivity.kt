package com.example.greatingcard.screen.splash

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.widget.ImageView
import com.example.greatingcard.MainActivity
import com.example.greatingcard.R
import com.example.greatingcard.screen.auth.LoginActivity
import com.example.greatingcard.databinding.ActivitySplashBinding
import com.google.firebase.auth.FirebaseAuth

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val user = FirebaseAuth.getInstance().currentUser
        val intent = if (user != null) {
            Intent(this, MainActivity::class.java)
        } else {
            Intent(this, LoginActivity::class.java)
        }

        findViewById<ImageView>(R.id.imageViewLogo).postDelayed({
            startActivity(intent)
            finish()
        }, 2000)
    }
}
