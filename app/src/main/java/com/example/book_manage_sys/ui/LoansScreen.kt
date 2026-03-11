package com.example.book_manage_sys.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.book_manage_sys.data.Borrow
import com.example.book_manage_sys.viewmodel.MainViewModel
import kotlinx.coroutines.delay
import java.text.SimpleDateFormat
import java.util.*

// ── Theme Colors ──────────────────────────────────────────────
private val BgColor       = Color(0xFFF0F4F2)
private val TealAccent    = Color(0xFF7ECEC4)
private val PurpleAccent  = Color(0xFF7E57C2)
private val AvailColor    = Color(0xFF4CAF50)
private val BorrowedColor = Color(0xFFE53935)

@Composable
fun LoansScreen(navController: NavController, viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val isAdmin = viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true
    var showStatusMenu by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserBorrows()
    }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {

        // ── Decorative background circles ──────────────────────
        Box(
            modifier = Modifier
                .size(220.dp)
                .offset(x = (-60).dp, y = (-60).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.18f))
        )
        Box(
            modifier = Modifier
                .size(160.dp)
                .align(Alignment.TopEnd)
                .offset(x = 50.dp, y = 30.dp)
                .clip(CircleShape)
                .background(PurpleAccent.copy(alpha = 0.10f))
        )
        Box(
            modifier = Modifier
                .size(120.dp)
                .align(Alignment.TopEnd)
                .offset(x = 20.dp, y = 140.dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.12f))
        )
        Box(
            modifier = Modifier
                .size(180.dp)
                .align(Alignment.BottomStart)
                .offset(x = (-50).dp, y = 50.dp)
                .clip(CircleShape)
                .background(PurpleAccent.copy(alpha = 0.08f))
        )
        Box(
            modifier = Modifier
                .size(100.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 30.dp, y = (-80).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.13f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp)
        ) {
            // ── Search Bar & Filter Button ─────────────────────────
            Row(verticalAlignment = Alignment.CenterVertically) {
                OutlinedTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("ค้นหารายการยืม...", color = Color(0xFF8A9B97)) },
                    leadingIcon = {
                        Icon(Icons.Default.Search, contentDescription = null, tint = TealAccent)
                    },
                    shape = RoundedCornerShape(14.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor   = Color.White,
                        unfocusedContainerColor = Color.White,
                        focusedBorderColor      = TealAccent,
                        unfocusedBorderColor    = Color(0xFFD6E4E1)
                    )
                )

                Spacer(modifier = Modifier.width(10.dp))

                Box {
                    IconButton(
                        onClick = { showStatusMenu = true },
                        modifier = Modifier
                            .clip(RoundedCornerShape(12.dp))
                            .background(
                                Brush.verticalGradient(listOf(TealAccent, Color(0xFF4DB6AC)))
                            )
                    ) {
                        Icon(Icons.Default.List, contentDescription = "Filter", tint = Color.White)
                    }

                    DropdownMenu(
                        expanded = showStatusMenu,
                        onDismissRequest = { showStatusMenu = false },
                        modifier = Modifier.background(Color.White, RoundedCornerShape(14.dp))
                    ) {
                        val statusOptions = listOf(
                            Pair("ทั้งหมด", null),
                            Pair("รอยืนยัน", "pending"),
                            Pair("ยืนยันการรับ", "picked_up"),
                            Pair("ยืนยันการคืน", "returned"),
                            Pair("ยังไม่คืน", "no_returned"),
                            Pair("ยกเลิกแล้ว", "cancel"),
                            Pair("เลยกำหนด", "forget")
                        )

                        statusOptions.forEach { (label, value) ->
                            DropdownMenuItem(
                                text = {
                                    Text(
                                        label,
                                        color = if (selectedStatusFilter == value) TealAccent
                                        else Color(0xFF2D3E3A),
                                        fontWeight = if (selectedStatusFilter == value) FontWeight.Bold
                                        else FontWeight.Normal
                                    )
                                },
                                onClick = {
                                    selectedStatusFilter = value
                                    showStatusMenu = false
                                }
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Accent bar
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(28.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.verticalGradient(
                                    listOf(TealAccent, Color(0xFF4DB6AC))
                                )
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = "รายการการยืมหนังสือ",
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp,
                            color = Color(0xFF1A2E2A)
                        )
                    }
                }

                // Count badge
                val totalCount = viewModel.userBorrows.filter {
                    (it.bookName?.contains(searchQuery, ignoreCase = true) == true ||
                            it.id.toString().contains(searchQuery)) &&
                            (selectedStatusFilter == null || it.pickupStatus == selectedStatusFilter)
                }.size
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TealAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, TealAccent.copy(alpha = 0.4f))
                ) {
                    Text(
                        text = "$totalCount รายการ",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealAccent
                    )
                }
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyColumn(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                val filteredBorrows = viewModel.userBorrows.filter {
                    (it.bookName?.contains(searchQuery, ignoreCase = true) == true ||
                            it.id.toString().contains(searchQuery)) &&
                            (selectedStatusFilter == null || it.pickupStatus == selectedStatusFilter)
                }

                items(
                    items = filteredBorrows,
                    key = { it.id }
                ) { borrow ->
                    LoanItem(borrow, isAdmin, viewModel)
                }
            }
        } // end Column
    } // end Box
}

