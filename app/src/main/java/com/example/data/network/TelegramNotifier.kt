package com.example.data.network

import android.util.Log
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

object TelegramNotifier {
    private val client = OkHttpClient()
    private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()

    suspend fun sendMessage(token: String, chatId: String, text: String): Result<Boolean> = withContext(Dispatchers.IO) {
        val trimmedToken = token.trim()
        val trimmedChatId = chatId.trim()

        if (trimmedToken.isEmpty() || trimmedToken == "YOUR_TELEGRAM_BOT_TOKEN" || trimmedToken == "TELEGRAM_BOT_TOKEN") {
            return@withContext Result.failure(Exception("Telegram Bot token sozlanmagan. Iltimos, Sozlamalar bo'limida kiriting."))
        }
        if (trimmedChatId.isEmpty() || trimmedChatId == "YOUR_TELEGRAM_CHAT_ID" || trimmedChatId == "TELEGRAM_CHAT_ID") {
            return@withContext Result.failure(Exception("Telegram Chat ID sozlanmagan. Iltimos, Sozlamalar bo'limida kiriting."))
        }

        try {
            val url = "https://api.telegram.org/bot$trimmedToken/sendMessage"
            
            val jsonBody = JSONObject().apply {
                put("chat_id", trimmedChatId)
                put("text", text)
                put("parse_mode", "HTML")
            }

            val body = jsonBody.toString().toRequestBody(JSON_MEDIA_TYPE)
            val request = Request.Builder()
                .url(url)
                .post(body)
                .build()

            client.newCall(request).execute().use { response ->
                if (response.isSuccessful) {
                    Result.success(true)
                } else {
                    val errorResponse = response.body?.string() ?: ""
                    var errorMessage = "Telegram API xatosi (Kod: ${response.code})"
                    try {
                        val json = JSONObject(errorResponse)
                        if (json.has("description")) {
                            errorMessage = json.getString("description")
                        }
                    } catch (e: Exception) {
                        // Keep default
                    }
                    Log.e("TelegramNotifier", "Failed response: $errorResponse")
                    Result.failure(Exception(errorMessage))
                }
            }
        } catch (e: Exception) {
            Log.e("TelegramNotifier", "Network error sending telegram message", e)
            Result.failure(Exception("Tarmoq xatosi: ${e.localizedMessage ?: "ulanish imkoni bo'lmadi"}"))
        }
    }
}
