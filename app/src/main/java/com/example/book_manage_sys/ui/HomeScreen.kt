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

// Colors
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

        // Teal blob top-left decoration
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = (-40).dp, y = (-30).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.4f))
        )
        Box(
            modifier = Modifier
                .size(160.dp)
                .offset(x = (-40).dp, y = (-30).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.4f))
        )

        Scaffold(
            containerColor = Color.Transparent,
            bottomBar = {
                NavigationBar(
                    containerColor = Color.White,
                    tonalElevation = 8.dp
                ) {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, contentDescription = null) },
                        label = { Text("Home", fontSize = 11.sp) },
                        colors = NavigationBarItemDefaults.colors(
                            selectedIconColor   = PurpleAccent,
                            selectedTextColor   = PurpleAccent,
                            indicatorColor      = Color.Transparent
                        )
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Screen.Loans.route) },
                        icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                        label = { Text("Loans", fontSize = 11.sp) }
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = { navController.navigate(Screen.Profile.route) },
                        icon = { Icon(Icons.Default.Person, contentDescription = null) },
                        label = { Text("Profile", fontSize = 11.sp) }
                    )
                }
            },
            floatingActionButton = {
                if (viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true) {
                    FloatingActionButton(
                        onClick = { showAddDialog = true },
                        containerColor = PurpleAccent,
                        contentColor = Color.White,
                        shape = CircleShape
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Book", modifier = Modifier.size(32.dp))
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
                // Search Bar + Filter
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Search field
                    OutlinedTextField(
                        value = searchQuery,
                        onValueChange = { searchQuery = it },
                        modifier = Modifier.weight(1f).height(52.dp),
                        placeholder = { Text("ค้นหา...", color = Color.Gray, fontSize = 14.sp) },
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null, tint = Color.Gray)
                        },
                        shape = RoundedCornerShape(50.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            unfocusedContainerColor = Color.White,
                            focusedContainerColor   = Color.White,
                            unfocusedBorderColor    = Color.Transparent,
                            focusedBorderColor      = TealAccent
                        ),
                        singleLine = true
                    )

                    Spacer(modifier = Modifier.width(8.dp))

                    // Filter button
                    Box {
                        Row(
                            modifier = Modifier
                                .height(52.dp)
                                .clip(RoundedCornerShape(50.dp))
                                .background(PurpleAccent)
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
                            Text("กรอง", color = Color.White, fontSize = 13.sp)
                            Spacer(modifier = Modifier.width(6.dp))
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }

                        DropdownMenu(
                            expanded = showTypeMenu,
                            onDismissRequest = { showTypeMenu = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("ทั้งหมด") },
                                onClick = {
                                    viewModel.filterBooksByType(null)
                                    showTypeMenu = false
                                }
                            )
                            viewModel.bookTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.bookTypeName) },
                                    onClick = {
                                        viewModel.filterBooksByType(type.id)
                                        showTypeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Book Grid
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
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        shape = RoundedCornerShape(12.dp)
    ) {
        Column {
            Box {
                AsyncImage(
                    model = RetrofitClient.getImageUrl(book.img),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)),
                    contentScale = ContentScale.Crop
                )
                // Favorite button
                IconButton(
                    onClick = onFavoriteClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                        .size(28.dp)
                        .background(Color.White.copy(alpha = 0.8f), CircleShape)
                ) {
                    Icon(
                        imageVector = if (isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                        contentDescription = "Favorite",
                        tint = if (isFavorite) Color.Red else Color.Gray,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)) {
                Text(
                    book.name,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    fontSize = 13.sp
                )
                Text(
                    text = if (book.status == 0) "ว่าง" else "จองแล้ว",
                    color = if (book.status == 0) AvailColor else BorrowedColor,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

// BookEditDialog — โครงสร้างเดิม ปรับสีให้ match theme
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
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier.fillMaxWidth().fillMaxHeight(0.9f).padding(vertical = 16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White)
        ) {
            Column(modifier = Modifier.padding(24.dp).verticalScroll(rememberScrollState())) {
                Text(title, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)

                viewModel.errorMessage?.let {
                    Text(it, color = Color.Red, fontSize = 12.sp, modifier = Modifier.padding(vertical = 8.dp))
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Image Picker
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFF5F5F5))
                        .clickable { launcher.launch("image/*") },
                    contentAlignment = Alignment.Center
                ) {
                    when {
                        imageUri != null -> AsyncImage(model = imageUri, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                        !book?.img.isNullOrEmpty() -> AsyncImage(model = RetrofitClient.getImageUrl(book?.img), contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Fit)
                        else -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Gray)
                            Text("เลือกรูปภาพหน้าปก", color = Color.Gray, fontSize = 12.sp)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text("ชื่อหนังสือ", fontSize = 12.sp, color = Color.Gray)
                OutlinedTextField(value = name, onValueChange = { name = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ราคา", fontSize = 12.sp, color = Color.Gray)
                        OutlinedTextField(value = price, onValueChange = { price = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ประเภท", fontSize = 12.sp, color = Color.Gray)
                        ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                            OutlinedTextField(
                                value = selectedType?.bookTypeName ?: "เลือก",
                                onValueChange = {},
                                readOnly = true,
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(8.dp)
                            )
                            ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                                viewModel.bookTypes.forEach { type ->
                                    DropdownMenuItem(text = { Text(type.bookTypeName) }, onClick = { selectedType = type; expanded = false })
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("ชื่อผู้แต่ง", fontSize = 12.sp, color = Color.Gray)
                OutlinedTextField(value = writer, onValueChange = { writer = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))

                Spacer(modifier = Modifier.height(12.dp))

                Row(modifier = Modifier.fillMaxWidth()) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("สำนักพิมพ์", fontSize = 12.sp, color = Color.Gray)
                        OutlinedTextField(value = office, onValueChange = { office = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text("ปีที่พิมพ์", fontSize = 12.sp, color = Color.Gray)
                        OutlinedTextField(value = birth, onValueChange = { birth = it }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(8.dp))
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text("เนื้อเรื่องย่อ", fontSize = 12.sp, color = Color.Gray)
                OutlinedTextField(value = story, onValueChange = { story = it }, modifier = Modifier.fillMaxWidth().height(100.dp), shape = RoundedCornerShape(8.dp))

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = {
                        if (name.isBlank()) {
                            Toast.makeText(context, "กรุณากรอกชื่อหนังสือ", Toast.LENGTH_SHORT).show()
                            return@Button
                        }
                        onConfirm(Book(id = book?.id ?: 0, name = name, writer = writer, office = office, birth = birth, shortStory = story, typeId = selectedType?.id ?: 1, price = price.toDoubleOrNull() ?: 0.0), imageFile)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                    shape = RoundedCornerShape(8.dp),
                    enabled = !viewModel.isLoading
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                    } else {
                        Text(buttonLabel, color = Color.White)
                    }
                }
            }
        }
    }
}