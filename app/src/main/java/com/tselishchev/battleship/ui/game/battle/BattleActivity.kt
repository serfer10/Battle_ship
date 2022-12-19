package com.tselishchev.battleship.ui.game.battle

import android.content.Intent
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
import com.tselishchev.battleship.ui.launcher.CreateGameActivity

class BattleActivity : AppCompatActivity(), CellClickListener {
    private lateinit var binding: ActivityBattleBinding
    private lateinit var opponentAdapter: ShipAdapter
    private lateinit var yourAdapter: ShipAdapter
    private val viewModel by viewModels<BattleActivityViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBattleBinding.inflate(layoutInflater)

        listenForUpdates()

        binding.run {
            opponentGridRV.isNestedScrollingEnabled = false
            yourGridRV.isNestedScrollingEnabled = false
            opponentGridRV.layoutManager = GridLayoutManager(this@BattleActivity, 10)
            yourGridRV.layoutManager = GridLayoutManager(this@BattleActivity, 10)
            opponentAdapter = ShipAdapter(this@BattleActivity)
            yourAdapter = ShipAdapter(this@BattleActivity)
            opponentGridRV.adapter = opponentAdapter
            yourGridRV.adapter = yourAdapter

            val context = GameIntentContext.getFrom(intent)

            gameIdText.text = getString(R.string.game_id, context.game)
            gameStateText.text = "Waiting for game start..."

            if(viewModel._endgame)
            {toolbar.setNavigationOnClickListener {
                context.resetGameInfo()

                val newIntent = Intent(this@BattleActivity, CreateGameActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)
            }}


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
        if (viewModel.userTurn.value == true && viewModel.userWins.value == null) {
            viewModel.act(position)
        }
    }

    private fun listenForUpdates() {
        viewModel.opponentCells.observe(this) { cells ->
            opponentAdapter.submitList(GameCells.toViewCells(cells))
        }

        viewModel.userCells.observe(this) { cells ->
            yourAdapter.submitList(cells.toViewCells())
        }

        viewModel.userTurn.observe(this) { userTurn ->
            binding.gameStateText.text =
                if (userTurn) "Your turn. Please click on cell to hit." else
                    "Waiting for another user to move"
        }

        viewModel.userWins.observe(this) { win ->
            binding.run {
                gameStateText.text = if (win) "Victory!" else "Defeat!"
                toolbar.navigationIcon = getDrawable(R.drawable.ic_baseline_close_24)
            }
        }
    }
}

