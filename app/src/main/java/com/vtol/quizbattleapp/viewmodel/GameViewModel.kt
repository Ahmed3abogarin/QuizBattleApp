package com.vtol.quizbattleapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.Player
import com.vtol.quizbattleapp.repository.GameRepository
import com.vtol.quizbattleapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class GameViewModel @Inject constructor(
    private val repository: GameRepository
): ViewModel() {

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