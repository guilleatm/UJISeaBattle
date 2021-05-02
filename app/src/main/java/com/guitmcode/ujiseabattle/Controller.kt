package com.guitmcode.ujiseabattle

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.guitmcode.ujiseabattle.Model.SoundPlayer
import com.guitmcode.ujiseabattle.Model.SquareColor
import es.uji.vj1229.framework.AnimatedBitmap
import es.uji.vj1229.framework.Graphics
import es.uji.vj1229.framework.IGameController
import es.uji.vj1229.framework.TouchHandler
import es.uji.vj1229.framework.TouchHandler.TouchEvent
import java.lang.Float.min

private const val TOTAL_CELLS_WIDTH = 24
private const val TOTAL_CELLS_HEIGHT = 14

class Controller(width: Int, height: Int, context: Context) : IGameController, SoundPlayer {
	companion object {

		private const val BACKGROUND_COLOR = -0xbf0fc0
		private const val LINE_COLOR = -0x1000000
		private const val WIN_COLOR = -0xfc000

		private const val MARGIN_FRACTION = 0.1f
		private const val LINEWIDTH_FRACTION = 0.01f
		private const val BALL_FRACTION = (1 - 2 * MARGIN_FRACTION - 2 * LINEWIDTH_FRACTION) / 3
	}


	private val cellSide : Float = min(width.toFloat() / TOTAL_CELLS_WIDTH, height.toFloat() / TOTAL_CELLS_HEIGHT)
	private val xOffset : Float = (width - TOTAL_CELLS_WIDTH * cellSide) / 2.0f
	private val yOffset : Float = (height - TOTAL_CELLS_HEIGHT * cellSide) / 2.0f

	private val board = Board(10, Pair(1, 2), cellSide)

	//val originX : Float = board.origin.first * cellSide + xOffset
	//val originY : Float = board.origin.second * cellSide + yOffset

	private val ballSide = (width * BALL_FRACTION).toInt()
	private val lineWidth = (width * LINEWIDTH_FRACTION).toInt()
	//private val xOffset = (width - 3 * ballSide - 2 * lineWidth) / 2
	//private val yOffset = (height - 3 * ballSide - 2 * lineWidth) / 2
	private val cellX = FloatArray(4)
	private val cellY = FloatArray(4)
	private val xReset = (0.5f * (width - ballSide)).toInt()
	private val yReset = (height * (1 - MARGIN_FRACTION) - ballSide / 2).toInt()

	private val graphics: Graphics
	private val model: Model
	private lateinit var soundPool: SoundPool
	private var victoryId = 0
	private var moveId = 0

	private var lastRow = -1
	private var lastCol = -1
	private var animation: AnimatedBitmap? = null

	init {
		//fillCellCoordinates()
		Assets.createAssets(context, ballSide)
		graphics = Graphics(width, height)
		//prepareSoundPool(context)
		model = Model(this)
	}

	private fun fillCellCoordinates() {
		val step = (cellSide + lineWidth).toFloat()
		for (i in 0 .. TOTAL_CELLS_WIDTH) {
			cellX[i] = xOffset + i * step
			cellY[i] = yOffset + i * step
		}
		cellX[3] = (xOffset + 3 * ballSide + 2 * lineWidth).toFloat()
		cellY[3] = (yOffset + 3 * ballSide + 2 * lineWidth).toFloat()
	}

	private fun prepareSoundPool(context: Context) {
		val attributes = AudioAttributes.Builder()
			.setUsage(AudioAttributes.USAGE_GAME)
			.setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
			.build()
		soundPool = SoundPool.Builder()
			.setMaxStreams(5)
			.setAudioAttributes(attributes)
			.build()
		victoryId = soundPool.load(context,
			R.raw.victory, 0)
		moveId = soundPool.load(context, R.raw.move, 0)
	}

	override fun onUpdate(deltaTime: Float, touchEvents: List<TouchEvent>) {
		animation?.update(deltaTime)
		for (event in touchEvents)
			if (event.type == TouchHandler.TouchType.TOUCH_UP) {
				if (event.x in xReset..xReset + ballSide && event.y in yReset..yReset + ballSide)
					model.restart()
				else {
					val row = Math.floorDiv(event.y - yOffset.toInt(), ballSide + lineWidth)
					val col = Math.floorDiv(event.x - xOffset.toInt(), ballSide + lineWidth)
					if (model.canPlay(row, col)) {
						model.play(row, col)
						lastRow = row
						lastCol = col
						animation = when (model.getSquare(row, col)) {
							SquareColor.BLUE -> Assets.blueBallAnimated
							SquareColor.RED -> Assets.redBallAnimated
							else -> null
						}
						animation?.restart()
					}
				}
			}
	}

	override fun onDrawingRequested(): Bitmap? {
		// if (!updated) return null
		graphics.clear(BACKGROUND_COLOR)
		graphics.drawDrawable(
			Assets.reset, xReset.toFloat(), yReset.toFloat(),
			ballSide.toFloat(), ballSide.toFloat())
		drawBoard()
		//drawPieces()
		//if (model.winner != SquareColor.EMPTY)
			//drawWinnerLine()
		return graphics.frameBuffer
	}

	private fun drawBoard() {

		val halfLineWidth = 0.5f * lineWidth

		val originX = board.origin.first - halfLineWidth + xOffset
		val originY = board.origin.second - halfLineWidth + yOffset

		val boardSize = cellSide * board.numCells
		var step = cellSide

		with(graphics) {

			for (i in 0 until board.numCells + 1) {
				drawLine(originX + i * step, originY, originX + i * step, originY + boardSize, lineWidth.toFloat(), LINE_COLOR)
				drawLine(originX, originY + i * step, originX + boardSize, originY + i * step, lineWidth.toFloat(), LINE_COLOR)

			}

		}
	}

	private fun drawPieces() {
		for (row in 0..2)
			for (col in 0..2) {
				val square = model.getSquare(row, col)
				if (square != SquareColor.EMPTY) {
					val bitmap = if (row == lastRow && col == lastCol)
						animation?.currentFrame
					else
						if (square == SquareColor.RED)
							Assets.redBall
						else
							Assets.blueBall
					graphics.drawBitmap(bitmap, cellX[col], cellY[row])
				}
			}
	}

	private fun drawWinnerLine() {
		val winnerCells = model.winnerCells
		val row0 = winnerCells[0][0]
		val row1 = winnerCells[2][0]
		val col0 = winnerCells[0][1]
		val col1 = winnerCells[2][1]
		val x0: Float
		val x1: Float
		val y0: Float
		val y1: Float
		if (row0 == row1 || col0 == col1) {
			if (row0 == row1) {
				y1 = cellY[row0] + 0.5f * ballSide
				y0 = y1
			} else {
				y0 = cellY[row0]
				y1 = cellY[row1 + 1]
			}
			if (col0 == col1) {
				x1 = cellX[col0] + 0.5f * ballSide
				x0 = x1
			} else {
				x0 = cellX[col0]
				x1 = cellX[col1 + 1]
			}
		} else {
			if (col0 == 0) {
				x0 = cellX[0]
				x1 = cellX[3]
			} else {
				x0 = cellX[3]
				x1 = cellX[0]
			}
			y0 = cellY[0]
			y1 = cellY[3]
		}
		graphics.drawLine(x0, y0, x1, y1, lineWidth.toFloat(),
			WIN_COLOR
		)
	}

	override fun playVictory() {
		//soundPool.play(victoryId, 0.6f, 0.8f, 0, 0, 1f)
	}

	override fun playMove() {
		//soundPool.play(moveId, 0.6f, 0.8f, 0, 0, 1f)
	}

}