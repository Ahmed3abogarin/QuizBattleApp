package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.StaggeredGridLayoutManager
import com.vtol.quizbattleapp.GameViewModel
import com.vtol.quizbattleapp.R
import com.vtol.quizbattleapp.Resource
import com.vtol.quizbattleapp.adapter.PlayersAdapter
import com.vtol.quizbattleapp.databinding.StartGameFragmentBinding
import kotlinx.coroutines.launch

class StartGameFragment : Fragment() {
    private lateinit var binding: StartGameFragmentBinding
    private val args by navArgs<StartGameFragmentArgs>()
    private val playersAdapter: PlayersAdapter = PlayersAdapter()

    private val gameViewModel by viewModels<GameViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = StartGameFragmentBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val quizId = args.quizId
        val roomId = args.roomId

        val action =
            StartGameFragmentDirections.actionStartGameFragmentToQuizFragment(quizId, roomId)



        setUpPlayerRV()

        gameViewModel.joinGame(roomId)
        gameViewModel.loadRoom(roomId)

        binding.startGameBtn.setOnClickListener {
            findNavController().navigate(action)
        }

        binding.exitBtn.setOnClickListener {
            findNavController().navigate(R.id.action_startGameFragment_to_homeFragment)
        }


        lifecycleScope.launch {
            gameViewModel.players.collect {
                when (it) {

                    is Resource.Loading -> {
                        binding.progressBar.visibility = View.VISIBLE

                    }
                    is Resource.Success -> {
                        binding.progressBar.visibility = View.GONE
                        it.data?.let { list ->
                            if (list.isNotEmpty()) {
                                Log.v("TOOL", list[0].playerName)
                                playersAdapter.differ.submitList(list)
                                binding.startGameBtn.isEnabled = list.size >= 3
                            }
                        }
                    }
                    is Resource.Error -> {
                        binding.apply {
                            progressBar.visibility = View.GONE
                            errorTv.text = it.message.toString()
                        }


                    }

                    else -> Unit
                }
            }
        }
    }

    private fun setUpPlayerRV() {
        binding.playersRv.apply {
            layoutManager =
                StaggeredGridLayoutManager(2, StaggeredGridLayoutManager.VERTICAL)
            adapter = playersAdapter

        }
    }
}