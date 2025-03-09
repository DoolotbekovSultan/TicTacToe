package com.sultan.tictactoe

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.sultan.tictactoe.databinding.ActivityMenuBinding

class MenuActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMenuBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        
        super.onCreate(savedInstanceState)
        binding = ActivityMenuBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        binding.playButton.setOnClickListener {
            intent = Intent(this@MenuActivity, GameActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.quitButton.setOnClickListener {
            finish()
        }
        
    }
    
}