package com.lancar.tugasakhir.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.lancar.tugasakhir.models.Book
import kotlinx.coroutines.flow.Flow

@Dao
interface BookDao {
    @Query("SELECT * FROM books WHERE (:categoryName IS NULL OR category = :categoryName)")
    fun getBooksByCategory(categoryName: String?): Flow<List<Book>>

    @Query("SELECT * FROM books WHERE barcode = :barcode LIMIT 1")
    suspend fun findByBarcode(barcode: String): Book?

    @Query("DELETE FROM books WHERE category = :categoryName")
    suspend fun deleteAllByCategory(categoryName: String)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(books: List<Book>)

    @Query("SELECT * FROM books LIMIT 5")
    fun getFeaturedBooks(): Flow<List<Book>>
}
