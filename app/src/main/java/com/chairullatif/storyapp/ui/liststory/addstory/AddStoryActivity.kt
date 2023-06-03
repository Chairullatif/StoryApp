package com.chairullatif.storyapp.ui.liststory.addstory

import android.Manifest
import android.content.Intent
import android.content.Intent.ACTION_GET_CONTENT
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
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
import java.io.File

class AddStoryActivity : AppCompatActivity() {

    private var getFile: File? = null
    private lateinit var binding: ActivityAddStoryBinding
    private val storyViewModel: StoryViewModel by viewModels { ViewModelFactory(this) }
    private lateinit var currentPhotoPath: String

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
        private const val REQUEST_CODE_PERMISSIONS = 10
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

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
            if (!allPermissionsGranted()) {
                ActivityCompat.requestPermissions(
                    this@AddStoryActivity,
                    REQUIRED_PERMISSIONS,
                    REQUEST_CODE_PERMISSIONS
                )
            }

            //btn back
            btnBack.setOnClickListener {
                onBackPressed()
            }

            // btn take photo camera
            btnCamera.setOnClickListener {
                startTakePhoto()
            }

            // btn choose from gallery
            btnGallery.setOnClickListener {
                startGallery()
            }

            // btn upload
            btnUpload.setOnClickListener {
                if (getFile != null && edtStory.text.toString().isNotEmpty()) {
                    reduceFileImage(getFile!!)
                    storyViewModel.addStory(edtStory.text.toString(), getFile!!)
                } else {
                    showToast(getString(R.string.choose_a_picture) + " & " + getString(R.string.fill_the_story))
                }
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (!allPermissionsGranted()) {
                Toast.makeText(
                    this,
                    getString(R.string.tidak_mendapatkan_permission),
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }
        }
    }
    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
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
}