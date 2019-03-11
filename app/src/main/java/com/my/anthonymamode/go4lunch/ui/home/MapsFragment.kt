package com.my.anthonymamode.go4lunch.ui.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.my.anthonymamode.go4lunch.R
import com.my.anthonymamode.go4lunch.utils.BaseFragment

class MapsFragment : BaseFragment(), OnMapReadyCallback {

    companion object {
        fun newInstance(): MapsFragment {
            return MapsFragment()
        }

        private lateinit var mapsView: MapView
        private val TOULOUSE = LatLng(43.600000, 1.433333)
        private const val ZOOM_LEVEL = 12f
        private const val MIN_ZOOM = 6f
        private const val MAX_ZOOM = 20f
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_maps, container, false)
        mapsView = view.findViewById(R.id.mapsView)
        mapsView.getMapAsync(this)
        mapsView.onCreate(savedInstanceState)
        return view
    }

    override fun onMapReady(googleMap: GoogleMap?) {
        googleMap ?: return
        with(googleMap) {
            setMinZoomPreference(MIN_ZOOM)
            setMaxZoomPreference(MAX_ZOOM)
            moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    TOULOUSE,
                    ZOOM_LEVEL
                )
            )
            addMarker(MarkerOptions().position(TOULOUSE))
        }
    }

    override fun onResume() {
        super.onResume()
        mapsView.onResume()
    }

    override fun onPause() {
        mapsView.onPause()
        super.onPause()
    }

    override fun onDestroy() {
        mapsView.onDestroy()
        super.onDestroy()
    }

    override fun onLowMemory() {
        mapsView.onLowMemory()
        super.onLowMemory()
    }
}
