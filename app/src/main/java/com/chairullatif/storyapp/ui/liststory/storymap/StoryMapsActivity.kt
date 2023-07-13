package com.chairullatif.storyapp.ui.liststory.storymap

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.data.StoryRepository
import com.chairullatif.storyapp.data.model.StoryModel
import com.chairullatif.storyapp.data.remote.ApiConfig

import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.chairullatif.storyapp.databinding.ActivityStoryMapsBinding
import com.chairullatif.storyapp.ui.ViewModelFactory
import com.chairullatif.storyapp.ui.liststory.StoryViewModel
import com.google.android.gms.maps.model.LatLngBounds

class StoryMapsActivity : AppCompatActivity(), OnMapReadyCallback {

    private lateinit var mMap: GoogleMap
    private lateinit var binding: ActivityStoryMapsBinding
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(
            this,
            StoryRepository(ApiConfig.getApiService())
        ) }
    private val boundsBuilder = LatLngBounds.Builder()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityStoryMapsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

    }

    private fun viewModelAction() {
        binding.apply {
            // get stories
            storyViewModel.getStoriesWithLocation()

            // listen stories
            storyViewModel.dataStories.observe(this@StoryMapsActivity) {
                Log.d(TAG, "viewModelAction stories: $it")
                setMarker(it)
            }
        }
    }

    private fun setMarker(data: List<StoryModel>?) {
        data?.forEach {
            val latLng = LatLng(it.lat, it.lon)
            Log.d(TAG, "viewModelAction stories: ${it.lat}, ${it.lon}")
            mMap.addMarker(
                MarkerOptions()
                    .position(latLng)
                    .title(it.name)
                    .snippet(it.description)
            )
            boundsBuilder.include(latLng)
        }

        val bounds: LatLngBounds = boundsBuilder.build()
        mMap.animateCamera(
            CameraUpdateFactory.newLatLngBounds(
                bounds,
                resources.displayMetrics.widthPixels,
                resources.displayMetrics.heightPixels,
                300
            )
        )
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        // map ui
        mapSetting()

        // view model
        viewModelAction()
    }

    private fun mapSetting() {
        mMap.apply {
            uiSettings.isZoomControlsEnabled = true
            uiSettings.isIndoorLevelPickerEnabled = true
            uiSettings.isCompassEnabled = true
        }
    }

    companion object {
        private const val TAG = "StoryMapsActivity"
    }
}