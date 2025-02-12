package com.example.salamtech_gp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button


class StartActivity_0 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_0, container, false)

        view.findViewById<Button>(R.id.buttonStart).setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_1())
        }

        return view
    }
}