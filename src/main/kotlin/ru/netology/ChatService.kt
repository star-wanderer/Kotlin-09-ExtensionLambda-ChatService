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

    fun getChats(userId: Int): List<Chat> {
        val userChats = chats.filter {  !it.isDeleted &&  it.messages.last().isNew && it.messages.last().createdById != userId
                && (it.companionId1 == userId || it.companionId2 == userId) }
        return userChats.ifEmpty { return listOf(Chat(messages = mutableListOf(Message(text = "Нет сообщений")))) }
    }

    fun getChatMessages(userId: Int, chatId: Int, msgIdStarts: Int, msgCount: Int): List<Message> {
        var messages = chats.find { !it.isDeleted && it.id == chatId }
            .let{ it?.messages ?:throw ChatNotFoundException(chatId)}
            .asSequence()
            .filter { !it.isDeleted && it.createdById != userId && it.id >= msgIdStarts }
            .take( msgCount )
            .toList()
        messages.forEach{ it.isNew = false }
        return messages
    }

    fun addMessage(userId: Int, sendToId: Int, message: Message) : Message {
        val targetChat = chats.find {
            (it.companionId1 == sendToId && it.companionId2 == userId)
                    || (it.companionId1 == userId && it.companionId2 == sendToId)
        }
        targetChat?: run {
            var message = message.copy(id = 0x0001)
            addChat(sendToId, message);
            return message
        }
        var message = message.copy(id = targetChat.messages.last().id + 1)
        targetChat.messages.add(message)
        return message
    }

    fun editMessage(userId: Int, chatId: Int, message: Message, newText: String) : Boolean {
        var message = chats.find { !it.isDeleted && it.id == chatId }
             .let { it?.messages ?:throw ChatNotFoundException(chatId)}
             .find { !it.isDeleted && it.id == message.id && message.createdById == userId }
             .let { it ?:throw MessageNotFoundException(userId, message.id)}
        message.isUpdated = false;
        message.text = newText
        return true
    }

    fun deleteMessage(userId: Int, chatId: Int, message: Message) : Boolean {
        var chat = chats.find { !it.isDeleted && it.id == chatId }
            .let { it?: throw ChatNotFoundException(chatId)}
        var message =  chat.messages
            .find { !it.isDeleted && it.id == message.id && message.createdById == userId }
            .let { it ?: throw MessageNotFoundException(userId, message.id) }
        message.isDeleted = true
      if ( chat.messages.none { !it.isDeleted } ) chat.isDeleted = true
        return true
    }
}