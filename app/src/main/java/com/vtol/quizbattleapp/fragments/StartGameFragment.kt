package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import com.vtol.quizbattleapp.GameViewModel
import com.vtol.quizbattleapp.Resource
import com.vtol.quizbattleapp.adapter.PlayersAdapter
import com.vtol.quizbattleapp.databinding.StartGameFragmentBinding
import kotlinx.coroutines.launch

class StartGameFragment : Fragment() {
    private lateinit var binding: StartGameFragmentBinding
    private val args by navArgs<StartGameFragmentArgs>()
    private lateinit var playersAdapter: PlayersAdapter

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


        binding.recyclerView.layoutManager = GridLayoutManager(requireContext(),2)


        gameViewModel.loadRoom(quizId,roomId)




        lifecycleScope.launch {
            gameViewModel.players.collect {
                when(it){
                    is Resource.Success -> {
                        it.data?.let { list ->
                            Log.v("TOOL", list[0].playerName)
                            playersAdapter = PlayersAdapter(list)
                            binding.recyclerView.adapter = playersAdapter

                            binding.startGameBtn.isEnabled = list.size >= 2
                        }
                    }

                    else -> Unit
                }
            }
        }



    }
}