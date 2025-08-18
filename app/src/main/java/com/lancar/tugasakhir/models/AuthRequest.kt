package com.lancar.tugasakhir.models

data class LoginRequest(val email: String, val pass: String)

data class RegisterRequest(
    val name: String,
    val address: String,
    val email: String,
    val pass: String,
    val birthDate: String,
    val phoneNumber: String,
    val institution: String?
)