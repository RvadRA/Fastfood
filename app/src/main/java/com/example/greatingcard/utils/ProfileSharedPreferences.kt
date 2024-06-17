package com.example.greatingcard.utils

import android.content.Context

class ProfileSharedPreferences(context: Context) {
    private val sharedPreferences = context.getSharedPreferences("ProfileSharedPreferences", Context.MODE_PRIVATE)

    var userImage: String
        get() = sharedPreferences.getString("image", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("image", value).apply()
        }

    var userName: String
        get() = sharedPreferences.getString("name", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("name", value).apply()
        }

    var userEmail: String
        get() = sharedPreferences.getString("email", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("email", value).apply()
        }

    var userPassword: String
        get() = sharedPreferences.getString("password", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("password", value).apply()
        }

    var userPhone: String
        get() = sharedPreferences.getString("phone", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("phone", value).apply()
        }

    var userAddress: String
        get() = sharedPreferences.getString("address", "") ?: ""
        set(value) {
            sharedPreferences.edit().putString("address", value).apply()
        }

    fun saveUserProfile(image: String, name: String, email: String, password: String, phone: String, address: String) {
        userImage = image
        userName = name
        userEmail = email
        userPassword = password
        userPhone = phone
        userAddress = address
    }
}
