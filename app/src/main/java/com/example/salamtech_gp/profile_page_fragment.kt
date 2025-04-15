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
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.cardview.widget.CardView
import androidx.lifecycle.ViewModelProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import org.json.JSONObject


class profile_page_fragment : Fragment() {

    private lateinit var profileImageView: ImageView

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