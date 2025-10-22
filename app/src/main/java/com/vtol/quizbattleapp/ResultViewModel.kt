package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.PlayerWithScore
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class ResultViewModel(
    private val repository: GameRepository = GameRepository()
): ViewModel() {

    private val _scores =
        MutableStateFlow<Resource<List<PlayerWithScore>>>(Resource.Unspecified())
    val scores = _scores.asStateFlow()


    fun loadResults(roomId: String){
        viewModelScope.launch {
            _scores.emit(Resource.Success(repository.loadResult(roomId)))
        }

    }

    fun removePlayer(roomId: String){
        repository.removePlayerFromRoom(roomId)
    }


}