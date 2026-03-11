package com.example.book_manage_sys.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.example.book_manage_sys.viewmodel.MainViewModel

@Composable
fun FavoritesScreen(navController: NavController, viewModel: MainViewModel) {
    LaunchedEffect(Unit) {
        viewModel.fetchFavorites()
    }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Text("My Favorites", style = MaterialTheme.typography.headlineMedium)
        Spacer(modifier = Modifier.height(16.dp))
        
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(viewModel.favorites) { book ->
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
