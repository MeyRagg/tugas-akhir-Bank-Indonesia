// models/NotificationHistory.kt  (punya kamu sudah ada, pastikan sama)
package com.lancar.tugasakhir.models

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notification_history")
data class NotificationHistory(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val body: String,
    val deepLink: String? = null,
    val is_read: Boolean = false,
    val created_at: Long = System.currentTimeMillis()
)
