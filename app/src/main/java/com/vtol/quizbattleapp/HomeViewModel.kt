package com.vtol.quizbattleapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.vtol.quizbattleapp.model.Quiz
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HomeViewModel: ViewModel() {

    private val _quizQuestions = MutableStateFlow<Resource<List<Quiz>>>(Resource.Unspecified())
    val quizQuestions = _quizQuestions.asStateFlow()


    fun fetchQuizQuestions(){
        viewModelScope.launch {
            _quizQuestions.emit(Resource.Loading())
        }
    }


}