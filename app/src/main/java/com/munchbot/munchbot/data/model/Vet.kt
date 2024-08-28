package com.munchbot.munchbot.data.model

import android.net.Uri


data class Vet(
    val profileImage: Uri?,
    val name: String,
    val category: String,
    val phoneNumber: String,
    val email: String,
    var isExpanded: Boolean = false
)
