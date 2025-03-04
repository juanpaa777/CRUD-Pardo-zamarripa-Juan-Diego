package com.example.crud2

data class Mascota(
    val id: Int,
    val nombre: String,
    val especie: String,
    val raza: String,
    val edad: Int,
    val vacunada: Boolean,
    val imagen: String
)