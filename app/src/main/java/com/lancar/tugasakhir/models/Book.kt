package com.lancar.tugasakhir.models

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName

@Entity(tableName = "books")
data class Book(
    @PrimaryKey
    @SerializedName("id")
    val id: String,                         // backend int, di app tetap String biar aman

    @SerializedName("title")
    val title: String,

    @SerializedName("author")
    val author: String?,

    @SerializedName("cover_url")
    val coverUrl: String?,

    @SerializedName("category_name")
    val category: String?,

    @SerializedName("publisher")
    val publisher: String?,

    @SerializedName("publish_year")
    val publishYear: String?,

    @SerializedName("isbn")
    val isbn: String?,

    @SerializedName("barcode")
    val barcode: String?,

    @SerializedName("classification")
    val classification: String?,

    @SerializedName("register_no")
    val registerNo: String?,

    @SerializedName("description")
    val description: String?,

    @SerializedName("status")
    val status: String?,

    // Khusus detail (cek koleksi)
    @SerializedName("isInCollection")
    val isInCollection: Boolean?
)
