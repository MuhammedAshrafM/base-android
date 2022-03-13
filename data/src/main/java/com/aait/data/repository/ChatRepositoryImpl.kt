package com.aait.data.repository

import com.aait.data.datasource.ChatDataSource
import com.aait.data.datasource.SocketDataSource
import com.aait.data.remote.utils.safeApiCall
import com.aait.domain.entities.BaseResponse
import com.aait.domain.entities.ChatResponse
import com.aait.domain.entities.RoomsResponse
import com.aait.domain.repository.ChatRepository
import com.aait.domain.util.DataState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flow
import org.json.JSONObject
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDataSource: ChatDataSource,
    private val socketDataSource: SocketDataSource
) : ChatRepository {

    override suspend fun getRooms(): Flow<DataState<BaseResponse<RoomsResponse>>> = safeApiCall {
        chatDataSource.getRooms()
    }

    override suspend fun getChatMessages(roomId: Int): Flow<DataState<BaseResponse<ChatResponse>>> =
        safeApiCall {
            chatDataSource.getChatMessages(roomId)
        }


    override fun connectSocket(): Flow<Boolean> = flow {
        socketDataSource.connectSocket().collect {
            emit(it)
        }
    }

    override fun disconnectSocket() = socketDataSource.disconnectSocket()

    override suspend fun addUser(key: String, jsonObject: JSONObject) {
        socketDataSource.setEmit(key, jsonObject)
    }

    override suspend fun exitChat(key: String, jsonObject: JSONObject) {
        socketDataSource.setEmit(key, jsonObject)
    }

    override suspend fun sendMessage(key: String, jsonObject: JSONObject) {
        socketDataSource.setEmit(key, jsonObject)
    }

    override suspend fun onMessageReceived(key: String): Flow<List<String>> = flow {
        emitAll(socketDataSource.openChannel(key))
    }

}