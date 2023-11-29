package com.chairullatif.storyapp.ui.liststory.addstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.chairullatif.storyapp.R
import com.chairullatif.storyapp.databinding.ActivityAddStoryBinding
import com.chairullatif.storyapp.helper.createCustomTempFile
import com.chairullatif.storyapp.helper.reduceFileImage
import com.chairullatif.storyapp.helper.rotateFile
import com.chairullatif.storyapp.helper.uriToFile
import com.chairullatif.storyapp.ui.ViewModelFactory
import com.chairullatif.storyapp.ui.liststory.StoryViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import java.io.File
import java.util.Locale

class AddStoryActivity : AppCompatActivity() {

    private var getFile: File? = null
    private lateinit var binding: ActivityAddStoryBinding
    private val storyViewModel: StoryViewModel by viewModels {
        ViewModelFactory(this)
    }
    private lateinit var currentPhotoPath: String
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    var latitude: Double = 0.0
    var longitude: Double = 0.0

    // launcher intent camera
    private val launcherIntentCamera = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) {
        if (it.resultCode == RESULT_OK) {
            // currentPhotoPath is path to image in temporary file that created before
            val myFile = File(currentPhotoPath)
            myFile.let { file ->
                rotateFile(file)
                getFile = file
                binding.ivStory.setImageBitmap(
                    BitmapFactory.decodeFile(file.path)
                )
            }
        }
    }

    // launcher intent gallery
    private val launcherIntentGallery = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            val selectedImg = result.data?.data as Uri
            selectedImg.let { uri ->
                val myFile = uriToFile(uri, this@AddStoryActivity)
                getFile = myFile
                binding.ivStory.setImageURI(uri)
            }
        }
    }

    // launcher get location
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            when {
                permissions[Manifest.permission.ACCESS_FINE_LOCATION] ?: false -> {
                    // Precise location access granted.
                    getMyCurrentLocation()
                }
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] ?: false -> {
                    // Only approximate location access granted.
                    getMyCurrentLocation()
                }
                else -> {
                    binding.switchLocation.isChecked = false
                    binding.btnUpload.isEnabled = true
                    binding.tvLocation.text = getString(R.string.add_location)
                    showToast(getString(R.string.tidak_mendapatkan_permission))
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // init fused location
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        // init view
        initView()

        // view model
        viewModelAction()
    }

    private fun viewModelAction() {
        storyViewModel.apply {
            // observe is upload loading
            isLoading.observe(this@AddStoryActivity) {
                binding.apply {
                    if (it) {
                        btnUpload.isEnabled = false
                        btnUpload.text = getString(R.string.uploading)
                        progressBar.visibility = View.VISIBLE
                    } else {
                        btnUpload.isEnabled = true
                        btnUpload.text = getString(R.string.upload)
                        progressBar.visibility = View.GONE
                    }
                }
            }

            // observe is upload success
            commonResponse.observe(this@AddStoryActivity) {
                if (it.error) {
                    showToast(it.message)
                } else {
                    showToast(it.message)
                    setResult(RESULT_OK)
                    finish()
                }
            }
        }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun initView() {
        binding.apply {
            if (!checkCameraPermission()) {
                ActivityCompat.requestPermissions(
                    this@AddStoryActivity,
                    REQUIRED_CAMERA_PERMISSIONS,
                    REQUEST_CODE_CAMERA_PERMISSIONS
                )
            }

            //btn back
            btnBack.setOnClickListener {
                onBackPressedDispatcher.onBackPressed()
            }

            // btn take photo camera
            btnCamera.setOnClickListener {
                startTakePhoto()
            }

            // btn choose from gallery
            btnGallery.setOnClickListener {
                startGallery()
            }

            switchLocation.setOnClickListener {
                if (switchLocation.isChecked) {
                    getMyCurrentLocation()
                } else {
                    tvLocation.text = getString(R.string.add_location)
                    binding.btnUpload.isEnabled = true
                }
            }

            // btn upload
            btnUpload.setOnClickListener {
                if (getFile != null && edtStory.text.toString().isNotEmpty()) {
                    reduceFileImage(getFile!!)
                    storyViewModel.addStory(edtStory.text.toString(), latitude, longitude, getFile!!)
                } else {
                    showToast(getString(R.string.choose_a_picture) + " & " + getString(R.string.fill_the_story))
                }
            }
        }
    }

    private fun getMyCurrentLocation() {
        binding.tvLocation.text = getString(R.string.getting_location)
        binding.btnUpload.isEnabled = false
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissionLauncher.launch(REQUIRED_LOCATION_PERMISSIONS)
        } else {
            fusedLocationClient.getCurrentLocation(
                LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY, null
            ).addOnSuccessListener { location ->
                if (location != null) {
                    // get location name
                    val geocoder = Geocoder(this, Locale.getDefault())
                    val addresses: List<Address>? = geocoder.getFromLocation(
                        location.latitude,
                        location.longitude,
                        1
                    )

                    //set latitude and longitude and tv location
                    val country: String = addresses?.get(0)?.countryName
                            ?: location.latitude.toString()
                    val subAdminArea: String = addresses?.get(0)?.subAdminArea
                            ?: location.longitude.toString()
                    binding.tvLocation.text = getString(R.string.location, subAdminArea, country)
                    latitude = location.latitude
                    longitude = location.longitude
                } else {
                    showToast(getString(R.string.failed_get_location))
                    binding.tvLocation.text = getString(R.string.failed_get_location)
                }
                binding.btnUpload.isEnabled = true
            }.addOnFailureListener {
                showToast(getString(R.string.failed_get_location))
                binding.btnUpload.isEnabled = true
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_CAMERA_PERMISSIONS) {
            if (!checkCameraPermission()) {
                showToast(getString(R.string.tidak_mendapatkan_permission))
                finish()
            }
        }
    }

    private fun checkCameraPermission() = REQUIRED_CAMERA_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(baseContext, it) == PackageManager.PERMISSION_GRANTED
    }

    private fun startTakePhoto() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.resolveActivity(packageManager)

        // create new temporary file
        createCustomTempFile(application).also {
            val photoURI: Uri = FileProvider.getUriForFile(
                this@AddStoryActivity,
                "com.chairullatif.storyapp",
                it
            )
            currentPhotoPath = it.absolutePath
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            launcherIntentCamera.launch(intent)
        }

    }

    private fun startGallery() {
        val intent = Intent()
        intent.action = ACTION_GET_CONTENT
        intent.type = "image/*"
        val chooser = Intent.createChooser(intent, getString(R.string.choose_a_picture))
        launcherIntentGallery.launch(chooser)
    }

    companion object {
        private val REQUIRED_CAMERA_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private val REQUIRED_LOCATION_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
        private const val REQUEST_CODE_CAMERA_PERMISSIONS = 10
    }
}

interface AddStoryCallback {
    fun onAddStorySuccess()
}