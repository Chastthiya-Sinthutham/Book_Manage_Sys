package com.example.book_manage_sys.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.book_manage_sys.viewmodel.MainViewModel

// ── Theme Colors ──────────────────────────────────────────────
private val BgColor       = Color(0xFFF0F4F2)
private val TealAccent    = Color(0xFF7ECEC4)
private val PurpleAccent  = Color(0xFF7E57C2)
private val AvailColor    = Color(0xFF4CAF50)
private val BorrowedColor = Color(0xFFE53935)

@Composable
fun FavoritesScreen(navController: NavController, viewModel: MainViewModel) {
    var showTypeMenu by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
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

        Scaffold(containerColor = Color.Transparent) { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(horizontal = 16.dp)
            ) {
                Spacer(modifier = Modifier.height(25.dp))

                // ── Header ─────────────────────────────────────
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
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
                                text = "My Favorite",
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 20.sp,
                                color = Color(0xFF1A2E2A)
                            )
                            Text(
                                text = "หนังสือที่ถูกใจ",
                                fontSize = 12.sp,
                                color = Color(0xFF8A9B97)
                            )
                        }
                    }

                    // Filter button — gradient teal
                    Box {
                        Row(
                            modifier = Modifier
                                .height(44.dp)
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
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                "กรอง",
                                color = Color.White,
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                            Icon(
                                Icons.Default.ArrowDropDown,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(18.dp)
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
                                    viewModel.filterFavoritesByType(null)
                                    showTypeMenu = false
                                }
                            )
                            viewModel.bookTypes.forEach { type ->
                                DropdownMenuItem(
                                    text = { Text(type.bookTypeName, color = Color(0xFF2D3E3A)) },
                                    onClick = {
                                        viewModel.filterFavoritesByType(type.id)
                                        showTypeMenu = false
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Badge จำนวน
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = TealAccent.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(
                        1.dp, TealAccent.copy(alpha = 0.4f)
                    )
                ) {
                    Text(
                        text = "${viewModel.filteredFavorites.size} เล่ม",
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 5.dp),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TealAccent
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                // ── Grid / Empty state ─────────────────────────
                if (viewModel.filteredFavorites.isEmpty()) {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                Icons.Default.FavoriteBorder,
                                contentDescription = null,
                                modifier = Modifier.size(64.dp),
                                tint = TealAccent.copy(alpha = 0.4f)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                "ไม่มีหนังสือที่ถูกใจ",
                                color = Color(0xFF8A9B97),
                                fontSize = 15.sp
                            )
                        }
                    }
                } else {
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        modifier = Modifier.fillMaxSize()
                    ) {
                        items(viewModel.filteredFavorites) { book ->
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
    }
}