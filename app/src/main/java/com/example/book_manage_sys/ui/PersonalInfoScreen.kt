package com.example.book_manage_sys.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.book_manage_sys.network.RetrofitClient
import com.example.book_manage_sys.viewmodel.MainViewModel
import java.io.File
import java.io.FileOutputStream

private val TealTop    = Color(0xFF7ECEC4)
private val TealBottom = Color(0xFFB2EBE6)
private val PurpleRing = Color(0xFF9B59B6)
private val BgColor    = Color(0xFFF0F4F2)
private val PurpleNav  = Color(0xFF9B59B6)
private val TealAccent     = Color(0xFFFFFFFF)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(navController: NavController, viewModel: MainViewModel) {
    val user = viewModel.currentUser ?: return

    // กำหนดสีตามที่คุณระบุ
    val TealTop = Color(0xFF7ECEC4)
    val TealBottom = Color(0xFFB2EBE6)
    val BgColor = Color(0xFFF0F4F2)

    Scaffold(
        containerColor = BgColor
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
        ) {
            // --- ส่วน Header สี Teal พร้อมส่วนโค้ง ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp)
                    .clip(RoundedCornerShape(bottomEnd = 120.dp))
                    .background(
                        brush = androidx.compose.ui.graphics.Brush.verticalGradient(
                            colors = listOf(TealTop, TealBottom)
                        )
                    )
            ) {
                // 1. วงกลมตกแต่งอันที่ 1 (ซ้ายบน)
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .offset(x = (-50).dp, y = (100).dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.35f))
                        .align(Alignment.TopStart) // ตอนนี้ใช้ได้แล้วเพราะอยู่ใน Box
                )

                // 2. วงกลมตกแต่งอันที่ 2 (ซ้ายล่าง/กลาง)
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .offset(x = 40.dp, y = (-80).dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.35f))
                        .align(Alignment.CenterStart)
                )

                // 3. UID (บนซ้าย)
                Text(
                    text = "UID : 000001",
                    modifier = Modifier.padding(top = 40.dp, start = 20.dp),
                    color = Color.Black
                )

                // 4. ส่วนโปรไฟล์ (ตรงกลาง)
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    AsyncImage(
                        model = user.profilePhotoPath,
                        contentDescription = null,
                        modifier = Modifier
                            .size(100.dp)
                            .clip(CircleShape)
                            .background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Welcome Dewwy",
                        color = Color.White,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            // --- ส่วน Card ข้อมูลสีขาว ---
            Surface(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .offset(y = (-20).dp), // ขยับขึ้นไปเกยส่วนสีเขียวเล็กน้อย
                shape = RoundedCornerShape(12.dp),
                color = Color.White,
                shadowElevation = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // Gmail Section
                    Column {
                        Text("Gmail", fontSize = 12.sp, color = Color.Gray)
                        Text(
                            text = user.email ?: "Dewwy49@gmail.com",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    Divider(color = Color.LightGray.copy(alpha = 0.4f))

                    // ข้อมูลแถวอื่นๆ (Gender, Phone, Age)
                    InfoRowItem(
                        icon = Icons.Default.Person,
                        label = "Gender",
                        value = user.gender ?: "Male"
                    )

                    InfoRowItem(
                        icon = Icons.Default.Phone,
                        label = "Phone",
                        value = user.phoneNumber ?: "093-555-5555"
                    )

                    InfoRowItem(
                        icon = Icons.Default.DateRange,
                        label = "Age",
                        value = user.age?.toString() ?: "13"
                    )
                }
            }

            // ปุ่ม Action ด้านล่าง (เช่น แก้ไข) สามารถเพิ่มต่อได้ตรงนี้
        }
    }
}

@Composable
fun InfoRowItem(icon: androidx.compose.ui.graphics.vector.ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = Color.DarkGray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(text = label, fontSize = 12.sp, color = Color.Gray)
            Text(
                text = value,
                fontSize = 14.sp,
                color = Color.DarkGray,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
