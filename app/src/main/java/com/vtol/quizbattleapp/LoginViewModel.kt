package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.Player
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LoginViewModel(
    private val repository: GameRepository = GameRepository()
): ViewModel() {

    private var _questLogin = MutableStateFlow<Resource<Unit>>(Resource.Unspecified())
    val  questLogin = _questLogin.asStateFlow()

    fun continueAsQuest(player: Player){
        viewModelScope.launch {
            repository.continueAsGuest(player).collect {
                _questLogin.emit(it)

            }
        }

    }
}