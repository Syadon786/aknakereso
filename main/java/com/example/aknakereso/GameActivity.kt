package com.example.aknakereso

import android.app.Activity
import android.graphics.Rect
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import kotlinx.android.synthetic.main.activity_game.*

class GameActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_game)

        val rowCount = intent.getIntExtra("EXTRA_ROWS", 20)
        val columnCount = intent.getIntExtra("EXTRA_COLUMNS", 10)
        val isPracticeMode = intent.getBooleanExtra("EXTRA_PRACTICE", false)

        val dpi = resources.displayMetrics.density.toDouble()
        var screenWidth = 0
        var screenHeight = 0

        bounds()?.apply {
            screenWidth = width()
            screenHeight = height()
            val msg = "${screenHeight}x${screenWidth} $dpi dpi"
            Log.d("DIMENSIONS", msg)
        }

        main_layout.viewTreeObserver.addOnGlobalLayoutListener(object: ViewTreeObserver.OnGlobalLayoutListener {
            override fun onGlobalLayout() {
                main_layout.viewTreeObserver.removeOnGlobalLayoutListener(this)

                Game.generateLayout(applicationContext, isPracticeMode, main_layout, minefield, end_layout, screenWidth, screenHeight, rowCount, columnCount, 3)

                btnBackToMenu.setOnClickListener {
                    finish()
                }
            }
        })

    }

}

    private fun Activity.bounds(): Rect?{
        return if (Build.VERSION.SDK_INT >=30){
            windowManager.currentWindowMetrics.bounds
        }else{
            null
        }
    }
