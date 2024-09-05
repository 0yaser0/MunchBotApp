package com.munchbot.munchbot.data.model

data class Medications(
    val name: String,
    val dosage: String,
    val amount: String,
    val duration: String,
    val medicationId: String = "",
    val notify: Boolean
){
    constructor() : this("", "", "", "", "", false)
}
