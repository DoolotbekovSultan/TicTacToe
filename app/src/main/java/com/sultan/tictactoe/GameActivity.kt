package com.sultan.tictactoe

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.updateLayoutParams
import com.sultan.tictactoe.databinding.ActivityGameBinding

@Suppress("CAST_NEVER_SUCCEEDS")
class GameActivity : AppCompatActivity() {

    private lateinit var binding : ActivityGameBinding
    private val x = R.drawable.ic_x
    private val zero = R.drawable.ic_zero
    private var now = R.drawable.ic_x
    private val board = Array(3) { IntArray(3) }

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        binding = ActivityGameBinding.inflate(layoutInflater)
        enableEdgeToEdge()
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(binding.main) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        for (i in 0 until binding.squares.childCount) {
            val imageView = binding.squares.getChildAt(i) as ImageView
            imageView.setOnClickListener { view ->
                val square = view as ImageView
                val row = i / 3
                val col = i % 3
                onSquareClick(square, row, col)
            }
        }
    }

    private fun onSquareClick(square : ImageView, i : Int, j : Int) {
        if (board[i][j] == 0) {
            board[i][j] = now
            square.setImageResource(now)
            if (checkGameOver(i, j)) {
                showWinAlertDialog()
            } else if (checkDraw()) {
                showDrawAlertDialog()
            }
            now = if (now == zero) x else zero
            change()
        }
    }

    private fun checkGameOver(i : Int, j : Int) : Boolean {
        var horizontal = true
        var vertical = true
        var mainDiagonal = true
        var antiDiagonal = true
        for (k in 0 until 3) {
            horizontal = horizontal && (board[i][k] == now)
            vertical = vertical && (board[k][j] == now)
            mainDiagonal = mainDiagonal && (board[k][k] == now)
            antiDiagonal = antiDiagonal && (board[k][2-k] == now)
        }
        return horizontal || vertical || mainDiagonal || antiDiagonal
    }

    private fun checkDraw() : Boolean {
        for (i in 0 until 3) {
            for (j in 0 until  3) {
                if (board[i][j] == 0) {
                    return false
                }
            }
        }
        return true
    }

    private fun showWinAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
        builder.setView(dialogView).setCancelable(false)
        dialogView.findViewById<ImageView>(R.id.image).setImageResource(now)
        val dialog = builder.create()
        dialog.show()
        dialogView.findViewById<Button>(R.id.restartButton).setOnClickListener {
            restartGame()
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.exitButton).setOnClickListener {
            finish()
        }
    }
    @SuppressLint("SetTextI18n")
    private fun showDrawAlertDialog() {
        val builder = AlertDialog.Builder(this)
        val dialogView = layoutInflater.inflate(R.layout.alert_dialog, null)
        builder.setView(dialogView).setCancelable(false)
        (dialogView.findViewById<ImageView>(R.id.text) as TextView).text = "Draw!"
        dialogView.findViewById<ImageView>(R.id.image).visibility = View.GONE
        val dialog = builder.create()
        dialog.show()
        dialogView.findViewById<Button>(R.id.restartButton).setOnClickListener {
            restartGame()
            dialog.dismiss()
        }
        dialogView.findViewById<Button>(R.id.exitButton).setOnClickListener {
            finish()
        }
    }

    private fun restartGame() {
        setDownImages(binding.x, binding.zero)

        board.forEach { row -> row.fill(0) }
        for (i in 0 until binding.squares.childCount) {
            val imageView = binding.squares.getChildAt(i) as ImageView
            imageView.setImageResource(0)
        }
        now = x
    }

    private fun change() {
        lateinit var active : ImageView
        lateinit var inactive : ImageView
        if (now == zero) {
            active = binding.zero
            inactive = binding.x
        } else {
            active = binding.x
            inactive = binding.zero
        }
        setDownImages(active, inactive)
    }

    private fun setDownImages(active : ImageView, inactive : ImageView) {
        val density = resources.displayMetrics.density.toDouble()
        active.updateLayoutParams {
            height = (100 * density).toInt()
            width = (100 * density).toInt()
        }
        inactive.updateLayoutParams {
            height = (70 * density).toInt()
            width = (70 * density).toInt()
        }
        active.setBackgroundResource(R.drawable.ic_active_square_background)
        inactive.setBackgroundResource(0)
    }
}