package com.example.playcarrito

import android.graphics.Rect
import android.os.Bundle
import android.view.MotionEvent
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var mio: ImageView
    private lateinit var carcol: ImageView
    private lateinit var frameLayout: FrameLayout

    private var carcolY = 0f
    private var speed = 10
    private var score = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mio = findViewById(R.id.mio)
        carcol = findViewById(R.id.carcol)
        frameLayout = findViewById(R.id.frameLayout)

        // Posicionar 'mio' en la parte inferior, centrado horizontalmente
        frameLayout.post {
            mio.x = (frameLayout.width - mio.width) / 2f
            mio.y = (frameLayout.height - mio.height).toFloat()
        }

        // Movimiento de 'mio' mediante touch
        mio.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_MOVE) {
                val newX = event.rawX - mio.width / 2
                // Limitar el movimiento dentro de los bordes del frameLayout
                mio.x = newX.coerceIn(0f, frameLayout.width - mio.width.toFloat())
            }
            true
        }

        startGame()
    }

    private fun startGame() {
        resetCarcolPosition()
        moveCarcol()
    }

    private fun resetCarcolPosition() {
        carcolY = -carcol.height.toFloat() // Iniciar fuera de la pantalla en la parte superior
        carcol.x = (0..(frameLayout.width - carcol.width)).random().toFloat()
        carcol.y = carcolY
    }

    private fun moveCarcol() {
        carcol.y += speed

        if (carcol.y > frameLayout.height) {
            score++
            increaseSpeed()
            resetCarcolPosition()
        }

        // Comprobar colisión solo si las rectas intersectan
        if (checkCollision()) {
            showGameOverDialog()
        } else {
            carcol.postDelayed({ moveCarcol() }, 16)
        }
    }

    private fun checkCollision(): Boolean {
        val carcolRect = Rect()
        carcol.getHitRect(carcolRect)

        val mioRect = Rect()
        mio.getHitRect(mioRect)

        // Verificar colisión solo si hay intersección significativa
        return Rect.intersects(carcolRect, mioRect)
    }

    private fun increaseSpeed() {
        if (score % 2 == 0) {
            speed++
        }
    }

    private fun showGameOverDialog() {
        AlertDialog.Builder(this)
            .setTitle("Perdiste")
            .setMessage("Esquivaste $score coches")
            .setPositiveButton("Reiniciar") { _, _ -> resetGame() }
            .show()
    }

    private fun resetGame() {
        speed = 10
        score = 0
        resetCarcolPosition()
        startGame()
    }
}