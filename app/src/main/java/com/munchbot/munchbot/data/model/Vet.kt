package com.munchbot.munchbot.data.model


data class Vet(
    val vetProfileImage: String? = null,
    val name: String,
    val category: String,
    val phoneNumber: String,
    val email: String,
    val vetId: String,
    var isExpanded: Boolean = false
){
    constructor() : this("", "", "", "", "","",)
}
