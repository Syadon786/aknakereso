package com.example.aknakereso

import android.annotation.SuppressLint
import android.content.Context
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton

@SuppressLint("ViewConstructor")
class Tile(context: Context, id: Int, params: LinearLayout.LayoutParams) : AppCompatButton(context) {


    companion object
    {
        val  tileResources = listOf(R.drawable.stl_tile_bomb,
            R.drawable.stl_tile_one,
            R.drawable.stl_tile_two,
            R.drawable.stl_tile_three,
            R.drawable.stl_tile_four,
            R.drawable.stl_tile_five,
            R.drawable.stl_tile_six,
            R.drawable.stl_tile_seven,
            R.drawable.stl_tile_eight,
            R.drawable.stl_tile_empty,
            R.drawable.stl_tile_test
        )
    }
    var isMine : Boolean = false
    var isEmpty : Boolean = false
    var minesNear : Int = 0
    var isFlagged : Boolean = false

    fun setProperties(isMine : Boolean, isEmpty : Boolean, minesNear: Int)
    {
        this.isMine = isMine
        this.isEmpty = isEmpty
        this.minesNear = minesNear
        when {
            this.isMine -> {
                setBackgroundResource(tileResources[0])
            }
            this.isEmpty -> {
                setBackgroundResource(tileResources[9])
            }
            else -> {
                setBackgroundResource(tileResources[this.minesNear])
            }
        }
    }

    init {
        setId(id)
        layoutParams = params
        setBackgroundResource(tileResources[9])
    }
}