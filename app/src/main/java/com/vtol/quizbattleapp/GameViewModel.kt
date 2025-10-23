package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: GameRepository = GameRepository(),
) : ViewModel() {

    private val _players =
        MutableStateFlow<Resource<List<Player>>>(Resource.Unspecified())
    val players = _players.asStateFlow()

    fun loadRoom(roomId: String) {
        viewModelScope.launch {
            repository.observePlayersWithInfo(roomId).collect {
                _players.emit(it)
            }
        }

    }
    fun joinGame(roomId: String){
        repository.joinRoom(roomId = roomId)
    }

    fun exitRoom(roomId: String){
        repository.removePlayerFromRoom(roomId)
    }
}