package com.example.salamtech_gp

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase

class UserViewModel : ViewModel() {
    private val _userProfile = MutableLiveData<UserProfile>()
    val userProfile: LiveData<UserProfile> get() = _userProfile

    val profileImageUrl = MutableLiveData<String>()

    companion object {
        private const val PREF_NAME = "AppPrefs"
        private const val KEY_SETUP_COMPLETE = "isSetupComplete"

        fun isSetupDone(context: Context): Boolean {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            return prefs.getBoolean(KEY_SETUP_COMPLETE, false)
        }

        fun setSetupDone(context: Context, done: Boolean) {
            val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
            prefs.edit().putBoolean(KEY_SETUP_COMPLETE, done).apply()
        }


    }

    fun fetchProfileImageUrl() {
        val uid = FirebaseAuth.getInstance().currentUser?.uid ?: return
        val ref = FirebaseDatabase.getInstance().getReference("users/$uid/profile/profileImageUrl")

        ref.get().addOnSuccessListener { snapshot ->
            Log.e("UserViewModel", "Success to fetch image URL")
            snapshot.value?.let {
                profileImageUrl.value = it.toString()
            }

        }.addOnFailureListener {
            Log.e("UserViewModel", "Failed to fetch image URL: ${it.message}")
        }
    }

    fun setUserProfile(profile: UserProfile) {
        _userProfile.value = profile
    }
}