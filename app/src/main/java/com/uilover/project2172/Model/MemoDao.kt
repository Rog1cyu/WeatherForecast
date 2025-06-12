package com.uilover.project2172.Model

import androidx.room.*

@Dao
interface MemoDao {
    @Query("SELECT * FROM memos ORDER BY timestamp DESC")
    suspend fun getAll(): List<Memo>

    @Insert
    suspend fun insert(memo: Memo)

    @Delete
    suspend fun delete(memo: Memo)
}
