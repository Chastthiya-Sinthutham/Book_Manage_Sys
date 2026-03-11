package com.example.book_manage_sys.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Create
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

private val BackgroundColor = Color(0xFFEEF2F0)
private val TealAccent     = Color(0xFF7ECEC4)
private val TextFieldBg    = Color(0xFFFFFFFF)
private val HintTextColor  = Color(0xFFAAAAAA)
private val DarkText       = Color(0xFF1A1A1A)
private val SignUpColor    = Color(0xFF3AAFA9)

@Composable
fun RegisterScreen(navController: NavController, viewModel: MainViewModel) {
    var name            by remember { mutableStateOf("") }
    var phone           by remember { mutableStateOf("") }
    var email           by remember { mutableStateOf("") }
    var gender          by remember { mutableStateOf("Other") }
    var age             by remember { mutableStateOf("") }
    var password        by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(BackgroundColor)
    ) {
        // Teal blob top-left
        Box(
            modifier = Modifier
                .size(170.dp)
                .offset(x = (-40).dp, y = 50.dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.45f))
        )
        Box(
            modifier = Modifier
                .size(150.dp)
                .offset(x = 10.dp, y = 10.dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.45f))
        )
        // Teal blob bottom-right
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = 60.dp, y = (-60).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.35f))
        )
        Box(
            modifier = Modifier
                .size(200.dp)
                .align(Alignment.BottomEnd)
                .offset(x = (-30).dp, y = (-90).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.35f))
        )
        Box(
            modifier = Modifier
                .size(50.dp)
                .align(Alignment.CenterEnd)
                .offset(x = 10.dp, y = (-50).dp)
                .clip(CircleShape)
                .background(TealAccent.copy(alpha = 0.45f))
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(56.dp))

            // Title
            Text(
                text = "Nice to meet you.",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                color = DarkText
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Illustration icon
            Icon(
                imageVector = Icons.Filled.Create,
                contentDescription = "Register Illustration",
                modifier = Modifier.size(110.dp),
                tint = DarkText
            )

            Spacer(modifier = Modifier.height(32.dp))

            // Name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                placeholder = { Text("Enter your name", color = HintTextColor) },
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor   = TextFieldBg,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = TealAccent
                ),
                enabled = !viewModel.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Phone
            OutlinedTextField(
                value = phone,
                onValueChange = { phone = it },
                placeholder = { Text("Phone", color = HintTextColor) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor   = TextFieldBg,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = TealAccent
                ),
                enabled = !viewModel.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Email
            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                placeholder = { Text("Enter your Email", color = HintTextColor) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor   = TextFieldBg,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = TealAccent
                ),
                enabled = !viewModel.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Gender radio buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text("Gender", color = DarkText, fontSize = 14.sp)
                Spacer(modifier = Modifier.width(8.dp))
                listOf("Male", "Female", "Other").forEach { option ->
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = gender == option,
                            onClick = { gender = option },
                            enabled = !viewModel.isLoading,
                            colors = RadioButtonDefaults.colors(selectedColor = TealAccent)
                        )
                        Text(option, fontSize = 13.sp, color = DarkText)
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Age
            OutlinedTextField(
                value = age,
                onValueChange = { age = it },
                placeholder = { Text("Age", color = HintTextColor) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor   = TextFieldBg,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = TealAccent
                ),
                enabled = !viewModel.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Password
            OutlinedTextField(
                value = password,
                onValueChange = { password = it },
                placeholder = { Text("Enter Password", color = HintTextColor) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor   = TextFieldBg,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = TealAccent
                ),
                enabled = !viewModel.isLoading,
                singleLine = true
            )

            Spacer(modifier = Modifier.height(12.dp))

            // Confirm password
            OutlinedTextField(
                value = confirmPassword,
                onValueChange = { confirmPassword = it },
                placeholder = { Text("Confirm password", color = HintTextColor) },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(50.dp)),
                shape = RoundedCornerShape(50.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    unfocusedContainerColor = TextFieldBg,
                    focusedContainerColor   = TextFieldBg,
                    unfocusedBorderColor    = Color.Transparent,
                    focusedBorderColor      = TealAccent
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

            // Register Button
            Button(
                onClick = {
                    if (password == confirmPassword) {
                        viewModel.register(name, email, password) {
                            navController.popBackStack()
                        }
                    }
                },
                modifier = Modifier.width(160.dp).height(48.dp),
                shape = RoundedCornerShape(50.dp),
                colors = ButtonDefaults.buttonColors(containerColor = TealAccent),
                enabled = !viewModel.isLoading
            ) {
                if (viewModel.isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color.White)
                } else {
                    Text("สมัคร", fontSize = 16.sp, fontWeight = FontWeight.Medium, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Sign in row
            TextButton(
                onClick = { navController.popBackStack() },
                enabled = !viewModel.isLoading
            ) {
                Text(
                    text = buildAnnotatedString {
                        withStyle(style = SpanStyle(color = DarkText)) {
                            append("Already have an account ? ")
                        }
                        withStyle(style = SpanStyle(color = SignUpColor, fontWeight = FontWeight.SemiBold)) {
                            append("Sign In")
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