package com.munchbot.munchbot.ui.fragments.signUp

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.Utils.SetupUI
import com.munchbot.munchbot.Utils.StatusBarUtils
import com.munchbot.munchbot.data.database.SignUpDataStoreManager
import com.munchbot.munchbot.data.repository.SignUpDataRepository
import com.munchbot.munchbot.data.viewmodel.MyViewModelDataFactory
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.data.viewmodel.SignUpViewModelData
import com.munchbot.munchbot.databinding.SignUp2Binding
import com.munchbot.munchbot.ui.adapters.SignUpAdapter
import com.munchbot.munchbot.ui.main_view.auth.SignUp

@Suppress("DEPRECATION")
class SignUpStep2Fragment : MunchBotFragments() {
    private lateinit var binding: SignUp2Binding
    private val pickImageRequest = 1
    private val cameraRequest = 2
    private lateinit var userProfileImage: Uri
    private lateinit var userViewModel: SignUpViewModelData
    private lateinit var sharedViewModel: SignUpSharedViewModel
    private lateinit var adapter: SignUpAdapter
    private lateinit var signUp: SignUp

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = SignUp2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        StatusBarUtils.setStatusBarColor(requireActivity().window, R.color.status_bar_color)
        SetupUI.setupUI(binding.root)

        setupViewModel()
        imageUpload()

        signUp = activity as SignUp
        adapter = signUp.adapter

    }

    private fun setupViewModel() {
        val repository = SignUpDataRepository(SignUpDataStoreManager(requireContext()))
        val factory = MyViewModelDataFactory(repository)
        userViewModel = ViewModelProvider(this, factory)[SignUpViewModelData::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]
    }

    fun validateInputAndProceed(callback: (Boolean) -> Unit) {
        val username = binding.userNameEditText.text.toString().trim()
        val status = binding.statusEditText.text.toString().trim()
        val bio = binding.textAreaBio.text.toString().trim()

        val lettersOnlyPattern = "^[A-Za-z]+$"
        val lettersRequiredPattern = ".*[A-Za-z].*"

        if (username.isEmpty()) {
            binding.userNameEditText.error = "Username cannot be empty"
            callback(false)
        } else if (!username.matches(lettersOnlyPattern.toRegex())) {
            binding.userNameEditText.error = "Username must contain only letters"
            callback(false)
        } else if (status.isEmpty()) {
            binding.statusEditText.error = "Status cannot be empty"
            callback(false)
        } else if (!status.matches(lettersOnlyPattern.toRegex())) {
            binding.statusEditText.error = "Status must contain only letters"
            callback(false)
        } else if (bio.isEmpty()) {
            binding.textAreaBio.error = "Bio cannot be empty"
            callback(false)
        } else if (!bio.matches(lettersRequiredPattern.toRegex())) {
            binding.textAreaBio.error = "Bio must contain at least one letter"
            callback(false)
        } else if (!::userProfileImage.isInitialized) {
            Toast.makeText(requireContext(), "Please select a picture.", Toast.LENGTH_LONG).show()
            callback(false)
        } else {
            android.util.Log.d("SignUpStep2Fragment", "Saving Username: $username")
            android.util.Log.d("SignUpStep2Fragment", "Saving Status: $status")
            android.util.Log.d("SignUpStep2Fragment", "Saving Bio: $bio")

            showLoader()
            sharedViewModel.setUsername(username)
            sharedViewModel.setStatus(status)
            sharedViewModel.setBio(bio)
            sharedViewModel.userProfileImage(userProfileImage)

            binding.root.postDelayed({
                hideLoader()
                Toast.makeText(requireContext(), "Data saved successfully!", Toast.LENGTH_LONG)
                    .show()
                callback(true)
            }, 2000)
        }
    }

    private fun imageUpload() {
        binding.camera.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            AlertDialog.Builder(requireContext())
                .setTitle("Select Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> dispatchTakePictureIntent()
                        1 -> dispatchChoosePictureIntent()
                        2 -> {}
                    }
                }
                .show()
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            startActivityForResult(takePictureIntent, cameraRequest)
        }
    }

    private fun dispatchChoosePictureIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickImageRequest)
    }

    private fun handleImageUpload(imageUri: Uri) {
        val requestOptions = RequestOptions()
            .transforms(CircleCrop())
            .placeholder(R.drawable.ic_camera)
            .error(R.drawable.ic_error)

        Glide.with(this)
            .load(imageUri)
            .apply(requestOptions)
            .into(binding.camera)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                pickImageRequest -> {
                    userProfileImage = data.data!!
                    handleImageUpload(userProfileImage)
                }

                cameraRequest -> {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    val uri = Uri.parse(
                        MediaStore.Images.Media.insertImage(
                            requireContext().contentResolver,
                            imageBitmap,
                            null,
                            null
                        )
                    )
                    handleImageUpload(uri)
                }
            }
        }
    }

    private fun showLoader() {
        (activity as? SignUp)?.showLoader(true)
        binding.root.isClickable = false
        binding.root.isEnabled = false
    }

    private fun hideLoader() {
        (activity as? SignUp)?.showLoader(false)
        binding.root.isClickable = true
        binding.root.isEnabled = true
    }


}
