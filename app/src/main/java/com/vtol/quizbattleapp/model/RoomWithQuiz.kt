package com.vtol.quizbattleapp.model


data class GameRoom(
    val id: String = "",
    val quizId: String ="",
    val status: String = "waiting", // waiting, started, finished
    val playerIds: Map<String, PlayerData> = emptyMap(),
)

data class PlayerData(
    val score: Int = 0,
    val hasFinished: Boolean = false
)

