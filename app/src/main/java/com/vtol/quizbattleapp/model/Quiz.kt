package com.vtol.quizbattleapp.model

data class Quiz(
    val quizId: String = "",
    val quizName: String = "",
    val quizCategory: String = "",
    val questions: List<QuizQuestion> = emptyList(),
)


