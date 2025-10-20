package com.vtol.quizbattleapp.model

data class QuizQuestion(
    val question: String = "",
    val optionA: String = "",
    val optionB: String = "",
    val optionC: String = "",
    val optionD: String = "",
    val correctAnswer: String = ""
)