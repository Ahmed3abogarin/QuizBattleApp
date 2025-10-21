package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import com.vtol.quizbattleapp.model.Player

class LoginViewModel(
    private val repository: GameRepository = GameRepository()
): ViewModel() {

    fun continueAsQuest(player: Player){
        repository.continueAsQuest(player)
    }
}