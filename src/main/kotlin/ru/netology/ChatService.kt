package ru.netology

object ChatService {

    private var chats = mutableListOf<Chat>()

    fun resetChats(){
        chats = mutableListOf()
    }

    private fun addChat(sendToId: Int, message: Message) : Chat {
        chats.add(
            Chat(
                id = if (chats.isEmpty()) 0x0001 else chats.last().id + 1,
                createdById = message.createdById,
                companionId1 = message.createdById,
                companionId2 = sendToId,
                messages = mutableListOf(message),
                hasNewMessages = true
            )
        )
        return chats.last()
    }

    fun deleteChat(userId: Int, chatId: Int) : Boolean {
        val targetChat = chats.find { !it.isDeleted && it.createdById == userId && it.id == chatId }
        targetChat?: throw ChatNotFoundException(chatId)
        targetChat.isDeleted = true
        targetChat.messages.forEach { it.isDeleted = true }
        return targetChat.isDeleted
    }

    fun getUpdatedChatsCount(userId: Int): Int {
        return chats.count { !it.isDeleted && it.createdById != userId && it.messages.last().isNew }
    }

    fun getChats(userId: Int): List<Chat>? {
        val userChats = chats.filter { !it.isDeleted && it.createdById == userId && it.messages.last() != null }
        return userChats.ifEmpty { println("Нет сообщений"); return null }
    }

    fun getChatMessages(userId: Int, chatId: Int, msgIdStarts: Int, msgCount: Int): List<Message>? {
        val oneChatList = chats.filter { !it.isDeleted && it.id == chatId }
        var messages = oneChatList.first().messages.filter { !it.isDeleted && it.createdById != userId }
        messages = messages.subList(msgIdStarts, messages.last().id-1).chunked(msgCount)[0]
        return if (messages.isEmpty()) null
        else {
            messages.forEach { it.isNew = false }; messages
        }
    }

    fun addMessage(userId: Int, sendToId: Int, message: Message) : Message {
        val targetChat = chats.find {
            (it.companionId1 == sendToId && it.companionId2 == userId) || (it.companionId1 == userId && it.companionId2 == sendToId)
        }
        targetChat?: run {var message = message.copy(id = 0x0001)
            addChat(sendToId, message); return message }
        var message = message.copy(id = targetChat.messages.last().id + 1)
        targetChat.messages.add(message)
        return message
    }

    fun editMessage(userId: Int, chatId: Int, message: Message, newText: String) : Boolean {
        val targetChat = chats.find { it.id == chatId }
        targetChat ?: throw ChatNotFoundException(chatId)
        val targetMessage = targetChat.messages.find { !it.isDeleted && it.id == message.id && message.createdById == userId }
        targetMessage?: throw MessageNotFoundException(userId, message.id)
        targetMessage.isUpdated = true
        targetMessage.text = newText
        return true
    }

    fun deleteMessage(userId: Int, chatId: Int, message: Message) : Boolean {
        val targetChat = chats.find { it.id == chatId }
        targetChat ?: throw ChatNotFoundException(chatId)
        var targetMessage = targetChat.messages.find { !it.isDeleted && it.id == message.id && message.createdById == userId }
        targetMessage?: throw MessageNotFoundException(userId, message.id)
        targetMessage.isDeleted = true
        if (targetChat.messages.none { !it.isDeleted }) targetChat.isDeleted = true
        return true
    }
}