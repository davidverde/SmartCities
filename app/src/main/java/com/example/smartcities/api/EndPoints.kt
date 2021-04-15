package com.example.smartcities.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @GET("myslim/api/utilizadores") // pedido GET para receber a lista de utilizadores
    fun getUsers(): Call<List<User>>

    @GET("myslim/api/utilizadores/{id}") // pedido GET para receber um utilizador em especifico
    fun getUserById(@Path("id") id: Int): Call<List<User>>

    @GET("myslim/api/anomalias") // pedido GET para receber a lista de anomalias
    fun getAnomalias(): Call<List<Anomalia>>

    @FormUrlEncoded
    @POST("myslim/api/utl") // pedido POST para verificar o login
    fun postUtl(@Field("email") first: String, @Field("pass") second: String,): Call<List<OutputPost>>

}