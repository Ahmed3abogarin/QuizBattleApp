package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.PlayerWithScore
import com.vtol.quizbattleapp.model.RoomWithQuiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(
    private val repository: GameRepository = GameRepository()
): ViewModel() {

    private val _scores =
        MutableStateFlow<Resource<Map<String, Int>>>(Resource.Unspecified())
    val scores = _scores.asStateFlow()


//    fun loadRoom(roomId: String) {
//        repository.getPlayers(roomId) { ids ->
//            repository.fetchPlayersInfo(ids) { players ->
//                players.map { PlayerWithScore(it) }
//            }
//        }
//    }


//    fun getRoom(roomId: String){
//
//        repository.getRoom("aa1"){ room ->
//                room.playerIds.map { id ->
//                    val info = repository.get
//                }
//        }
//    }

}