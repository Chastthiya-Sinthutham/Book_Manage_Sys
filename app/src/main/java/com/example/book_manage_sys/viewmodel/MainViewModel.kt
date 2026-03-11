package com.example.book_manage_sys.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.book_manage_sys.data.*
import com.example.book_manage_sys.network.RetrofitClient
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel : ViewModel() {
    var currentUser by mutableStateOf<User?>(null)
    var books by mutableStateOf<List<Book>>(emptyList())
    var bookTypes by mutableStateOf<List<BookType>>(emptyList())
    var favorites by mutableStateOf<List<Book>>(emptyList())
    var userBorrows by mutableStateOf<List<Borrow>>(emptyList())
    var allUsers by mutableStateOf<List<User>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var selectedTypeId by mutableStateOf<Int?>(null)

    init {
        fetchBooks()
        fetchBookTypes()
    }

    fun fetchBooks() {
        viewModelScope.launch {
            try {
                books = RetrofitClient.apiService.getBooks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchBookTypes() {
        viewModelScope.launch {
            try {
                bookTypes = RetrofitClient.apiService.getBookTypes()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun filterBooksByType(typeId: Int?) {
        selectedTypeId = typeId
        viewModelScope.launch {
            try {
                books = if (typeId == null) {
                    RetrofitClient.apiService.getBooks()
                } else {
                    RetrofitClient.apiService.getBooksByType(typeId)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val user = RetrofitClient.apiService.login(mapOf("email" to email, "password" to pass))
                currentUser = user
                fetchFavorites()
                fetchUserBorrows()
                if (user.role.equals("admin", ignoreCase = true)) {
                    fetchAllUsers()
                }
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401) {
                    "อีเมลหรือรหัสผ่านไม่ถูกต้อง"
                } else {
                    "เกิดข้อผิดพลาด: ${e.message()}"
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้"
            } finally {
                isLoading = false
            }
        }
    }

    fun register(name: String, email: String, pass: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.register(mapOf(
                    "name" to name,
                    "email" to email,
                    "password" to pass
                ))
                onSuccess()
            } catch (e: HttpException) {
                val errorMsg = e.response()?.errorBody()?.string() ?: e.message()
                errorMessage = "Registration Failed: $errorMsg"
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchAllUsers() {
        viewModelScope.launch {
            try {
                allUsers = RetrofitClient.apiService.getAllUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteUser(userId)
                fetchAllUsers()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun updateUserProfile(name: String, phoneNumber: String, age: String, gender: String, imageFile: File?, onSuccess: () -> Unit) {
        val user = currentUser ?: return
        updateUserProfileById(user.id, name, phoneNumber, age, gender, user.role, imageFile, onSuccess)
    }

    fun updateUserProfileById(userId: Int, name: String, phoneNumber: String, age: String, gender: String, role: String, imageFile: File?, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val namePart = name.toRequestBody("text/plain".toMediaTypeOrNull())
                val phonePart = phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                val agePart = age.toRequestBody("text/plain".toMediaTypeOrNull())
                val genderPart = gender.toRequestBody("text/plain".toMediaTypeOrNull())
                val rolePart = role.toRequestBody("text/plain".toMediaTypeOrNull())
                
                val photoPart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("profile_photo", it.name, requestFile)
                }

                RetrofitClient.apiService.updateUser(userId, namePart, phonePart, agePart, genderPart, rolePart, photoPart)
                
                if (currentUser?.id == userId) {
                    val updatedUser = RetrofitClient.apiService.getUser(userId)
                    currentUser = updatedUser
                }
                
                onSuccess()
            } catch (e: HttpException) {
                val errorMsg = e.response()?.errorBody()?.string() ?: e.message()
                errorMessage = "Server Error (${e.code()}): $errorMsg"
                e.printStackTrace()
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    fun borrowBook(bookId: Int, onSuccess: () -> Unit) {
        val userId = currentUser?.id ?: return
        isLoading = true
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.borrowBook(mapOf("book_id" to bookId, "user_id" to userId))
                fetchBooks()
                fetchUserBorrows()
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = "ยืมไม่สำเร็จ: หนังสือเล่มนี้อาจถูกยืมไปแล้ว"
            } catch (e: Exception) {
                errorMessage = "เกิดข้อผิดพลาดในการยืม"
            } finally {
                isLoading = false
            }
        }
    }

    fun fetchFavorites() {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            try {
                favorites = RetrofitClient.apiService.getFavorites(userId)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite(bookId: Int) {
        val userId = currentUser?.id ?: return
        val isFav = favorites.any { it.id == bookId }
        viewModelScope.launch {
            try {
                if (isFav) {
                    RetrofitClient.apiService.removeFavorite(mapOf("user_id" to userId, "book_id" to bookId))
                } else {
                    RetrofitClient.apiService.addFavorite(mapOf("user_id" to userId, "book_id" to bookId))
                }
                fetchFavorites()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun fetchUserBorrows() {
        val user = currentUser ?: return
        viewModelScope.launch {
            try {
                val borrows = if (user.role.equals("admin", ignoreCase = true)) {
                    RetrofitClient.apiService.getAllBorrows()
                } else {
                    RetrofitClient.apiService.getUserBorrows(user.id)
                }
                
                userBorrows = borrows
                checkAndExpireBorrows(borrows)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun checkAndExpireBorrows(borrows: List<Borrow>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
        
        borrows.forEach { borrow ->
            if (borrow.pickupStatus == "pending") {
                try {
                    val borrowDate = sdf.parse(borrow.borrowDate)
                    if (borrowDate != null) {
                        val diff = now.time - borrowDate.time
                        val hours = diff / (1000 * 60 * 60)
                        if (hours >= 24) {
                            viewModelScope.launch {
                                try {
                                    RetrofitClient.apiService.updateBorrowStatus(borrow.id, mapOf("status" to "forget"))
                                } catch (e: Exception) {}
                            }
                        }
                    }
                } catch (e: Exception) {}
            } else if (borrow.pickupStatus == "picked_up") {
                try {
                    val borrowDate = sdf.parse(borrow.borrowDate)
                    if (borrowDate != null) {
                        val calendar = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        calendar.time = borrowDate
                        calendar.add(Calendar.DAY_OF_YEAR, 5)
                        if (now.after(calendar.time)) {
                            viewModelScope.launch {
                                try {
                                    RetrofitClient.apiService.updateBorrowStatus(borrow.id, mapOf("status" to "no_returned"))
                                } catch (e: Exception) {}
                            }
                        }
                    }
                } catch (e: Exception) {}
            }
        }
    }

    fun updateBorrowStatus(borrowId: Int, status: String) {
        viewModelScope.launch {
            try {
                val response = RetrofitClient.apiService.updateBorrowStatus(borrowId, mapOf("status" to status))
                if (response.error == false || response.error == null) {
                    fetchUserBorrows()
                    fetchBooks()
                } else {
                    errorMessage = response.message
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "ไม่สามารถอัปเดตสถานะได้"
            }
        }
    }

    fun cancelBorrow(borrowId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updateBorrowStatus(borrowId, mapOf("status" to "cancel"))
                fetchUserBorrows()
                fetchBooks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun addBook(book: Book, imageFile: File?, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val name = book.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val price = "Nan".toRequestBody("text/plain".toMediaTypeOrNull())
                val pdfUrl = "Nan".toRequestBody("text/plain".toMediaTypeOrNull())
                
                val story = (book.shortStory ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val writer = (book.writer ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val office = (book.office ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val birth = (book.birth ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val typeId = book.typeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("Book_img", it.name, requestFile)
                }

                RetrofitClient.apiService.createBook(
                    name, price, story, writer, office, birth, typeId, pdfUrl, imagePart
                )
                fetchBooks()
                onSuccess()
            } catch (e: HttpException) {
                val errorMsg = e.response()?.errorBody()?.string() ?: e.message()
                errorMessage = "Server Error: $errorMsg"
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "เพิ่มหนังสือไม่สำเร็จ: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateBook(id: Int, book: Book, imageFile: File?, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val name = book.name.toRequestBody("text/plain".toMediaTypeOrNull())
                val price = "Nan".toRequestBody("text/plain".toMediaTypeOrNull())
                val pdfUrl = "Nan".toRequestBody("text/plain".toMediaTypeOrNull())
                
                val story = (book.shortStory ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val writer = (book.writer ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val office = (book.office ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val birth = (book.birth ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val typeId = book.typeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("Book_img", it.name, requestFile)
                }

                RetrofitClient.apiService.updateBook(
                    id, name, price, story, writer, office, birth, typeId, pdfUrl, imagePart
                )
                fetchBooks()
                onSuccess()
            } catch (e: HttpException) {
                val errorMsg = e.response()?.errorBody()?.string() ?: e.message()
                errorMessage = "Server Error: $errorMsg"
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = "แก้ไขข้อมูลไม่สำเร็จ: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteBook(id)
                fetchBooks()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}
