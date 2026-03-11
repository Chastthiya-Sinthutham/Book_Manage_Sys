package com.example.book_manage_sys.ui

import android.widget.Toast
import androidx.compose.foundation.background
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.book_manage_sys.network.RetrofitClient
import com.example.book_manage_sys.viewmodel.MainViewModel
import androidx.compose.ui.platform.LocalContext
private val BgColor      = Color(0xFFF0F4F2)
private val TealAccent   = Color(0xFF7ECEC4)
private val PurpleAccent = Color(0xFF9B59B6)
private val CardBg       = Color(0xFFFFFFFF)
private val LabelGray    = Color(0xFF9E9E9E)
private val ValueDark    = Color(0xFF2C3E50)

@Composable
fun BookDetailScreen(navController: NavController, viewModel: MainViewModel, bookId: Int?) {
    val book = viewModel.books.find { it.id == bookId } ?: return
    var showEditDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        // Teal blob top-right
        Box(
            modifier = Modifier
                .size(140.dp)
                .align(Alignment.TopEnd)
                .offset(x = 40.dp, y = (-20).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.4f))
        )

        Column(modifier = Modifier.fillMaxSize()) {

            // ── Scrollable body ──────────────────────────────────────
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // Search icon top-left (back nav)
                Row(modifier = Modifier.fillMaxWidth()) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.Default.Search, contentDescription = "Back", tint = Color.DarkGray)
                    }
                }

                // Book title
                Text(
                    text = book.name,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = ValueDark,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                // Book cover image with shadow
                AsyncImage(
                    model = RetrofitClient.getImageUrl(book.img),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth(0.65f)
                        .aspectRatio(0.72f)
                        .shadow(8.dp, RoundedCornerShape(12.dp))
                        .clip(RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                Spacer(modifier = Modifier.height(24.dp))

                // Detail card
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

                        DetailRow(label = "ผู้แต่ง", value = book.writer ?: "-")
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(label = "สำนักพิมพ์", value = book.office ?: "-")
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(label = "ปีที่พิมพ์", value = book.birth ?: "-")
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(label = "ประเภท", value = book.typeName ?: "-")
                        HorizontalDivider(color = Color(0xFFF0F0F0), modifier = Modifier.padding(vertical = 8.dp))

                        // Synopsis
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                "เนื้อเรื่องย่อ",
                                fontWeight = FontWeight.Medium,
                                color = LabelGray,
                                fontSize = 14.sp,
                                modifier = Modifier.width(90.dp)
                            )
                            Text(
                                text = book.shortStory ?: "-",
                                color = ValueDark,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                modifier = Modifier
                                    .weight(1f)
                                    .padding(start = 16.dp),
                                textAlign = TextAlign.End
                            )
                        }
                    }
                }

                if (viewModel.errorMessage != null) {
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(viewModel.errorMessage!!, color = Color.Red, fontSize = 12.sp)
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Action buttons ───────────────────────────────────
                if (viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        Button(
                            onClick = {
                                viewModel.deleteBook(book.id)
                                navController.popBackStack()
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text("ลบหนังสือ", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showEditDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PurpleAccent),
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(50.dp)
                        ) {
                            Text("แก้ไข", color = Color.White, fontWeight = FontWeight.Bold)
                        }
                    }
                } else {
                    Button(
                        onClick = {
                            viewModel.borrowBook(book.id) {
                                Toast.makeText(context, "จองสำเร็จแล้ว!", Toast.LENGTH_SHORT).show()
                                navController.popBackStack()
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        enabled = book.status == 0 && !viewModel.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = PurpleAccent,
                            disabledContainerColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(50.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(modifier = Modifier.size(20.dp), color = Color.White, strokeWidth = 2.dp)
                        } else {
                            Text(
                                text = if (book.status == 0) "จองเลย" else "ไม่ว่าง",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }

            // ── Bottom Nav ───────────────────────────────────────────
            NavigationBar(containerColor = CardBg, tonalElevation = 8.dp) {
                NavigationBarItem(
                    selected = true,
                    onClick = { navController.navigate(Screen.Home.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleAccent,
                        selectedTextColor = PurpleAccent,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Loans.route) },
                    icon = { Icon(Icons.Default.Menu, contentDescription = null) },
                    label = { Text("Loans", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Profile.route) },
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }

    if (showEditDialog) {
        BookEditDialog(
            title = "แก้ไขข้อมูลหนังสือ",
            buttonLabel = "ยืนยันการแก้ไข",
            book = book,
            onDismiss = { showEditDialog = false },
            onConfirm = { updatedBook, imageFile ->
                viewModel.updateBook(book.id, updatedBook, imageFile) {
                    showEditDialog = false
                    viewModel.fetchBooks()
                }
            },
            viewModel = viewModel
        )
    }
}

@Composable
fun DetailRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontWeight = FontWeight.Medium,
            color = LabelGray,
            fontSize = 14.sp,
            modifier = Modifier.width(90.dp)
        )
        Text(
            text = value,
            fontWeight = FontWeight.Medium,
            color = ValueDark,
            fontSize = 14.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 16.dp),
            textAlign = TextAlign.End
        )
    }
}