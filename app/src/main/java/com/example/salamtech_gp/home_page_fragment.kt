package com.example.salamtech_gp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ScrollView
import androidx.core.widget.NestedScrollView

class home_page_fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_home_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val frameLayout = view.findViewById<FrameLayout>(R.id.profile_card)
        val scrollView = view.findViewById<ScrollView>(R.id.scroll_view_homepage)
        val button_setup_to_devices = view.findViewById<Button>(R.id.button_setup_to_devices)
        val imageView = view.findViewById<ImageView>(R.id.profile_picture_id)

        //on button setup click move to devices fragment
        button_setup_to_devices.setOnClickListener {
            val fragment = device_page_fragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment) // Replace with your container ID
                .addToBackStack(null) // Allows back navigation
                .commit()
        }

        imageView.setOnClickListener {
            val fragment = profile_page_fragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment) // Replace with your container ID
                .addToBackStack(null) // Allows back navigation
                .commit()
        }
    }
}
