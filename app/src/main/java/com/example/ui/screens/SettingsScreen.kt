package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*
import com.example.ui.viewmodel.BookingUiState
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val currentBotToken by viewModel.botToken.collectAsState()
    val currentChatId by viewModel.chatId.collectAsState()
    val testState by viewModel.testState.collectAsState()

    var tokenInput by remember { mutableStateOf(currentBotToken) }
    var chatIdInput by remember { mutableStateOf(currentChatId) }
    var hideToken by remember { mutableStateOf(true) }

    // Sync input states if viewmodel loads later
    LaunchedEffect(currentBotToken, currentChatId) {
        tokenInput = currentBotToken
        chatIdInput = currentChatId
    }

    // Observing tests and notifying
    LaunchedEffect(testState) {
        when (testState) {
            is BookingUiState.Success -> {
                Toast.makeText(context, (testState as BookingUiState.Success).msg, Toast.LENGTH_LONG).show()
                viewModel.clearTestState()
            }
            is BookingUiState.Error -> {
                Toast.makeText(context, (testState as BookingUiState.Error).error, Toast.LENGTH_LONG).show()
                viewModel.clearTestState()
            }
            else -> {}
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Title block
        Column {
            Text(
                text = "Telegram Sozlamalari",
                color = GoldPrimary,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = "Online arizalar boradigan Telegram Bot integratsiyasi",
                color = TextMuted,
                fontSize = 13.sp
            )
        }

        // Credentials form card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text(
                    text = "Bot va Chat parametrlari",
                    color = GoldPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = tokenInput,
                    onValueChange = { tokenInput = it },
                    label = { Text("Telegram Bot Token", color = TextMuted) },
                    visualTransformation = if (hideToken) PasswordVisualTransformation() else VisualTransformation.None,
                    trailingIcon = {
                        IconButton(onClick = { hideToken = !hideToken }) {
                            Icon(
                                imageVector = if (hideToken) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                contentDescription = "Tokenni ko'rsatish/yashirish",
                                tint = GoldPrimary
                            )
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SlateLight,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = GoldPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("telegram_token_input"),
                    singleLine = true
                )

                OutlinedTextField(
                    value = chatIdInput,
                    onValueChange = { chatIdInput = it },
                    label = { Text("Telegram Chat ID / Group ID", color = TextMuted) },
                    placeholder = { Text("Masalan: -10023456789", color = TextMuted) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = GoldPrimary,
                        unfocusedBorderColor = SlateLight,
                        focusedTextColor = TextWhite,
                        unfocusedTextColor = TextWhite,
                        focusedLabelColor = GoldPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("telegram_chat_id_input"),
                    singleLine = true
                )

                // Save button
                Button(
                    onClick = {
                        viewModel.saveTelegramSettings(tokenInput, chatIdInput)
                        Toast.makeText(context, "Sozlamalar saqlandi!", Toast.LENGTH_SHORT).show()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .testTag("save_settings_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = SlateDark
                    ),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Icon(Icons.Default.Save, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Sozlamalarni saqlash", fontWeight = FontWeight.Bold)
                }

                // Divider and test section
                Divider(color = SlateLight, thickness = 1.dp, modifier = Modifier.padding(vertical = 4.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    // Test button
                    Button(
                        onClick = {
                            viewModel.testTelegramConnection(tokenInput, chatIdInput)
                        },
                        modifier = Modifier
                            .weight(1f)
                            .height(44.dp)
                            .testTag("test_telegram_button"),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = SlateLight,
                            contentColor = TextWhite
                        ),
                        shape = RoundedCornerShape(10.dp),
                        enabled = testState !is BookingUiState.Loading
                    ) {
                        if (testState is BookingUiState.Loading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = GoldPrimary, strokeWidth = 2.dp)
                        } else {
                            Icon(Icons.Default.Send, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Ulanishni sinash", fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                        }
                    }
                }
            }
        }

        // Guide / Tutorial card
        Card(
            colors = CardDefaults.cardColors(containerColor = SlateCard),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(Icons.Default.HelpOutline, contentDescription = null, tint = GoldPrimary, modifier = Modifier.size(22.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Botni sozlash bo'yicha qo'llanma",
                        color = GoldPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Divider(color = SlateLight)

                GuideStep(
                    stepNum = "1",
                    text = "Telegramda @BotFather ga kiring va /newbot komandasini yuborib yangi bot yarating."
                )

                GuideStep(
                    stepNum = "2",
                    text = "Bot yaratilgach, BotFather bergan API tokenini nusxalab yuqoridagi 'Telegram Bot Token' maydoniga kiriting."
                )

                GuideStep(
                    stepNum = "3",
                    text = "Guruh yoki kanal ochib, yaratgan botingizni unga a'zo qiling va guruhga xabar yuborish ruxsatini berish uchun uni Admin qiling."
                )

                GuideStep(
                    stepNum = "4",
                    text = "Guruh ID sini olish uchun guruhga istalgan xabarni yozib, uni @userinfobot ga yo'naltiring. Chiqqan 'Id' qiymatini nusxalab 'Telegram Chat ID' maydoniga kiriting (guruh ID lari odatda minus '-' belgisi bilan boshlanadi)."
                )

                GuideStep(
                    stepNum = "5",
                    text = "Hamma ma'lumotlarni kiritgach, 'Sozlamalarni saqlash' tugmasini bosing va 'Ulanishni sinash' orqali tekshirib ko'ring!"
                )
            }
        }

        Spacer(modifier = Modifier.height(80.dp))
    }
}

@Composable
fun GuideStep(stepNum: String, text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalAlignment = Alignment.Top
    ) {
        Box(
            modifier = Modifier
                .size(24.dp)
                .clip(RoundedCornerShape(12.dp))
                .background(GoldPrimary),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = stepNum,
                color = SlateDark,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Text(
            text = text,
            color = TextWhite,
            fontSize = 13.sp,
            lineHeight = 18.sp,
            modifier = Modifier.weight(1f)
        )
    }
}
