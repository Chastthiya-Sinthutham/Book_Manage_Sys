package com.example.book_manage_sys.ui

import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import com.example.book_manage_sys.data.Book
import com.example.book_manage_sys.network.RetrofitClient
import com.example.book_manage_sys.viewmodel.MainViewModel
import java.io.File
import java.io.FileOutputStream

// ── Theme Colors (เหมือน LoansScreen) ────────────────────────
private val BgColor       = Color(0xFFF0F4F2)
private val TealAccent    = Color(0xFF7ECEC4)
private val PurpleAccent  = Color(0xFF7E57C2)
private val AvailColor    = Color(0xFF4CAF50)
private val BorrowedColor = Color(0xFFE53935)

@Composable
fun HomeScreen(navController: NavController, viewModel: MainViewModel) {
    val context = LocalContext.current
    var searchQuery   by remember { mutableStateOf("") }
    var showAddDialog by remember { mutableStateOf(false) }
    var showTypeMenu  by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxSize().background(BgColor)) {

        // ── Decorative background circles (เหมือน LoansScreen) ─
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
                if (viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true) {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = TealAccent,
                        contentColor = Color.White,
                        shape = CircleShape,
                        modifier = Modifier.shadow(8.dp, CircleShape)
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add Book",
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        ) { padding ->
            Column(
                modifier = Modifier
                    .padding(padding)
                    .padding(horizontal = 16.dp)
                    .padding(top = 16.dp)
            ) {
                // ── Search Bar + Filter ────────────────────────
                Row(verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).height(52.dp),
                        placeholder = { Text("ค้นหา...", color = Color(0xFF8A9B97), fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = TealAccent)
                        },
                        shape = RoundedCornerShape(14.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor   = Color.White,
                            unfocusedBorderColor    = Color(0xFFD6E4E1),
                            focusedBorderColor      = TealAccent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(10.dp))

                    // Filter button — gradient teal เหมือน LoansScreen
                    Box {
                        Row(
                            modifier = Modifier
                                .height(52.dp)
                                .clip(RoundedCornerShape(14.dp))
                                .background(
                                    Brush.verticalGradient(
                                        listOf(TealAccent, Color(0xFF4DB6AC))
                                    )
                                )
                                .clickable { showTypeMenu = true }
                                .padding(horizontal = 14.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                Icons.Default.Menu,
                                contentDescription = "Filter",
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("กรอง", color = Color.White, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false },
                            modifier = Modifier.background(Color.White, RoundedCornerShape(14.dp))
                        ) {
                            DropdownMenuItem(
                                text = { Text("ทั้งหมด", color = Color(0xFF2D3E3A)) },
                                onClick = {
                                    viewModel.filterBooksByType(null)
                                    showTypeMenu = false
                                }
                            )
                            viewModel.bookTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.bookTypeName, color = Color(0xFF2D3E3A)) },
                                    onClick = {
                                        viewModel.filterBooksByType(type.id)
                                        showTypeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Section Header (เหมือน LoansScreen) ───────
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
                                text = "หนังสือทั้งหมด",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color(0xFF1A2E2A)
                            )
                            Text(
                                text = "เลือกหนังสือที่ต้องการยืม",
                                fontSize = 12.sp,
                                color = Color(0xFF8A9B97)
                            )
                        }
                    }

                    val bookCount = viewModel.books.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }.size
                    Surface(
                        shape = RoundedCornerShape(20.dp),
                        color = TealAccent.copy(alpha = 0.15f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, TealAccent.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            text = "$bookCount เล่ม",
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TealAccent
                        )
                    }
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── Book Grid ──────────────────────────────────
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.books.filter {
                        it.name.contains(searchQuery, ignoreCase = true)
                    }) { book ->
                        val isFavorite = viewModel.favorites.any { it.id == book.id }
                        BookItem(
                            book = book,
                            isFavorite = isFavorite,
                            onFavoriteClick = { viewModel.toggleFavorite(book.id) },
                            onClick = {
                                navController.navigate(Screen.BookDetail.createRoute(book.id))
                            }
                        )
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        BookEditDialog(
            title = "เพิ่มหนังสือใหม่",
            buttonLabel = "บันทึกข้อมูลหนังสือ",
            onDismiss = { showAddDialog = false },
            onConfirm = { newBook, imageFile ->
                viewModel.addBook(newBook, imageFile) {
                    Toast.makeText(context, "เพิ่มหนังสือสำเร็จ", Toast.LENGTH_SHORT).show()
                    showAddDialog = false
                }
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun BookItem(book: Book, isFavorite: Boolean, onFavoriteClick: () -> Unit, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .shadow(3.dp, RoundedCornerShape(16.dp))
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(0.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = RetrofitClient.getImageUrl(book.img),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)),
                    contentScale = ContentScale.Crop
                )

                // Status badge (top-left)
                Surface(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(8.dp),
                    shape = RoundedCornerShape(8.dp),
                    color = if (book.status == 0) AvailColor.copy(alpha = 0.92f)
                    else BorrowedColor.copy(alpha = 0.92f)
                ) {
                    Text(
                        text = if (book.status == 0) "ว่าง" else "จองแล้ว",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                // Favorite button (top-right)
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(30.dp)
                        .background(Color.White.copy(alpha = 0.85f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite
                        else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) BorrowedColor else Color(0xFFB0BEC5),
                        modifier = Modifier.size(16.dp)
                    )
                }

                // Gradient overlay bottom
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .align(Alignment.BottomCenter)
                        .background(
                            Brush.verticalGradient(
                                listOf(Color.Transparent, Color.Black.copy(alpha = 0.25f))
                            )
                        )
                )
            }

            // ── Accent bar ─────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(3.dp)
                    .background(
                        if (book.status == 0)
                            Brush.horizontalGradient(listOf(TealAccent, Color(0xFF4DB6AC)))
                        else
                            Brush.horizontalGradient(listOf(BorrowedColor, Color(0xFFEF9A9A)))
                    )
            )

            Column(modifier = Modifier.padding(horizontal = 10.dp, vertical = 8.dp)) {
                Text(
                    book.name,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 13.sp,
                    color = Color(0xFF1A2E2A)
                )
            }
        }
    }
}

