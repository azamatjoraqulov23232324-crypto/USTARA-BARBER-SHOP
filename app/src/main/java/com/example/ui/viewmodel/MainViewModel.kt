package com.example.ui.viewmodel

import android.app.Application
import android.content.Context
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.BuildConfig
import com.example.data.database.AppDatabase
import com.example.data.database.AppointmentEntity
import com.example.data.repository.AppointmentRepository
import com.example.data.network.TelegramNotifier
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

sealed interface BookingUiState {
    object Idle : BookingUiState
    object Loading : BookingUiState
    data class Success(val msg: String) : BookingUiState
    data class Error(val error: String) : BookingUiState
}

class MainViewModel(application: Application) : AndroidViewModel(application) {

    private val sharedPrefs = application.getSharedPreferences("ustara_prefs", Context.MODE_PRIVATE)
    private val database = AppDatabase.getDatabase(application)
    private val repository = AppointmentRepository(database.appointmentDao())

    // UI Navigation
    private val _currentTab = MutableStateFlow(0) // 0: Booking, 1: Bookings, 2: Settings
    val currentTab = _currentTab.asStateFlow()

    fun setTab(index: Int) {
        _currentTab.value = index
    }

    // Admin State
    private val _isAdminLoggedIn = MutableStateFlow(false)
    val isAdminLoggedIn = _isAdminLoggedIn.asStateFlow()

    // Onboarding (First-time launch) State
    private val _isOnboarded = MutableStateFlow(false)
    val isOnboarded = _isOnboarded.asStateFlow()

    fun tryAdminLogin(phone: String): Boolean {
        val cleanPhone = phone.replace("+", "").replace(" ", "").trim()
        val isValidAdmin = cleanPhone == "998931182275" || cleanPhone == "931182275" ||
                cleanPhone == "998974849098" || cleanPhone == "974849098" ||
                cleanPhone == "998884396666" || cleanPhone == "884396666"

        if (isValidAdmin) {
            _isAdminLoggedIn.value = true
            sharedPrefs.edit().putBoolean("is_admin_logged_in", true).apply()
            return true
        }
        return false
    }

    fun onboardUser(name: String, phone: String) {
        val cleanPhone = phone.replace("+", "").replace(" ", "").trim()
        val isAdmin = cleanPhone == "998931182275" || cleanPhone == "931182275" ||
                cleanPhone == "998974849098" || cleanPhone == "974849098" ||
                cleanPhone == "998884396666" || cleanPhone == "884396666"

        sharedPrefs.edit().apply {
            putBoolean("is_onboarded", true)
            putString("saved_client_name", name.trim())
            putString("saved_client_phone", phone.trim())
            putBoolean("is_admin_logged_in", isAdmin)
            apply()
        }

        clientName.value = name.trim()
        clientPhone.value = phone.trim()
        _isAdminLoggedIn.value = isAdmin
        _isOnboarded.value = true
    }

    fun adminLogout() {
        _isAdminLoggedIn.value = false
        sharedPrefs.edit().putBoolean("is_admin_logged_in", false).apply()
        _currentTab.value = 0 // Go back to booking tab on logout
    }

    fun logoutUser() {
        _isOnboarded.value = false
        _isAdminLoggedIn.value = false
        clientName.value = ""
        clientPhone.value = "+998"
        sharedPrefs.edit().apply {
            putBoolean("is_onboarded", false)
            putBoolean("is_admin_logged_in", false)
            putString("saved_client_name", "")
            putString("saved_client_phone", "+998")
            apply()
        }
        _currentTab.value = 0 // Go back to booking tab on logout
    }

    // Settings State
    private val _botToken = MutableStateFlow("")
    val botToken = _botToken.asStateFlow()

    private val _chatId = MutableStateFlow("")
    val chatId = _chatId.asStateFlow()

    // Form inputs state
    var clientName = MutableStateFlow("")
    var clientPhone = MutableStateFlow("+998")
    var selectedServiceIndex = MutableStateFlow(0)
    var selectedBarberIndex = MutableStateFlow(0)
    var bookingDate = MutableStateFlow("")
    var bookingTime = MutableStateFlow("")
    var bookingNote = MutableStateFlow("")

    // Statuses
    private val _bookingState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val bookingState = _bookingState.asStateFlow()

    private val _testState = MutableStateFlow<BookingUiState>(BookingUiState.Idle)
    val testState = _testState.asStateFlow()

