package com.example.salamtech_gp

import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import android.widget.Button
import android.widget.Toolbar
import androidx.fragment.app.Fragment


class start_activity_1 : Fragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_1, container, false)

        view.findViewById<Button>(R.id.button_next).setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_2())
        }

        view.findViewById<Toolbar>(R.id.toolbar_back).setOnClickListener {
            //finish


        }

        return view
    }

}