// ── BookEditDialog — ไม่แตะ logic, ปรับสีให้ match theme ─────
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookEditDialog(
    title: String,
    buttonLabel: String,
    book: Book? = null,
    onDismiss: () -> Unit,
    onConfirm: (Book, File?) -> Unit,
    viewModel: MainViewModel
) {
    val context = LocalContext.current
    var name         by remember { mutableStateOf(book?.name ?: "") }
    var writer       by remember { mutableStateOf(book?.writer ?: "") }
    var office       by remember { mutableStateOf(book?.office ?: "") }
    var birth        by remember { mutableStateOf(book?.birth ?: "") }
    var price        by remember { mutableStateOf(book?.price?.toString() ?: "0.0") }
    var story        by remember { mutableStateOf(book?.shortStory ?: "") }
    var selectedType by remember { mutableStateOf(viewModel.bookTypes.find { it.id == book?.typeId } ?: viewModel.bookTypes.firstOrNull()) }
    var expanded     by remember { mutableStateOf(false) }
    var imageUri     by remember { mutableStateOf<Uri?>(null) }
    var imageFile    by remember { mutableStateOf<File?>(null) }

    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        imageUri = uri
        uri?.let {
            try {
                val file = File(context.cacheDir, "temp_image_${System.currentTimeMillis()}.jpg")
                context.contentResolver.openInputStream(it)?.use { input ->
                    FileOutputStream(file).use { output -> input.copyTo(output) }
                }
                imageFile = file
            } catch (e: Exception) {
                Toast.makeText(context, "ไม่สามารถโหลดรูปภาพได้", Toast.LENGTH_SHORT).show()
            }
        }
    }

    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(20.dp),
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.9f)
                .padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(
                modifier = Modifier
                    .padding(24.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                // Dialog header with accent bar
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .width(5.dp)
                            .height(26.dp)
                            .clip(RoundedCornerShape(4.dp))
                            .background(
                                Brush.verticalGradient(listOf(TealAccent, Color(0xFF4DB6AC)))
                            )
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        title,
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
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, BorrowedColor.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            it,
                            color = BorrowedColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(BgColor)
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        imageUri != null -> AsyncImage(
                            model = imageUri,
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        !book?.img.isNullOrEmpty() -> AsyncImage(
                            model = RetrofitClient.getImageUrl(book?.img),
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                        else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = TealAccent)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text("เลือกรูปภาพหน้าปก", color = Color(0xFF8A9B97), fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("ชื่อหนังสือ", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = name, onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = Color(0xFFD6E4E1)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ราคา", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = price, onValueChange = { price = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealAccent,
                                unfocusedBorderColor = Color(0xFFD6E4E1)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ประเภท", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                        ExposedDropdownMenuBox(
                            expanded = expanded,
                            onExpandedChange = { expanded = !expanded }
                        ) {
                            OutlinedTextField(
                                value = selectedType?.bookTypeName ?: "เลือก",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(10.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedBorderColor = TealAccent,
                                    unfocusedBorderColor = Color(0xFFD6E4E1)
                                )
                            )
                            ExposedDropdownMenu(
                                expanded = expanded,
                                onDismissRequest = { expanded = false },
                                modifier = Modifier.background(Color.White, RoundedCornerShape(12.dp))
                            ) {
                                viewModel.bookTypes.forEach { type ->
                                    DropdownMenuItem(
                                        text = { Text(type.bookTypeName) },
                                        onClick = { selectedType = type; expanded = false }
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("ชื่อผู้แต่ง", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = writer, onValueChange = { writer = it },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = Color(0xFFD6E4E1)
                    )
                )

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("สำนักพิมพ์", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = office, onValueChange = { office = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealAccent,
                                unfocusedBorderColor = Color(0xFFD6E4E1)
                            )
                        )
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ปีที่พิมพ์", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                        OutlinedTextField(
                            value = birth, onValueChange = { birth = it },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(10.dp),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = TealAccent,
                                unfocusedBorderColor = Color(0xFFD6E4E1)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("เนื้อเรื่องย่อ", fontSize = 12.sp, color = Color(0xFF8A9B97), fontWeight = FontWeight.Medium)
                OutlinedTextField(
                    value = story, onValueChange = { story = it },
                    modifier = Modifier.fillMaxWidth().height(100.dp),
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = TealAccent,
                        unfocusedBorderColor = Color(0xFFD6E4E1)
                    )
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "กรุณากรอกชื่อหนังสือ", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onConfirm(
                            Book(
                                id = book?.id ?: 0,
                                name = name, writer = writer, office = office,
                                birth = birth, shortStory = story,
                                typeId = selectedType?.id ?: 1,
                                price = price.toDoubleOrNull() ?: 0.0
                            ),
                            imageFile
                        )
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                    shape = RoundedCornerShape(12.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(buttonLabel, color = Color.White, fontWeight = FontWeight.SemiBold)
                    }
                }
            }
        }
    }
}