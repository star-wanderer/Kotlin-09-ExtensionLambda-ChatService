package ru.netology

class MessageNotFoundException(userId: Int, msgId: Int) : RuntimeException ("Message with id: $msgId from userId: $userId was not found") {
}