package com.example.ui.screens

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.theme.*
import com.example.ui.viewmodel.MainViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OnboardingScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    var name by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("+998") }
    var errorText by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(SlateDark)
    ) {
        // Aesthetic Dark/Gold background overlay or hero
        Image(
            painter = painterResource(id = R.drawable.barber_shop_hero), // Corrected resource name
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f),
            alpha = 0.35f
        )

        // Gradient overlay
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.42f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, SlateDark)
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Spacer(modifier = Modifier.height(20.dp))

            // Branding Section
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(80.dp)
                        .background(SlateCard, RoundedCornerShape(24.dp))
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCut,
                        contentDescription = null,
                        tint = GoldPrimary,
                        modifier = Modifier.size(44.dp)
                    )
                }

                Text(
                    text = "USTARA",
                    color = GoldPrimary,
                    fontSize = 36.sp,
                    fontWeight = FontWeight.Black,
                    letterSpacing = 4.sp
                )

                Text(
                    text = "Barber Shop Online Qabul",
                    color = TextWhite,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(40.dp))

            // Registration/Login Card
            Card(
                colors = CardDefaults.cardColors(containerColor = SlateCard),
                shape = RoundedCornerShape(24.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f, fill = false)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Ilovaga Kirish",
                        color = GoldPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Text(
                        text = "Buyurtmalar berish, usta va vaqtni tanlash uchun ism hamda telefon raqamingizni kiriting.",
                        color = TextMuted,
                        fontSize = 14.sp
                    )

                    OutlinedTextField(
                        value = name,
                        onValueChange = { 
                            name = it
                            errorText = ""
                        },
                        label = { Text("Ism-sharifingiz", color = TextMuted) },
                        placeholder = { Text("Masalan: Azamat", color = TextMuted) },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SlateLight,
                            focusedLabelColor = GoldPrimary,
                            cursorColor = GoldPrimary,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboard_name_input"),
                        singleLine = true
                    )

                    OutlinedTextField(
                        value = phone,
                        onValueChange = { 
                            phone = it
                            errorText = ""
                        },
                        label = { Text("Telefon raqamingiz", color = TextMuted) },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = GoldPrimary,
                            unfocusedBorderColor = SlateLight,
                            focusedLabelColor = GoldPrimary,
                            cursorColor = GoldPrimary,
                            focusedTextColor = TextWhite,
                            unfocusedTextColor = TextWhite
                        ),
                        leadingIcon = { Icon(Icons.Default.Phone, contentDescription = null, tint = GoldPrimary) },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("onboard_phone_input"),
                        singleLine = true
                    )

                    if (errorText.isNotEmpty()) {
                        Text(
                            text = errorText,
                            color = Color.Red,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Button(
                        onClick = {
                            val cleanPhone = phone.replace("+", "").replace(" ", "").trim()
                            if (name.trim().length < 3) {
                                errorText = "Iltimos, ismingizni to'liq kiriting (kamida 3 ta harf)!"
                            } else if (cleanPhone.length < 9) {
                                errorText = "Iltimos, telefon raqamingizni to'g'ri kiriting!"
                            } else {
                                // Successful validation
                                val isThreeAdmins = cleanPhone == "998931182275" || cleanPhone == "931182275" ||
                                        cleanPhone == "998974849098" || cleanPhone == "974849098" ||
                                        cleanPhone == "998884396666" || cleanPhone == "884396666"

                                viewModel.onboardUser(name, phone)
                                
                                if (isThreeAdmins) {
                                    Toast.makeText(context, "Hush kelibsiz Admin / Sartosh! Barcha panellar ochiq.", Toast.LENGTH_LONG).show()
                                } else {
                                    Toast.makeText(context, "Muvaffaqiyatli kirdingiz!", Toast.LENGTH_SHORT).show()
                                }
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = GoldPrimary,
                            contentColor = SlateDark
                        ),
                        shape = RoundedCornerShape(14.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp)
                            .testTag("onboard_submit_button")
                    ) {
                        Text(
                            text = "KIRISH",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            letterSpacing = 1.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Footer Text
            Text(
                text = "© Ustara Premium Barber Shop v1.2",
                color = TextMuted,
                fontSize = 12.sp,
                textAlign = TextAlign.Center,
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
