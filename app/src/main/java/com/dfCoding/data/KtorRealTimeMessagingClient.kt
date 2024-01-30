package com.dfCoding.data

import io.ktor.client.*
import io.ktor.client.features.websocket.*
import io.ktor.client.request.*
import io.ktor.http.cio.websocket.*
import kotlinx.coroutines.flow.*
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class KtorRealTimeMessagingClient(
    private val client: HttpClient = TODO()
): RealtimeMessagingClient  {
    private var session: WebSocketSession? = null

    override fun getGameStateStream(): Flow<GameState> {
        return flow {
            session = client.webSocketSession {
                url("ws://10.100.102.9:8081/play") //ipconfig
//                url("ws://<YOUR IP>/pla y") //ipconfig --> Wireless LAN adapter Wi-Fi -->   IPv4 Address. . . . . . . .
            }
            val gameStates = session!!
                .incoming
                .consumeAsFlow()
                .filterIsInstance<Frame.Text>()
                .mapNotNull { Json.decodeFromString<GameState>(it.readText()) }

            emitAll(gameStates)
        }
    }

    override suspend fun sendAction(action: MakeTurn) {
        session?.outgoing?.send(
            Frame.Text("make_turn#${Json.encodeToString(action)}")
        )
    }

    override suspend fun close() {
        TODO("Not yet implemented")
    }


}