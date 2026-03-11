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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
private val PurpleRing = Color(0xFF7E57C2)
private val BgColor    = Color(0xFFF0F4F2)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalInfoScreen(navController: NavController, viewModel: MainViewModel) {
    val user = viewModel.currentUser ?: return
    val context = LocalContext.current

    var name        by remember { mutableStateOf(user.name) }
    var phoneNumber by remember { mutableStateOf(user.phoneNumber ?: "") }
    var age         by remember { mutableStateOf(user.age?.toString() ?: "") }
    var gender      by remember { mutableStateOf(user.gender ?: "") }
    var imageUri    by remember { mutableStateOf<Uri?>(null) }
    var imageFile   by remember { mutableStateOf<File?>(null) }
    var isEditMode  by remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        imageUri = uri
        uri?.let {
            try {
                val file = File(context.cacheDir, "profile_temp_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(it)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                imageFile = file
            } catch (e: Exception) {
                Toast.makeText(context, "ไม่สามารถโหลดรูปภาพได้", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .verticalScroll(rememberScrollState())
    ) {
        // ── Header ────────────────────────────────────────────────
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(260.dp)
                .clip(RoundedCornerShape(bottomEnd = 120.dp))
                .background(brush = Brush.verticalGradient(listOf(TealTop, TealBottom)))
        ) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .offset(x = (-40).dp, y = (-30).dp)
                    .clip(CircleShape)
                    .background(Color.White.copy(alpha = 0.2f))
            )

            Text(
                text = "UID : ${user.id.toString().padStart(6, '0')}",
                modifier = Modifier.padding(top = 40.dp, start = 20.dp),
                color = Color.Black,
                fontWeight = FontWeight.Bold,
                fontSize = 13.sp
            )

            IconButton(
                onClick = { isEditMode = !isEditMode },
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(top = 32.dp, end = 12.dp)
            ) {
                Icon(
                    imageVector = if (isEditMode) Icons.Default.Close else Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = Color.White
                )
            }

            Column(
                modifier = Modifier.align(Alignment.Center),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier.clickable { if (isEditMode) launcher.launch("image/*") }
                ) {
                    Box(modifier = Modifier.size(90.dp).clip(CircleShape).background(PurpleRing))
                    AsyncImage(
                        model = imageUri ?: RetrofitClient.getImageUrl(user.profilePhotoPath),
                        contentDescription = null,
                        modifier = Modifier.size(84.dp).clip(CircleShape).background(Color.White),
                        contentScale = ContentScale.Crop
                    )
                    if (isEditMode) {
                        Box(
                            modifier = Modifier
                                .size(84.dp)
                                .clip(CircleShape)
                                .background(Color.Black.copy(alpha = 0.35f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.AccountBox, contentDescription = null, tint = Color.White, modifier = Modifier.size(28.dp))
                        }
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text("Welcome ${user.name}", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            }
        }

        // ── Card ข้อมูล ───────────────────────────────────────────
        Surface(
            modifier = Modifier.fillMaxWidth().padding(16.dp).offset(y = (-20).dp),
            shape = RoundedCornerShape(16.dp),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Column {
                    Text("Gmail", fontSize = 12.sp, color = Color.Gray)
                    Text(user.email, fontSize = 15.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(top = 4.dp))
                }

                HorizontalDivider(color = Color(0xFFF0F0F0))

                EditableInfoRow(
                    icon = Icons.Default.Person,
                    label = "Gender",
                    value = gender,
                    isEditMode = isEditMode,
                    onValueChange = { gender = it }
                )

                EditableInfoRow(
                    icon = Icons.Default.Phone,
                    label = "Phone",
                    value = phoneNumber,
                    isEditMode = isEditMode,
                    onValueChange = { phoneNumber = it }
                )

                EditableInfoRow(
                    icon = Icons.Default.DateRange,
                    label = "Age",
                    value = age,
                    isEditMode = isEditMode,
                    onValueChange = { age = it }
                )
            }
        }

        // ── Save button ───────────────────────────────────────────
        if (isEditMode) {
            if (viewModel.errorMessage != null) {
                Text(
                    viewModel.errorMessage!!,
                    color = Color.Red,
                    fontSize = 12.sp,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )
            }

            Spacer(modifier = Modifier.height(4.dp))

            Button(
                onClick = {
                    if (name.isBlank()) {
                        viewModel.errorMessage = "กรุณากรอกชื่อ"
                        return@Button
                    }
                    viewModel.updateUserProfile(
                        name = name,
                        phoneNumber = phoneNumber,
                        age = age,
                        gender = gender,
                        imageFile = imageFile
                    ) {
                        Toast.makeText(context, "อัปเดตข้อมูลสำเร็จ", Toast.LENGTH_SHORT).show()
                        isEditMode = false
                    }
                },
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp).height(50.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealTop),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                } else {
                    Text("Save Changes", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditableInfoRow(
    icon: ImageVector,
    label: String,
    value: String,
    isEditMode: Boolean,
    onValueChange: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.DarkGray)
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            if (isEditMode) {
                OutlinedTextField(
                    value = value,
                    onValueChange = onValueChange,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor      = TealTop,
                        unfocusedBorderColor    = Color(0xFFD6E4E1),
                        focusedContainerColor   = Color(0xFFF8FFFE),
                        unfocusedContainerColor = Color(0xFFF8FFFE)
                    )
                )
            } else {
                Text(
                    text = value.ifEmpty { "-" },
                    fontSize = 14.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}

@Composable
fun InfoRowItem(icon: ImageVector, label: String, value: String) {
    Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Icon(icon, contentDescription = null, modifier = Modifier.size(22.dp), tint = Color.DarkGray)
        Spacer(modifier = Modifier.width(16.dp))
        Column {
            Text(label, fontSize = 12.sp, color = Color.Gray)
            Text(value, fontSize = 14.sp, color = Color.DarkGray, modifier = Modifier.padding(top = 2.dp))
        }
    }
}