//SUDAH BISA DIHAPUS NANTI

package com.lancar.tugasakhir.data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

// Langkah 1: Modifikasi data class untuk menyertakan icon
data class LibraryCategory(
    val number: String,
    val name: String,
    val icon: ImageVector // Tambahkan properti ini
)

// Daftar mentah kategori (nomor dan nama)
private val rawCategories = listOf(
    "00-09" to "Sains dan Pengetahuan. Organisasi. Informasi.",
    "1-159.943" to "Filsafat dan Psikologi",
    "3-316" to "Ilmu Pengetahuan Sosial. Sosiologi. Kajian deskriptif masyarakat.",
    "32-328" to "Politik",
    "330" to "Ekonomika. Ilmu ekonomi",
    "330-335" to "Ilmu Ekonomi. Administrasi negara. Pemerintahan. Masalah kemiliteran",
    "336" to "Keuangan. Keuangan negara. Perbankan. Uang",
    "336.2-336.8" to "Keuangan. Keuangan negara. Perbankan. Uang",
    "338" to "Situasi ekonomi. Kebijakan ekonomi",
    "339" to "Perdagangan. Ekonomi dunia",
    "340-346" to "Ilmu Hukum, Perundang-undangan",
    "347" to "Ilmu Hukum, Perundang-undangan",
    "350-368" to "Administrasi negara. Pemerintahan. Asuransi.",
    "370-395.7" to "Pendidikan. Adat istiadat. Tradisi",
    "5-597.42" to "Ilmu Pengetahuan Alam",
    "600-618" to "Ilmu Terapan. Kedokteran. Teknologi",
    "62-629" to "Teknik. Rekayasa. Teknologi pada umumnya",
    "65-650" to "Industri komunikasi. Akuntansi. Manajemen bisnis. Hubungan masyarakat",
    "630-639" to "Pertanian dan ilmu-ilmu berkaitan serta teknik. Kehutanan. Bertani",
    "640-649.1" to "Manajemen Keluarga. Kesejahteraan keluarga",
    "650-656" to "Industri komunikasi. Akuntansi. Manajemen bisnis. Hubungan masyarakat",
    "657" to "Akuntansi",
    "658" to "Manajemen, administrasi bisnis. Organisasi komersial",
    "658.1-658.3" to "Keuangan. Hubungan Orang dalam perusahaan",
    "658.3-658.8" to "Manajemen kepegawaian. Pemasaran. Penjualan",
    "659-659.4" to "Hubungan masyarakat. Publisitas.",
    "663-691" to "Teknologi kimia, perdagangan dan kerajinan. Industri, seni.",
    "7-796.8" to "Seni. Rekreasi. Hiburan. Olahraga",
    "8-82-3" to "Bahasa. Linguistik. Sastra. Fiksi. Novel"
)

// Langkah 2: Buat fungsi untuk memilih ikon berdasarkan nomor kategori
private fun getIconForCategory(categoryNumber: String): ImageVector {
    return when {
        categoryNumber.startsWith("0") -> Icons.Default.Science
        categoryNumber.startsWith("1") -> Icons.Default.Psychology
        categoryNumber.startsWith("32") -> Icons.Default.Gavel
        categoryNumber.startsWith("33") || categoryNumber.startsWith("658.1") -> Icons.Default.MonetizationOn
        categoryNumber.startsWith("34") -> Icons.Default.Gavel
        categoryNumber.startsWith("35") || categoryNumber.startsWith("36") -> Icons.Default.AssuredWorkload
        categoryNumber.startsWith("37") -> Icons.Default.School
        categoryNumber.startsWith("5") -> Icons.Default.Eco
        categoryNumber.startsWith("60") || categoryNumber.startsWith("61") -> Icons.Default.Biotech
        categoryNumber.startsWith("62") -> Icons.Default.Engineering
        categoryNumber.startsWith("63") -> Icons.Default.Agriculture
        categoryNumber.startsWith("64") -> Icons.Default.FamilyRestroom
        categoryNumber.startsWith("65") || categoryNumber.startsWith("66") -> Icons.Default.BusinessCenter
        categoryNumber.startsWith("7") -> Icons.Default.Palette
        categoryNumber.startsWith("8") -> Icons.Default.HistoryEdu
        else -> Icons.Default.MenuBook // Ikon default
    }
}

// Langkah 3: Buat list final yang sudah memiliki ikon
val allCategories: List<LibraryCategory> = rawCategories.map { (number, name) ->
    LibraryCategory(
        number = number,
        name = name,
        icon = getIconForCategory(number)
    )
}