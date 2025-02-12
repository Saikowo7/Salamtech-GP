package com.example.salamtech_gp

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ImageView

class start_activity_4 : Fragment() {
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_start_activity_4, container, false)

        val reloadIcon = view.findViewById<ImageView>(R.id.reload_icon)

        val rotateAnimation = AnimationUtils.loadAnimation(requireContext(), R.anim.reload_rotate)
        rotateAnimation.repeatCount = Animation.INFINITE // Makes it loop continuously

        reloadIcon.startAnimation(rotateAnimation)

        view.findViewById<Button>(R.id.button_next).setOnClickListener {
            (activity as SetupPage).loadFragment(finish_activity())
        }

        view.findViewById<ImageView>(R.id.back_arrow).setOnClickListener {
            (activity as SetupPage).loadFragment(start_activity_3())
        }

        return view
    }
}