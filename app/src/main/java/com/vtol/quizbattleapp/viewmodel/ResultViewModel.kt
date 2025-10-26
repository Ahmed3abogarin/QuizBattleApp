package com.vtol.quizbattleapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.PlayerWithScore
import com.vtol.quizbattleapp.repository.GameRepository
import com.vtol.quizbattleapp.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ResultViewModel @Inject constructor(
    private val repository: GameRepository
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