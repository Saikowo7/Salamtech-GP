package com.example.salamtech_gp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class main_intro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_intro)

        this.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT)

        val rotatingView = findViewById<ImageView>(R.id.logoImage) // Replace with your view ID

        // Load the animation from XML
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.reload_rotate)

        // Start the animation
        rotatingView.startAnimation(rotateAnimation)

        android.os.Handler().postDelayed({
            val intent = Intent(this, SetupPage::class.java)
            startActivity(intent)
            finish()
        }, 3000)
        }

    }
