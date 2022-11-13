package ru.netology

import org.junit.Assert.*
import org.junit.Test

class ChatServiceTest {

    @Test
    fun addMessage() {

        ChatService.resetChats()
        val user1 = User(0x0001, "piggy", "12345")
        val user2 = User(0x0002, "chick", "54321")

        ChatService.addMessage(user1.id, user2.id, Message(createdById = user1.id, text = "Message 1"))
        ChatService.addMessage(user1.id, user2.id, Message(createdById = user1.id, text = "Message 2"))

        assertEquals(0x0002, ChatService.getChats(user2.id).last().messages.last().id)

    }

    @Test (expected = ChatNotFoundException::class)
    fun deleteChatFail() {

        ChatService.resetChats()
        val user1 = User(0x0001,"user1","12345")
        val chatId = 0x0010

        ChatService.deleteChat(user1.id,chatId)
    }

    @Test
    fun deleteChatSuccess() {

        ChatService.resetChats()
        val user1 = User(0x0001,"user1","12345")
        val user2 = User(0x0002,"user2","54321")
        ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Hello my friend! How are you?"))
        val chatId = 0x0001

        assertTrue(ChatService.deleteChat(user1.id,chatId))
    }

    @Test
    fun getUpdatedChatsCount() {

        ChatService.resetChats()
        val user1 = User(0x0001,"user1","12345")
        val user2 = User(0x0002,"user2","54321")
        val user3 = User(0x0003,"user3","qwerty")
        ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Hello my friend! How are you?"))
        ChatService.addMessage(user3.id,user2.id, Message(createdById = user3.id, text = "Let's go fishing tomorrow!"))

        assertEquals(0x0002, ChatService.getUpdatedChatsCount(user2.id))
    }

    @Test
    fun getChats() {

        ChatService.resetChats()
        val user1 = User(0x0001, "piggy", "12345")
        val user2 = User(0x0002, "chick", "54321")
        val user3 = User(0x0003, "casper", "54321")
        ChatService.addMessage(user1.id, user2.id, Message(createdById = user1.id, text = "Hello my friend! How are you?"))
        ChatService.addMessage(user1.id, user2.id, Message(createdById = user1.id, text = "Let's go fishing tomorrow!"))
        ChatService.addMessage(user2.id, user1.id, Message(createdById = user2.id, replyToMessageId = 0x0002 , text = "Hi. Mmmm. Maybe"))
        ChatService.addMessage(user2.id, user3.id, Message(createdById = user2.id, text = "I'd love to spend life with you"))
//        ChatService.addMessage(user1.id, user3.id, Message(createdById = user1.id, text = "Hi! What about going to bar this evening?"))
//        ChatService.addMessage(user3.id, user1.id, Message(createdById = user3.id, replyToMessageId = 0x0001, text = "Great idea! I'm in!"))

        // println(ChatService.getChats(user1.id)[0].messages.last().text)
        assertEquals(0x0001, ChatService.getChats(user1.id).size)
    }

    @Test
    fun getChatMessages() {

        ChatService.resetChats()
        val user1 = User(0x0001,"user1","12345")
        val user2 = User(0x0002,"user2","54321")
        val msg1 = ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Message 1"))
        val msg2 = ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Message 2"))
        val msg3 = ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Message 3"))
        val msg4 = ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Message 4"))
        val msgStartsIndex = 0x0000
        val msgReadCount = 10
        val chatId = 0x0001

        ChatService.deleteMessage(user1.id,chatId,msg2)

        assertEquals(0x0003, ChatService.getChatMessages(user2.id,chatId, msgStartsIndex,msgReadCount)?.size)
    }

    @Test
    fun editMessageSuccess() {
        ChatService.resetChats()
        val user1 = User(0x0001,"user1","12345")
        val user2 = User(0x0002,"user2","54321")
        ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Hello my friend! How are you?"))
        val msg = ChatService.addMessage(user2.id,user2.id, Message(createdById = user2.id, text = "I'm fine, thank u"))
        val chatId = 0x0001
        val correctMessage = "I'm fine, thank you"

        assertTrue(ChatService.editMessage(user2.id,chatId,msg,correctMessage))
    }

    @Test (expected = MessageNotFoundException :: class)
    fun deleteMessageFail() {

        ChatService.resetChats()
        val user1 = User(0x0001,"user1","12345")
        val user2 = User(0x0002,"user2","54321")
        val msg = ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Message 1"))
        val chatId = 0x0001

        ChatService.deleteMessage(user1.id,chatId,Message(createdById = user1.id, text = "Message without initialized Id"))
    }

    @Test
    fun deleteLastMessageInChat() {

        ChatService.resetChats()
        val user1 = User(0x0001,"user1","12345")
        val user2 = User(0x0002,"user2","54321")
        val msg = ChatService.addMessage(user1.id,user2.id, Message(createdById = user1.id, text = "Message 1"))
        val chatId = 0x0001
        val noMessages = "Нет сообщений"

        assertTrue(ChatService.deleteMessage(user1.id,chatId,msg))

        assertEquals(noMessages,ChatService.getChats(user1.id).first().messages.first().text)
    }
}