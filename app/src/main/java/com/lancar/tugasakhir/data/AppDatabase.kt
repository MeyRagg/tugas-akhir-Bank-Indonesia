package com.lancar.tugasakhir.data

import androidx.room.Database
import androidx.room.RoomDatabase
import com.lancar.tugasakhir.models.Book
import com.lancar.tugasakhir.models.NotificationHistory

@Database(
    entities = [Book::class, NotificationHistory::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun bookDao(): BookDao
    abstract fun notificationHistoryDao(): NotificationHistoryDao
}
