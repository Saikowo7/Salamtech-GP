package com.example.salamtech_gp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class main_activity : AppCompatActivity() {

    private lateinit var userViewModel: UserViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_activity)

        val bottomNavigationView = findViewById<BottomNavigationView>(R.id.bottom_navigation)

        //Shared data
        userViewModel = ViewModelProvider(this)[UserViewModel::class.java]
        fetchUserData()

        // Set default fragment (Home)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.main, home_page_fragment())
                .commit()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    loadFragment(home_page_fragment())
                    true
                }
                R.id.nav_browse -> {
                    loadFragment(browse_page_fragment())
                    true
                }
                R.id.nav_settings -> {
                    loadFragment(setting_page_fragment())
                    true
                }
                else -> false
            }
        }
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.main, fragment)
            .commit()
    }

    private fun fetchUserData() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().reference
        Log.d("MainActivity", "Before loading")
        dbRef.child("users").child(uid).child("profile")
            .get()
            .addOnSuccessListener { snapshot ->
                val user = snapshot.getValue(UserProfile::class.java)
                user?.let {
                    userViewModel.setUserProfile(it)
                    Log.d("MainActivity", "User profile loaded: ${it.fullName}")
                }
            }
            .addOnFailureListener {
                Log.e("MainActivity", "Failed to load user profile: ${it.message}")
            }
    }
}