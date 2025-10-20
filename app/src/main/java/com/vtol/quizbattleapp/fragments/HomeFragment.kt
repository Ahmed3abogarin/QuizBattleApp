package com.vtol.quizbattleapp.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.vtol.quizbattleapp.ViewPagerAdapter
import com.vtol.quizbattleapp.databinding.FragmentHomeBinding
import com.vtol.quizbattleapp.model.Quiz
import com.vtol.quizbattleapp.model.QuizQuestion
import com.vtol.quizbattleapp.util.VerticalItemDecoration

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var adapter: ViewPagerAdapter

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

        setUpRv(onClick = {
            Toast.makeText(context, "clicked!!!", Toast.LENGTH_SHORT).show()
        })
    }

    private fun setUpRv(onClick: () -> Unit) {

        val quizList = listOf(
            Quiz(
                quizName = "General technology questions",
                players = listOf("", "", ""),
                quizCategory = "Tech",
                questions = listOf(QuizQuestion())
            ),
            Quiz(
                quizName = "General technology questions",
                players = listOf("", "", "", "", ""),
                quizCategory = "Tech",
                questions = listOf(QuizQuestion(), QuizQuestion(), QuizQuestion())
            ),
            Quiz(
                quizName = "كيف يا فرطة",
                players = listOf(""),
                quizCategory = "Tech",
                questions = listOf(QuizQuestion(), QuizQuestion(), QuizQuestion(), QuizQuestion())
            )
        )
        adapter = ViewPagerAdapter(onClick = { onClick() })
        adapter.differ.submitList(quizList)
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