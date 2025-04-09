package com.example.salamtech_gp

//Used for sharing retrieved data from firebase along with UserViewModel.kt
data class UserProfile(
    val fullName: String = "",
    val email: String = ""
)