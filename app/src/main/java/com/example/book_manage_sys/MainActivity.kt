package com.example.book_manage_sys

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.book_manage_sys.ui.MainApp
import com.example.book_manage_sys.ui.theme.Book_Manage_SysTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            Book_Manage_SysTheme {
                MainApp()
            }
        }
    }
}
