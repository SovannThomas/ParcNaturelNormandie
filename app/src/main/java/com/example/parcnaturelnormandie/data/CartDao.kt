package com.example.parcnaturelnormandie.data

import androidx.lifecycle.LiveData
import androidx.room.*

@Dao
interface CartDao {

    @Query("SELECT * FROM cart_items")
    suspend fun getAll(): List<CartItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(item: CartItemEntity)

    @Insert
    suspend fun insertAll(items: List<CartItemEntity>)

    @Delete
    suspend fun delete(item: CartItemEntity)

    @Query("DELETE FROM cart_items")
    suspend fun clear()

    @Query("SELECT * FROM cart_items")
    fun getAllLive(): LiveData<List<CartItemEntity>>

}