package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vtol.quizbattleapp.QuizViewModel
import com.vtol.quizbattleapp.R
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
    private var score = 0
    private val selectedOption = MutableLiveData<Int?>(null)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentQuizBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val roomId = args.roomId
        val quizId = args.quizId

        // Fetch quiz questions
        viewModel.getQuizQuestions(quizId)

        lifecycleScope.launch {
            viewModel.quizQuestions.collect { result ->
                when (result) {
                    is Resource.Success -> {
                        result.data?.let { quiz ->
                            questionsList = quiz.questions
                            if (questionsList.isNotEmpty()) {
                                currentQuestionIndex = 0
                                showQuestion(currentQuestionIndex)
                            }
                        }
                    }
                    else -> Unit
                }
            }
        }

        binding.nextBtn.setOnClickListener {
            onNextClicked(roomId, quizId)
        }
    }

    private fun showQuestion(questionIndex: Int) {
        if (questionsList.isEmpty()) return

        val quizQuestion = questionsList[questionIndex]
        binding.apply {
            questionTV.text = quizQuestion.questionText
            optionA.text = quizQuestion.optionA
            optionB.text = quizQuestion.optionB
            optionC.text = quizQuestion.optionC
            optionD.text = quizQuestion.optionD


            // update progress text
            questionProgressText.text = "${questionIndex + 1} of ${questionsList.size}"

            // update progress bar
            questionProgress.max = questionsList.size
            questionProgress.progress = questionIndex + 1
        }

        // Reset button states
        val buttons = listOf(binding.optionA, binding.optionB, binding.optionC, binding.optionD)
        buttons.forEach { it.isSelected = false }

        // Handle option clicks
        buttons.forEachIndexed { index, button ->
            button.setOnClickListener {
                buttons.forEach { it.isSelected = false }
                button.isSelected = true
                selectedOption.value = index
            }
        }

        // navigate back to home screen
        binding.backBtn.setOnClickListener {
            val dialog = AlertDialog.Builder(requireContext())
                .setTitle("Are you sure?")
                .setMessage("You will lose your progress")
                .setPositiveButton("Exit") { _, _ ->
                    findNavController().navigate(R.id.action_quizFragment_to_homeFragment)
                }
                .setNegativeButton("Cancel", null)
                .create()

            dialog.show()
        }

        // Update button text if last question
        binding.nextBtn.text = if (questionIndex == questionsList.size - 1) "Finish" else "Next"
    }

    private fun onNextClicked(roomId: String, quizId: String) {
        val selected = selectedOption.value ?: return
        // user must select an answer first

        val correctIndex = questionsList[currentQuestionIndex].correctAnswerIndex
        if (selected == correctIndex) score++

        if (currentQuestionIndex < questionsList.size - 1) {
            currentQuestionIndex++
            selectedOption.value = null
            showQuestion(currentQuestionIndex)
        } else {
            // Quiz finished
            viewModel.updateScore(roomId, score)
            val action = QuizFragmentDirections.actionQuizFragmentToResultFragment(quizId, roomId)
            findNavController().navigate(action)
        }
    }
}
