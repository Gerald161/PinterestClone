package com.example.pinterestclone.api
import com.example.pinterestclone.model.PinObject
import com.example.pinterestclone.model.ResponseStatus
import okhttp3.MultipartBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path

interface SimpleApi {
    @GET("/pins/")
    suspend fun GetPins(): Response<List<PinObject>>

    @Multipart
    @POST("/pins/")
    suspend fun UploadPin(
        @Part image: MultipartBody.Part,
        @Part title: MultipartBody.Part
    ): Response<ResponseStatus>

    @Multipart
    @POST("/search/")
    suspend fun ImageSearch(
        @Part image: MultipartBody.Part,
    ): Response<List<PinObject>>

    @GET("/search/{word}")
    suspend fun WordSearch(
        @Path("word") word: String,
    ): Response<List<PinObject>>
}