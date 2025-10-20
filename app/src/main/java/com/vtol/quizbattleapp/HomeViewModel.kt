package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.RoomWithQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: GameRepository = GameRepository(),
) : ViewModel() {

    private val _quizQuestions =
        MutableStateFlow<Resource<List<RoomWithQuiz>>>(Resource.Unspecified())
    val quizQuestions = _quizQuestions.asStateFlow()

    init {
        fetchQuizQuestions()
    }
    private fun fetchQuizQuestions() {
        repository.observeAllRooms { rooms ->
            viewModelScope.launch {
                val roomWithQuizzes = rooms.map { room ->
                    val quiz = repository.getQuiz(room.quizId)
                    RoomWithQuiz(room, quiz)
                }
                _quizQuestions.emit(Resource.Success(roomWithQuizzes))
            }
        }
    }
}