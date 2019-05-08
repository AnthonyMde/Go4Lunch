package com.my.anthonymamode.go4lunch.utils

import android.os.Build
import android.view.Window
import android.view.WindowManager

/**
 * Can't be done directly in the xml theme to the sake of
 * backward compatibility to android 4.4 (kitkat).
 */
fun Window.setStatusBarTransparent() {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
        setFlags(
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS
        )
    }
}