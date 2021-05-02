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

    @FormUrlEncoded
    @POST("myslim/api/anomalia/editar") // pedido POST para editar anomalia
    fun editarAnom(@Field("id_amon") first: Int, @Field("titulo") second: String,
                   @Field("descricao") third: String, @Field("tipo") fourth: String): Call<List<EditarAnom>>

    @FormUrlEncoded
    @POST("myslim/api/anomalia/delete") // pedido POST para eliminar anomalia
    fun eliminarAnom(@Field("id_amon") first: Int): Call<List<DeleteAnom>>

    @FormUrlEncoded
    @POST("myslim/api/anomalia") // pedido POST para reportar anomalia
    fun addAnom(@Field("utilizador_id") first: Int, @Field("titulo") second: String,
                   @Field("descricao") third: String, @Field("tipo") fourth: String,
                    @Field("imagem") fifth: String, @Field("latitude") sixth: Float,
                     @Field("longitude") seventh: Float): Call<Anomalia>

}