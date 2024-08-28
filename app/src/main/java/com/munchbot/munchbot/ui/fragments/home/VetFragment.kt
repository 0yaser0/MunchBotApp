package com.munchbot.munchbot.ui.fragments.home

import com.munchbot.munchbot.ui.adapters.VetAdapter
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.data.model.Vet
import com.munchbot.munchbot.databinding.DialogAddVetBinding
import com.munchbot.munchbot.databinding.FragmentVetBinding

@Suppress("DEPRECATION")
class VetFragment : MunchBotFragments() {
    private var _binding: FragmentVetBinding? = null
    private val binding get() = _binding!!
    private lateinit var vetAdapter: VetAdapter
    private var vetProfileImage: Uri? = null
    private val pickImageRequest = 1000
    private val cameraRequest = 1001
    private lateinit var dialogBinding: DialogAddVetBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        vetAdapter = VetAdapter(mutableListOf())
        binding.vetRecyclerView.apply {
            adapter = vetAdapter
            layoutManager = LinearLayoutManager(context)
        }

        binding.addVetButton.setOnClickListener {
            showAddVetDialog()
        }
    }

    private fun showAddVetDialog() {
        dialogBinding = DialogAddVetBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())

        with(builder) {
            setTitle("Add Veterinarian")
            setView(dialogBinding.root)
            setPositiveButton("Add") { _, _ ->
                val vet = Vet(
                    profileImage = vetProfileImage,
                    name = dialogBinding.nameEditText.text.toString(),
                    category = dialogBinding.categoryEditText.text.toString(),
                    phoneNumber = dialogBinding.phoneEditText.text.toString(),
                    email = dialogBinding.emailEditText.text.toString()
                )

                vetAdapter.addVet(vet)
            }
            setNegativeButton("Cancel", null)
            show()
        }

        imageUpload(dialogBinding.profileImage)
    }

    private fun imageUpload(profileImageView: ImageView) {
        profileImageView.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            AlertDialog.Builder(requireContext())
                .setTitle("Select Action")
                .setItems(options) { _, which ->
                    when (which) {
                        0 -> dispatchTakePictureIntent()
                        1 -> dispatchChoosePictureIntent()
                    }
                }
                .show()
        }
    }

    private fun dispatchChoosePictureIntent() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, pickImageRequest)
    }

    private fun dispatchTakePictureIntent() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, cameraRequest)
    }

    private fun handleImageUpload(imageUri: Uri, imageView: ImageView) {
        val requestOptions = RequestOptions()
            .transform(CircleCrop())
            .placeholder(R.drawable.ic_camera)
            .error(R.drawable.ic_error)

        Glide.with(this)
            .load(imageUri)
            .apply(requestOptions)
            .into(imageView)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                pickImageRequest -> {
                    vetProfileImage = data.data!!
                    handleImageUpload(vetProfileImage!!, dialogBinding.profileImage)
                }
                cameraRequest -> {
                    val imageBitmap = data.extras?.get("data") as Bitmap
                    val uri = saveBitmapAndGetUri(imageBitmap)
                    handleImageUpload(uri, dialogBinding.profileImage)
                    vetProfileImage = uri
                }
            }
        }
    }

    private fun saveBitmapAndGetUri(bitmap: Bitmap): Uri {
        val path = MediaStore.Images.Media.insertImage(
            requireContext().contentResolver,
            bitmap,
            "VetImage",
            null
        )
        return Uri.parse(path)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
