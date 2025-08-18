package com.lancar.tugasakhir.models

import com.google.gson.annotations.SerializedName
import com.lancar.tugasakhir.models.User

data class AuthResponse(
    @SerializedName("token")
    val token: String,

    @SerializedName("user")
    val user: User
)