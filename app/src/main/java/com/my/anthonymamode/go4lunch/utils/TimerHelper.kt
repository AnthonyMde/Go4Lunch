package com.my.anthonymamode.go4lunch.utils

import android.os.Handler

/**
 * Define a time while the target function could not be triggered again.
 * @param lambda the function to be debounced
 * @param debounceTime the duration of the debounce in millisecond
 * @param lastChange the last time in millis, where the debounced function was called
 * @return current time which should be use to set the next lastChange parameter.
 */
fun debounceThatFunction(lambda: () -> Unit, debounceTime: Long, lastChange: Long): Long {
    Handler().postDelayed({
        if (System.currentTimeMillis() - lastChange >= debounceTime) {
            lambda()
        }
    }, debounceTime)
    return System.currentTimeMillis()
}
