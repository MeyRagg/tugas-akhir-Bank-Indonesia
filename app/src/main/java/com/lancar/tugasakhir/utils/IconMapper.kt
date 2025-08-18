package com.lancar.tugasakhir.utils

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

fun mapIconNameToIcon(iconName: String): ImageVector {
    return when (iconName.lowercase()) {
        "science" -> Icons.Default.Science
        "psychology" -> Icons.Default.Psychology
        "politics" -> Icons.Default.Gavel
        "economy" -> Icons.Default.MonetizationOn
        "law" -> Icons.Default.Gavel
        "education" -> Icons.Default.School
        "nature" -> Icons.Default.Eco
        "medicine" -> Icons.Default.Biotech
        "engineering" -> Icons.Default.Engineering
        "agriculture" -> Icons.Default.Agriculture
        "family" -> Icons.Default.FamilyRestroom
        "business" -> Icons.Default.BusinessCenter
        "art" -> Icons.Default.Palette
        "literature" -> Icons.Default.HistoryEdu
        else -> Icons.Default.MenuBook
    }
}