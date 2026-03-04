package com.liveperson.sample.app.activities

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.WindowCompat
import com.liveperson.sample.app.utils.clearInsetListener

open class BaseActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        val controller = WindowCompat.getInsetsController(window, window.getDecorView())
        controller.setAppearanceLightStatusBars(true)
        controller.setAppearanceLightNavigationBars(true)
    }

    override fun onDestroy() {
        clearInsetListener()
        super.onDestroy()
    }
}