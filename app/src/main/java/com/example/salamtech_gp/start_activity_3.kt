package com.example.salamtech_gp

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth

class start_activity_3 : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_3, container, false)

        view.findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_2())
        }

        auth = FirebaseAuth.getInstance()

        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.button_next)

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginUser(email, password)
        }

        return view
    }

    private fun loginUser(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.e("LoginDebug", "PreLogIN LogE")
                Log.d("LoginDebug", "PreLogIN LogD")
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                val currentActivity = activity
                if (currentActivity is SetupPage) {
                    Log.d("LoginDebug", "Current avtivity is setup page!!")
                    currentActivity.loadFragment(start_activity_4())
                } else {
                    Log.e("LoginDebug", "Activity is not SetupPagelogD")
                    Log.e("LoginDebug", "Activity is not SetupPage")
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Login failed: ${it.message}", Toast.LENGTH_LONG).show()
                Log.e("LoginDebug", "Login failed", it)
            }
    }
}