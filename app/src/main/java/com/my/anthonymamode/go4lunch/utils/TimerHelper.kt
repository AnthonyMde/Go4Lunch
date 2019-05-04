package com.my.anthonymamode.go4lunch.utils

import android.os.Handler

fun debounceThatFunction(lambda: () -> Unit, debounceTime: Long, lastChange: Long): Long {
    Handler().postDelayed({
        if (System.currentTimeMillis() - lastChange >= debounceTime) {
            lambda()
        }
    }, debounceTime)
    return System.currentTimeMillis()
}