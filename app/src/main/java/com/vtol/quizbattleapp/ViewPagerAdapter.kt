package com.vtol.quizbattleapp

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ViewHolder
import com.vtol.quizbattleapp.databinding.PagerItemBinding
import com.vtol.quizbattleapp.model.RoomWithQuiz

class ViewPagerAdapter(private val onClick: (String, String) -> Unit) :
    RecyclerView.Adapter<ViewPagerAdapter.PagerViewHolder>() {

    inner class PagerViewHolder(val binding: PagerItemBinding) : ViewHolder(binding.root) {
        fun bind(quiz: RoomWithQuiz) {
            binding.apply {
                quizName.text = quiz.quiz?.quizName
                quizCategory.text = quiz.quiz?.quizCategory
                playersNumber.text = "${quiz.room.playerIds.size} players"
                questionsNumber.text = "${quiz.quiz?.questions?.size} questions"
                playButton.setOnClickListener {
                    quiz.quiz?.let {
                        onClick(it.quizId, quiz.room.id)
                    }
                }
            }
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<RoomWithQuiz>() {
        override fun areItemsTheSame(oldItem: RoomWithQuiz, newItem: RoomWithQuiz): Boolean {
            return oldItem.room.id == newItem.room.id
        }

        override fun areContentsTheSame(oldItem: RoomWithQuiz, newItem: RoomWithQuiz): Boolean {
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