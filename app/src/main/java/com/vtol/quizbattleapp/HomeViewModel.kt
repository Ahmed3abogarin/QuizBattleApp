package com.vtol.quizbattleapp

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.RoomWithQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: GameRepository = GameRepository(),
) : ViewModel() {

    private val _quizQuestions =
        MutableStateFlow<Resource<List<RoomWithQuiz>>>(Resource.Unspecified())
    val quizQuestions = _quizQuestions.asStateFlow()


    private val _name = MutableStateFlow("")
    var name = _name.asSharedFlow()

    init {
        fetchQuizQuestions()
        getUserName()
    }

    private fun fetchQuizQuestions() {
        repository.observeAllRooms { rooms ->
            Log.v("MMMMM","size ${rooms.size}")
            viewModelScope.launch {
                val roomWithQuizzes = rooms.map { room ->
                    val quiz = repository.getQuiz(room.quizId)
                    RoomWithQuiz(room, quiz)
                }
                _quizQuestions.emit(Resource.Success(roomWithQuizzes))
            }
        }
    }

    private fun getUserName(){
        repository.getUserName {
            viewModelScope.launch {
                _name.emit(it?.playerName ?: "")
            }

        }

    }

    fun signOut(){
        repository.signOut()

    }
}