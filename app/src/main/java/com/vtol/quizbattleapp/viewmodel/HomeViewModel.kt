package com.vtol.quizbattleapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.Player
import com.vtol.quizbattleapp.model.RoomWithQuiz
import com.vtol.quizbattleapp.repository.GameRepository
import com.vtol.quizbattleapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class HomeViewModel @Inject constructor(
    private val repository: GameRepository
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