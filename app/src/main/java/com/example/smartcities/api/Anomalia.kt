package com.example.smartcities.api

data class Anomalia(
    val id_amon: Int,
    val utilizador_id: Int,
    val titulo: String,
    val descricao: String,
    val imagem: String,
    val latitude: Double,
    val longitude: Double,
)
