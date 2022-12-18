package com.tselishchev.battleship.ui.game.battle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tselishchev.battleship.R
import com.tselishchev.battleship.databinding.ActivityBattleBinding
import com.tselishchev.battleship.models.GameCells
import com.tselishchev.battleship.ui.activities.ShipAdapter
import com.tselishchev.battleship.ui.game.CellClickListener
import com.tselishchev.battleship.ui.game.GameIntentContext

class BattleActivity : AppCompatActivity(), CellClickListener {
    private lateinit var binding: ActivityBattleBinding
    private lateinit var adapter: ShipAdapter
    private val viewModel by viewModels<BattleActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBattleBinding.inflate(layoutInflater)

        listenForUpdates()

        binding.run {
            gridRV.layoutManager = GridLayoutManager(this@BattleActivity, 10)
            adapter = ShipAdapter(this@BattleActivity)
            gridRV.adapter = adapter

            val context = GameIntentContext.getFrom(intent)

            gameIdText.text = getString(R.string.game_id, context.game)

            gameStateText.text = "Waiting for game start..."

            if (context.isFulfilled()) {
                viewModel.initialize(
                    context.user!!,
                    context.game!!,
                    GameCells(context.cells!!)
                )
            }
        }

        setContentView(binding.root)
    }

    override fun onCellClicked(position: Int) {
        if (viewModel.userTurn.value == true) {
            viewModel.act(position)
        }
    }

    private fun listenForUpdates() {
        viewModel.userTurn.observe(this) { userTurn ->
            if (userTurn) {
                binding.gameStateText.text = "Your turn. Please click on cell to hit."

                val cells = viewModel.opponentCells.value
                if (cells != null) {
                    adapter.submitList(GameCells.toViewCells(cells))
                }
            } else {
                binding.gameStateText.text =
                    "Waiting for another user move. Here is your battlefield."

                val cells = viewModel.userCells.value
                if (cells != null) {
                    adapter.submitList(cells.toViewCells())
                }
            }
        }

        viewModel.userWins.observe(this) { win ->
            val cells = viewModel.opponentCells.value
            if (cells != null) {
                adapter.submitList(GameCells.toViewCells(cells))
            }

            binding.gameStateText.text = if (win) "Victory!" else "Defeat!"
        }
    }
}

