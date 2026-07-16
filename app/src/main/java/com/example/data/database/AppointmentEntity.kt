package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "appointments")
data class AppointmentEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val clientName: String,
    val clientPhone: String,
    val serviceName: String,
    val price: Long,
    val barberName: String,
    val dateTime: String,
    val note: String = "",
    val status: String = "Kutilmoqda", // "Kutilmoqda" (Pending), "Tasdiqlandi" (Confirmed)
    val isSynced: Boolean = false,     // True if Telegram notification sent successfully
    val createdAt: Long = System.currentTimeMillis()
)
