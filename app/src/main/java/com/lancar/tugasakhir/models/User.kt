package com.lancar.tugasakhir.models

import com.google.gson.annotations.SerializedName

data class User(
    @SerializedName("id")
    val id: String?,

    @SerializedName("profile_image_url")
    val profileImageUrl: String?,

    @SerializedName("name")
    val name: String?,

    @SerializedName("email")
    val email: String?,

    @SerializedName("birth_date")
    val birthDate: String?,

    @SerializedName("institution")
    val institution: String?,

    @SerializedName("phone_number")
    val phoneNumber: String?,

    @SerializedName("address")
    val address: String?
) {
    val fullProfileImageUrl: String?
    get() {
        if (profileImageUrl.isNullOrBlank()) {
            return null
        }
        val baseUrl = "https://satuperpustakaanku.my.id" + profileImageUrl
        return "$baseUrl?v=${System.currentTimeMillis()}"
    }
}