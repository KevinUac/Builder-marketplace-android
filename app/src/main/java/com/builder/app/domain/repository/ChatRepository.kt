package com.builder.app.domain.repository

import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.Chat
import com.builder.app.domain.model.Mensaje
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getMessages(chatId: String): Flow<Resource<List<Mensaje>>>
    suspend fun sendMessage(chatId: String, mensaje: Mensaje): Resource<Unit>
    fun getUserChats(userId: String): Flow<Resource<List<Chat>>>
    suspend fun getOrCreateChat(user1Id: String, user2Id: String, user1Name: String, user2Name: String): Resource<String>
}
