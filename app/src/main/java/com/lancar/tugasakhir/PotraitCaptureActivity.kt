package com.lancar.tugasakhir

import android.view.View
import com.journeyapps.barcodescanner.CaptureActivity

class PortraitCaptureActivity : CaptureActivity() {
    // Dipanggil dari XML lewat android:onClick
    fun onCloseClicked(view: View) {
        finish() // kembali ke ScannerScreen
    }
}
