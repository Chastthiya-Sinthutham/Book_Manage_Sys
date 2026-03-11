package com.example.book_manage_sys.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
//import androidx.room.util.copy
import coil.compose.AsyncImage
import com.example.book_manage_sys.network.RetrofitClient
import com.example.book_manage_sys.viewmodel.MainViewModel

private val TealTop    = Color(0xFF7ECEC4)
private val TealBottom = Color(0xFFB2EBE6)
private val PurpleRing = Color(0xFF9B59B6)
private val BgColor    = Color(0xFFF0F4F2)
private val PurpleNav  = Color(0xFF9B59B6)

private val TealAccent     = Color(0xFFFFFFFF)


@Composable
fun ProfileScreen(navController: NavController, viewModel: MainViewModel) {
    val user = viewModel.currentUser ?: return

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BgColor)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {

            // ── Header ──────────────────────────────────────────────
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(listOf(TealTop, TealBottom)),
                        shape = RoundedCornerShape(bottomStart = 40.dp, bottomEnd = 40.dp)
                    )
            ) {
                // Teal blob top-left
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.TopStart)
                        .offset(x = (-50).dp, y = 100.dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.35f))
                )
                Box(
                    modifier = Modifier
                        .size(200.dp)
                        .align(Alignment.TopStart)
                        .offset(x = 40.dp, y = (-80).dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.35f))
                )

                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // UID top-left
                    Text(
                        text = "UID : ${user.id.toString().padStart(6, '0')}",
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 20.dp, top = 16.dp),
                        color = Color.Black,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // Avatar with purple ring
                    Box(contentAlignment = Alignment.Center) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .clip(CircleShape)
                                .background(PurpleRing)
                        )
                        AsyncImage(
                            model = RetrofitClient.getImageUrl(user.profilePhotoPath),
                            contentDescription = null,
                            modifier = Modifier
                                .size(90.dp)
                                .clip(CircleShape)
                                .background(Color.White),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = "Welcome ${user.name}",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Gmail card ──────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp)) {
                    Text("Gmail", color = Color.Gray, fontSize = 12.sp)
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(user.email, fontWeight = FontWeight.Bold, fontSize = 15.sp)
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // ── Menu card ───────────────────────────────────────────
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Column(modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)) {
                    ProfileMenuItem(
                        icon = Icons.Default.Person,
                        title = "Personal Info",
                        subtitle = "Menu description."
                    ) { navController.navigate(Screen.PersonalInfo.route) }

                    Divider(color = Color(0xFFF0F0F0))

                    ProfileMenuItem(
                        icon = Icons.Default.ThumbUp,
                        title = "Favorites",
                        subtitle = "Menu description."
                    ) { navController.navigate(Screen.Favorites.route) }

                    Divider(color = Color(0xFFF0F0F0))

                    ProfileMenuItem(
                        icon = Icons.Default.List,
                        title = "Loans",
                        subtitle = "Menu description."
                    ) { navController.navigate(Screen.Loans.route) }

                    Divider(color = Color(0xFFF0F0F0))
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // ── Bottom Nav ──────────────────────────────────────────
            NavigationBar(
                containerColor = Color.White,
                tonalElevation = 8.dp
            ) {
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Home.route) },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("Home", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = false,
                    onClick = { navController.navigate(Screen.Loans.route) },
                    icon = { Icon(Icons.Default.ShoppingCart, contentDescription = null) },
                    label = { Text("Loans", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        unselectedIconColor = Color.Gray,
                        unselectedTextColor = Color.Gray,
                        indicatorColor = Color.Transparent
                    )
                )
                NavigationBarItem(
                    selected = true,
                    onClick = {},
                    icon = { Icon(Icons.Default.Person, contentDescription = null) },
                    label = { Text("Profile", fontSize = 11.sp) },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = PurpleNav,
                        selectedTextColor = PurpleNav,
                        indicatorColor = Color.Transparent
                    )
                )
            }
        }
    }
}

@Composable
fun ProfileMenuItem(icon: ImageVector, title: String, subtitle: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            icon,
            contentDescription = null,
            modifier = Modifier.size(22.dp),
            tint = Color.DarkGray
        )
        Spacer(modifier = Modifier.width(16.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(title, fontWeight = FontWeight.Medium, fontSize = 14.sp)
            Text(subtitle, fontSize = 12.sp, color = Color.Gray)
        }
        Icon(
            Icons.Default.KeyboardArrowRight,
            contentDescription = null,
            tint = Color.LightGray,
            modifier = Modifier.size(20.dp)
        )
    }
}