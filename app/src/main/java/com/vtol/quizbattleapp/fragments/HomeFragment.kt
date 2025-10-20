package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.vtol.quizbattleapp.HomeViewModel
import com.vtol.quizbattleapp.Resource
import com.vtol.quizbattleapp.ViewPagerAdapter
import com.vtol.quizbattleapp.databinding.FragmentHomeBinding
import com.vtol.quizbattleapp.util.VerticalItemDecoration
import kotlinx.coroutines.launch

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ViewPagerAdapter

    private val homeVM by viewModels<HomeViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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


        lifecycleScope.launch {
            homeVM.quizQuestions.collect{
                when(it) {
                    is Resource.Loading -> {

                    }
                    is Resource.Success ->{
                        adapter.differ.submitList(it.data)
                    }
                    is Resource.Error -> {

                    }
                    else -> Unit
                }


            }
        }


        setUpRv(onClick = {
            Toast.makeText(context, "clicked!!!", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpRv(onClick: () -> Unit) {

        adapter = ViewPagerAdapter(onClick = { onClick() })
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
}