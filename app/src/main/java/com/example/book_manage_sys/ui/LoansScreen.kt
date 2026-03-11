package com.example.book_manage_sys.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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

@Composable
fun LoansScreen(navController: NavController, viewModel: MainViewModel) {
    var searchQuery by remember { mutableStateOf("") }
    val isAdmin = viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true
    var showStatusMenu by remember { mutableStateOf(false) }
    var selectedStatusFilter by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchUserBorrows()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // Search Bar & Filter Button
        Row(verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("ค้นหารายการยืม...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color(0xFFF3E5F5),
                    unfocusedContainerColor = Color(0xFFF3E5F5),
                    focusedBorderColor = Color.Transparent,
                    unfocusedBorderColor = Color.Transparent
                )
            )
            
            Spacer(modifier = Modifier.width(8.dp))
            
            Box {
                IconButton(
                    onClick = { showStatusMenu = true },
                    modifier = Modifier.background(Color(0xFF7E57C2), RoundedCornerShape(8.dp))
                ) {
                    Icon(imageVector = Icons.Default.List, contentDescription = "Filter", tint = Color.White)
                }
                
                DropdownMenu(
                    expanded = showStatusMenu,
                    onDismissRequest = { showStatusMenu = false }
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
                            text = { Text(label) },
                            onClick = {
                                selectedStatusFilter = value
                                showStatusMenu = false
                            }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
        Text(text = "รายการการยืมหนังสือ", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            val filteredBorrows = viewModel.userBorrows.filter {
                (it.bookName?.contains(searchQuery, ignoreCase = true) == true || it.id.toString().contains(searchQuery)) &&
                (selectedStatusFilter == null || it.pickupStatus == selectedStatusFilter)
            }
            
            items(
                items = filteredBorrows,
                key = { it.id }
            ) { borrow ->
                LoanItem(borrow, isAdmin, viewModel)
            }
        }
    }
}

@Composable
fun LoanItem(borrow: Borrow, isAdmin: Boolean, viewModel: MainViewModel) {
    var expanded by remember { mutableStateOf(false) }
    var countdownText by remember { mutableStateOf("") }
    var isOverdueState by remember { mutableStateOf(false) }

    // ใช้เวลาอัปเดตล่าสุด (updated_at) แทนเวลาที่เริ่มจอง (borrow_date)
    val referenceDate = if (!borrow.updatedAt.isNullOrEmpty()) borrow.updatedAt else borrow.borrowDate

    LaunchedEffect(borrow.pickupStatus, referenceDate) {
        if (borrow.pickupStatus == "picked_up" || borrow.pickupStatus == "no_returned") {
            while (true) {
                if (borrow.pickupStatus == "no_returned") {
                    // สำหรับสถานะ "ยังไม่คืน" ให้เริ่มนับเดินหน้าจาก 0 (เวลาที่เข้าสู่สถานะนี้)
                    val diff = calculateTimeDiff(referenceDate, 0) 
                    isOverdueState = true // บังคับเป็นสีแดง
                    countdownText = formatDiffTime(Math.abs(diff))
                } else {
                    // สำหรับสถานะ "ยืนยันการรับ" ให้นับถอยหลังจาก 5 วัน
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

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(2.dp),
        shape = RoundedCornerShape(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "ID: ${borrow.id}", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    Text(text = borrow.bookName ?: "ไม่ทราบชื่อหนังสือ", color = Color.DarkGray, fontSize = 14.sp)
                    
                    // แสดงเวลาอัปเดตแบบอ่านง่าย (ตัดตัว T และเสี้ยววินาทีออก)
                    val displayDate = referenceDate?.replace("T", " ")?.split(".")?.get(0)?.replace("Z", "") ?: ""
                    Text(text = "อัปเดตล่าสุด: $displayDate", color = Color.Gray, fontSize = 12.sp)
                    
                    if (isAdmin) {
                        Text(text = "User ID: ${borrow.userId}", color = Color(0xFF7E57C2), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                    }
                }

                Column(horizontalAlignment = Alignment.End) {
                    val statusColor = if (isOverdueState && (borrow.pickupStatus == "picked_up" || borrow.pickupStatus == "no_returned")) {
                        Color.Red
                    } else {
                        when (borrow.pickupStatus) {
                            "pending" -> Color(0xFFFBC02D)
                            "picked_up" -> Color(0xFF4CAF50)
                            "returned" -> Color(0xFF2196F3)
                            "cancel", "forget" -> Color.Gray
                            "no_returned" -> Color.Red
                            else -> Color.Black
                        }
                    }

                    val statusText = when (borrow.pickupStatus) {
                        "pending" -> "รอยืนยัน"
                        "picked_up" -> "ยืนยันการรับ"
                        "returned" -> "ยืนยันการคืน"
                        "cancel" -> "ยกเลิกแล้ว"
                        "forget" -> "เลยกำหนด"
                        "no_returned" -> "ยังไม่คืน"
                        else -> borrow.pickupStatus
                    }

                    if (isAdmin) {
                        Box {
                            TextButton(onClick = { expanded = true }) {
                                Text(text = statusText, color = statusColor, fontWeight = FontWeight.Bold)
                                Icon(Icons.Default.ArrowDropDown, contentDescription = null, tint = statusColor)
                            }
                            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
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
                        Text(text = statusText, color = statusColor, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }

                    if ((borrow.pickupStatus == "picked_up" || borrow.pickupStatus == "no_returned") && countdownText.isNotEmpty()) {
                        // ปรับข้อความให้เหมาะสมกับแต่ละสถานะ
                        val labelText = if (borrow.pickupStatus == "no_returned") "ยังไม่คืนมาแล้ว" else if (isOverdueState) "เกินกำหนดมา" else "ต้องคืนในอีก"
                        val textColor = if (isOverdueState || borrow.pickupStatus == "no_returned") Color.Red else Color(0xFFFBC02D)
                        
                        Text(
                            text = "$labelText $countdownText",
                            color = textColor,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }
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
            // บังคับใช้ UTC เพื่อให้ตรงกับมาตรฐานเวลาของ Server/DB ป้องกันเวลาเคลื่อน
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
