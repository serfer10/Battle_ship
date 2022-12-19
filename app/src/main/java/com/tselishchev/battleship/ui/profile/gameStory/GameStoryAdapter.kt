package com.tselishchev.battleship.ui.profile.gameStory

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tselishchev.battleship.R
import com.tselishchev.battleship.databinding.ItemStatisticsBinding
import com.tselishchev.battleship.models.Game

class GameStoryAdapter(
    private val userId: String
) : ListAdapter<Game, GameStoryAdapter.StatisticsViewHolder>(GameDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): StatisticsViewHolder {
        return StatisticsViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.item_statistics, parent, false)
        )
    }


    override fun onBindViewHolder(holder: StatisticsViewHolder, position: Int) {
        val statisticItem = getItem(position)
        holder.bind(statisticItem)
    }

    open inner class StatisticsViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ItemStatisticsBinding.bind(itemView)
        fun bind(game: Game) {
            with(game) {
                binding.user1.text = user1
                binding.user2.text = user2
                binding.winner.text = winner
                binding.time.text = createTime
                binding.gameId.text = id
                binding.statisticsImage.setImageResource(
                    if (userId == winner) {
                        R.drawable.ic_baseline_check_circle_outline_24
                    } else {
                        R.drawable.ic_baseline_cancel_24
                    }
                )
            }
        }
    }

}

class GameDiffCallback : DiffUtil.ItemCallback<Game>() {
    override fun areItemsTheSame(oldItem: Game, newItem: Game): Boolean =
        oldItem.id == newItem.id

    override fun areContentsTheSame(oldItem: Game, newItem: Game): Boolean =
        oldItem.winner == newItem.winner

}



