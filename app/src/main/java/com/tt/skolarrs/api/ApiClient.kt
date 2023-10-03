package com.tt.skolarrs.api

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.tt.skolarrs.utilz.Constant
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object APIClient {
    //add okHttp client
    val client: ApiInterface
        get() {
            val gson: Gson = GsonBuilder().setLenient().create()

            val mHttpLoggingInterceptor = HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY)

            val mOkHttpClient = OkHttpClient
                .Builder()
                .addInterceptor(mHttpLoggingInterceptor)
                .connectTimeout(80, TimeUnit.SECONDS) // Set your connect timeout here (e.g., 30 seconds)
                .readTimeout(80, TimeUnit.SECONDS)
                .build()

            val retrofit: Retrofit = Retrofit.Builder()
                .baseUrl(Constant.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(mOkHttpClient)
                .build()
            return retrofit.create(ApiInterface::class.java)
        }



}