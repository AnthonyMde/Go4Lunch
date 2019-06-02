package com.my.anthonymamode.go4lunch.utils

import android.os.Handler
import android.view.View
import android.view.animation.AccelerateInterpolator
import android.view.animation.LinearInterpolator
import com.google.android.material.floatingactionbutton.FloatingActionButton

fun FloatingActionButton.scaleDown() {
    Handler().post {
        animate()
            .withEndAction { visibility = View.GONE }
            .setDuration(100L)
            .setInterpolator(LinearInterpolator())
            .scaleX(0f)
            .scaleY(0f)
            .start()
    }
}

fun FloatingActionButton.scaleUp() {
    Handler().postDelayed(
        {
            animate()
                .withStartAction { visibility = View.VISIBLE }
                .setDuration(100L)
                .setInterpolator(AccelerateInterpolator())
                .scaleX(1f)
                .scaleY(1f)
                .start()
        },
        200L
    )
}