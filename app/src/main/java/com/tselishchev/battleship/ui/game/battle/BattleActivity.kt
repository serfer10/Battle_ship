package com.tselishchev.battleship.ui.game.battle

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
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

            if (context != null && context.isFulfilled()) {
                viewModel.initialize(
                    context.user!!,
                    context.game!!,
                    context.cells!!
                )
            }
        }

        setContentView(binding.root)
    }

    override fun onCellClicked(position: Int) {
        viewModel.act(position)
    }

    private fun listenForUpdates() {
        viewModel.userCells.observe(this) { cells ->
            adapter.submitList(GameCells(cells).toViewCells())
        }
    }
}

