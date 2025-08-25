package com.lancar.tugasakhir.viewmodel

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class RegistrationState(
    var name: String = "",
    var address: String = "",
    var email: String = "",
    var birthDate: String = "",
    var phoneNumber: String = "",
    var institution: String = ""
) : Parcelable