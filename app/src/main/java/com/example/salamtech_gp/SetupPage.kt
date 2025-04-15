package com.example.salamtech_gp

import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment

class SetupPage : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_setup_page)

        val sharedPref = getSharedPreferences("AppPrefs", MODE_PRIVATE)
        val isSetupDone = sharedPref.getBoolean("isSetupComplete", false)
        Log.d("isSetupComplete", "Setup is"+isSetupDone)

        if (isSetupDone) {
            // Setup completed, send to Login
            loadFragment(start_activity_3())
        }

        // Load the first step when activity starts
        if (savedInstanceState == null && !isSetupDone) {
            loadFragment(StartActivity_0())
        }
    }

    // Function to replace the current fragment with a new one
    fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .addToBackStack(null)  // Enables back navigation
            .commit()


    }


}