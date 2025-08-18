package com.lancar.tugasakhir.models

import com.google.gson.annotations.SerializedName

data class BorrowedBook(
    @SerializedName("id")
    val id: String,

    @SerializedName("title")
    val title: String,

    @SerializedName("author")
    val author: String,

    @SerializedName("coverUrl")
    val coverUrl: String,

    @SerializedName("status")
    val status: String,

    @SerializedName("requestDate")
    val requestDate: String?,

    @SerializedName("borrowDate")
    val borrowDate: String?,

    @SerializedName("returnDate")
    val returnDate: String?,

    @SerializedName("actualReturnDate")
    val actualReturnDate: String?,

    @SerializedName("pickupDeadline")
    val pickupDeadline: String?
)