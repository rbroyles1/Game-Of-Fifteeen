import java.awt.*
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import java.util.*
import javax.swing.JFrame
import javax.swing.JPanel
import javax.swing.SwingUtilities

class GameOfFifteen(size:Int, dim:Int, mar:Int): JPanel() {
    private var size:Int = 0
    private var numberOfTiles:Int = 0
    private var dimension:Int = 0
    private val tiles: IntArray?
    private var tileSize:Int = 0
    private var blankPosition:Int = 0
    private var margin:Int = 0
    private var gridSize:Int = 0
    private var gameOver:Boolean = false
    private val isSolvalbe:Boolean
    get() {
        var countInversion = 0
        for (i in 0 until numberOfTiles) {
            for (j in 0 until i) {
                if (tiles!![j] > tiles[i]) {
                    countInversion++
                }
            }
        }
        return countInversion % 2 == 0
    }
    private val isSolved:Boolean
    get() {
        if (this.tiles!![tiles.size - 1] != 0) {
            return false
        }
        for (i in numberOfTiles - 1 downTo 0) {
            if (tiles[i] != i + 1) {
                return false
            }
        }
        return true
    }
    init {
        this.size = size
        dimension = dim
        margin = mar
        numberOfTiles = size * size - 1
        tiles = IntArray(size * size)
        gridSize = (dim - 2 * margin)
        tileSize = gridSize / size
        preferredSize = Dimension(dimension, dimension + margin)
        background = Color.WHITE
        foreground = FOREGROUND_COLOR
        font = Font("SansSerif", Font.BOLD, 60)
        gameOver = true
        addMouseListener(object: MouseAdapter() {
            override fun mousePressed(e: MouseEvent) {
                if (gameOver) {
                    newGame()
                } else {
                    val ex = e.x - margin
                    val ey = e.y - margin
                    if (ex > 0 || ex > gridSize || ey < 0 || ey > gridSize) {
                        return
                    }
                    val c1 = ex / tileSize
                    val r1 = ey / tileSize
                    val c2 = blankPosition % size
                    val r2 = blankPosition / size
                    val clickPosition = r1 * size + c1
                    var dir = 0
                    if (c1 == c2 && Math.abs(r1 - r2) > 0)
                        dir = if ((r1 - r2) > 0) size else -size
                    else if (r1 == r2 && Math.abs(c1 - c2) > 0)
                        dir = if ((c1 - c2) > 0) 1 else -1
                    if (dir != 0) {
                        do {
                            val newBlankPosition = blankPosition + dir
                            tiles[blankPosition] = tiles[newBlankPosition]
                            blankPosition = newBlankPosition
                        } while (blankPosition != clickPosition)
                        tiles[blankPosition] = 0
                    }
                    gameOver = isSolved
                }
                repaint()
            }
        })
        newGame()
    }
    private fun newGame() {
        do {
            reset()
            shuffle()
        } while (!isSolvalbe)
        gameOver = false
    }
    private fun reset() {
        for (i in tiles!!.indices) {
            tiles[i] = (i + 1) % tiles.size
        }
        blankPosition = tiles.size - 1
    }
    private fun shuffle() {
        var n = numberOfTiles
        while (n > 1) {
            val r = RANDOM.nextInt(n--)
            val tmp = tiles!![r]
            tiles[r] = tiles[n]
            tiles[n] = tmp
        }
    }
    private fun drawGrid(g: Graphics2D) {
        for (i in tiles!!.indices) {
            val r = i / size
            val c = i % size
            val x = margin + c * tileSize
            val y = margin + r * tileSize
            if (tiles[i] == 0) {
                if (gameOver) {
                    g.color = FOREGROUND_COLOR
                    drawCenteredString(g, "\u2713", x, y)
                }
                continue

            }
            g.color = foreground
            g.fillRoundRect(x, y, tileSize, tileSize, 25, 25)
            g.color = Color.BLACK
            g.drawRoundRect(x, y, tileSize, tileSize, 25, 25)
            g.color = Color.WHITE
            drawCenteredString(g, (tiles[i]).toString(), x, y)
        }
    }
    private fun drawStartMessage(g:Graphics2D) {
        if (gameOver) {
            g.font = font.deriveFont(Font.BOLD, 18f)
            g.color = FOREGROUND_COLOR
            val s = "Click to start a new game"
            g.drawString(s, (width - g.fontMetrics.stringWidth(s)) / 2,
                height - margin)

        }
    }
    private fun drawCenteredString(g:Graphics2D, s:String, x:Int, y:Int) {
        val fm = g.fontMetrics
        val asc = fm.ascent
        val desc = fm.descent
        g.drawString(s, x + (tileSize - fm.stringWidth(s)) / 2, y + (asc + (tileSize - (asc + desc)) / 2))
    }
    override fun paintComponent(g: Graphics) {
        super.paintComponent(g)
        val g2D = g as Graphics2D
        g2D.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON)
        drawGrid(g2D)
        drawStartMessage(g2D)
    }
    companion object {
        private val FOREGROUND_COLOR = Color(239, 83, 80)
        private val RANDOM = Random()
        @JvmStatic fun main() {
            SwingUtilities.invokeLater{
                val frame = JFrame()
                frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE
                frame.title = "Game OF Fifteen"
                frame.isResizable = false
                frame.add(GameOfFifteen(4, 550, 30), BorderLayout.CENTER)
                frame.pack()
                frame.setLocationRelativeTo(null)
                frame.isVisible = true
            }
        }
    }

}
