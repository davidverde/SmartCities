package com.example.smartcities.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("myslim/api/utilizadores")
    fun getUsers(): Call<List<User>>

    @GET("myslim/api/utilizadores/{id}")
    fun getUserById(@Path("id") id: Int): Call<User>

    @FormUrlEncoded
    @POST("myslim/api/utl")
    fun postUtl(@Field("email") first: String, @Field("pass") second: String,): Call<List<OutputPost>>

}