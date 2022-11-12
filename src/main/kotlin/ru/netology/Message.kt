package ru.netology

data class Message (
    val id: Int = 0x0000,
    val createdById: Int = 0x0000,
    val replyToMessageId: Int = 0x0000,
    var text: String = "",
    var isNew: Boolean = true,
    var isUpdated: Boolean = false,
    var isDeleted: Boolean = false,
    val dateCreated: Int = (System.currentTimeMillis()/1000).toInt()
)