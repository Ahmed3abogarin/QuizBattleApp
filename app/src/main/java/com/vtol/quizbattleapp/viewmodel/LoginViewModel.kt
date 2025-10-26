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
class LoginViewModel @Inject constructor(
    private val repository: GameRepository
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