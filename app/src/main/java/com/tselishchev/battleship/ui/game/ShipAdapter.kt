package com.tselishchev.battleship.ui.activities

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.tselishchev.battleship.databinding.ItemEmptyCellBinding
import com.tselishchev.battleship.databinding.ItemMissCellBinding
import com.tselishchev.battleship.databinding.ItemShipCellBinding
import com.tselishchev.battleship.databinding.ItemShipKilledBinding
import com.tselishchev.battleship.models.GameCell
import com.tselishchev.battleship.models.ViewCell
import com.tselishchev.battleship.ui.game.CellClickListener

class ShipAdapter(
    private val clickListener: CellClickListener
) : ListAdapter<ViewCell, CellViewHolder>(CellDiffCallback()) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CellViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> {
                EmptyViewHolder(
                    ItemEmptyCellBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ), clickListener
                )
            }
            1 -> {
                ShipViewHolder(
                    ItemShipCellBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ), clickListener
                )
            }
            2 -> {
                KilledViewHolder(
                    ItemShipKilledBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ), clickListener
                )
            }
            else -> {
                MissViewHolder(
                    ItemMissCellBinding.inflate(
                        layoutInflater,
                        parent,
                        false
                    ), clickListener
                )
            }
        }
    }

    override fun onBindViewHolder(holder: CellViewHolder, position: Int) {
        val shipCell = getItem(position)
        holder.bind(shipCell, position)
    }

    override fun getItemViewType(position: Int): Int {
        return when (getItem(position).type) {
            GameCell.Empty -> 0
            GameCell.Ship -> 1
            GameCell.BeatShip -> 2
            GameCell.Miss -> 3
        }
    }

    fun submitNewList(
        list: MutableList<ViewCell>,
        positionFirst: Int,
        positionSecond: Int? = null,
        positionThird: Int? = null,
        positionFourth: Int? = null
    ) {
        submitList(list)
        notifyItemChanged(positionFirst)
        positionSecond?.let { notifyItemChanged(it) }
        positionThird?.let { notifyItemChanged(it) }
        positionFourth?.let { notifyItemChanged(it) }
    }
}

class CellDiffCallback : DiffUtil.ItemCallback<ViewCell>() {
    override fun areItemsTheSame(oldItem: ViewCell, newItem: ViewCell): Boolean =
        oldItem.position == newItem.position

    override fun areContentsTheSame(oldItem: ViewCell, newItem: ViewCell): Boolean =
        oldItem.type == newItem.type

}

abstract class CellViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(shipCell: ViewCell, position: Int)
}

open class EmptyViewHolder(
    private val binding: ItemEmptyCellBinding,
    private val clickListener: CellClickListener
) : CellViewHolder(binding.root) {
    override fun bind(shipCell: ViewCell, position: Int) {
        binding.root.setOnClickListener {
            clickListener.onCellClicked(position)
        }
    }
}

open class MissViewHolder(
    private val binding: ItemMissCellBinding,
    private val clickListener: CellClickListener
) : CellViewHolder(binding.root) {
    override fun bind(shipCell: ViewCell, position: Int) {
        binding.root.setOnClickListener {
            clickListener.onCellClicked(position)
        }
    }
}

open class ShipViewHolder(
    private val binding: ItemShipCellBinding,
    private val clickListener: CellClickListener
) : CellViewHolder(binding.root) {
    override fun bind(shipCell: ViewCell, position: Int) {
        binding.root.setOnClickListener {
            clickListener.onCellClicked(position)
        }
    }
}

open class KilledViewHolder(
    private val binding: ItemShipKilledBinding,
    private val clickListener: CellClickListener
) : CellViewHolder(binding.root) {
    override fun bind(viewCell: ViewCell, position: Int) {
        binding.root.setOnClickListener {
            clickListener.onCellClicked(position)
        }
    }
}