package com.lancar.tugasakhir.models

import com.google.gson.annotations.SerializedName

data class UserProfile(
    @SerializedName("username")      val username: String,
    @SerializedName("namaLengkap")   val namaLengkap: String,
    @SerializedName("tanggalLahir")  val tanggalLahir: String,
    @SerializedName("email")         val email: String,
    @SerializedName("institusi")     val institusi: String,
    @SerializedName("nomorHp")       val nomorHp: String,
    @SerializedName("alamat")        val alamat: String
)
