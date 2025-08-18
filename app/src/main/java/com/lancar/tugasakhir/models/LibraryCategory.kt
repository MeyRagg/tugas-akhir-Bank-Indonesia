package com.lancar.tugasakhir.models

import com.google.gson.annotations.SerializedName

data class LibraryCategory(
    @SerializedName("number")
    val number: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("icon_name")
    val iconName: String
)