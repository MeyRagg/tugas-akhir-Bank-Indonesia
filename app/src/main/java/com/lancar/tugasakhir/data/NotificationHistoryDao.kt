package com.lancar.tugasakhir.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lancar.tugasakhir.models.NotificationHistory
import kotlinx.coroutines.flow.Flow

@Dao
interface NotificationHistoryDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: NotificationHistory)

    @Query("SELECT * FROM notification_history ORDER BY created_at DESC")
    fun getAll(): Flow<List<NotificationHistory>>

    @Query("UPDATE notification_history SET is_read = 1 WHERE is_read = 0")
    suspend fun markAllAsRead()
}
