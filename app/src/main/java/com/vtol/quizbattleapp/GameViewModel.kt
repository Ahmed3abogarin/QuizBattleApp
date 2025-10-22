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

    fun loadRoom(quizId: String, roomId: String) {
        repository.observePlayers(roomId) { ids ->
            repository.fetchPlayersInfo(ids) { players ->
                viewModelScope.launch {
                    _players.emit(Resource.Success(players))
                }
            }
        }
    }
    fun joinGame(roomId: String){
        repository.joinRoom(roomId = roomId)
    }
}