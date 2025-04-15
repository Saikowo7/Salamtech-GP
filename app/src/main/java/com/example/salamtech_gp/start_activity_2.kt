package com.example.salamtech_gp

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.text.method.PasswordTransformationMethod
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
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
    private lateinit var pageTracker: TextView

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_2, container, false)

        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isSetupDone = sharedPref.getBoolean("isSetupComplete", false)
        val goBackButton = view.findViewById<ImageView>(R.id.back_arrow)
        var isPasswordVisible = false
        var isConfirmPasswordVisible = false
        val eyeIcon = view.findViewById<ImageView>(R.id.eye_icon)
        val eyeIcon2 = view.findViewById<ImageView>(R.id.eye_icon2)

        // Firebase
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        // Fields
        fullNameField = view.findViewById(R.id.full_name_input)
        emailField = view.findViewById(R.id.email_input)
        passwordField = view.findViewById(R.id.password_input)
        confirmPasswordField = view.findViewById(R.id.confirm_password_input)
        createAccountButton = view.findViewById(R.id.button_next)

        //show/hide password
        passwordField.transformationMethod = PasswordTransformationMethod.getInstance() // hide password
        eyeIcon.setImageResource(R.drawable.eye_svg_icon)

        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                passwordField.transformationMethod = null // show password
                eyeIcon.setImageResource(R.drawable.eye_off_svg_icon) // optional: change icon
            } else {
                passwordField.transformationMethod = PasswordTransformationMethod.getInstance() // hide password
                eyeIcon.setImageResource(R.drawable.eye_svg_icon)
            }

            // Move cursor to the end of text
            passwordField.setSelection(passwordField.text?.length ?: 0)
        }
        //show/hide confirm password
        confirmPasswordField.transformationMethod = PasswordTransformationMethod.getInstance() // hide password
        eyeIcon2.setImageResource(R.drawable.eye_svg_icon)

        eyeIcon2.setOnClickListener {
            isConfirmPasswordVisible = !isConfirmPasswordVisible

            if (isConfirmPasswordVisible) {
                confirmPasswordField.transformationMethod = null // show password
                eyeIcon2.setImageResource(R.drawable.eye_off_svg_icon) // optional: change icon
            } else {
                confirmPasswordField.transformationMethod = PasswordTransformationMethod.getInstance() // hide password
                eyeIcon2.setImageResource(R.drawable.eye_svg_icon)
            }

            // Move cursor to the end of text
            confirmPasswordField.setSelection(confirmPasswordField.text?.length ?: 0)
        }

        pageTracker = view.findViewById(R.id.pageTracker)

        goBackButton.setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_1())
        }

        val goToLoginText = view.findViewById<TextView>(R.id.haveAccountTextView)

        if (isSetupDone) {
            // Setup completed, Hide some features
            pageTracker.visibility = View.GONE
            goBackButton.visibility = View.GONE
        } else {
            // Setup not completed, Turn on some features
            pageTracker.visibility = View.VISIBLE
            goBackButton.visibility = View.VISIBLE
        }

        goToLoginText.setOnClickListener {
            val currentActivity = activity
            if (currentActivity is SetupPage) {
                currentActivity.loadFragment(start_activity_3())
            } else {
                Log.e("Navigation", "Activity is not SetupPage")
            }
        }



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
        val isSetup = UserViewModel.isSetupDone(requireContext())
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
                        if(isSetup)
                        {
                            val intent = Intent(requireContext(), main_activity::class.java)
                            startActivity(intent)
                            requireActivity().finish()
                        }
                        else{
                            (activity as SetupPage).loadFragment(start_activity_3())
                        }

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