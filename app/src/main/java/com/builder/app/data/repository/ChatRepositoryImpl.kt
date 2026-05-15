package com.builder.app.data.repository

import com.builder.app.core.utils.Resource
import com.builder.app.domain.model.Chat
import com.builder.app.domain.model.Mensaje
import com.builder.app.domain.repository.ChatRepository
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : ChatRepository {

    override fun getMessages(chatId: String): Flow<Resource<List<Mensaje>>> = callbackFlow {
        trySend(Resource.Loading())
        val subscription = firestore.collection("chats")
            .document(chatId)
            .collection("mensajes")
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                val mensajes = snapshot?.toObjects(Mensaje::class.java) ?: emptyList()
                trySend(Resource.Success(mensajes))
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun sendMessage(chatId: String, mensaje: Mensaje): Resource<Unit> {
        return try {
            firestore.collection("chats")
                .document(chatId)
                .collection("mensajes")
                .add(mensaje)
                .await()
            
            // Actualizar último mensaje en el chat
            firestore.collection("chats")
                .document(chatId)
                .update(mapOf(
                    "ultimoMensaje" to mensaje.texto,
                    "timestamp" to mensaje.timestamp
                )).await()
                
            Resource.Success(Unit)
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al enviar mensaje")
        }
    }

    override fun getUserChats(userId: String): Flow<Resource<List<Chat>>> = callbackFlow {
        trySend(Resource.Loading())
        val subscription = firestore.collection("chats")
            .whereArrayContains("participantes", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Resource.Error(error.message ?: "Error desconocido"))
                    return@addSnapshotListener
                }
                val chats = snapshot?.toObjects(Chat::class.java)
                    ?.sortedByDescending { it.timestamp }
                    ?: emptyList()
                trySend(Resource.Success(chats))
            }
        awaitClose { subscription.remove() }
    }

    override suspend fun getOrCreateChat(
        user1Id: String,
        user2Id: String,
        user1Name: String,
        user2Name: String
    ): Resource<String> {
        return try {
            val query = firestore.collection("chats")
                .whereArrayContains("participantes", user1Id)
                .get()
                .await()
            
            val existingChat = query.documents.find { doc ->
                val participantes = doc.get("participantes") as? List<*>
                participantes?.contains(user2Id) == true
            }

            if (existingChat != null) {
                Resource.Success(existingChat.id)
            } else {
                val newChatRef = firestore.collection("chats").document()
                val chat = Chat(
                    id = newChatRef.id,
                    participantes = listOf(user1Id, user2Id),
                    nombresParticipantes = mapOf(user1Id to user1Name, user2Id to user2Name),
                    ultimoMensaje = "Inicia una conversación",
                    timestamp = System.currentTimeMillis()
                )
                newChatRef.set(chat).await()
                Resource.Success(newChatRef.id)
            }
        } catch (e: Exception) {
            Resource.Error(e.message ?: "Error al obtener o crear chat")
        }
    }
}
