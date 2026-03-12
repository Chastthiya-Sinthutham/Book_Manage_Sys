package com.example.book_manage_sys.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.book_manage_sys.viewmodel.MainViewModel

// Colors matching the design
private val BackgroundColor = Color(0xFFEEF2F0)
private val TealAccent = Color(0xFF7ECEC4)
private val TextFieldBg = Color(0xFFFFFFFF)
private val HintTextColor = Color(0xFFAAAAAA)
private val DarkText = Color(0xFF1A1A1A)
private val SignUpColor = Color(0xFF3AAFA9)

@Composable
fun LoginScreen(navController: NavController, viewModel: MainViewModel) {
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            Spacer(modifier = Modifier.height(80.dp))

            // Title
            Text(
                text = "Welcome back !!!",
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Avatar box with purple border and teal blob background
            // ลบ .clip(RoundedCornerShape(4.dp)) ออก
            Box(
                modifier = Modifier
                    .size(190.dp)
                    .background(BackgroundColor),  // ลบ .clip() ออก
                contentAlignment = Alignment.Center
            ) {
                // Teal blob (left)
                Box(
                    modifier = Modifier
                        .size(220.dp)
                        .offset(x = (-20).dp, y = 10.dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.5f))
                )
                // Teal blob (right)
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .offset(x = 30.dp, y = (-15).dp)
                        .clip(CircleShape)
                        .background(TealAccent.copy(alpha = 0.4f))
                )

                // Purple border + icon ยังคงอยู่ข้างใน
                Box(
                    modifier = Modifier
                        .size(180.dp)
                        .background(Color.Transparent),
                    contentAlignment = Alignment.Center
                ) {
                    Surface(
                        modifier = Modifier.size(180.dp),
                        shape = RoundedCornerShape(4.dp),
                        color = Color.Transparent
                    ) {}

                    Icon(
                        imageVector = Icons.Filled.Person,
                        contentDescription = "User Avatar",
                        modifier = Modifier.size(120.dp),
                        tint = DarkText
                    )
                }
            }

            Spacer(modifier = Modifier.height(48.dp))

            // Email TextField
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your Email", color = HintTextColor) },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor = TextFieldBg,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = TealAccent
                ),
                enabled = !viewModel.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Password TextField
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter Password", color = HintTextColor) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor = TextFieldBg,
                    unfocusedBorderColor = Color.Transparent,
                    focusedBorderColor = TealAccent
                ),
                enabled = !viewModel.isLoading,
                singleLine = true
            )

            // Error message
            if (viewModel.errorMessage != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = viewModel.errorMessage!!,
                    color = Color.Red,
                    style = MaterialTheme.typography.bodySmall
                )
            }

            Spacer(modifier = Modifier.height(28.dp))

            // Login Button
            Button(
                onClick = {
                    viewModel.login(email, password) {
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Login.route) { inclusive = true }
                        }
                    }
                },
                modifier = Modifier
                    .width(160.dp)
                    .height(48.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = TealAccent
                ),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White
                    )
                } else {
                    Text(
                        text = "Login",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.weight(1f))

            // Sign up row
            TextButton(
                onClick = { navController.navigate(Screen.Register.route) },
                enabled = !viewModel.isLoading
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = DarkText)) {
                            append("Don't have an account ? ")
                        }
                        withStyle(style = SpanStyle(color = SignUpColor, fontWeight = FontWeight.SemiBold)) {
                            append("Sign Up")
                        }
                    },
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
            }

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}