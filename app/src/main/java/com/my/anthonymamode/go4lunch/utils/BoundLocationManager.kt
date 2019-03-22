package com.my.anthonymamode.go4lunch.utils

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest

class BoundLocationManager {
    companion object {
        fun boundLocationListenerIn(
            lifecycleOwner: LifecycleOwner,
            listener: FusedLocationProviderClient,
            callback: LocationCallback
        ) {
            BoundLocationListener(lifecycleOwner, listener, callback)
        }
    }

    @SuppressWarnings("MissingPermission")
    class BoundLocationListener(
        lifecycleOwner: LifecycleOwner,
        private val listener: FusedLocationProviderClient,
        private val callback: LocationCallback
    ) : LifecycleObserver {
        init {
            lifecycleOwner.lifecycle.addObserver(this)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
        fun addListener() {
            /**
             * @val locationRequest is used to configure location updates.
             * locationRequest.interval is the update interval by default.
             * locationRequest.fastestInterval is the fastest interval than can be
             * get from other application location updates.
             * locationRequest.priority defines that the more accurate geolocalisation
             * should be used by the phone (i.e : GPS).
             */
            val locationRequest = LocationRequest().apply {
                interval = 10000
                fastestInterval = 5000
                priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            }
            listener.requestLocationUpdates(locationRequest, callback, null)
        }

        @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
        fun removeListener() {
            listener.removeLocationUpdates(callback)
        }
    }
}
