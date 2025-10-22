package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.Quiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class QuizViewModel(
    private val repository: GameRepository = GameRepository()
): ViewModel() {

    private val _quizQuestions =
        MutableStateFlow<Resource<Quiz>>(Resource.Unspecified())
    val quizQuestions = _quizQuestions.asStateFlow()

    private val _isAllFinished = MutableStateFlow(false)
    val isAllFinished = _isAllFinished.asStateFlow()

    fun getQuizQuestions(quizId: String){
        viewModelScope.launch {
            val questions = repository.getQuizQuestions(quizId)
            questions?.let {
                _quizQuestions.emit(Resource.Success(it))
            }
        }
    }

    fun updateScore(roomId: String, score: Int){
        viewModelScope.launch {
            repository.setScore(roomId, score)
        }
    }

    fun removePlayer(roomId: String){
        viewModelScope.launch {
            repository.removePlayerFromRoom(roomId)
        }
    }

    fun setUserFinished(roomId: String){
        viewModelScope.launch {
            repository.setPlayerFinished(roomId)
        }
    }

    fun checkAllFinished(roomId: String){
        repository.checkAllFinished(roomId) {
            viewModelScope.launch {
                _isAllFinished.emit(true)
            }
        }
    }
}