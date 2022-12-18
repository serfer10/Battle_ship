package com.tselishchev.battleship.ui.game.createField

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.tselishchev.battleship.R
import com.tselishchev.battleship.databinding.ActivityCreateFieldBinding
import com.tselishchev.battleship.models.Ship
import com.tselishchev.battleship.models.ShipType
import com.tselishchev.battleship.ui.activities.ShipAdapter
import com.tselishchev.battleship.ui.game.CellClickListener
import com.tselishchev.battleship.ui.game.GameIntentContext
import com.tselishchev.battleship.ui.game.battle.BattleActivity

class CreateFieldActivity : AppCompatActivity(), CellClickListener {

    private lateinit var binding: ActivityCreateFieldBinding
    private lateinit var adapter: ShipAdapter
    private val viewModel by viewModels<CreateFieldViewModel>()
    private var horizontal = true
    private lateinit var context: GameIntentContext

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateFieldBinding.inflate(layoutInflater)
        context = GameIntentContext.getFrom(intent)

        binding.run {
            toolbar.setNavigationOnClickListener { finish() } // back button

            gridRV.layoutManager = GridLayoutManager(this@CreateFieldActivity, 10)
            adapter = ShipAdapter(this@CreateFieldActivity)
            gridRV.adapter = adapter

            radioOne.isChecked = true

            gameIdText.text = getString(R.string.game_id, context.game)
        }

        initShipSelectionListeners()
        initBattleListener()
        initBoardListener()
        initShipListener()

        setContentView(binding.root)
    }

    private fun initBattleListener() {
        binding.run {
            joinBattleButton.setOnClickListener {
                context.cells = viewModel.cells.value?.toArray()

                val newIntent = Intent(this@CreateFieldActivity, BattleActivity::class.java)
                GameIntentContext.setTo(newIntent, context)
                startActivity(newIntent)
            }
        }
    }

    private fun initBoardListener() {
        viewModel.cells.observe(this) {
            adapter.submitList(it.toViewCells())
        }
    }

    private fun initShipListener() {
        viewModel.shipChange.observe(this) { ship ->
            hideAllShips()

            if (ship == null) {
                setBattleState(allow = true)
            } else {
                setBattleState(allow = false)
                ship.horizontal = horizontal
                updateShipsView(ship)
            }
        }
    }

    private fun initShipSelectionListeners() {
        binding.run {
            twoHorizontalLL.setOnClickListener {
                changeShipHorizontality(newHorizontal = true)
            }

            twoVerticalLL.setOnClickListener {
                changeShipHorizontality(newHorizontal = false)
            }

            threeHorizontalLL.setOnClickListener {
                changeShipHorizontality(newHorizontal = true)
            }

            threeVerticalLL.setOnClickListener {
                changeShipHorizontality(newHorizontal = false)
            }

            fourHorizontalLL.setOnClickListener {
                changeShipHorizontality(newHorizontal = true)
            }

            fourVerticalLL.setOnClickListener {
                changeShipHorizontality(newHorizontal = false)
            }
        }
    }

    override fun onCellClicked(position: Int) {
        viewModel.onShipPlaced(position)
    }

    private fun hideAllShips() {
        binding.run {
            oneLL.visibility = View.GONE
            twoHorizontalLL.visibility = View.GONE
            twoVerticalLL.visibility = View.GONE
            threeHorizontalLL.visibility = View.GONE
            threeVerticalLL.visibility = View.GONE
            fourVerticalLL.visibility = View.GONE
            fourHorizontalLL.visibility = View.GONE
        }
    }

    private fun updateShipsView(ship: Ship?) {
        if (ship == null) {
            hideAllShips()
            return
        }

        binding.run {
            when (ship.type) {
                ShipType.Boat -> {
                    oneLL.visibility = View.VISIBLE
                    radioOne.isChecked = true
                }
                ShipType.Destroyer -> {
                    twoHorizontalLL.visibility = View.VISIBLE
                    twoVerticalLL.visibility = View.VISIBLE

                    radioTwoVertical.isChecked = !ship.horizontal
                    radioTwoHorizontal.isChecked = ship.horizontal
                }
                ShipType.Cruiser -> {
                    threeVerticalLL.visibility = View.VISIBLE
                    threeHorizontalLL.visibility = View.VISIBLE

                    radioThreeVertical.isChecked = !ship.horizontal
                    radioThreeHorizontal.isChecked = ship.horizontal
                }
                ShipType.Battleship -> {
                    fourVerticalLL.visibility = View.VISIBLE
                    fourHorizontalLL.visibility = View.VISIBLE

                    radioFourVertical.isChecked = !ship.horizontal
                    radioFourHorizontal.isChecked = ship.horizontal
                }
            }
        }
    }

    private fun setBattleState(allow: Boolean) {
        binding.run {
            joinBattleButton.isEnabled = allow
        }
    }

    private fun changeShipHorizontality(newHorizontal: Boolean) {
        val ship = viewModel.shipChange.value
        horizontal = newHorizontal


        if (ship != null && ship.horizontal == newHorizontal) {
            return
        }

        ship?.horizontal = newHorizontal
        updateShipsView(ship)
    }
}