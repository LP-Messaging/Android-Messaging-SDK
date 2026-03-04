package com.liveperson.sample.app.utils


import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.OnApplyWindowInsetsListener
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

@JvmOverloads
internal fun AppCompatActivity.applyInsets(allowResize: Boolean = true) {
    val listener: OnApplyWindowInsetsListener = object : OnApplyWindowInsetsListener {
        override fun onApplyWindowInsets(
            view: View,
            insets: WindowInsetsCompat
        ): WindowInsetsCompat {
            val imeVisible = insets.isVisible(WindowInsetsCompat.Type.ime())
            val systemBarInsets = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            val imeInsets = insets.getInsets(WindowInsetsCompat.Type.ime())

            // The fragments inside will inherit this padding
            val bottomPadding = if (imeVisible && allowResize) {
                imeInsets.bottom
            } else {
                systemBarInsets.bottom
            }
            view.setPadding(
                systemBarInsets.left,
                systemBarInsets.top,
                systemBarInsets.right,
                bottomPadding
            )

            return WindowInsetsCompat.CONSUMED
        }
    }
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), listener)
}

internal fun AppCompatActivity.clearInsetListener() {
    ViewCompat.setOnApplyWindowInsetsListener(findViewById(android.R.id.content), null)
}