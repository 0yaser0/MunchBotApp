package com.munchbot.munchbot.ui.fragments.home

import android.animation.ArgbEvaluator
import android.app.Activity
import android.app.AlertDialog
import android.content.ContentValues.TAG
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.bumptech.glide.request.RequestOptions
import com.google.firebase.storage.FirebaseStorage
import com.munchbot.munchbot.MunchBotFragments
import com.munchbot.munchbot.R
import com.munchbot.munchbot.data.database.getPetId
import com.munchbot.munchbot.data.database.getUserId
import com.munchbot.munchbot.data.viewmodel.PetViewModel
import com.munchbot.munchbot.data.viewmodel.SignUpSharedViewModel
import com.munchbot.munchbot.data.viewmodel.UserViewModel
import com.munchbot.munchbot.data.viewmodel.VetViewModel
import com.munchbot.munchbot.databinding.DialogAddVetBinding
import com.munchbot.munchbot.databinding.FragmentVetBinding
import com.munchbot.munchbot.ui.adapters.VetAdapter
import java.util.UUID
import kotlin.math.abs

@Suppress("DEPRECATION")
class VetFragment : MunchBotFragments() {
    private var _binding: FragmentVetBinding? = null
    private val binding get() = _binding!!
    private lateinit var vetAdapter: VetAdapter
    private var vetProfileImage: Uri? = null
    private val pickImageRequest = 1000
    private val cameraRequest = 1001
    private lateinit var dialogBinding: DialogAddVetBinding
    private lateinit var sharedViewModel: SignUpSharedViewModel
    private val vetViewModel: VetViewModel by viewModels()
    private val petViewModel: PetViewModel by viewModels()
    private val userViewModel: UserViewModel by viewModels()
    private val userId = getUserId()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVetBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewModel()
        vetAdapter = VetAdapter(mutableListOf())
        binding.vetRecyclerView.adapter = vetAdapter
        binding.vetRecyclerView.layoutManager = LinearLayoutManager(context)

        observeVetsLiveData()

        userId?.let {
            vetViewModel.loadAllVets(it)
        }

        setupGetter()

        petViewModel.petLiveData.observe(viewLifecycleOwner) { pet ->
            pet?.let {
                val petImageView = binding.petImageVet
                if (it.petProfileImage.isNotEmpty()) {
                    Log.d("PlannerFragment", "Loading image URL: ${it.petProfileImage}")
                    Glide.with(this)
                        .load(it.petProfileImage)
                        .error(R.drawable.ic_error)
                        .into(petImageView)
                } else {
                    petImageView.setImageResource(R.drawable.ic_error)
                }
            }
        }

        deleteVetWithSwipe()

