package es.usj.mastertsa.onunez.session301

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.Window
import android.view.WindowInsets
import android.view.WindowManager
import android.widget.Toast
import es.usj.mastertsa.onunez.session301.databinding.ActivitySecondBinding
import java.lang.Integer.SIZE

const val BOMB : Int = 9
const val EMPTY : Int = 0

class SecondActivity : AppCompatActivity() {
    var board : Board? = null
    private var active : Boolean = true
    var tiles : Array<Array<Tile>>? = null
    private lateinit var bindings: ActivitySecondBinding

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        @Suppress("DEPRECATION")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            window.insetsController?.hide(WindowInsets.Type.statusBars())
        } else {
            window.setFlags(
                WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN
            )
        }
        super.onCreate(savedInstanceState)
        bindings = ActivitySecondBinding.inflate(layoutInflater)
        setContentView(bindings.root)
        board = Board(this)
        board!!.setOnTouchListener { _, event ->
            if(active) {
                for (row in 0 until SIZE) {
                    for (column in 0 until SIZE) {
                        val tile = tiles!![row][column]
                        if (tile.isInside(event.x.toInt(),
                                event.y.toInt())) {
                            tile.shown = true
                            if(tile.content == BOMB) {
                                Toast.makeText(this, "Booooooommmmmmm!!",
                                    Toast.LENGTH_SHORT).show()
                                active = false
                            } else if (tile.content == EMPTY) {
                                crawl(row, column)
                            }
                            board!!.invalidate()
                        }
                    }
                }
            }
            if(won() && active) {
                Toast.makeText(this, "You win!!!",
                    Toast.LENGTH_LONG).show()
                active = false
            }
            return@setOnTouchListener true
        }
        bindings.btnReset.setOnClickListener {
            resetTiles()
            active = true
            board!!.invalidate()
        }
        bindings.linearLayout.addView(board)
        resetTiles()
        supportActionBar?.hide()
    }

    private fun resetTiles() {
        tiles = Array(SIZE) {
            Array(SIZE) {
                Tile(0, 0, board!!.width)
            }
        }
        setBombs()
        countPerimeterBombs()
    }

    private fun setBombs() {
        var bombs = 9
        do {
            val row = (Math.random() * SIZE).toInt()
            val column = (Math.random() * SIZE).toInt()
            if(tiles!![row][column].content == EMPTY) {
                tiles!![row][column].content = BOMB
                bombs--
            }
        } while (bombs != 0)
    }

    private fun won(): Boolean {
        var shownTiles = 0
        tiles!!.forEach {
            it.forEach { tile ->
                if (tile.shown) shownTiles++
            }
        }
        return SIZE * SIZE - shownTiles - 9 == 0
    }

    private fun countPerimeterBombs() {
        for (row in 0 until SIZE) {
            for (column in 0 until SIZE) {
                if(tiles!![row][column].content == EMPTY) {
                    val neighbors = findBombsByTile(row,
                        column)
                    tiles!![row][column].content = neighbors
                }
            }
        }
    }

    private fun crawl(row: Int, column: Int) {
        if (row in 0 until SIZE && column >= 0 && column <
            SIZE) {
            if (tiles!![row][column].content == EMPTY) {
                tiles!![row][column].shown = true
                tiles!![row][column].content = 50
                crawl(row, column + 1)
                crawl(row, column - 1)
                crawl(row + 1, column)
                crawl(row - 1, column)
                crawl(row - 1, column - 1)
                crawl(row - 1, column + 1)
                crawl(row + 1, column + 1)
                crawl(row + 1, column - 1)
            } else if (tiles!![row][column].content in 1..9) {
                tiles!![row][column].shown = true
            }
        }
    }

    private fun findBombsByTile(row: Int, column: Int) : Int {
        var total = 0
        if (row - 1 >= 0 && column - 1 >= 0)
            if (tiles!![row - 1][column - 1].content == BOMB)
                total++
        if (row - 1 >= 0)
            if (tiles!![row - 1][column].content == BOMB)
                total++
        if (row - 1 >= 0 && column + 1 < SIZE)
            if (tiles!![row - 1][column + 1].content == BOMB)
                total++
        if (column + 1 < SIZE)
            if (tiles!![row][column + 1].content == BOMB)
                total++
        if (row + 1 < SIZE && column + 1 < SIZE)
            if (tiles!![row + 1][column + 1].content == BOMB)
                total++
        if (row + 1 < SIZE)
            if (tiles!![row + 1][column].content == BOMB)
                total++
        if (row + 1 < SIZE && column - 1 >= 0)
            if (tiles!![row + 1][column - 1].content == BOMB)
                total++
        if (column - 1 >= 0)
            if (tiles!![row][column - 1].content == BOMB)
                total++
        return total
    }


}

data class Tile (var x: Int, var y: Int, var width: Int, var content: Int = EMPTY, var shown: Boolean = false) {

    fun isInside(newX: Int, newY: Int): Boolean {
        return newX >= x && newX <= x + width && newY >= y && newY <= y + width
    }
}

class Board(context: Context) : View(context) {
    private val text = Paint()
    private val tilePainter = Paint()
    private val lines = Paint()
    private val bomb = Paint()
    override fun onDraw(canvas: Canvas?) {
        canvas!!.drawRGB(0,0,0)
        val activity = context as SecondActivity
        val width = minOf(activity.board!!.width,
            activity.board!!.height)
        val tileSize = width / SIZE
        tilePainter.textSize = 50f
        text.textSize = 50f
        text.typeface = Typeface.DEFAULT_BOLD
        text.setARGB(255,0,0,255)
        lines.setARGB(255,255,255,255)
        var currentRow = 0
        for (row in 0 until SIZE) {
            for (column in 0 until SIZE) {
                val tile = activity.tiles!![row][column]
                tile.x = column * tileSize
                tile.y = currentRow
                tile.width = tileSize
                if(tile.shown)
                    tilePainter.setARGB(153, 204, 204, 204)
                else
                    tilePainter.setARGB(255, 153, 153, 153)
                canvas.drawRect((column * tileSize).toFloat(),
                    currentRow.toFloat(),
                    (column * tileSize + tileSize -2).toFloat(),
                    (currentRow + tileSize - 2).toFloat(), tilePainter)
                canvas.drawLine(
                    (column * tileSize).toFloat(),
                    currentRow.toFloat(),
                    (column * tileSize + tileSize).toFloat(),
                    currentRow.toFloat(),
                    lines)
                canvas.drawLine(
                    (column * tileSize + tileSize - 1).toFloat(),
                    currentRow.toFloat(),
                    (column * tileSize + tileSize - 1).toFloat(),
                    (currentRow + tileSize).toFloat(),
                    lines)
                if(tile.content in 1 until 9 && tile.shown) {
                    canvas.drawText("${tile.content}",
                        (column * tileSize + tileSize / 2 - 1 *
                                SIZE).toFloat(),
                        (currentRow + tileSize / 2).toFloat(),
                        text)
                }
                if(tile.content == BOMB && tile.shown) {
                    bomb.setARGB(255,255,0,0)
                    canvas.drawCircle((column * tileSize + tileSize /
                            2).toFloat(),
                        (currentRow + tileSize / 2).toFloat(),
                        SIZE.toFloat(), bomb)
                }
                activity.tiles!![row][column] = tile
            }
            currentRow += tileSize
        }
    }
}