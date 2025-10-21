package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.vtol.quizbattleapp.QuizViewModel
import com.vtol.quizbattleapp.Resource
import com.vtol.quizbattleapp.databinding.FragmentQuizBinding
import com.vtol.quizbattleapp.model.QuizQuestion
import kotlinx.coroutines.launch

class QuizFragment : Fragment() {

    private lateinit var binding: FragmentQuizBinding

    private val viewModel by viewModels<QuizViewModel>()

    private val args by navArgs<QuizFragmentArgs>()
    private var currentQuestionIndex = 0
    private var questionsList = emptyList<QuizQuestion>()

    private var selectedOptionIndex = -1

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roomId = args.roomId
        val quizId = args.quizId

        viewModel.getQuizQuestions(quizId)

        lifecycleScope.launch {
            viewModel.quizQuestions.collect {
                when (it) {
                    is Resource.Success -> {
                        it.data?.let {
                            questionsList = it.questions
                            showQuestion(0)
                        }
                    }

                    else -> Unit
                }
            }
        }
        // selected option
        val buttons = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD)

       // Reset selection for a new question
        buttons.forEach { it.isSelected = false }

        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                // Deselect all buttons
                buttons.forEach { it.isSelected = false }
                // Select clicked button
                button.isSelected = true

                // Store user's choice if needed
                val selectedAnswer = it
            }
        }

        // Next button
        binding.nextBtn.setOnClickListener {
            if (currentQuestionIndex < questionsList.size - 1) {
                currentQuestionIndex++
                showQuestion(currentQuestionIndex)
            } else {
                // maybe navigate to results
            }
        }

    }

    fun showQuestion(questionIndex: Int) {

        // TODO: 1- Make the correct answer button green
        // TODO: 2- Calculate the score using 'roomId'
        if (questionsList.isNotEmpty()) {
            val quizQuestion = questionsList[questionIndex]
            binding.apply {
                questionTV.text = quizQuestion.questionText

                optionA.text = quizQuestion.optionA
                optionB.text = quizQuestion.optionB
                optionC.text = quizQuestion.optionC
                optionD.text = quizQuestion.optionD

            }
        }


    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {

        binding = FragmentQuizBinding.inflate(inflater)
        return binding.root
    }

}