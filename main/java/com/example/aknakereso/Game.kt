package com.example.aknakereso
import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.util.TypedValue
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_game.view.*
import kotlin.random.Random
import kotlin.math.floor


abstract class Game {


    companion object {

        private var mineCount : Int = 0
        private var remainingFlags : Int = 0
        private var isFirstClick : Boolean = true
        private var isPracticeMode : Boolean = false
        private var revealedCount : Int = 0

        //Kiszámolja a gombok optimális méretét a képernyő és a layout méreteit figyelembe véve
        private fun calcOptBtnSize(main_layout : RelativeLayout, rowCount: Int = 20, columnCount: Int = 10, padding : Int = 3) : Int
        {
            val mainHeight = main_layout.height - 2 * padding
            val mainWidth = main_layout.width - 2 * padding

            val calculatedByHeight = floor(mainHeight / (rowCount.toDouble()+1)).toInt()
            val calculatedByWidth = floor(mainWidth / columnCount.toDouble()).toInt()

            if(calculatedByHeight > calculatedByWidth)
                return calculatedByWidth
            return calculatedByHeight
        }

        //Sor*oszlop szám alatt meghatározza az aknák mennyiségét és annyi random indexet generál
        private fun chooseMines(nOfTiles : Int, excludedId : Int) : List<Int>
        {
            val mineIndexes = mutableListOf<Int>()
            do
            {
                val mineIndex = Random.nextInt(nOfTiles)
                if((mineIndex !in mineIndexes) && (excludedId != mineIndex))
                {
                    mineIndexes.add(mineIndex)
                }
                Log.d("MINEINDEX", mineIndex.toString())
            } while(mineIndexes.size < mineCount)

            return mineIndexes
        }
        private fun numberOfMinesNearby(mineIndexes : List<Int>, actId : Int, columns: Int) : Int
        {
            val counter: Int

            //Bal széle
            when {
                actId % columns == 0 -> {
                    counter = (if((actId - columns) in mineIndexes) 1 else 0) +
                            (if((actId + columns) in mineIndexes) 1 else 0) +
                            (if((actId + 1 ) in mineIndexes) 1 else 0) +
                            (if(((actId - columns) + 1) in mineIndexes) 1 else 0) +
                            (if(((actId + columns)  + 1) in mineIndexes) 1 else 0)
                }
                //Jobb széle
                actId % columns == (columns-1) -> {
                    counter = (if((actId - columns) in mineIndexes) 1 else 0) +
                            (if((actId + columns) in mineIndexes) 1 else 0) +
                            (if((actId - 1 ) in mineIndexes) 1 else 0) +
                            (if(((actId - columns) - 1) in mineIndexes) 1 else 0) +
                            (if(((actId + columns) - 1) in mineIndexes) 1 else 0)
                }
                //Minden más
                else -> {
                    counter = (if((actId - columns) in mineIndexes) 1 else 0) +
                            (if((actId + columns) in mineIndexes) 1 else 0) +
                            (if((actId + 1 ) in mineIndexes) 1 else 0) +
                            (if(((actId - columns) + 1) in mineIndexes) 1 else 0) +
                            (if(((actId + columns)  + 1) in mineIndexes) 1 else 0) +
                            (if((actId - 1 ) in mineIndexes) 1 else 0) +
                            (if(((actId - columns) - 1) in mineIndexes) 1 else 0) +
                            (if(((actId + columns) - 1) in mineIndexes) 1 else 0)
                }
            }
            return counter
        }

        private fun onLongClick(temp : Tile)
        {
            if(temp.isFlagged)
            {
                temp.isFlagged = false
                remainingFlags++
                when {
                    temp.isMine -> {
                        temp.setBackgroundResource(Tile.tileResources[0])
                    }
                    temp.isEmpty -> {
                        temp.setBackgroundResource(Tile.tileResources[9])
                    }
                    else -> {
                        temp.setBackgroundResource(Tile.tileResources[temp.minesNear])
                    }
                }
            }
            else
            {
                if(remainingFlags > 0) {
                    temp.isFlagged = true
                    remainingFlags--
                    temp.setBackgroundResource(R.drawable.tile_flagged)
                }
            }
        }

        private fun onClick(temp : Tile, columns: Int, rows: Int, tile: Int, line: Int, nOfTiles: Int, minefield: LinearLayout, end_layout: ConstraintLayout)
        {
            val neededToReveal = nOfTiles - mineCount
            if(!temp.isFlagged && temp.isEnabled)
            {
                temp.isEnabled = false
                if(temp.isEmpty)
                {
                    if(isPracticeMode && !temp.isMine || !isPracticeMode)
                        revealedCount++
                    revealEmpties(temp.id, columns, rows, tile, line, minefield)
                }
                else if(temp.isMine && !isPracticeMode)
                {
                    end_layout.visibility = View.VISIBLE
                    for (i in 0 until nOfTiles)
                    {
                        minefield.findViewById<Tile>(i).isEnabled = false
                    }
                    val text = "Játék vége"
                    end_layout.txtEnd.text = text
                }
                else
                {
                    if(isPracticeMode && !temp.isMine || !isPracticeMode)
                        revealedCount++
                }

                if(revealedCount == neededToReveal)
                {
                    for (i in 0 until nOfTiles)
                    {
                        minefield.findViewById<Tile>(i).isEnabled = false
                    }
                    end_layout.visibility = View.VISIBLE
                    val text = "Győztél"
                    end_layout.txtEnd.text = text
                }
            }
        }
        private fun revealEmpties(actId : Int, columns: Int, rows: Int, tile : Int, line : Int, minefield: LinearLayout)
        {
            when {
                    //Bal felső
                (actId == 0) -> {
                    Log.d("BALFELSO", "I was fired")
                    minefield.findViewById<Tile>(actId+1).performClick()
                    minefield.findViewById<Tile>(actId+columns).performClick()
                    minefield.findViewById<Tile>(actId+columns+1).performClick()
                }

                //Jobb felső
                (tile == (columns-1)) && (line == 0) -> {
                    Log.d("JOBBFELSO", "I was fired")
                    minefield.findViewById<Tile>(actId-1).performClick()
                    minefield.findViewById<Tile>(actId+columns-1).performClick()
                    minefield.findViewById<Tile>(actId+columns).performClick()
                }

               //Bal alsó
                (tile == 0) && (line == (rows-1)) -> {
                    Log.d("BALALSO", "I was fired")
                    minefield.findViewById<Tile>(actId+1).performClick()
                    minefield.findViewById<Tile>(actId-columns).performClick()
                    minefield.findViewById<Tile>(actId-columns+1).performClick()
                }
                //Jobb alsó
                (tile == (columns-1)) && (line == (rows-1)) -> {
                    Log.d("JOBBALSO", "I was fired")
                    minefield.findViewById<Tile>(actId-1).performClick()
                    minefield.findViewById<Tile>(actId-columns-1).performClick()
                    minefield.findViewById<Tile>(actId-columns).performClick()
                }

                //Bal oldal
                (tile == 0) && (line != (rows-1)) && (line != 0) -> {
                    Log.d("BALOLDAL", "I was fired")
                    minefield.findViewById<Tile>(actId+1).performClick()
                    minefield.findViewById<Tile>(actId+columns).performClick()
                    minefield.findViewById<Tile>(actId+columns+1).performClick()
                    minefield.findViewById<Tile>(actId-columns).performClick()
                    minefield.findViewById<Tile>(actId-columns+1).performClick()
                }

                //Jobb oldal
                (tile == (columns-1)) && (line != (rows-1)) && (line != 0) -> {
                    Log.d("JOBBOLDAL", "I was fired")
                    minefield.findViewById<Tile>(actId-1).performClick()
                    minefield.findViewById<Tile>(actId-columns-1).performClick()
                    minefield.findViewById<Tile>(actId+columns-1).performClick()
                    minefield.findViewById<Tile>(actId-columns).performClick()
                    minefield.findViewById<Tile>(actId+columns).performClick()
                }

                //Felső sor
                (line == 0) && (tile != 0) && (tile != (columns-1)) -> {
                    Log.d("FELSOSOR", "I was fired")
                    minefield.findViewById<Tile>(actId-1).performClick()
                    minefield.findViewById<Tile>(actId+1).performClick()
                    minefield.findViewById<Tile>(actId+columns-1).performClick()
                    minefield.findViewById<Tile>(actId+columns).performClick()
                    minefield.findViewById<Tile>(actId+columns+1).performClick()
                }

                //Alsó sor
                (line == (rows-1)) && (tile != 0) && (tile != (columns-1)) -> {
                    Log.d("ALSOSOR", "I was fired")
                    minefield.findViewById<Tile>(actId-1).performClick()
                    minefield.findViewById<Tile>(actId+1).performClick()
                    minefield.findViewById<Tile>(actId-columns-1).performClick()
                    minefield.findViewById<Tile>(actId-columns+1).performClick()
                    minefield.findViewById<Tile>(actId-columns).performClick()
                }

                else -> {
                    Log.d("EGYEB", "I was fired")
                    minefield.findViewById<Tile>(actId-1).performClick()
                    minefield.findViewById<Tile>(actId+1).performClick()
                    minefield.findViewById<Tile>(actId-columns-1).performClick()
                    minefield.findViewById<Tile>(actId-columns).performClick()
                    minefield.findViewById<Tile>(actId-columns+1).performClick()
                    minefield.findViewById<Tile>(actId+columns-1).performClick()
                    minefield.findViewById<Tile>(actId+columns+1).performClick()
                    minefield.findViewById<Tile>(actId+columns).performClick()
                }
            }
        }

        @SuppressLint("ResourceAsColor")
        private fun createHeader(applicationContext: Context, flagText : TextView, minefield: LinearLayout)
        {
            val uiLine = LinearLayout(applicationContext)
            uiLine.orientation = LinearLayout.HORIZONTAL
            uiLine.setBackgroundColor(R.color.black)
            uiLine.setPadding(5,5,5,5)
            uiLine.addView(flagText)
            minefield.addView(uiLine)
        }

        @SuppressLint("ResourceAsColor")
        private fun createHeaderText(applicationContext: Context, optimalButtonSize : Int) : TextView
        {
            val flagText = TextView(applicationContext)
            val text = "Zászlók száma: $remainingFlags"
            flagText.text = text
            flagText.setTextColor(Color.parseColor("#FFFFFF"))
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                flagText.setAutoSizeTextTypeUniformWithConfiguration(1 , 200, 1, TypedValue.COMPLEX_UNIT_DIP)
            }
            flagText.gravity = Gravity.CENTER_VERTICAL
            flagText.height = optimalButtonSize

            return flagText
        }

