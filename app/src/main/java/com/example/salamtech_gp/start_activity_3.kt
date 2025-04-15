package com.example.salamtech_gp

import android.content.Context
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

class start_activity_3 : Fragment() {

    private lateinit var auth: FirebaseAuth
    private lateinit var emailEditText: TextInputEditText
    private lateinit var passwordEditText: TextInputEditText
    private lateinit var loginButton: Button
    private lateinit var pageTracker: TextView
    private lateinit var backArrow: ImageView


    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_3, container, false)

        val sharedPref = requireActivity().getSharedPreferences("AppPrefs", Context.MODE_PRIVATE)
        val isSetupDone = sharedPref.getBoolean("isSetupComplete", false)

        auth = FirebaseAuth.getInstance()

        backArrow = view.findViewById(R.id.back_arrow)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        loginButton = view.findViewById(R.id.button_next)
        pageTracker = view.findViewById(R.id.pageTracker)

        backArrow.setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_2())
        }

        if (isSetupDone) {
            // Setup completed, Hide some features
            backArrow.visibility = View.GONE
            pageTracker.visibility = View.GONE

        } else {
            // Setup not completed, Turn on some features
            backArrow.visibility = View.VISIBLE
            pageTracker.visibility = View.VISIBLE
        }

        val goToLoginText = view.findViewById<TextView>(R.id.haveAccountTextView)

        goToLoginText.setOnClickListener {
            val currentActivity = activity
            if (currentActivity is SetupPage) {
                currentActivity.loadFragment(start_activity_2())
            } else {
                Log.e("Navigation", "Activity is not SetupPage")
            }
        }

        loginButton.setOnClickListener {
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(context, "Email and password are required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            loginUser(email, password)
        }

        var isPasswordVisible = false

        val passwordEditText = view.findViewById<EditText>(R.id.passwordEditText)
        val eyeIcon = view.findViewById<ImageView>(R.id.eye_icon)

        passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance() // hide password
        eyeIcon.setImageResource(R.drawable.eye_svg_icon)

        eyeIcon.setOnClickListener {
            isPasswordVisible = !isPasswordVisible

            if (isPasswordVisible) {
                passwordEditText.transformationMethod = null // show password
                eyeIcon.setImageResource(R.drawable.eye_off_svg_icon) // optional: change icon
            } else {
                passwordEditText.transformationMethod = PasswordTransformationMethod.getInstance() // hide password
                eyeIcon.setImageResource(R.drawable.eye_svg_icon)
            }

            // Move cursor to the end of text
            passwordEditText.setSelection(passwordEditText.text?.length ?: 0)
        }

        return view
    }

    private fun loginUser(email: String, password: String) {
        val isSetup = UserViewModel.isSetupDone(requireContext())
        auth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                Log.e("LoginDebug", "PreLogIN LogE")
                Log.d("LoginDebug", "PreLogIN LogD")
                Toast.makeText(context, "Login successful!", Toast.LENGTH_SHORT).show()
                val currentActivity = activity
                if (currentActivity is SetupPage) {
                    Log.d("LoginDebug", "Current avtivity is setup page!!")
                    if(isSetup)
                    {
                        val intent = Intent(requireContext(), main_activity::class.java)
                        startActivity(intent)
                        requireActivity().finish()
                    }
                    else{
                        currentActivity.loadFragment(start_activity_4())
                    }

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