package com.example.pinterestclone.api

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object RetrofitInstance {
    val client: OkHttpClient.Builder = OkHttpClient.Builder()
        .callTimeout(10, TimeUnit.MINUTES)
        .readTimeout(10, TimeUnit.MINUTES)
        .connectTimeout(10, TimeUnit.MINUTES)
        .writeTimeout(10, TimeUnit.MINUTES)

    val api: SimpleApi by lazy{
        Retrofit.Builder()
            .baseUrl("http://10.0.2.2:8000")
            .client(client.build())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(SimpleApi::class.java)
    }
}