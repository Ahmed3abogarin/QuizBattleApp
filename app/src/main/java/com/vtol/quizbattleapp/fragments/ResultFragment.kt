package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.vtol.quizbattleapp.R
import com.vtol.quizbattleapp.Resource
import com.vtol.quizbattleapp.ResultViewModel
import com.vtol.quizbattleapp.databinding.FragmentResultBinding
import com.vtol.quizbattleapp.model.PlayerWithScore
import kotlinx.coroutines.launch

class ResultFragment: Fragment(){
    private lateinit var binding: FragmentResultBinding
    private val viewModel by viewModels<ResultViewModel>()

    private val args by navArgs<ResultFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentResultBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val roomId = args.roomId

        viewModel.loadResults(roomId)

        lifecycleScope.launch {
            viewModel.scores.collect {
                when(it){
                    is Resource.Success ->{
                        it.data?.let { list ->
                            showResult(list)
                        }

                    }
                    else -> Unit
                }

            }
        }

        binding.homeBtn.setOnClickListener {
            findNavController().navigate(R.id.action_resultFragment_to_homeFragment)
        }
    }

    private fun showResult(resultList: List<PlayerWithScore>) {
        binding.apply {
            firstPlayerTV.text = resultList[0].name
            firstScoreTV.text = "${resultList[0].playerData.score} pts"

            secondPlayerTV.text = resultList[1].name
            secondScoreTV.text = "${resultList[1].playerData.score} pts"

            thirdPlayerTV.text = resultList[2].name
            thirdScoreTV.text = "${resultList[2].playerData.score} pts"

        }

    }
}