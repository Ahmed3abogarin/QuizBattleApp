package com.vtol.quizbattleapp.model

data class QuizQuestion(
    val questionText: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswerIndex: Int = 0
)