package com.arestov.playlistmaker

import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonSearch = findViewById<Button>(R.id.button_search)
        val buttonMedia = findViewById<Button>(R.id.button_media)
        val button_setting = findViewById<Button>(R.id.button_setting)

        buttonSearch.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Search!", Toast.LENGTH_SHORT).show()
        }
        buttonMedia.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Media!", Toast.LENGTH_SHORT).show()
        }
        button_setting.setOnClickListener {
            Toast.makeText(this@MainActivity, "Нажали на кнопку Setting!", Toast.LENGTH_SHORT).show()
        }
    }
}