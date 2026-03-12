package com.example.book_manage_sys.network

import com.example.book_manage_sys.data.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.http.*

interface ApiService {
    @GET("api/book-types")
    suspend fun getBookTypes(): List<BookType>

    @GET("api/books")
    suspend fun getBooks(): List<Book>

    @GET("api/books/search")
    suspend fun searchBooks(@Query("q") query: String): List<Book>

    @GET("api/books/type/{typeId}")
    suspend fun getBooksByType(@Path("typeId") typeId: Int): List<Book>

    @GET("api/books/{id}")
    suspend fun getBookById(@Path("id") id: Int): Book

    @Multipart
    @POST("api/books")
    suspend fun createBook(
        @Part("Book_name") name: RequestBody,
        @Part("Book_price") price: RequestBody,
        @Part("Book_short_story") shortStory: RequestBody,
        @Part("Book_writer") writer: RequestBody,
        @Part("Book_office") office: RequestBody,
        @Part("Book_birth") birth: RequestBody,
        @Part("Book_type_ID") typeId: RequestBody,
        @Part("Book_PDF_URL") pdfUrl: RequestBody,
        @Part image: MultipartBody.Part?
    ): ApiResponse

    @Multipart
    @PUT("api/books/{id}")
    suspend fun updateBook(
        @Path("id") id: Int,
        @Part("Book_name") name: RequestBody,
        @Part("Book_price") price: RequestBody,
        @Part("Book_short_story") shortStory: RequestBody,
        @Part("Book_writer") writer: RequestBody,
        @Part("Book_office") office: RequestBody,
        @Part("Book_birth") birth: RequestBody,
        @Part("Book_type_ID") typeId: RequestBody,
        @Part("Book_PDF_URL") pdfUrl: RequestBody,
        @Part image: MultipartBody.Part?
    ): ApiResponse

    @DELETE("api/books/{id}")
    suspend fun deleteBook(@Path("id") id: Int): ApiResponse

    @GET("api/borrows")
    suspend fun getAllBorrows(): List<Borrow>

    @POST("api/borrows")
    suspend fun borrowBook(@Body body: Map<String, Int>): ApiResponse

    @GET("api/borrows/user/{userId}")
    suspend fun getUserBorrows(@Path("userId") userId: Int): List<Borrow>

    @PATCH("api/borrows/{id}/status")
    suspend fun updateBorrowStatus(@Path("id") id: Int, @Body body: Map<String, String>): ApiResponse

    @POST("api/register")
    suspend fun register(@Body body: Map<String, String>): ApiResponse

    @POST("api/login")
    suspend fun login(@Body body: Map<String, String>): User

    @GET("api/users")
    suspend fun getAllUsers(): List<User>

    @GET("api/users/{id}")
    suspend fun getUser(@Path("id") id: Int): User

    @Multipart
    @PUT("api/users/{id}")
    suspend fun updateUser(
        @Path("id") id: Int,
        @Part("name") name: RequestBody,
        @Part("phone_number") phoneNumber: RequestBody,
        @Part("age") age: RequestBody,
        @Part("gender") gender: RequestBody,
        @Part("role") role: RequestBody,
        @Part profile_photo: MultipartBody.Part?
    ): ApiResponse

    @DELETE("api/users/{id}")
    suspend fun deleteUser(@Path("id") id: Int): ApiResponse

    @GET("api/favorites/{userId}")
    suspend fun getFavorites(@Path("userId") userId: Int): List<Book>

    @POST("api/favorites")
    suspend fun addFavorite(@Body request: FavoriteRequest): ApiResponse

    @HTTP(method = "DELETE", path = "api/favorites", hasBody = true)
    suspend fun removeFavorite(@Body request: FavoriteRequest): ApiResponse
}
