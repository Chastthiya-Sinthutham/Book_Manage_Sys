package com.example.book_manage_sys.viewmodel

import android.app.Application
import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.book_manage_sys.data.*
import com.example.book_manage_sys.network.RetrofitClient
import com.google.gson.Gson
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class MainViewModel(application: Application) : AndroidViewModel(application) {
    private val prefs = application.getSharedPreferences("user_prefs", Context.MODE_PRIVATE)
    private val gson = Gson()

    var currentUser by mutableStateOf<User?>(null)
    var books by mutableStateOf<List<Book>>(emptyList())
    var bookTypes by mutableStateOf<List<BookType>>(emptyList())
    var favorites by mutableStateOf<List<Book>>(emptyList())
    var filteredFavorites by mutableStateOf<List<Book>>(emptyList())
    var userBorrows by mutableStateOf<List<Borrow>>(emptyList())
    var allUsers by mutableStateOf<List<User>>(emptyList())
    var isLoading by mutableStateOf(false)
    var errorMessage by mutableStateOf<String?>(null)
    var selectedTypeId by mutableStateOf<Int?>(null)
    var favoriteSelectedTypeId by mutableStateOf<Int?>(null)

    init {
        val userJson = prefs.getString("current_user", null)
        if (userJson != null) {
            try {
                currentUser = gson.fromJson(userJson, User::class.java)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        fetchInitialData()
    }

    private fun fetchInitialData() {
        viewModelScope.launch {
            try {
                val booksDeferred = async { RetrofitClient.apiService.getBooks() }
                val typesDeferred = async { RetrofitClient.apiService.getBookTypes() }
                
                books = try { booksDeferred.await() } catch (e: Exception) { emptyList() }
                bookTypes = try { typesDeferred.await() } catch (e: Exception) { emptyList() }

                currentUser?.let { user ->
                    fetchFavorites()
                    fetchUserBorrows()
                    if (user.role.equals("admin", ignoreCase = true)) {
                        fetchAllUsers()
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private fun saveSession(user: User) {
        prefs.edit().putString("current_user", gson.toJson(user)).apply()
    }

    private fun clearSession() {
        prefs.edit().remove("current_user").apply()
        currentUser = null
        favorites = emptyList()
        filteredFavorites = emptyList()
        userBorrows = emptyList()
        allUsers = emptyList()
        selectedTypeId = null
        favoriteSelectedTypeId = null
    }

    fun logout(onSuccess: () -> Unit) {
        clearSession()
        onSuccess()
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
                saveSession(user)
                fetchFavorites()
                fetchUserBorrows()
                if (user.role.equals("admin", ignoreCase = true)) {
                    fetchAllUsers()
                }
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401) "อีเมลหรือรหัสผ่านไม่ถูกต้อง" else "เกิดข้อผิดพลาด: ${e.message()}"
            } catch (e: Exception) {
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

    // --- FAVORITE LOGIC ---
    fun fetchFavorites() {
        val user = currentUser ?: return
        viewModelScope.launch {
            try {
                val favs = RetrofitClient.apiService.getFavorites(user.id)
                favorites = favs
                applyFavoriteFilter()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun toggleFavorite(bookId: Int) {
        val user = currentUser ?: return
        val isFav = favorites.any { it.id == bookId }
        val request = FavoriteRequest(user.id, bookId)

        viewModelScope.launch {
            try {
                if (isFav) {
                    RetrofitClient.apiService.removeFavorite(request)
                } else {
                    RetrofitClient.apiService.addFavorite(request)
                }
                fetchFavorites()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun filterFavoritesByType(typeId: Int?) {
        favoriteSelectedTypeId = typeId
        applyFavoriteFilter()
    }

    private fun applyFavoriteFilter() {
        filteredFavorites = if (favoriteSelectedTypeId == null) {
            favorites
        } else {
            favorites.filter { it.typeId == favoriteSelectedTypeId }
        }
    }

    // --- BORROW LOGIC ---
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
            } catch (e: Exception) {
                e.printStackTrace()
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
            } catch (e: Exception) {
                errorMessage = "เกิดข้อผิดพลาดในการยืม"
            } finally {
                isLoading = false
            }
        }
    }

    fun updateBorrowStatus(borrowId: Int, status: String) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updateBorrowStatus(borrowId, mapOf("status" to status))
                fetchUserBorrows()
                fetchBooks()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun cancelBorrow(borrowId: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.updateBorrowStatus(borrowId, mapOf("status" to "cancel"))
                fetchUserBorrows()
                fetchBooks()
            } catch (e: HttpException) {
                // เพิ่มบรรทัดนี้เพื่อดู error จาก server
                val errorBody = e.response()?.errorBody()?.string()
                android.util.Log.e("CANCEL_ERROR", "HTTP ${e.code()}: $errorBody")
                errorMessage = "ยกเลิกไม่สำเร็จ: $errorBody"
            } catch (e: Exception) {
                android.util.Log.e("CANCEL_ERROR", "Exception: ${e.message}")
                errorMessage = "ยกเลิกไม่สำเร็จ: ${e.message}"
            }
        }
    }
    // --- BOOK MANAGEMENT ---
    fun addBook(book: Book, imageFile: File?, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val name = (book.name).toRequestBody("text/plain".toMediaTypeOrNull())
                val price = "0".toRequestBody("text/plain".toMediaTypeOrNull())
                val pdfUrl = "".toRequestBody("text/plain".toMediaTypeOrNull())
                val story = (book.shortStory ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val writer = (book.writer ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val office = (book.office ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val birth = (book.birth ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val typeId = book.typeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("Book_img", it.name, requestFile)
                }

                RetrofitClient.apiService.createBook(name, price, story, writer, office, birth, typeId, pdfUrl, imagePart)
                fetchBooks()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "เพิ่มหนังสือไม่สำเร็จ: ${e.message}"
            } finally { isLoading = false }
        }
    }

    fun updateBook(id: Int, book: Book, imageFile: File?, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val name = (book.name).toRequestBody("text/plain".toMediaTypeOrNull())
                val price = "0".toRequestBody("text/plain".toMediaTypeOrNull())
                val pdfUrl = "".toRequestBody("text/plain".toMediaTypeOrNull())
                val story = (book.shortStory ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val writer = (book.writer ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val office = (book.office ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val birth = (book.birth ?: "").toRequestBody("text/plain".toMediaTypeOrNull())
                val typeId = book.typeId.toString().toRequestBody("text/plain".toMediaTypeOrNull())

                val imagePart = imageFile?.let {
                    val requestFile = it.asRequestBody("image/*".toMediaTypeOrNull())
                    MultipartBody.Part.createFormData("Book_img", it.name, requestFile)
                }

                RetrofitClient.apiService.updateBook(id, name, price, story, writer, office, birth, typeId, pdfUrl, imagePart)
                fetchBooks()
                onSuccess()
            } catch (e: Exception) {
                errorMessage = "แก้ไขหนังสือไม่สำเร็จ: ${e.message}"
            } finally { isLoading = false }
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            try {
                RetrofitClient.apiService.deleteBook(id)
                fetchBooks()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // --- USER MANAGEMENT ---
    fun fetchAllUsers() {
        viewModelScope.launch {
            try { allUsers = RetrofitClient.apiService.getAllUsers() } catch (e: Exception) {}
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try { RetrofitClient.apiService.deleteUser(userId); fetchAllUsers() } catch (e: Exception) {}
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
                    saveSession(updatedUser)
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
}
