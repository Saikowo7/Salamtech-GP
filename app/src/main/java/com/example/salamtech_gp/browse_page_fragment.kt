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

class browse_page_fragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_browse_page_fragment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val button_caregiver = view.findViewById<Button>(R.id.button_caregiver)

        //on button_caregiver click move to caregiver fragment
        button_caregiver.setOnClickListener {
            val fragment = caregiver_page_fragment()
            requireActivity().supportFragmentManager.beginTransaction()
                .replace(R.id.main, fragment) // Replace with your container ID
                .addToBackStack(null) // Allows back navigation
                .commit()
        }
    }

}