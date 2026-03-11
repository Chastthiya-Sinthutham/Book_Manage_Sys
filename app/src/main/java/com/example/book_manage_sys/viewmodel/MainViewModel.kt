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

    private val prefs = application.getSharedPreferences("session", Context.MODE_PRIVATE)

    var currentUser            by mutableStateOf<User?>(null)
    var books                  by mutableStateOf<List<Book>>(emptyList())
    var bookTypes              by mutableStateOf<List<BookType>>(emptyList())
    var favorites              by mutableStateOf<List<Book>>(emptyList())
    var filteredFavorites      by mutableStateOf<List<Book>>(emptyList())
    var userBorrows            by mutableStateOf<List<Borrow>>(emptyList())
    var allUsers               by mutableStateOf<List<User>>(emptyList())
    var isLoading              by mutableStateOf(false)
    var errorMessage           by mutableStateOf<String?>(null)
    var selectedTypeId         by mutableStateOf<Int?>(null)
    var favoriteSelectedTypeId by mutableStateOf<Int?>(null)
    var isSessionRestored      by mutableStateOf(false)

    init {
        fetchBooks()
        fetchBookTypes()
        restoreSession()
    }

    // ── Session ────────────────────────────────────────────────
    private fun saveSession(userId: Int) {
        prefs.edit().putInt("user_id", userId).apply()
    }

    private fun clearSession() {
        prefs.edit().clear().apply()
    }

    private fun restoreSession() {
        val userId = prefs.getInt("user_id", -1)
        if (userId == -1) {
            isSessionRestored = true
            return
        }
        viewModelScope.launch {
            try {
                val user = RetrofitClient.apiService.getUser(userId)
                currentUser = user
                fetchFavorites()
                fetchUserBorrows()
                if (user.role.equals("admin", ignoreCase = true)) {
                    fetchAllUsers()
                }
            } catch (e: Exception) {
                clearSession()
                e.printStackTrace()
            } finally {
                isSessionRestored = true
            }
        }
    }

    fun logout() {
        clearSession()
        currentUser            = null
        favorites              = emptyList()
        filteredFavorites      = emptyList()
        userBorrows            = emptyList()
        favoriteSelectedTypeId = null
    }

    // ── Books ──────────────────────────────────────────────────
    fun fetchBooks() {
        viewModelScope.launch {
            try { books = RetrofitClient.apiService.getBooks() }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun fetchBookTypes() {
        viewModelScope.launch {
            try { bookTypes = RetrofitClient.apiService.getBookTypes() }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun filterBooksByType(typeId: Int?) {
        selectedTypeId = typeId
        viewModelScope.launch {
            try {
                books = if (typeId == null) RetrofitClient.apiService.getBooks()
                else RetrofitClient.apiService.getBooksByType(typeId)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // ── Auth ───────────────────────────────────────────────────
    fun login(email: String, pass: String, onSuccess: () -> Unit) {
        isLoading = true
        errorMessage = null
        viewModelScope.launch {
            try {
                val user = RetrofitClient.apiService.login(
                    mapOf("email" to email, "password" to pass)
                )
                currentUser = user
                saveSession(user.id)
                fetchFavorites()
                fetchUserBorrows()
                if (user.role.equals("admin", ignoreCase = true)) {
                    fetchAllUsers()
                }
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = if (e.code() == 401) "อีเมลหรือรหัสผ่านไม่ถูกต้อง"
                else "เกิดข้อผิดพลาด: ${e.message()}"
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
                RetrofitClient.apiService.register(
                    mapOf("name" to name, "email" to email, "password" to pass)
                )
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = "Registration Failed: ${e.response()?.errorBody()?.string() ?: e.message()}"
            } catch (e: Exception) {
                errorMessage = "ไม่สามารถเชื่อมต่อเซิร์ฟเวอร์ได้"
            } finally {
                isLoading = false
            }
        }
    }

    // ── Users ──────────────────────────────────────────────────
    fun fetchAllUsers() {
        viewModelScope.launch {
            try { allUsers = RetrofitClient.apiService.getAllUsers() }
            catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun deleteUser(userId: Int) {
        viewModelScope.launch {
            try { RetrofitClient.apiService.deleteUser(userId); fetchAllUsers() }
            catch (e: Exception) { e.printStackTrace() }
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
                val photoPart = imageFile?.let {
                    MultipartBody.Part.createFormData("profile_photo", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
                }
                RetrofitClient.apiService.updateUser(
                    userId,
                    name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    phoneNumber.toRequestBody("text/plain".toMediaTypeOrNull()),
                    age.toRequestBody("text/plain".toMediaTypeOrNull()),
                    gender.toRequestBody("text/plain".toMediaTypeOrNull()),
                    role.toRequestBody("text/plain".toMediaTypeOrNull()),
                    photoPart
                )
                if (currentUser?.id == userId) {
                    currentUser = RetrofitClient.apiService.getUser(userId)
                }
                onSuccess()
            } catch (e: HttpException) {
                errorMessage = "Server Error (${e.code()}): ${e.response()?.errorBody()?.string() ?: e.message()}"
                e.printStackTrace()
            } catch (e: Exception) {
                errorMessage = "Error: ${e.localizedMessage}"
                e.printStackTrace()
            } finally {
                isLoading = false
            }
        }
    }

    // ── Borrow ─────────────────────────────────────────────────
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

    fun fetchUserBorrows() {
        val user = currentUser ?: return
        viewModelScope.launch {
            try {
                val borrows = if (user.role.equals("admin", ignoreCase = true))
                    RetrofitClient.apiService.getAllBorrows()
                else RetrofitClient.apiService.getUserBorrows(user.id)
                userBorrows = borrows
                checkAndExpireBorrows(borrows)
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    private fun checkAndExpireBorrows(borrows: List<Borrow>) {
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val now = Calendar.getInstance(TimeZone.getTimeZone("UTC")).time
        borrows.forEach { borrow ->
            if (borrow.pickupStatus == "pending") {
                try {
                    val d = sdf.parse(borrow.borrowDate)
                    if (d != null && (now.time - d.time) / (1000 * 60 * 60) >= 24) {
                        viewModelScope.launch {
                            try { RetrofitClient.apiService.updateBorrowStatus(borrow.id, mapOf("status" to "forget")) } catch (_: Exception) {}
                        }
                    }
                } catch (_: Exception) {}
            } else if (borrow.pickupStatus == "picked_up") {
                try {
                    val d = sdf.parse(borrow.borrowDate)
                    if (d != null) {
                        val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                        cal.time = d
                        cal.add(Calendar.DAY_OF_YEAR, 5)
                        if (now.after(cal.time)) {
                            viewModelScope.launch {
                                try { RetrofitClient.apiService.updateBorrowStatus(borrow.id, mapOf("status" to "no_returned")) } catch (_: Exception) {}
                            }
                        }
                    }
                } catch (_: Exception) {}
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
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    // ── Favorites ──────────────────────────────────────────────
    fun fetchFavorites() {
        val userId = currentUser?.id ?: return
        viewModelScope.launch {
            try {
                val favs = RetrofitClient.apiService.getFavorites(userId)
                favorites = favs
                applyFavoriteFilter()
            } catch (e: Exception) { e.printStackTrace() }
        }
    }

    fun filterFavoritesByType(typeId: Int?) {
        favoriteSelectedTypeId = typeId
        applyFavoriteFilter()
    }

    private fun applyFavoriteFilter() {
        filteredFavorites = if (favoriteSelectedTypeId == null) favorites
        else favorites.filter { it.typeId == favoriteSelectedTypeId }
    }

    fun toggleFavorite(bookId: Int) {
        val userId = currentUser?.id ?: return
        val isFav = favorites.any { it.id == bookId }
        // Optimistic update
        if (isFav) {
            favorites = favorites.filter { it.id != bookId }
        } else {
            val bookToAdd = books.find { it.id == bookId }
            if (bookToAdd != null) favorites = favorites + bookToAdd
        }
        applyFavoriteFilter()
        viewModelScope.launch {
            try {
                if (isFav) RetrofitClient.apiService.removeFavorite(mapOf("user_id" to userId, "book_id" to bookId))
                else RetrofitClient.apiService.addFavorite(mapOf("user_id" to userId, "book_id" to bookId))
                val favs = RetrofitClient.apiService.getFavorites(userId)
                favorites = favs
                applyFavoriteFilter()
            } catch (e: Exception) {
                fetchFavorites()
                e.printStackTrace()
            }
        }
    }

    // ── Books CRUD ─────────────────────────────────────────────
    fun addBook(book: Book, imageFile: File?, onSuccess: () -> Unit) {
        isLoading = true; errorMessage = null
        viewModelScope.launch {
            try {
                val imagePart = imageFile?.let {
                    MultipartBody.Part.createFormData("Book_img", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
                }
                RetrofitClient.apiService.createBook(
                    book.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "Nan".toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.shortStory ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.writer ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.office ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.birth ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    book.typeId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    "Nan".toRequestBody("text/plain".toMediaTypeOrNull()),
                    imagePart
                )
                fetchBooks(); onSuccess()
            } catch (e: HttpException) {
                errorMessage = "Server Error: ${e.response()?.errorBody()?.string() ?: e.message()}"; e.printStackTrace()
            } catch (e: Exception) {
                errorMessage = "เพิ่มหนังสือไม่สำเร็จ: ${e.message}"; e.printStackTrace()
            } finally { isLoading = false }
        }
    }

    fun updateBook(id: Int, book: Book, imageFile: File?, onSuccess: () -> Unit) {
        isLoading = true; errorMessage = null
        viewModelScope.launch {
            try {
                val imagePart = imageFile?.let {
                    MultipartBody.Part.createFormData("Book_img", it.name, it.asRequestBody("image/*".toMediaTypeOrNull()))
                }
                RetrofitClient.apiService.updateBook(
                    id,
                    book.name.toRequestBody("text/plain".toMediaTypeOrNull()),
                    "Nan".toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.shortStory ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.writer ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.office ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    (book.birth ?: "").toRequestBody("text/plain".toMediaTypeOrNull()),
                    book.typeId.toString().toRequestBody("text/plain".toMediaTypeOrNull()),
                    "Nan".toRequestBody("text/plain".toMediaTypeOrNull()),
                    imagePart
                )
                fetchBooks(); onSuccess()
            } catch (e: HttpException) {
                errorMessage = "Server Error: ${e.response()?.errorBody()?.string() ?: e.message()}"; e.printStackTrace()
            } catch (e: Exception) {
                errorMessage = "แก้ไขข้อมูลไม่สำเร็จ: ${e.message}"; e.printStackTrace()
            } finally { isLoading = false }
        }
    }

    fun deleteBook(id: Int) {
        viewModelScope.launch {
            try { RetrofitClient.apiService.deleteBook(id); fetchBooks() }
            catch (e: Exception) { e.printStackTrace() }
        }
    }
}