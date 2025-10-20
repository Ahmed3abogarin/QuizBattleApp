package com.vtol.quizbattleapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vtol.quizbattleapp.databinding.PagerItemBinding
import com.vtol.quizbattleapp.model.Quiz

class ViewPagerAdapter(private val onClick: () -> Unit) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(val binding: PagerItemBinding) : ViewHolder(binding.root) {
        fun bind(quiz: Quiz) {
            binding.apply {
                quizName.text = quiz.quizName
                quizCategory.text = quiz.quizCategory
                playersNumber.text = "${quiz.players.size} players"
                questionsNumber.text = "${quiz.questions.size} questions"
                playButton.setOnClickListener { onClick()}
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Quiz>() {
        override fun areItemsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Quiz, newItem: Quiz): Boolean {
            return oldItem == newItem
        }

    }
     val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerViewHolder {
        return PagerViewHolder(
            PagerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PagerViewHolder, position: Int) {
        val currentQuiz = differ.currentList[position]
        holder.bind(currentQuiz)
    }
}