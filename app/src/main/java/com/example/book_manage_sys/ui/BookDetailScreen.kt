package com.example.book_manage_sys.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.book_manage_sys.network.RetrofitClient
import com.example.book_manage_sys.viewmodel.MainViewModel

@Composable
fun BookDetailScreen(navController: NavController, viewModel: MainViewModel, bookId: Int?) {
    // Re-fetch the book from viewModel to ensure UI updates after editing
    val book = viewModel.books.find { it.id == bookId } ?: return
    var showEditDialog by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = book.name, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        
        Spacer(modifier = Modifier.height(16.dp))
        
        // แสดงรูปภาพโดยใช้ URL เต็มจาก RetrofitClient
        AsyncImage(
            model = RetrofitClient.getImageUrl(book.img),
            contentDescription = null,
            modifier = Modifier
                .fillMaxWidth(0.7f)
                .height(300.dp),
            contentScale = ContentScale.Fit
        )
        
        Spacer(modifier = Modifier.height(24.dp))
        
        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = Color(0xFFF0FDFD)),
            shape = RoundedCornerShape(16.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                DetailRow(label = "ผู้แต่ง :", value = book.writer ?: "-")
                DetailRow(label = "สำนักพิมพ์ :", value = book.office ?: "-")
                DetailRow(label = "ปีที่พิมพ์ :", value = book.birth ?: "-")
                DetailRow(label = "ประเภท :", value = book.typeName ?: "-")
                DetailRow(label = "เนื้อเรื่องย่อ :", value = book.shortStory ?: "-")
            }
        }

        if (viewModel.errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = viewModel.errorMessage!!, color = Color.Red, style = MaterialTheme.typography.bodySmall)
        }
        
        Spacer(modifier = Modifier.height(32.dp))
        
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            if (viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true) {
                Button(
                    onClick = { 
                        viewModel.deleteBook(book.id)
                        navController.popBackStack()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF5252)),
                    modifier = Modifier.weight(1f).padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("ลบหนังสือ", color = Color.White)
                }
                
                Button(
                    onClick = { showEditDialog = true },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF80CBC4)),
                    modifier = Modifier.weight(1f).padding(8.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("แก้ไข", color = Color.Black)
                }
            } else {
                Button(
                    onClick = { 
                        viewModel.borrowBook(book.id) {
                            navController.popBackStack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().padding(8.dp),
                    enabled = book.status == 0 && !viewModel.isLoading,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (book.status == 0) MaterialTheme.colorScheme.primary else Color.Gray
                    )
                ) {
                    if (viewModel.isLoading) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(20.dp),
                            color = Color.White,
                            strokeWidth = 2.dp
                        )
                    } else {
                        Text(if (book.status == 0) "ยืมหนังสือ" else "ไม่ว่าง")
                    }
                }
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
    Row(modifier = Modifier.padding(vertical = 4.dp)) {
        Text(text = label, fontWeight = FontWeight.Bold, modifier = Modifier.width(100.dp))
        Text(text = value, modifier = Modifier.weight(1f))
    }
}
