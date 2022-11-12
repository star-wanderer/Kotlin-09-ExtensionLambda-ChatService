package ru.netology

class ChatNotFoundException(chatId: Int) : RuntimeException ("Chat with id: $chatId was not found")