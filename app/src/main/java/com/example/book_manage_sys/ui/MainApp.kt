package com.example.book_manage_sys.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.*
import com.example.book_manage_sys.viewmodel.MainViewModel

sealed class BottomBarScreen(val route: String, val title: String, val icon: ImageVector) {
    object Home : BottomBarScreen(Screen.Home.route, "Home", Icons.Default.Home)
    object Loans : BottomBarScreen(Screen.Loans.route, "Loans", Icons.AutoMirrored.Filled.List)
    object UserManage : BottomBarScreen(Screen.UserManage.route, "Users", Icons.Default.AccountCircle)
    object Profile : BottomBarScreen(Screen.Profile.route, "Profile", Icons.Default.Person)
}

@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: MainViewModel = viewModel()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val isAdmin = viewModel.currentUser?.role?.equals("admin", ignoreCase = true) == true
    
    // กำหนดหน้าเริ่มต้น: ถ้ามี User อยู่แล้วให้ไป Home ถ้าไม่มีให้ไป Login
    val startRoute = if (viewModel.currentUser != null) Screen.Home.route else Screen.Login.route

    val showBottomBar = currentDestination?.route in listOf(
        BottomBarScreen.Home.route,
        BottomBarScreen.Loans.route,
        BottomBarScreen.Profile.route,
        BottomBarScreen.UserManage.route,
        Screen.BookDetail.route,
        Screen.Favorites.route,
        Screen.PersonalInfo.route
    )
    val profileSubRoutes = listOf(Screen.PersonalInfo.route, Screen.Favorites.route)


    Scaffold(
        bottomBar = {
            if (showBottomBar) {
                NavigationBar {
                    val items = mutableListOf(
                        BottomBarScreen.Home,
                        BottomBarScreen.Loans
                    )
                    if (isAdmin) {
                        items.add(BottomBarScreen.UserManage)
                    }
                    items.add(BottomBarScreen.Profile)

                    items.forEach { screen ->
                        val profileSubRoutes = listOf(
                            Screen.PersonalInfo.route,
                            Screen.Favorites.route
                        )

                        val isSelected = currentDestination?.hierarchy?.any { it.route == screen.route } == true ||
                                (screen.route == Screen.Profile.route &&
                                        currentDestination?.route in profileSubRoutes)

                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = null) },
                            label = { Text(screen.title) },
                            selected = isSelected,
                            onClick = {
                                val targetRoute = screen.route
                                val currentRoute = currentDestination?.route

                                // ถ้าอยู่ใน sub-route ของ Profile และกด Profile
                                if (screen.route == Screen.Profile.route && currentRoute in profileSubRoutes) {
                                    navController.popBackStack(Screen.Profile.route, inclusive = false)
                                    return@NavigationBarItem
                                }

                                navController.navigate(targetRoute) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }


                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = startRoute,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Login.route) {
                LoginScreen(navController, viewModel)
            }
            composable(Screen.Register.route) {
                RegisterScreen(navController, viewModel)
            }
            composable(Screen.Home.route) {
                HomeScreen(navController, viewModel)
            }
            composable(Screen.BookDetail.route) { backStackEntry ->
                val bookId = backStackEntry.arguments?.getString("bookId")?.toIntOrNull()
                BookDetailScreen(navController, viewModel, bookId)
            }
            composable(Screen.Profile.route) {
                ProfileScreen(navController, viewModel)
            }
            composable(Screen.Loans.route) {
                LoansScreen(navController, viewModel)
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(navController, viewModel)
            }
            composable(Screen.PersonalInfo.route) {
                PersonalInfoScreen(navController, viewModel)
            }
            composable(Screen.UserManage.route) {
                UserManageScreen(navController, viewModel)
            }
        }
    }
}
