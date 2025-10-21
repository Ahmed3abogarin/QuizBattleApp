package com.vtol.quizbattleapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.vtol.quizbattleapp.databinding.PlayerItemBinding
import com.vtol.quizbattleapp.model.Player


class PlayersAdapter(
    private val players: List<Player>
) : RecyclerView.Adapter<PlayersAdapter.PlayersViewHolder>(){

    class PlayersViewHolder(private val binding: PlayerItemBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(playerName: String){
            binding.playerName.text = playerName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PlayersViewHolder {
        val binding = PlayerItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PlayersViewHolder(binding)
    }

    override fun getItemCount(): Int = players.size

    override fun onBindViewHolder(holder: PlayersViewHolder, position: Int) {
        val player = players[position]
        holder.bind(player.playerName)
    }

}