package com.example.salamtech_gp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class start_activity_2 : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference

    private lateinit var fullNameField: TextInputEditText
    private lateinit var emailField: TextInputEditText
    private lateinit var passwordField: TextInputEditText
    private lateinit var confirmPasswordField: TextInputEditText
    private lateinit var createAccountButton: Button

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_2, container, false)

        view.findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_1())
        }

        // Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Fields
        fullNameField = view.findViewById(R.id.full_name_input)
        emailField = view.findViewById(R.id.email_input)
        passwordField = view.findViewById(R.id.password_input)
        confirmPasswordField = view.findViewById(R.id.confirm_password_input)
        createAccountButton = view.findViewById(R.id.button_next)

        createAccountButton.setOnClickListener {
            val fullName = fullNameField.text.toString().trim()
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString()
            val confirmPassword = confirmPasswordField.text.toString()

            if (fullName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                showToast("Please fill in all fields")
            } else if (password != confirmPassword) {
                showToast("Passwords do not match")
            } else if (password.length < 6) {
                showToast("Password must be at least 6 characters")
            } else {
                createAccount(email, password, fullName)
            }
        }

        return view
    }

    private fun createAccount(email: String, password: String, fullName: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnSuccessListener { result ->
                val uid = result.user?.uid ?: return@addOnSuccessListener
                val userMap = mapOf(
                    "fullName" to fullName,
                    "email" to email
                )

                database.child("users").child(uid).child("profile")
                    .setValue(userMap)
                    .addOnSuccessListener {
                        showToast("Account created successfully!")
                        (activity as SetupPage).loadFragment(start_activity_3())
                    }
                    .addOnFailureListener {
                        showToast("Account created, but failed to save user data.")
                    }
            }
            .addOnFailureListener {
                showToast("Error: ${it.message}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(requireContext(), message, Toast.LENGTH_SHORT).show()
    }

}