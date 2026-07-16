package com.example.ui.screens

import android.widget.Toast
import androidx.compose.animation.*
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
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
import com.example.ui.viewmodel.BookingUiState
import com.example.ui.viewmodel.MainViewModel
import java.text.SimpleDateFormat
import java.util.*

data class ServiceItem(val name: String, val price: Long, val duration: String, val isStartingPrice: Boolean = false)
data class BarberItem(val name: String, val role: String, val iconRes: Int)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingScreen(viewModel: MainViewModel) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    // Data Sources
    val services = remember {
        listOf(
            ServiceItem("Soch olish (Kattalarga)", 60000, "30-40 daq", isStartingPrice = true),
            ServiceItem("Soch olish (Bolalarga)", 40000, "25-30 daq", isStartingPrice = true),
            ServiceItem("Yuzni chistka qilish", 150000, "40 daq"),
            ServiceItem("Soqol olish", 35000, "20 daq"),
            ServiceItem("Sochni ukladka qilish", 35000, "15 daq"),
            ServiceItem("Sochni bo'yash", 35000, "30 daq"),
            ServiceItem("Soqol bo'yash", 25000, "20 daq"),
            ServiceItem("Sochni kantovka qilish", 25000, "15 daq"),
            ServiceItem("Sochni yuvib qo'yish", 25000, "10 daq"),
            ServiceItem("Tarmoqlardagi prichoskalar (Insta, TikTok)", 125000, "45 daq"),
            ServiceItem("Kuyov pardoz", 435000, "90 daq")
        )
    }

    val barbers = remember {
        listOf(
            BarberItem("Usta Xolmurod", "Bosh sartosh (Master)", Icons.Default.Face.hashCode()),
            BarberItem("Usta Iskandar", "Professional Stilist", Icons.Default.Face.hashCode())
        )
    }

    val availableTimes = remember {
        listOf("09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00", "20:00")
    }

    // Dynamic dates for next 30 days (1 oy)
    val dates = remember {
        val list = mutableListOf<String>()
        val sdf = SimpleDateFormat("dd-MMMM (EEEE)", Locale("uz"))
        val cal = Calendar.getInstance()
        for (i in 0..29) {
            list.add(sdf.format(cal.time))
            cal.add(Calendar.DAY_OF_YEAR, 1)
        }
        list
    }

    // Observing states
    val clientName by viewModel.clientName.collectAsState()
    val clientPhone by viewModel.clientPhone.collectAsState()
    val selectedServiceIndex by viewModel.selectedServiceIndex.collectAsState()
    val selectedBarberIndex by viewModel.selectedBarberIndex.collectAsState()
    val bookingDate by viewModel.bookingDate.collectAsState()
    val bookingTime by viewModel.bookingTime.collectAsState()
    val bookingNote by viewModel.bookingNote.collectAsState()
    val bookingState by viewModel.bookingState.collectAsState()
    val isAdminLoggedIn by viewModel.isAdminLoggedIn.collectAsState()

    var showLoginDialog by remember { mutableStateOf(false) }
    var loginPhone by remember { mutableStateOf("") }
    var loginPin by remember { mutableStateOf("") }
    var loginError by remember { mutableStateOf("") }

    // Set default date if empty
    LaunchedEffect(Unit) {
        if (bookingDate.isEmpty() && dates.isNotEmpty()) {
            viewModel.bookingDate.value = dates[0]
        }
    }

    // Success / Error dialogs or Toasts
    LaunchedEffect(bookingState) {
        when (bookingState) {
            is BookingUiState.Success -> {
                Toast.makeText(context, (bookingState as BookingUiState.Success).msg, Toast.LENGTH_LONG).show()
                viewModel.clearBookingState()
            }
            is BookingUiState.Error -> {
                Toast.makeText(context, (bookingState as BookingUiState.Error).error, Toast.LENGTH_LONG).show()
                viewModel.clearBookingState()
            }
            else -> {}
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
        ) {
            // Hero Image Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.barber_shop_hero),
                    contentDescription = "Ustara Barber Shop Hero",
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Crop
                )
                // Dark Gradient overlay
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color.Transparent, SlateDark.copy(alpha = 0.95f)),
                                startY = 100f
                            )
                        )
                )
                // App Title & Admin Login Button overlays
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .align(Alignment.BottomStart)
                        .padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Default.ContentCut,
                                contentDescription = null,
                                tint = GoldPrimary,
                                modifier = Modifier.size(28.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "USTARA",
                                color = GoldPrimary,
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Black,
                                letterSpacing = 2.sp
                            )
                        }
                        Text(
                            text = "Barber Shop Online Qabul",
                            color = TextWhite,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }

                    // Admin icon button to log in/out
                    IconButton(
                        onClick = {
                            if (isAdminLoggedIn) {
                                viewModel.adminLogout()
                                Toast.makeText(context, "Admin panelidan chiqdingiz", Toast.LENGTH_SHORT).show()
                            } else {
                                loginPhone = ""
                                loginPin = ""
                                loginError = ""
                                showLoginDialog = true
                            }
                        },
                        modifier = Modifier
                            .size(44.dp)
                            .background(SlateCard.copy(alpha = 0.8f), RoundedCornerShape(22.dp))
                    ) {
                        Icon(
                            imageVector = if (isAdminLoggedIn) Icons.Default.LockOpen else Icons.Default.Lock,
                            contentDescription = "Admin login",
                            tint = if (isAdminLoggedIn) GoldPrimary else TextWhite,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // Service Choice
                Column {
                    Text(
                        text = "Xizmatni tanlang",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(8.dp)) {
                            services.forEachIndexed { index, service ->
                                val isSelected = index == selectedServiceIndex
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedCornerShape(8.dp))
                                        .background(if (isSelected) SlateLight else Color.Transparent)
                                        .clickable { viewModel.selectedServiceIndex.value = index }
                                        .padding(vertical = 10.dp, horizontal = 12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Row(
                                        modifier = Modifier.weight(1f),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        RadioButton(
                                            selected = isSelected,
                                            onClick = { viewModel.selectedServiceIndex.value = index },
                                            colors = RadioButtonDefaults.colors(selectedColor = GoldPrimary, unselectedColor = TextMuted)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = service.name,
                                                color = TextWhite,
                                                fontWeight = FontWeight.SemiBold,
                                                fontSize = 14.sp,
                                                maxLines = 2,
                                                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
                                            )
                                            Spacer(modifier = Modifier.height(2.dp))
                                            Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                                            ) {
                                                Icon(
                                                    imageVector = Icons.Default.AccessTime,
                                                    contentDescription = null,
                                                    tint = TextMuted,
                                                    modifier = Modifier.size(12.dp)
                                                )
                                                Text(
                                                    text = service.duration,
                                                    color = TextMuted,
                                                    fontSize = 12.sp
                                                )
                                            }
                                        }
                                    }
                                    Spacer(modifier = Modifier.width(16.dp))
                                    Column(
                                        horizontalAlignment = Alignment.End,
                                        modifier = Modifier.widthIn(min = 90.dp)
                                    ) {
                                        Text(
                                            text = String.format("%,d", service.price),
                                            color = GoldPrimary,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 16.sp,
                                            textAlign = TextAlign.End
                                        )
                                        Text(
                                            text = if (service.isStartingPrice) "so'mdan" else "so'm",
                                            color = TextMuted,
                                            fontSize = 11.sp,
                                            textAlign = TextAlign.End
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Barber Choice
                Column {
                    Text(
                        text = "Sartoshni tanlang",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                        contentPadding = PaddingValues(end = 16.dp)
                    ) {
                        itemsIndexed(barbers) { index, barber ->
                            val isSelected = index == selectedBarberIndex
                            Card(
                                modifier = Modifier
                                    .width(140.dp)
                                    .clickable { viewModel.selectedBarberIndex.value = index },
                                colors = CardDefaults.cardColors(
                                    containerColor = if (isSelected) SlateLight else SlateCard
                                ),
                                border = BorderStroke(1.5.dp, if (isSelected) GoldPrimary else Color.Transparent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Column(
                                    modifier = Modifier.padding(16.dp),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(56.dp)
                                            .clip(RoundedCornerShape(28.dp))
                                            .background(if (isSelected) GoldPrimary else SlateLight),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.Person,
                                            contentDescription = barber.name,
                                            tint = if (isSelected) SlateDark else GoldPrimary,
                                            modifier = Modifier.size(28.dp)
                                        )
                                    }
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Text(
                                        text = barber.name,
                                        color = TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp,
                                        textAlign = TextAlign.Center
                                    )
                                    Text(
                                        text = barber.role,
                                        color = TextMuted,
                                        fontSize = 11.sp,
                                        textAlign = TextAlign.Center
                                    )
                                }
                            }
                        }
                    }
                }

                // Date Picker (Horizontal Calendar)
                Column {
                    Text(
                        text = "Kunni tanlang",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        itemsIndexed(dates) { _, date ->
                            val isSelected = date == bookingDate
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(12.dp))
                                    .background(if (isSelected) GoldPrimary else SlateCard)
                                    .border(1.dp, if (isSelected) GoldPrimary else SlateLight, RoundedCornerShape(12.dp))
                                    .clickable { viewModel.bookingDate.value = date }
                                    .padding(horizontal = 16.dp, vertical = 12.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                val dateParts = date.split(" ")
                                val dayAndMonth = dateParts.firstOrNull() ?: ""
                                val weekday = dateParts.getOrNull(1)?.replace("(", "")?.replace(")", "") ?: ""

                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = dayAndMonth,
                                        color = if (isSelected) SlateDark else TextWhite,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 15.sp
                                    )
                                    Text(
                                        text = weekday,
                                        color = if (isSelected) SlateDark.copy(alpha = 0.8f) else TextMuted,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }
                    }
                }

                // Time Grid Selection
                Column {
                    Text(
                        text = "Soatni tanlang",
                        color = GoldPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    Card(
                        colors = CardDefaults.cardColors(containerColor = SlateCard),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            // Custom Composable Flow Grid Row for Time selectors
                            val rows = availableTimes.chunked(4)
                            rows.forEach { rowTimes ->
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    rowTimes.forEach { time ->
                                        val isSelected = time == bookingTime
                                        Box(
                                            modifier = Modifier
                                                .weight(1f)
                                                .height(44.dp)
                                                .clip(RoundedCornerShape(8.dp))
                                                .background(if (isSelected) GoldPrimary else SlateLight)
                                                .clickable { viewModel.bookingTime.value = time }
                                                .padding(4.dp),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text(
                                                text = time,
                                                color = if (isSelected) SlateDark else TextWhite,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 14.sp
                                            )
                                        }
                                    }
                                    // Fill up remaining slots for asymmetrical grids
                                    if (rowTimes.size < 4) {
                                        for (i in 0 until (4 - rowTimes.size)) {
                                            Spacer(modifier = Modifier.weight(1f))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                // Customer Information Card
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Mijoz ma'lumotlari",
                                color = GoldPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                            
                            // Log out/Chiqish button
                            TextButton(
                                onClick = {
                                    viewModel.logoutUser()
                                    Toast.makeText(context, "Tizimdan chiqdingiz", Toast.LENGTH_SHORT).show()
                                },
                                modifier = Modifier.testTag("logout_button"),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ExitToApp,
                                    contentDescription = "Chiqish",
                                    tint = Color.Red.copy(alpha = 0.85f),
                                    modifier = Modifier.size(18.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "Chiqish",
                                    color = Color.Red.copy(alpha = 0.85f),
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                            }
                        }

                        OutlinedTextField(
                            value = clientName,
                            onValueChange = { viewModel.clientName.value = it },
                            label = { Text("Ism-sharifingiz *", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SlateLight,
                                focusedLabelColor = GoldPrimary,
                                cursorColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null, tint = GoldPrimary) },
                            modifier = Modifier.fillMaxWidth().testTag("client_name_input"),
                            singleLine = true
                        )

                        OutlinedTextField(
                            value = clientPhone,
                            onValueChange = { viewModel.clientPhone.value = it },
                            label = { Text("Telefon raqamingiz *", color = TextMuted) },
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
                            modifier = Modifier.fillMaxWidth().testTag("client_phone_input"),
                            singleLine = true
                        )
                    }
                }

                // Optional Notes Field
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Ustaga qo'shimcha izoh (ixtiyoriy)",
                            color = GoldPrimary,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        OutlinedTextField(
                            value = bookingNote,
                            onValueChange = { viewModel.bookingNote.value = it },
                            placeholder = { Text("Masalan: Soch tarash uslubi yoki alohida xohishlar...", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SlateLight,
                                cursorColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            modifier = Modifier.fillMaxWidth().height(90.dp).testTag("booking_note_input")
                        )
                    }
                }

                // Submit Button
                Button(
                    onClick = {
                        val currentService = services[selectedServiceIndex]
                        viewModel.bookAppointment(
                            name = clientName,
                            phone = clientPhone,
                            service = currentService.name,
                            price = currentService.price,
                            barber = barbers[selectedBarberIndex].name,
                            date = bookingDate,
                            time = bookingTime,
                            note = bookingNote
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("submit_booking_button"),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = GoldPrimary,
                        contentColor = SlateDark
                    ),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Icon(Icons.Default.Check, contentDescription = null)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "Ariza topshirish (Online)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                }

                // Spacer at bottom
                Spacer(modifier = Modifier.height(80.dp))
            }
        }

        // Loading HUD Layer
        if (bookingState is BookingUiState.Loading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.7f)),
                contentAlignment = Alignment.Center
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = SlateCard),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        CircularProgressIndicator(color = GoldPrimary)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "Buyurtma yuborilmoqda...",
                            color = TextWhite,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = "Telegram botga ulanyapti...",
                            color = TextMuted,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
                }
            }
        }
        // Admin Login Dialog
        if (showLoginDialog) {
            AlertDialog(
                onDismissRequest = { showLoginDialog = false },
                containerColor = SlateCard,
                titleContentColor = GoldPrimary,
                title = {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(Icons.Default.AdminPanelSettings, contentDescription = null, tint = GoldPrimary)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Admin Kirish", fontWeight = FontWeight.Bold, fontSize = 20.sp)
                    }
                },
                text = {
                    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                        Text(
                            "Sozlamalar va navbatlarni ko'rish uchun telefon raqamingiz va PIN kodni kiriting.",
                            color = TextWhite,
                            fontSize = 14.sp
                        )
                        
                        OutlinedTextField(
                            value = loginPhone,
                            onValueChange = { loginPhone = it },
                            label = { Text("Telefon raqami", color = TextMuted) },
                            placeholder = { Text("Masalan: +998931182275", color = TextMuted) },
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SlateLight,
                                focusedLabelColor = GoldPrimary,
                                cursorColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        OutlinedTextField(
                            value = loginPin,
                            onValueChange = { loginPin = it },
                            label = { Text("PIN kod", color = TextMuted) },
                            placeholder = { Text("1234", color = TextMuted) },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = GoldPrimary,
                                unfocusedBorderColor = SlateLight,
                                focusedLabelColor = GoldPrimary,
                                cursorColor = GoldPrimary,
                                focusedTextColor = TextWhite,
                                unfocusedTextColor = TextWhite
                            ),
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth()
                        )

                        if (loginError.isNotEmpty()) {
                            Text(
                                text = loginError,
                                color = Color.Red,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            if (loginPin != "1234") {
                                loginError = "Xato PIN kod! (Tavsiya etilgan: 1234)"
                            } else if (viewModel.tryAdminLogin(loginPhone)) {
                                showLoginDialog = false
                                Toast.makeText(context, "Muvaffaqiyatli kirdingiz!", Toast.LENGTH_SHORT).show()
                            } else {
                                loginError = "Ruxsat etilmagan telefon raqami!"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = GoldPrimary, contentColor = SlateDark)
                    ) {
                        Text("Kirish", fontWeight = FontWeight.Bold)
                    }
                },
                dismissButton = {
                    TextButton(onClick = { showLoginDialog = false }) {
                        Text("Bekor qilish", color = TextMuted)
                    }
                }
            )
        }
    }
}