        private fun updateHeaderText(flagText: TextView)
        {
            val text = "Zászlók száma: $remainingFlags"
            flagText.text = text
        }

        private fun generateMap(temp : Tile, nOfTiles: Int, rows: Int, columns: Int, minefield: LinearLayout)
        {
            isFirstClick = false
            val mineIndexes = chooseMines(nOfTiles, temp.id)
            var currentId = 0
            for (line in 0 until rows)
            {
                for(tile in 0 until columns)
                {
                    val numberOfMinesNear = if(currentId in mineIndexes) 0 else numberOfMinesNearby(mineIndexes, currentId, columns)
                    minefield.findViewById<Tile>(currentId).setProperties(isMine = currentId in mineIndexes, isEmpty = ((numberOfMinesNear == 0) && currentId !in mineIndexes), minesNear = numberOfMinesNear)
                    currentId++
                }
            }
        }


        //Soronként egy LinearLayout és azokban pedig Tile objektumokat helyez el
        fun generateLayout(applicationContext : Context, isPracticeMode: Boolean, main_layout: RelativeLayout, minefield : LinearLayout, end_layout : ConstraintLayout, screenWidth : Int, screenHeight : Int, rowCount: Int = 20, columnCount: Int = 10, padding: Int)
        {
            var actId = 0
            isFirstClick = true
            revealedCount = 0
            this.isPracticeMode = isPracticeMode

            val rows = if((screenWidth > screenHeight) && (rowCount > columnCount)) columnCount else rowCount
            val columns = if((screenWidth > screenHeight) && (rowCount > columnCount)) rowCount else columnCount

            val nOfTiles = rows * columns
            this.mineCount = floor((10/50.0) * (nOfTiles)).toInt()
            this.remainingFlags = mineCount

            val optimalButtonSize = calcOptBtnSize(main_layout = main_layout, rows, columns, padding)

            val flagText = createHeaderText(applicationContext, optimalButtonSize)
            createHeader(applicationContext, flagText, minefield)

            for (line in 0 until rows)
            {
                val actLine = LinearLayout(applicationContext)
                actLine.orientation = LinearLayout.HORIZONTAL

                for(tile in 0 until columns)
                {

                    val temp = Tile(context = applicationContext, id = actId, params =  LinearLayout.LayoutParams(optimalButtonSize, optimalButtonSize))

                    temp.setOnClickListener {
                        if(isFirstClick)
                        {
                            generateMap(temp, nOfTiles, rows, columns, minefield)
                        }
                        onClick(temp, columns, rows, tile, line, nOfTiles, minefield, end_layout)
                    }
                    temp.setOnLongClickListener {

                        onLongClick(temp)
                        updateHeaderText(flagText)
                        true
                    }

                    actLine.addView(temp)
                    actId++
                }
                minefield.addView(actLine)
            }
        }
    }

}
