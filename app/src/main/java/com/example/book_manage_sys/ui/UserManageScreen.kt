package com.example.book_manage_sys.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.book_manage_sys.data.User
import com.example.book_manage_sys.network.RetrofitClient
import com.example.book_manage_sys.viewmodel.MainViewModel
import java.io.File
import java.io.FileOutputStream

// ── Theme Colors ──────────────────────────────────────────────
private val BgColor       = Color(0xFFF0F4F2)
private val TealAccent    = Color(0xFF7ECEC4)
private val PurpleAccent  = Color(0xFF7E57C2)
private val BorrowedColor = Color(0xFFE53935)

@Composable
fun UserManageScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    var showAddDialog by remember { mutableStateOf(false) }
    var userToEdit by remember { mutableStateOf<User?>(null) }

    LaunchedEffect(Unit) {
        viewModel.fetchAllUsers()
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

        Scaffold(
            containerColor = Color.Transparent,
            floatingActionButton = {
                FloatingActionButton(
                    onClick = { showAddDialog = true },
                    containerColor = TealAccent,
                    contentColor = Color.White,
                    shape = CircleShape,
                    modifier = Modifier.shadow(8.dp, CircleShape)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add User")
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // ── Section Header ─────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
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
                                text = "User Management",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color(0xFF1A2E2A)
                            )
                            Text(
                                text = "จัดการข้อมูลผู้ใช้",
                                fontSize = 12.sp,
                                color = Color(0xFF8A9B97)
                            )
                        }
                    }
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TealAccent.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, TealAccent.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            text = "${viewModel.allUsers.size} คน",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Box(modifier = Modifier.fillMaxSize()) {
                    if (viewModel.isLoading && viewModel.allUsers.isEmpty()) {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.Center),
                            color = TealAccent
                        )
                    } else if (viewModel.errorMessage != null && viewModel.allUsers.isEmpty()) {
                        Column(
                            modifier = Modifier.align(Alignment.Center),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text("เกิดข้อผิดพลาด", color = BorrowedColor, fontWeight = FontWeight.Bold)
                            Text(viewModel.errorMessage!!, color = Color(0xFF8A9B97), fontSize = 12.sp)
                            Spacer(modifier = Modifier.height(8.dp))
                            Button(
                                onClick = { viewModel.fetchAllUsers() },
                                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Text("ลองใหม่", color = Color.White)
                            }
                        }
                    } else {
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxSize()
                        ) {
                            items(viewModel.allUsers) { user ->
                                UserItem(
                                    user = user,
                                    onEdit = { userToEdit = user },
                                    onDelete = {
                                        viewModel.deleteUser(user.id)
                                        Toast.makeText(context, "User deleted", Toast.LENGTH_SHORT).show()
                                    }
                                )
                            }
                            item { Spacer(modifier = Modifier.height(80.dp)) }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        UserAddDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { name, email, password ->
                viewModel.register(name, email, password) {
                    viewModel.fetchAllUsers()
                    Toast.makeText(context, "User added successfully", Toast.LENGTH_SHORT).show()
                    showAddDialog = false
                }
            },
            viewModel = viewModel
        )
    }

    userToEdit?.let { user ->
        UserEditDialog(
            user = user,
            onDismiss = { userToEdit = null },
            onConfirm = { name, phone, age, gender, role, imageFile ->
                viewModel.updateUserProfileById(user.id, name, phone, age, gender, role, imageFile) {
                    viewModel.fetchAllUsers()
                    Toast.makeText(context, "User updated successfully", Toast.LENGTH_SHORT).show()
                    userToEdit = null
                }
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun UserItem(user: User, onEdit: () -> Unit, onDelete: () -> Unit) {
    var showDeleteDialog by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp)),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        // Accent bar top
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(3.dp)
                .background(
                    Brush.horizontalGradient(listOf(TealAccent, Color(0xFF4DB6AC)))
                )
        )
        Row(
            modifier = Modifier.padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Avatar
            Box {
                AsyncImage(
                    model = RetrofitClient.getImageUrl(user.profilePhotoPath),
                    contentDescription = null,
                    modifier = Modifier
                        .size(52.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCEDE9)),
                    contentScale = ContentScale.Crop
                )
                // Role indicator dot
                val dotColor = if (user.role.equals("admin", ignoreCase = true))
                    PurpleAccent else TealAccent
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .align(Alignment.BottomEnd)
                        .clip(CircleShape)
                        .background(dotColor)
                        .shadow(2.dp, CircleShape)
                )
            }

            Spacer(modifier = Modifier.width(14.dp))

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = user.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = Color(0xFF1A2E2A)
                )
                Text(
                    text = user.email,
                    color = Color(0xFF8A9B97),
                    fontSize = 13.sp
                )
                Spacer(modifier = Modifier.height(4.dp))
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = if (user.role.equals("admin", ignoreCase = true))
                        PurpleAccent.copy(alpha = 0.12f) else TealAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp,
                        if (user.role.equals("admin", ignoreCase = true))
                            PurpleAccent.copy(alpha = 0.4f) else TealAccent.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = user.role,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (user.role.equals("admin", ignoreCase = true))
                            PurpleAccent else TealAccent
                    )
                }
            }

            IconButton(onClick = onEdit) {
                Icon(
                    Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = TealAccent,
                    modifier = Modifier.size(20.dp)
                )
            }
            IconButton(onClick = { showDeleteDialog = true }) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete",
                    tint = BorrowedColor,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            shape = RoundedCornerShape(20.dp),
            containerColor = Color.White,
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(4.dp).height(22.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(BorrowedColor)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("ยืนยันการลบ", fontWeight = FontWeight.Bold, color = Color(0xFF1A2E2A))
                }
            },
            text = {
                Text(
                    "ต้องการลบผู้ใช้ ${user.name} ใช่ไหม?",
                    color = Color(0xFF8A9B97),
                    fontSize = 14.sp
                )
            },
            confirmButton = {
                Button(
                    onClick = { showDeleteDialog = false; onDelete() },
                    colors = ButtonDefaults.buttonColors(containerColor = BorrowedColor),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ลบ", color = Color.White, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                OutlinedButton(
                    onClick = { showDeleteDialog = false },
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD6E4E1)),
                    shape = RoundedCornerShape(10.dp)
                ) {
                    Text("ยกเลิก", color = Color(0xFF8A9B97))
                }
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserAddDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit,
    viewModel: MainViewModel
) {
    var name     by remember { mutableStateOf("") }
    var email    by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(5.dp).height(26.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.verticalGradient(listOf(TealAccent, Color(0xFF4DB6AC)))
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Add New User",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFF1A2E2A)
                    )
                }

                viewModel.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BorrowedColor.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorrowedColor.copy(alpha = 0.4f))
                    ) {
                        Text(it, color = BorrowedColor, fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                listOf(
                    Triple("ชื่อ", name) { v: String -> name = v },
                    Triple("Email", email) { v: String -> email = v },
                    Triple("Password", password) { v: String -> password = v }
                ).forEach { (label, value, onChange) ->
                    Text(label, fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = value,
                        onValueChange = onChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor   = TealAccent,
                            unfocusedBorderColor = Color(0xFFD6E4E1)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onConfirm(name, email, password) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Register User", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserEditDialog(
    user: User,
    onDismiss: () -> Unit,
    onConfirm: (String, String, String, String, String, File?) -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var name         by remember { mutableStateOf(user.name) }
    var phone        by remember { mutableStateOf(user.phoneNumber ?: "") }
    var age          by remember { mutableStateOf(user.age?.toString() ?: "") }
    var gender       by remember { mutableStateOf(user.gender ?: "") }
    var role         by remember { mutableStateOf(user.role) }
    var roleExpanded by remember { mutableStateOf(false) }
    var imageUri     by remember { mutableStateOf<Uri?>(null) }
    var imageFile    by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        uri?.let {
            val file = File(context.cacheDir, "edit_user_${user.id}.jpg")
            context.contentResolver.openInputStream(it)?.use { input ->
                FileOutputStream(file).use { output -> input.copyTo(output) }
            }
            imageFile = file
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.85f).padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(5.dp).height(26.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.verticalGradient(listOf(TealAccent, Color(0xFF4DB6AC)))
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        "Edit User",
                        fontWeight = FontWeight.ExtraBold,
                        fontSize = 20.sp,
                        color = Color(0xFF1A2E2A)
                    )
                }

                viewModel.errorMessage?.let {
                    Spacer(modifier = Modifier.height(8.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BorrowedColor.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(1.dp, BorrowedColor.copy(alpha = 0.4f))
                    ) {
                        Text(it, color = BorrowedColor, fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Avatar picker
                Box(
                    modifier = Modifier
                        .size(90.dp)
                        .clip(CircleShape)
                        .background(Color(0xFFDCEDE9))
                        .align(Alignment.CenterHorizontally)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    AsyncImage(
                        model = imageUri ?: RetrofitClient.getImageUrl(user.profilePhotoPath),
                        contentDescription = null,
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )
                    Box(
                        modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.22f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.AccountCircle, contentDescription = null, tint = Color.White, modifier = Modifier.size(26.dp))
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Name
                Text("ชื่อ", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent, unfocusedBorderColor = Color(0xFFD6E4E1)
                    )
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Role dropdown
                Text("Role", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                ExposedDropdownMenuBox(
                    expanded = roleExpanded,
                    onExpandedChange = { roleExpanded = !roleExpanded }
                ) {
                    OutlinedTextField(
                        value = role,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roleExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealAccent, unfocusedBorderColor = Color(0xFFD6E4E1)
                        )
                    )
                    ExposedDropdownMenu(
                        expanded = roleExpanded,
                        onDismissRequest = { roleExpanded = false },
                        modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
                    ) {
                        listOf("user", "admin").forEach { r ->
                            DropdownMenuItem(
                                text = { Text(r, color = Color(0xFF2D3E3A)) },
                                onClick = { role = r; roleExpanded = false }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                listOf(
                    Pair("Gender", gender) to { v: String -> gender = v },
                    Pair("Phone", phone) to { v: String -> phone = v },
                    Pair("Age", age) to { v: String -> age = v }
                ).forEach { (labelValue, onChange) ->
                    val (label, value) = labelValue
                    Text(label, fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                    OutlinedTextField(
                        value = value, onValueChange = onChange,
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp),
                        singleLine = true,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = TealAccent, unfocusedBorderColor = Color(0xFFD6E4E1)
                        )
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                }

                Spacer(modifier = Modifier.height(8.dp))

                Button(
                    onClick = { onConfirm(name, phone, age, gender, role, imageFile) },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text("Save Changes", color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}