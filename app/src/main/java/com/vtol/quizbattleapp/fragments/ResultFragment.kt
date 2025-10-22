package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vtol.quizbattleapp.Resource
import com.vtol.quizbattleapp.ResultViewModel
import com.vtol.quizbattleapp.databinding.FragmentResultBinding
import kotlinx.coroutines.launch

class ResultFragment: Fragment(){
    lateinit var binding: FragmentResultBinding
    private val viewModel by viewModels<ResultViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        val roomId = "aa1"

        lifecycleScope.launch {
            viewModel.scores.collect {
                when(it){
                    is Resource.Success ->{
                        showResult(it.data)
                    }
                    else -> Unit
                }

            }
        }



        binding = FragmentResultBinding.inflate(inflater)
        return binding.root
    }

    private fun showResult(scores: Map<String, Int>?) {

    }
}