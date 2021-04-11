package com.example.smartcities.api

data class User(    // os nomes dos val tem de ser iguais aos nomes do JSON
        val id_utl: Int,
        val nome: String,
        val email: String,
        val pass: String,
)