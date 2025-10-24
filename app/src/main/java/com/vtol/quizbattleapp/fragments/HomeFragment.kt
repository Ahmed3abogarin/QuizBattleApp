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
import com.vtol.quizbattleapp.HomeViewModel
import com.vtol.quizbattleapp.R
import com.vtol.quizbattleapp.Resource
import com.vtol.quizbattleapp.ViewPagerAdapter
import com.vtol.quizbattleapp.databinding.FragmentHomeBinding
import com.vtol.quizbattleapp.util.VerticalItemDecoration
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ViewPagerAdapter

    private val homeVM by viewModels<HomeViewModel>()



    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)



        binding.signOutBtn.setOnClickListener {
            homeVM.signOut()
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        lifecycleScope.launch {
            homeVM.name.collect {
                when (it){
                    is Resource.Loading -> {
                        binding.userProgressLayout.visibility = View.VISIBLE

                    }

                    is Resource.Success -> {
                        binding.userProgressLayout.visibility = View.GONE
                        it.data?.playerName?.let {
                            binding.userNameTV.text = it
                        }

                    }

                    is Resource.Error -> {
                        binding.userProgressLayout.visibility = View.GONE
                        binding.userNameTV.text = it.message
                    }

                    else -> Unit

                }


            }
        }


        lifecycleScope.launch {
            homeVM.rooms.collect {
                when (it) {
                    is Resource.Loading -> {
                        Log.v("SSSS","The current state is loading")
                        // make the loading indicator visible
                        showLoadingIndicator()
                    }

                    is Resource.Success -> {
                        Log.v("SSSS","The current state is success")
                        hideLoadingIndicator()
                        binding.quizPager.visibility = View.VISIBLE

                        adapter.differ.submitList(it.data)
                    }

                    is Resource.Error -> {
                        Log.v("SSSS","The current state is error: ${it.message}")
                        // in case of error hide the loading indicator and display the error text
                        binding.quizPager.visibility = View.INVISIBLE
                       showErrorState(it.message.toString())

                    }

                    else -> Unit
                }


            }
        }


        // navigate to start game screen
        setUpRv(onClick = { quizId, roomId ->
            val action = HomeFragmentDirections.actionHomeFragmentToStartGameFragment(quizId = quizId, roomId = roomId)
            findNavController().navigate(action)
//            Toast.makeText(context, "clicked!!!", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpRv(onClick: (String, String) -> Unit) {

        adapter = ViewPagerAdapter(onClick = { quizId, roomId -> onClick(quizId,roomId) })
        binding.quizPager.offscreenPageLimit = 3
        val pageMargin = 40
        val pageOffset = 20
        binding.quizPager.addItemDecoration(VerticalItemDecoration())
        binding.quizPager.setPageTransformer { page, position ->
            val offset = position * -(2 * pageOffset + pageMargin)
            page.translationX = offset
            page.scaleY = 1 - (0.15f * kotlin.math.abs(position))
        }
        binding.quizPager.adapter = adapter

    }

    private fun showLoadingIndicator() {
        binding.progressLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun hideLoadingIndicator() {
        binding.progressLayout.visibility = View.GONE
        binding.progressBar.visibility = View.GONE
    }

    private fun showErrorState(errorTxt: String) {
        binding.progressLayout.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
        binding.errorTv.apply {
            visibility = View.VISIBLE
            text = errorTxt
        }

    }
}