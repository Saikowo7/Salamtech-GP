package com.example.salamtech_gp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView

class finish_activity : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_finish_activity, container, false)

        //Click event for button id = buttonStart
        view.findViewById<Button>(R.id.buttonStart).setOnClickListener {
            //loading fragment to what is inside the parameter()!!
            (activity as SetupPage).loadFragment(start_activity_4())
        }

        return view
    }
}