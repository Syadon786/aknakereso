package com.example.aknakereso

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.widget.Toast
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        btnStart.setOnClickListener {

                val rowNumbers = etSorokSzama.text.toString().toIntOrNull()
                val columnNumber = etOszlopokSzama.text.toString().toIntOrNull()
                val isPracticeMode = ckGyakorloMod.isChecked
                  if (rowNumbers != null && columnNumber != null)
                  {
                      if(rowNumbers < 8 || columnNumber < 8 || rowNumbers > 30 || columnNumber > 30)
                      {
                          Toast.makeText(applicationContext, "Az értékeknek 8-30 között kell lennie!", Toast.LENGTH_SHORT).show()
                          etSorokSzama.text.clear()
                          etOszlopokSzama.text.clear()
                          etSorokSzama.requestFocus()
                      }
                      else
                      {
                          Intent(this, GameActivity::class.java).also {
                              it.putExtra("EXTRA_ROWS", rowNumbers)
                              it.putExtra("EXTRA_COLUMNS", columnNumber)
                              it.putExtra("EXTRA_PRACTICE", isPracticeMode)
                              startActivity(it)
                          }
                      }
                  }
                  else
                  {
                      Toast.makeText(applicationContext, "Számokat kell megadni 8-30 között!", Toast.LENGTH_SHORT).show()
                      etSorokSzama.text.clear()
                      etOszlopokSzama.text.clear()
                      etSorokSzama.requestFocus()
                  }

            }
        }
    }