@Composable
fun LoanItem(borrow: Borrow, isAdmin: Boolean, viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var countdownText by remember { mutableStateOf("") }
    var isOverdueState by remember { mutableStateOf(false) }

    val referenceDate = if (!borrow.updatedAt.isNullOrEmpty()) borrow.updatedAt else borrow.borrowDate

    LaunchedEffect(borrow.pickupStatus, referenceDate) {
        if (borrow.pickupStatus == "picked_up" || borrow.pickupStatus == "no_returned") {
            while (true) {
                if (borrow.pickupStatus == "no_returned") {
                    val diff = calculateTimeDiff(referenceDate, 0)
                    isOverdueState = true
                    countdownText = formatDiffTime(Math.abs(diff))
                } else {
                    val diff = calculateTimeDiff(referenceDate, 5)
                    isOverdueState = diff <= 0
                    countdownText = formatDiffTime(Math.abs(diff))
                    if (diff <= 0) {
                        viewModel.updateBorrowStatus(borrow.id, "no_returned")
                    }
                }
                delay(1000)
            }
        } else {
            countdownText = ""
            isOverdueState = false
        }
    }

    val statusColor = if (isOverdueState &&
        (borrow.pickupStatus == "picked_up" || borrow.pickupStatus == "no_returned")
    ) {
        BorrowedColor
    } else {
        when (borrow.pickupStatus) {
            "pending"          -> Color(0xFFFFA726)
            "picked_up"        -> AvailColor
            "returned"         -> Color(0xFF29B6F6)
            "cancel", "forget" -> Color(0xFFB0BEC5)
            "no_returned"      -> BorrowedColor
            else               -> Color.Black
        }
    }

    val statusText = when (borrow.pickupStatus) {
        "pending"     -> "รอยืนยัน"
        "picked_up"   -> "ยืนยันการรับ"
        "returned"    -> "ยืนยันการคืน"
        "cancel"      -> "ยกเลิกแล้ว"
        "forget"      -> "เลยกำหนด"
        "no_returned" -> "ยังไม่คืน"
        else          -> borrow.pickupStatus
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp),
        shape = RoundedCornerShape(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .background(statusColor)
        )

        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    // ID badge + book name
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(6.dp))
                                .background(TealAccent.copy(alpha = 0.15f))
                                .padding(horizontal = 7.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = "#${borrow.id}",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold,
                                color = TealAccent
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = borrow.bookName ?: "ไม่ทราบชื่อหนังสือ",
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = Color(0xFF1A2E2A)
                        )
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    val displayDate = referenceDate
                        ?.replace("T", " ")
                        ?.split(".")?.get(0)
                        ?.replace("Z", "") ?: ""
                    Text(
                        text = "อัปเดตล่าสุด: $displayDate",
                        color = Color(0xFF8A9B97),
                        fontSize = 12.sp
                    )

                    if (isAdmin) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = "User ID: ${borrow.userId}",
                            color = PurpleAccent,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    if (isAdmin) {
                        Box {
                            Surface(
                                onClick = { expanded = true },
                                shape = RoundedCornerShape(10.dp),
                                color = statusColor.copy(alpha = 0.12f),
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp, statusColor.copy(alpha = 0.5f)
                                )
                            ) {
                                Row(
                                    modifier = Modifier.padding(
                                        horizontal = 10.dp, vertical = 6.dp
                                    ),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(
                                        text = statusText,
                                        color = statusColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 13.sp
                                    )
                                    Icon(
                                        Icons.Default.ArrowDropDown,
                                        contentDescription = null,
                                        tint = statusColor,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                            }
                            DropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(
                                    Color.White, RoundedCornerShape(12.dp)
                                )
                            ) {
                                val options = listOf(
                                    Pair("รอยืนยัน", "pending"),
                                    Pair("ยืนยันการรับ", "picked_up"),
                                    Pair("ยืนยันการคืน", "returned"),
                                    Pair("ยังไม่คืน", "no_returned")
                                )
                                options.forEach { (label, value) ->
                                    DropdownMenuItem(
                                        text = { Text(label) },
                                        onClick = {
                                            expanded = false
                                            viewModel.updateBorrowStatus(borrow.id, value)
                                        }
                                    )
                                }
                            }
                        }
                    } else {
                        Surface(
                            shape = RoundedCornerShape(10.dp),
                            color = statusColor.copy(alpha = 0.12f),
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp, statusColor.copy(alpha = 0.4f)
                            )
                        ) {
                            Text(
                                text = statusText,
                                modifier = Modifier.padding(
                                    horizontal = 10.dp, vertical = 6.dp
                                ),
                                color = statusColor,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }
                }
            }

            if ((borrow.pickupStatus == "picked_up" || borrow.pickupStatus == "no_returned") &&
                countdownText.isNotEmpty()
            ) {
                Spacer(modifier = Modifier.height(10.dp))

                val labelText = if (borrow.pickupStatus == "no_returned") "ยังไม่คืนมาแล้ว"
                else if (isOverdueState) "เกินกำหนดมา"
                else "ต้องคืนในอีก"
                val textColor = if (isOverdueState || borrow.pickupStatus == "no_returned")
                    BorrowedColor else TealAccent

                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .background(textColor.copy(alpha = 0.10f))
                        .padding(horizontal = 10.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "$labelText  $countdownText",
                        color = textColor,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                    )
                }
            }
        }
    }
}

fun calculateTimeDiff(dateString: String?, daysLimit: Int): Long {
    if (dateString.isNullOrEmpty()) return 0
    val formats = listOf(
        "yyyy-MM-dd HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'",
        "yyyy-MM-dd'T'HH:mm:ss",
        "yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'"
    )
    for (format in formats) {
        try {
            val sdf = SimpleDateFormat(format, Locale.getDefault())
            sdf.timeZone = TimeZone.getTimeZone("UTC")
            val date = sdf.parse(dateString)
            if (date != null) {
                val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                calendar.time = date
                calendar.add(Calendar.DAY_OF_YEAR, daysLimit)
                val now = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                return calendar.timeInMillis - now.timeInMillis
            }
        } catch (e: Exception) { continue }
    }
    return 0
}

fun formatDiffTime(millis: Long): String {
    val seconds = millis / 1000
    val d = seconds / (24 * 3600)
    val h = (seconds % (24 * 3600)) / 3600
    val m = (seconds % 3600) / 60
    val s = seconds % 60
    return String.format("%02d:%02d:%02d:%02d", d, h, m, s)
}