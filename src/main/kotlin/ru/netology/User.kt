package ru.netology

data class User(
    override val id: Int,
    val login: String,
    val password: String
): Entity (id)