package com.example.salamtech_gp

import okhttp3.OkHttpClient
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import okhttp3.Request

import com.bumptech.glide.Glide

import java.io.IOException
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONObject


class profile_page_fragment : Fragment() {

    private lateinit var profileImageView: ImageView
    private lateinit var database: DatabaseReference
    private lateinit var auth: FirebaseAuth

    private val pickImageLauncher = registerForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        uri?.let {
            // 🟢 This is the image the user picked
            uploadImageToCloudinary(it)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_profile_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val viewModel = ViewModelProvider(requireActivity())[UserViewModel::class.java]
        val backArrow: ImageView = view.findViewById(R.id.back_arrow)
        val logoutButton = view.findViewById<Button>(R.id.logoutButton)
        val uploadButton = view.findViewById<CardView>(R.id.uploadButtonCardView)
        profileImageView = view.findViewById<ImageView>(R.id.profileImageView)

        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val dbRef = FirebaseDatabase.getInstance().reference

        //Loading Email and Username
        auth = FirebaseAuth.getInstance()
        database = FirebaseDatabase.getInstance().reference

        //Fetch User And Email From Firebase
        val tvName = view.findViewById<TextView>(R.id.tvUserName)
        val tvEmail = view.findViewById<TextView>(R.id.tvUserEmail)
        val editUsername = view.findViewById<TextView>(R.id.editUsername)
        val editEmail = view.findViewById<TextView>(R.id.editEmail)

        //Change Username and Email And Password
        //Username
        val usernameSection = view.findViewById<LinearLayout>(R.id.usernameSection)
        val usernameTextView = view.findViewById<TextView>(R.id.editUsername)
        val usernameEditText = view.findViewById<EditText>(R.id.usernameEditText)

        usernameSection.setOnClickListener {
            if (usernameTextView.visibility == View.VISIBLE) {
                usernameEditText.setText(usernameTextView.text.toString())
                usernameTextView.visibility = View.GONE
                usernameEditText.visibility = View.VISIBLE
            } else {
                val newName = usernameEditText.text.toString().trim()
                usernameTextView.text = newName
                usernameTextView.visibility = View.VISIBLE
                usernameEditText.visibility = View.GONE

                // Save to Firebase
                if (uid != null) {
                    val ref = FirebaseDatabase.getInstance().getReference("users/$uid/profile/fullName")
                    ref.setValue(newName)
                }
            }
        }

        // Email
        val emailSection = view.findViewById<LinearLayout>(R.id.emailSection)
        val emailTextView = view.findViewById<TextView>(R.id.editEmail)
        val emailEditText = view.findViewById<EditText>(R.id.emailEditText)

        val user = FirebaseAuth.getInstance().currentUser
        var userFlagEmail = false
        if (user != null && !user.isEmailVerified) {
            Log.d("FirebaseCheck", "Email is not verified. Cannot update.")
            userFlagEmail = false
        }
        else{
            userFlagEmail = true
        }

        if(userFlagEmail)
        {
            emailSection.setOnClickListener {
                if (emailTextView.visibility == View.VISIBLE) {
                    emailEditText.setText(emailTextView.text.toString())
                    emailTextView.visibility = View.GONE
                    emailEditText.visibility = View.VISIBLE
                } else {

                    val newEmail = emailEditText.text.toString().trim()
                    emailTextView.text = newEmail
                    emailTextView.visibility = View.VISIBLE
                    emailEditText.visibility = View.GONE

                    // Update email in Firebase
                    if (uid != null) {
                        val ref = FirebaseDatabase.getInstance().getReference("users/$uid/profile/email")
                        ref.setValue(newEmail)

                        FirebaseAuth.getInstance().currentUser?.updateEmail(newEmail)
                            ?.addOnSuccessListener {
                                Log.d("FirebaseAuthUpdate", "Email updated successfully")
                            }
                            ?.addOnFailureListener { e ->
                                Log.d("FirebaseAuthUpdate", "Failed to update Auth email: ${e.message}", e)
                            }

                    }
                }

            }
        }

        //Password
        val passwordSection = view.findViewById<LinearLayout>(R.id.passwordSection)

        passwordSection.setOnClickListener {
            val email = user?.email

            if (!email.isNullOrEmpty()) {
                FirebaseAuth.getInstance().sendPasswordResetEmail(email)
                    .addOnSuccessListener {
                        Toast.makeText(requireContext(), "Password reset email sent to $email", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Log.d("ResetPassword", "Failed to send reset email: ${e.message}")
                        Toast.makeText(requireContext(), "Failed to send reset email", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(requireContext(), "No email associated with this account", Toast.LENGTH_SHORT).show()
            }
        }

        if (uid != null) {
            val userRef = database.child("users").child(uid).child("profile")
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val name = snapshot.child("fullName").getValue(String::class.java)
                    val email = snapshot.child("email").getValue(String::class.java)

                    tvName.text = name ?: "Name not found"
                    tvEmail.text = email ?: "Email not found"


                    editUsername.text = name ?: "Name not found"
                    editEmail.text = email ?: "Email not found"
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(context, "Failed to load profile: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        //Loading profile image
        viewModel.fetchProfileImageUrl()
        viewModel.profileImageUrl.observe(viewLifecycleOwner) { url ->
            Glide.with(requireContext())
                .load(url)
                .circleCrop()
                .into(profileImageView)
        }

        dbRef.child("users").child(uid).child("profile").child("profileImageUrl")
            .get()
            .addOnSuccessListener { snapshot ->
                val imageUrl = snapshot.getValue(String::class.java)
                if (!imageUrl.isNullOrEmpty()) {
                    Glide.with(requireContext())
                        .load(imageUrl)
                        .placeholder(R.drawable.user_icon) // optional
                        .error(R.drawable.logo_png)          // optional
                        .into(profileImageView)
                }
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "Failed to load profile image", Toast.LENGTH_SHORT).show()
                Log.e("FirebaseLoad", "Error: ${it.message}")
            }

        logoutButton.setOnClickListener {
            FirebaseAuth.getInstance().signOut()

            val intent = Intent(requireContext(), SetupPage::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
        }

        //On click it will change profile picture using the function below
        uploadButton.setOnClickListener{
            pickImageLauncher.launch("image/*")
        }

        backArrow.setOnClickListener {
            requireActivity().supportFragmentManager.popBackStack() // Go back to previous fragment
        }

        //Delete Account
        val deleteSection = view.findViewById<LinearLayout>(R.id.deleteAccount)

        deleteSection.setOnClickListener {
            user?.delete()
                ?.addOnSuccessListener {
                    Toast.makeText(requireContext(), "Account deleted successfully", Toast.LENGTH_SHORT).show()
                    val intent = Intent(requireContext(), SetupPage::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                    startActivity(intent)
                }
                ?.addOnFailureListener { e ->
                    Log.d("DeleteAccount", "Error: ${e.message}")
                    Toast.makeText(requireContext(), "Failed to delete account: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }
    }

    private fun uploadImageToCloudinary(imageUri: Uri) {
        val cloudName = "dx4dumbod" // Cloud name from Cloudinary
        val uploadPreset = "unsigned_preset" // Upload preset name!!

        val contentResolver = requireContext().contentResolver
        val inputStream = contentResolver.openInputStream(imageUri)
        val requestBody = inputStream?.readBytes()?.let { imageBytes ->
            MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart("file", "profile.jpg",
                    imageBytes.toRequestBody("image/*".toMediaTypeOrNull()))
                .addFormDataPart("upload_preset", uploadPreset)
                .build()
        } ?: return

        val request = Request.Builder()
            .url("https://api.cloudinary.com/v1_1/$cloudName/image/upload")
            .post(requestBody)
            .build()

        val client = OkHttpClient()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                Log.e("Cloudinary", "Upload failed: ${e.message}")
                requireActivity().runOnUiThread {
                    Toast.makeText(context, "Upload failed", _root_ide_package_.android.widget.Toast.LENGTH_SHORT).show()
                }
            }

            override fun onResponse(call: Call, response: Response) {

                response.body?.string()?.let { responseBody ->
                    val json = JSONObject(responseBody)
                    val imageUrl = json.getString("secure_url")

                    Log.d("Cloudinary", "Image uploaded: $imageUrl")

                    requireActivity().runOnUiThread {
                        Toast.makeText(context, "Uploaded successfully", Toast.LENGTH_SHORT).show()

                        // Optionally: Save imageUrl to Firebase
                        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return@runOnUiThread
                        FirebaseDatabase.getInstance().reference
                            .child("users").child(uid).child("profile").child("profileImageUrl")
                            .setValue(imageUrl)
                            .addOnSuccessListener {
                                Toast.makeText(requireContext(), "Image URL saved to Firebase", Toast.LENGTH_SHORT).show()
                            }
                            .addOnFailureListener {
                                Toast.makeText(requireContext(), "Failed to save URL: ${it.message}", Toast.LENGTH_SHORT).show()
                            }
                    }
                }

            }
        })
    }
}