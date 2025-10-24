package com.vtol.quizbattleapp.model

data class RoomWithQuiz(
    val room: GameRoom,
    val quiz: Quiz?
)