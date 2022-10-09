package com.example.myweather.domain.room

import android.database.Cursor
import androidx.room.*

@Dao
interface HistoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    fun insert(entity: HistoryEntity)

    @Delete
    fun delete(entity: HistoryEntity)

    @Query("DELETE FROM history_table WHERE id=:id") // LIKE
    fun deleteByID(id:Long)

    @Update
    fun update(entity: HistoryEntity)

    @Query("SELECT * FROM history_table")
    fun getAll():List<HistoryEntity>

    @Query("SELECT * FROM history_table WHERE city=:city")
    fun getForCity(city:String):List<HistoryEntity>

    @Query("SELECT * FROM history_table WHERE id=:id")
    fun getHistoryCursor(id:Long): Cursor

    @Query("SELECT * FROM history_table")
    fun getHistoryCursor(): Cursor
}