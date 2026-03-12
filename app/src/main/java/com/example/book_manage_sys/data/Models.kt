package com.example.book_manage_sys.data

import com.google.gson.annotations.SerializedName

data class BookType(
    val id: Int,
    @SerializedName("Book_type_name") val bookTypeName: String
)

data class Book(
    val id: Int = 0,
    @SerializedName("fav_id") val favId: Int? = null, // เพิ่มสำหรับเก็บ id จาก users_book_favs
    @SerializedName("Book_price") val price: Double = 0.0,
    @SerializedName("Book_name") val name: String = "",
    @SerializedName("Book_img") val img: String? = null,
    @SerializedName("Book_PDF_URL") val pdfUrl: String? = null,
    @SerializedName("Book_short_story") val shortStory: String? = null,
    @SerializedName("Book_birth") val birth: String? = null,
    @SerializedName("Book_office") val office: String? = null,
    @SerializedName("Book_writer") val writer: String? = null,
    @SerializedName("Book_type_ID") val typeId: Int = 0,
    @SerializedName("Book_type_name") val typeName: String? = null,
    val status: Int = 0,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    val role: String,
    @SerializedName("phone_number") val phoneNumber: String? = null,
    val age: Int? = null,
    val gender: String? = null,
    @SerializedName("profile_photo_path") val profilePhotoPath: String? = null,
    @SerializedName("created_at") val createdAt: String? = null
)

data class Borrow(
    val id: Int,
    @SerializedName("user_id") val userId: Int,
    @SerializedName("book_id") val bookId: Int,
    @SerializedName("borrow_date") val borrowDate: String,
    @SerializedName("pickup_status") val pickupStatus: String,
    @SerializedName("Book_name") val bookName: String?,
    @SerializedName("Book_img") val bookImg: String?,
    @SerializedName("Book_writer") val bookWriter: String?,
    @SerializedName("Book_office") val bookOffice: String?,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ApiResponse(
    val error: Boolean? = null,
    val message: String? = null,
    val insertId: Int? = null
)

// Data class สำหรับส่ง Request Favorite
data class FavoriteRequest(
    @SerializedName("user_id") val userId: Int,
    @SerializedName("book_id") val bookId: Int
)
