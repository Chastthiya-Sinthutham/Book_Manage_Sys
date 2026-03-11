package com.example.book_manage_sys.ui

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object BookDetail : Screen("bookDetail/{bookId}") {
        fun createRoute(bookId: Int) = "bookDetail/$bookId"
    }
    object Profile : Screen("profile")
    object Favorites : Screen("favorites")
    object Loans : Screen("loans")
    object PersonalInfo : Screen("personal_info")
    object UserManage : Screen("user_manage")
}