    // Observable Appointmens
    val appointments: StateFlow<List<AppointmentEntity>> = repository.allAppointments
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    init {
        // Load settings from SharedPrefs, default to BuildConfig if empty
        val savedToken = sharedPrefs.getString("tg_bot_token", "") ?: ""
        val savedChatId = sharedPrefs.getString("tg_chat_id", "") ?: ""

        val finalToken = savedToken.ifBlank {
            "8623101407:AAHAyF3Q81dXW9b1ynQtD1AonqKffvQcH1M"
        }.removePrefix("YOUR_TELEGRAM_BOT_TOKEN") // Clear placeholder if generated

        val finalChatId = savedChatId.ifBlank {
            "615548145"
        }.removePrefix("YOUR_TELEGRAM_CHAT_ID") // Clear placeholder if generated

        _botToken.value = finalToken
        _chatId.value = finalChatId
        
        // Load onboarding state
        val savedIsOnboarded = sharedPrefs.getBoolean("is_onboarded", false)
        val savedClientName = sharedPrefs.getString("saved_client_name", "") ?: ""
        val savedClientPhone = sharedPrefs.getString("saved_client_phone", "") ?: ""

        var isAdmin = false
        if (savedIsOnboarded) {
            _isOnboarded.value = true
            clientName.value = savedClientName
            clientPhone.value = savedClientPhone
            
            // If phone corresponds to admin, keep admin state aligned
            val cleanPhone = savedClientPhone.replace("+", "").replace(" ", "").trim()
            isAdmin = cleanPhone == "998931182275" || cleanPhone == "931182275" ||
                    cleanPhone == "998974849098" || cleanPhone == "974849098" ||
                    cleanPhone == "998884396666" || cleanPhone == "884396666"
        }
        
        sharedPrefs.edit().putBoolean("is_admin_logged_in", isAdmin).apply()
        _isAdminLoggedIn.value = isAdmin
    }

    fun saveTelegramSettings(token: String, id: String) {
        _botToken.value = token.trim()
        _chatId.value = id.trim()
        sharedPrefs.edit().apply {
            putString("tg_bot_token", token.trim())
            putString("tg_chat_id", id.trim())
            apply()
        }
    }

    fun clearBookingState() {
        _bookingState.value = BookingUiState.Idle
    }

    fun clearTestState() {
        _testState.value = BookingUiState.Idle
    }

    // Reset Booking form
    fun resetForm() {
        clientName.value = ""
        clientPhone.value = "+998"
        selectedServiceIndex.value = 0
        selectedBarberIndex.value = 0
        bookingDate.value = ""
        bookingTime.value = ""
        bookingNote.value = ""
    }

    fun bookAppointment(
        name: String,
        phone: String,
        service: String,
        price: Long,
        barber: String,
        date: String,
        time: String,
        note: String
    ) {
        if (name.isBlank() || phone.isBlank() || date.isBlank() || time.isBlank()) {
            _bookingState.value = BookingUiState.Error("Iltimos, barcha majburiy maydonlarni to'ldiring!")
            return
        }

        viewModelScope.launch {
            _bookingState.value = BookingUiState.Loading
            try {
                val entity = AppointmentEntity(
                    clientName = name.trim(),
                    clientPhone = phone.trim(),
                    serviceName = service,
                    price = price,
                    barberName = barber,
                    dateTime = "$date, soat $time",
                    note = note.trim()
                )

                // Save to local database first
                val newId = repository.insert(entity)

                // Try to send to Telegram
                val token = _botToken.value
                val chat = _chatId.value

                if (token.isBlank() || chat.isBlank()) {
                    _bookingState.value = BookingUiState.Success("Buyurtma qabul qilindi (Local), lekin Telegram bot sozlanmaganligi sababli xabar yuborilmadi.")
                    return@launch
                }

                val syncResult = repository.syncAppointmentToTelegram(newId.toInt(), token, chat)
                if (syncResult.isSuccess) {
                    _bookingState.value = BookingUiState.Success("Buyurtma muvaffaqiyatli qabul qilindi va Telegram botga yuborildi!")
                    resetForm()
                } else {
                    val errorMsg = syncResult.exceptionOrNull()?.message ?: "Nomalum Telegram API xatosi"
                    _bookingState.value = BookingUiState.Success("Buyurtma qabul qilindi (Local), lekin Telegramga yuborishda xatolik yuz berdi: $errorMsg")
                }
            } catch (e: Exception) {
                _bookingState.value = BookingUiState.Error("Saqlashda xatolik yuz berdi: ${e.localizedMessage}")
            }
        }
    }

    fun resyncAppointment(id: Int) {
        viewModelScope.launch {
            val token = _botToken.value
            val chat = _chatId.value
            if (token.isBlank() || chat.isBlank()) {
                // Cannot sync without credentials
                return@launch
            }
            repository.syncAppointmentToTelegram(id, token, chat)
        }
    }

    fun deleteAppointment(appointment: AppointmentEntity) {
        viewModelScope.launch {
            repository.delete(appointment)
        }
    }

    fun testTelegramConnection(token: String, chat: String) {
        if (token.isBlank() || chat.isBlank()) {
            _testState.value = BookingUiState.Error("Token va Chat ID kiritilishi shart!")
            return
        }

        viewModelScope.launch {
            _testState.value = BookingUiState.Loading
            val testMsg = "🔔 <b>Ustara Barber Shop</b>\n\nTelegram aloqasi muvaffaqiyatli o'rnatildi! 🎉"
            val result = TelegramNotifier.sendMessage(token, chat, testMsg)
            if (result.isSuccess) {
                _testState.value = BookingUiState.Success("Ulanish muvaffaqiyatli! Botdan sinov xabari yuborildi.")
            } else {
                val error = result.exceptionOrNull()?.message ?: "Xatolik"
                _testState.value = BookingUiState.Error("Ulanib bo'lmadi: $error")
            }
        }
    }
}
