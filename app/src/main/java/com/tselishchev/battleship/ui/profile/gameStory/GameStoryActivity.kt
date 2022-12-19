package com.tselishchev.battleship.ui.profile.gameStory

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import com.tselishchev.battleship.databinding.ActivityGameStoryBinding
import com.tselishchev.battleship.ui.game.GameIntentContext


class GameStoryActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameStoryBinding
    private val viewModel by viewModels<GameStoryViewModel>()
    private lateinit var context: GameIntentContext
    private lateinit var gameAdapter: GameStoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameStoryBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)
        setContentView(binding.root)
        gameAdapter = GameStoryAdapter(context.user.toString())
        viewModel.tryGetStatistics(context.user.toString(), 10)

        binding.run {
            gameStoryToolbar.setNavigationOnClickListener { finish() }

            val dividerItemDecoration = DividerItemDecoration(
                recyclerView.context,
                LinearLayoutManager.VERTICAL
            )

            recyclerView.apply {
                layoutManager =
                    LinearLayoutManager(this@GameStoryActivity, LinearLayoutManager.VERTICAL, false)
                adapter = gameAdapter
                addItemDecoration(dividerItemDecoration)
            }


        }

        viewModel.statisticsResult.observe(this){
            if(it != null) gameAdapter.submitList(it)
        }
    }
}
