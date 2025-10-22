package com.vtol.quizbattleapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.vtol.quizbattleapp.databinding.PlayerItemBinding
import com.vtol.quizbattleapp.model.Player


class PlayersAdapter
    : RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>(){

    inner class PlayersViewHolder(private val binding: PlayerItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(playerName: String){
            binding.playerName.text = playerName
        }
    }

    private val differCallBack = object : DiffUtil.ItemCallback<Player>() {
        override fun areItemsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(oldItem: Player, newItem: Player): Boolean {
            return oldItem == newItem
        }

    }
    val differ = AsyncListDiffer(this,differCallBack)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        return PlayersViewHolder(
            PlayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        )
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        val currentQuiz = differ.currentList[position]
        holder.bind(currentQuiz.playerName)
    }

}