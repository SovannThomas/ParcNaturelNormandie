package com.example.parcnaturelnormandie.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItemEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val duration: String,
    val price: String,
    val dateTimeMillis: Long // on stocke la date en millis, plus simple au début
)