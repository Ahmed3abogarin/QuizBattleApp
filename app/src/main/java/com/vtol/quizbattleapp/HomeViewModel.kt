package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.Player
import com.vtol.quizbattleapp.model.RoomWithQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel(
    private val repository: GameRepository = GameRepository(),
) : ViewModel() {

    private val _rooms =
        MutableStateFlow<Resource<List<RoomWithQuiz>>>(Resource.Unspecified())
    val rooms = _rooms.asStateFlow()


    private val _name = MutableStateFlow<Resource<Player>>(Resource.Unspecified())
    var name = _name.asSharedFlow()

    init {
        fetchQuizzes()
        getUserName()
    }

    private fun fetchQuizzes() {
        viewModelScope.launch {
            repository.observeRooms().collect {
                _rooms.emit(it)
            }
        }

    }

    private fun getUserName(){
        viewModelScope.launch {
            repository.getUserName().collect {
                _name.emit(it)
            }
        }


    }

    fun signOut(){
        repository.signOut()

    }
}