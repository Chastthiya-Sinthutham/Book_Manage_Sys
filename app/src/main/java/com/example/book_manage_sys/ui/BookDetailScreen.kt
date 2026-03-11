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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.book_manage_sys.network.RetrofitClient
import com.example.book_manage_sys.viewmodel.MainViewModel

// ── Theme Colors (เหมือน HomeScreen & LoansScreen) ────────────
private val BgColor       = Color(0xFFF0F4F2)
private val TealAccent    = Color(0xFF7ECEC4)
private val PurpleAccent  = Color(0xFF7E57C2)
private val AvailColor    = Color(0xFF4CAF50)
private val BorrowedColor = Color(0xFFE53935)
private val CardBg        = Color(0xFFFFFFFF)
private val LabelGray     = Color(0xFF8A9B97)
private val ValueDark     = Color(0xFF1A2E2A)

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

        Column(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(20.dp))

                // ── Top bar: Back button ───────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { navController.popBackStack() },
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(10.dp))
                            .background(Color.White)
                    ) {
                        Icon(
                            Icons.Default.ArrowBack,
                            contentDescription = "Back",
                            tint = TealAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // ── Book cover ─────────────────────────────────
                Box {
                    AsyncImage(
                        model = RetrofitClient.getImageUrl(book.img),
                        contentDescription = null,
                        modifier = Modifier
                            .fillMaxWidth(0.60f)
                            .aspectRatio(0.72f)
                            .shadow(12.dp, RoundedCornerShape(16.dp))
                            .clip(RoundedCornerShape(16.dp)),
                        contentScale = ContentScale.Crop
                    )
                    // Status badge on cover
                    Surface(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(8.dp),
                        shape = RoundedCornerShape(8.dp),
                        color = if (book.status == 0) AvailColor.copy(alpha = 0.92f)
                        else BorrowedColor.copy(alpha = 0.92f)
                    ) {
                        Text(
                            text = if (book.status == 0) "ว่าง" else "จองแล้ว",
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                            color = Color.White,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Section header: ชื่อหนังสือ ────────────────
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
                        text = book.name,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = ValueDark,
                        textAlign = TextAlign.Start
                    )
                }

                Spacer(modifier = Modifier.height(20.dp))

                // ── Detail card ────────────────────────────────
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .shadow(3.dp, RoundedCornerShape(20.dp)),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = CardBg),
                    elevation = CardDefaults.cardElevation(0.dp)
                ) {
                    // Teal accent bar top
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(4.dp)
                            .background(
                                Brush.horizontalGradient(listOf(TealAccent, Color(0xFF4DB6AC)))
                            )
                    )
                    Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp)) {

                        DetailRow(label = "ผู้แต่ง",    value = book.writer ?: "-")
                        HorizontalDivider(color = Color(0xFFEEF3F1), modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(label = "สำนักพิมพ์", value = book.office ?: "-")
                        HorizontalDivider(color = Color(0xFFEEF3F1), modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(label = "ปีที่พิมพ์", value = book.birth  ?: "-")
                        HorizontalDivider(color = Color(0xFFEEF3F1), modifier = Modifier.padding(vertical = 8.dp))

                        DetailRow(label = "ประเภท",     value = book.typeName ?: "-")
                        HorizontalDivider(color = Color(0xFFEEF3F1), modifier = Modifier.padding(vertical = 8.dp))

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
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = BorrowedColor.copy(alpha = 0.10f),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp, BorrowedColor.copy(alpha = 0.4f)
                        )
                    ) {
                        Text(
                            viewModel.errorMessage!!,
                            color = BorrowedColor,
                            fontSize = 12.sp,
                            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // ── Action buttons ─────────────────────────────
                if (viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                viewModel.deleteBook(book.id)
                                navController.popBackStack()
                            },
                            border = androidx.compose.foundation.BorderStroke(
                                1.5.dp, BorrowedColor.copy(alpha = 0.7f)
                            ),
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = null,
                                tint = BorrowedColor,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("ลบหนังสือ", color = BorrowedColor, fontWeight = FontWeight.Bold)
                        }
                        Button(
                            onClick = { showEditDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                            modifier = Modifier.weight(1f).height(50.dp),
                            shape = RoundedCornerShape(14.dp)
                        ) {
                            Icon(
                                Icons.Default.Edit,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
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
                        modifier = Modifier.fillMaxWidth().height(52.dp),
                        enabled = book.status == 0 && !viewModel.isLoading,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = TealAccent,
                            disabledContainerColor = Color(0xFFB0BEC5)
                        ),
                        shape = RoundedCornerShape(14.dp)
                    ) {
                        if (viewModel.isLoading) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                color = Color.White,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Text(
                                text = if (book.status == 0) "จองเลย" else "ไม่ว่าง",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(32.dp))
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