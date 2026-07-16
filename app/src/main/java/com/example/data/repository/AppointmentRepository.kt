package com.example.data.repository

import com.example.data.database.AppointmentDao
import com.example.data.database.AppointmentEntity
import com.example.data.network.TelegramNotifier
import kotlinx.coroutines.flow.Flow
import java.text.NumberFormat
import java.util.Locale

class AppointmentRepository(private val appointmentDao: AppointmentDao) {

    val allAppointments: Flow<List<AppointmentEntity>> = appointmentDao.getAllAppointments()

    suspend fun getAppointmentById(id: Int): AppointmentEntity? {
        return appointmentDao.getAppointmentById(id)
    }

    suspend fun insert(appointment: AppointmentEntity): Long {
        return appointmentDao.insertAppointment(appointment)
    }

    suspend fun delete(appointment: AppointmentEntity) {
        appointmentDao.deleteAppointment(appointment)
    }

    suspend fun updateStatus(id: Int, status: String) {
        appointmentDao.updateStatus(id, status)
    }

    suspend fun syncAppointmentToTelegram(
        appointmentId: Int,
        botToken: String,
        chatId: String
    ): Result<Boolean> {
        val appointment = getAppointmentById(appointmentId) 
            ?: return Result.failure(Exception("Buyurtma topilmadi ($appointmentId)"))

        val formattedPrice = NumberFormat.getIntegerInstance(Locale.US).format(appointment.price) + " so'm"
        val noteSection = if (appointment.note.isNotBlank()) {
            "\n📝 <b>Izoh:</b> ${appointment.note}"
        } else ""

        val textMessage = """
            💈 <b>YANGI BUYURTMA - USTARA</b> 💈
            
            👤 <b>Mijoz:</b> ${appointment.clientName}
            📞 <b>Telefon:</b> <a href="tel:${appointment.clientPhone}">${appointment.clientPhone}</a>
            ✂️ <b>Xizmat:</b> ${appointment.serviceName}
            💰 <b>Narxi:</b> $formattedPrice
            🧔 <b>Sartosh:</b> ${appointment.barberName}
            📅 <b>Sana va Vaqt:</b> ${appointment.dateTime}
            $noteSection
            
            ⚡ <i>Mijoz ilova orqali online buyurtma qoldirdi.</i>
        """.trimIndent()

        // Smart routing targets
        val targets = mutableSetOf<String>()
        
        // Add default admin 1 (shunchaki admin)
        targets.add("615548145")
        
        // Add user-configured ID if customized
        if (chatId.isNotBlank()) {
            targets.add(chatId.trim())
        }

        // Add specific barber IDs based on selection
        if (appointment.barberName.contains("Xolmurod", ignoreCase = true)) {
            targets.add("7865645872") // Usta Xolmurod
        } else if (appointment.barberName.contains("Iskandar", ignoreCase = true)) {
            targets.add("6998381129") // Usta Iskandar
        }

        var atLeastOneSuccess = false
        var lastError: Throwable? = null

        targets.forEach { targetId ->
            val result = TelegramNotifier.sendMessage(botToken, targetId, textMessage)
            if (result.isSuccess) {
                atLeastOneSuccess = true
            } else {
                lastError = result.exceptionOrNull()
            }
        }

        if (atLeastOneSuccess) {
            appointmentDao.updateSyncStatus(appointmentId, true)
            return Result.success(true)
        } else {
            return Result.failure(lastError ?: Exception("SMS xabarlarini Telegramga yuborib bo'lmadi."))
        }
    }
}
