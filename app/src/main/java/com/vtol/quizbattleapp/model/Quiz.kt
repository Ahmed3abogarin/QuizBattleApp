package com.vtol.quizbattleapp.model

data class Quiz(
    val quizId: String = "",
    val quizName: String = "",
    val quizCategory: String = "",
    val hostId: String = "",
    val players: List<String> = emptyList(),
    val questions: List<QuizQuestion> = emptyList(),
    val status: String = "waiting" // "waiting", "started", "ended"
)


