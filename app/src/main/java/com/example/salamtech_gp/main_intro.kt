package com.example.salamtech_gp

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.animation.AnimationUtils
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase


//Main class where programs run this first

class main_intro : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main_intro)

        // Write a test value to Firebase
        Log.d("FirebaseDebug", "Going in!!")
        val db = FirebaseDatabase.getInstance().reference
        Log.d("FirebaseTest", "Firebase App name: ${FirebaseApp.getInstance().name}")
        db.child("test").setValue("Hello Firebaseasd!")
            .addOnSuccessListener {
                Log.d("FirebaseTest", "Write successful!")
            }
            .addOnFailureListener {
                Log.e("FirebaseTest", "Write failed: ${it.message}")
            }

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