        binding.addVetButton.setOnClickListener {
            showAddVetDialog()
        }
    }

    private fun setupViewModel() {
        sharedViewModel = ViewModelProvider(requireActivity())[SignUpSharedViewModel::class.java]
    }

    private fun observeVetsLiveData() {
        vetViewModel.vetsLiveData.observe(viewLifecycleOwner) { vets ->
            vets?.let {
                vetAdapter.setVets(it)
            }
        }
    }

    private fun setupGetter() {
        val userId = getUserId()
        Log.d(TAG, "User ID $userId")

        if (userId != null) {
            val petId = getPetId(userId)
            Log.d(TAG, "Pet ID $petId")
            petViewModel.loadPet(userId, petId)
            userViewModel.loadUser(userId)
        }
    }

    private fun deleteVetWithSwipe() {
        val itemTouchHelperCallback =
            object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
                private val backgroundColorStart =
                    ContextCompat.getColor(requireContext(), R.color.white)
                private val backgroundColorEnd =
                    ContextCompat.getColor(requireContext(), R.color.red)

                override fun onMove(
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    target: RecyclerView.ViewHolder
                ): Boolean = false

                override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                    val position = viewHolder.adapterPosition
                    val vet = vetAdapter.getVetAtPosition(position)

                    AlertDialog.Builder(requireContext()).apply {
                        setTitle("Delete Vet")
                        setMessage("Are you sure you want to delete this vet?")
                        setPositiveButton("Yes") { dialog, _ ->
                            vetAdapter.removeVetAtPosition(position)
                            userId?.let {
                                vetViewModel.deleteVet(it, vet.vetId)
                            }
                            dialog.dismiss()
                        }
                        setNegativeButton("No") { dialog, _ ->
                            vetAdapter.notifyItemChanged(position)
                            dialog.dismiss()
                        }
                        show()

                        val alertDialog = this.create()
                        alertDialog.setOnShowListener {
                            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
                            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

                            positiveButton.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.firstColor
                                )
                            )
                            negativeButton.setTextColor(
                                ContextCompat.getColor(
                                    requireContext(),
                                    R.color.thirdColor
                                )
                            )
                        }
                    }
                }

                override fun onChildDraw(
                    c: Canvas,
                    recyclerView: RecyclerView,
                    viewHolder: RecyclerView.ViewHolder,
                    dX: Float,
                    dY: Float,
                    actionState: Int,
                    isCurrentlyActive: Boolean
                ) {
                    val itemView = viewHolder.itemView
                    val backgroundColorStart = ContextCompat.getColor(requireContext(), com.firebase.ui.auth.R.color.fui_transparent)
                    val backgroundColorEnd = ContextCompat.getColor(requireContext(), R.color.red)

                    val paint = Paint()
                    paint.color = if (dX > 0) {
                        backgroundColorEnd
                    } else {
                        val fraction = abs(dX) / itemView.width
                        val interpolatedColor = ArgbEvaluator().evaluate(fraction, backgroundColorStart, backgroundColorEnd) as Int
                        interpolatedColor
                    }

                    paint.alpha = (abs(dX) / itemView.width * 255).toInt()

                    c.drawRect(itemView.left.toFloat(), itemView.top.toFloat(), itemView.right.toFloat(), itemView.bottom.toFloat(), paint)

                    super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
                }

                override fun clearView(recyclerView: RecyclerView, viewHolder: RecyclerView.ViewHolder) {
                    super.clearView(recyclerView, viewHolder)
                }
            }

        val itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback)
        itemTouchHelper.attachToRecyclerView(binding.vetRecyclerView)
    }

    private fun showAddVetDialog() {
        dialogBinding = DialogAddVetBinding.inflate(layoutInflater)

        val builder = AlertDialog.Builder(requireContext())
        builder.setTitle("Add Veterinarian")
            .setView(dialogBinding.root)
            .setPositiveButton("Add") { dialog, _ ->
                val name = dialogBinding.nameEditText.text.toString()
                val category = dialogBinding.categoryEditText.text.toString()
                val phoneNumber = dialogBinding.phoneEditText.text.toString()
                val email = dialogBinding.emailEditText.text.toString()
                val isImageSelected = dialogBinding.profileImage.drawable != null

                when {
                    name.isEmpty() || category.isEmpty() || phoneNumber.isEmpty() || email.isEmpty() -> {
                        Toast.makeText(
                            requireContext(),
                            "Please fill all fields",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    !isImageSelected -> {
                        Toast.makeText(
                            requireContext(),
                            "Please select an image",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    else -> {
                        val imageUri = vetProfileImage
                        if (imageUri != null) {
                            uploadVetImageToFirebase(imageUri) { imageUrl ->
                                userId?.let { uid ->
                                    sharedViewModel.saveNewVet(
                                        uid,
                                        name,
                                        category,
                                        phoneNumber,
                                        email,
                                        vetAdapter,
                                        imageUrl
                                    )
                                }
                                dialog.dismiss()
                            }
                        } else {
                            Toast.makeText(
                                requireContext(),
                                "Please fill all fields and select an image",
                                Toast.LENGTH_SHORT
                            ).show()
                            dialog.dismiss()
                        }
                    }
                }
            }
            .setNegativeButton("Cancel", null)

        val alertDialog = builder.create()
        alertDialog.setOnShowListener {
            val positiveButton = alertDialog.getButton(AlertDialog.BUTTON_POSITIVE)
            val negativeButton = alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE)

            positiveButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.firstColor
                )
            )
            negativeButton.setTextColor(
                ContextCompat.getColor(
                    requireContext(),
                    R.color.thirdColor
                )
            )
        }

        alertDialog.show()

        imageUpload(dialogBinding.profileImage)
    }

    private fun uploadVetImageToFirebase(imageUri: Uri, callback: (String) -> Unit) {
        val storageReference = FirebaseStorage.getInstance().reference
            .child("vetProfileImages/${UUID.randomUUID()}")

        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    callback(uri.toString())
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    private fun imageUpload(vetProfileImageView: ImageView) {
        vetProfileImageView.setOnClickListener {
            val options = arrayOf("Take Photo", "Choose from Gallery", "Cancel")
            androidx.appcompat.app.AlertDialog.Builder(requireContext())
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
        try {
            val requestOptions = RequestOptions()
                .transforms(CircleCrop())
                .error(R.drawable.ic_error)

            Glide.with(this)
                .load(imageUri)
                .apply(requestOptions)
                .into(dialogBinding.profileImage)

        } catch (e: Exception) {
            Log.e(TAG, "Error loading image: ${e.message}")
        }
    }

    private fun uploadVetImageToFirebase(imageUri: Uri) {
        val storageReference = FirebaseStorage.getInstance().reference
            .child("vetProfileImages/${UUID.randomUUID()}")

        storageReference.putFile(imageUri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnSuccessListener { uri ->
                    sharedViewModel.setVetProfileImage(uri)
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(
                    requireContext(),
                    "Failed to upload image: ${exception.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && data != null) {
            when (requestCode) {
                pickImageRequest -> {
                    data.data?.let {
                        vetProfileImage = it
                        handleImageUpload(it)
                    }
                }

                cameraRequest -> {
                    val imageBitmap = data.extras?.get("data") as? Bitmap
                    imageBitmap?.let {
                        val uri = Uri.parse(
                            MediaStore.Images.Media.insertImage(
                                requireContext().contentResolver,
                                it,
                                null,
                                null
                            )
                        )
                        vetProfileImage = uri
                        handleImageUpload(uri)
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
