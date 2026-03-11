package com.example.book_manage_sys.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.book_manage_sys.viewmodel.MainViewModel

// Colors
private val BgColor       = Color(0xFFF0F4F2)
private val TealAccent    = Color(0xFF7ECEC4)
private val PurpleAccent  = Color(0xFF7E57C2)
private val AvailColor    = Color(0xFF4CAF50)
private val BorrowedColor = Color(0xFFE53935)


@Composable
fun FavoritesScreen(navController: NavController, viewModel: MainViewModel) {
    var showTypeMenu  by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
            .padding(horizontal = 16.dp)
    ) {

        Spacer(modifier = Modifier.height(25.dp))

        // HEADER
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {

            Text(
                text = "My Favorite",
                style = MaterialTheme.typography.titleLarge
            )

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

        Spacer(modifier = Modifier.height(12.dp))

        // GRID BOOKS
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.fillMaxSize()
        ) {

            items(viewModel.favorites) { book ->

                val isFavorite = viewModel.favorites.any { it.id == book.id }

                BookItem(
                    book = book,
                    isFavorite = isFavorite,
                    onFavoriteClick = {
                        viewModel.toggleFavorite(book.id)
                    },
                    onClick = {
                        navController.navigate(
                            Screen.BookDetail.createRoute(book.id)
                        )
                    }
                )
            }
        }
    }
}
