package com.tselishchev.battleship.utils

import android.view.View


class SafeClickListener(
    private var defaultInterval: Long = 1000L,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0

    override fun onClick(v: View) {
        if (System.currentTimeMillis() - lastTimeClicked < defaultInterval) {
            return
        }

        lastTimeClicked = System.currentTimeMillis()
        onSafeCLick(v)
    }
}