package ru.netology

data class Chat (
    val id: Int = 0x0000,
    val createdById: Int = 0x0000,
    val companionId1: Int = 0x0000,
    val companionId2: Int = 0x0000,
    val messages: MutableList<Message>,
    val hasNewMessages: Boolean = false,
    var isDeleted: Boolean = false,
    val dateCreated: Int = (System.currentTimeMillis()/1000).toInt()
)