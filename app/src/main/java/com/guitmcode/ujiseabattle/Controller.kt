package com.guitmcode.ujiseabattle

import android.content.Context
import android.graphics.Bitmap
import android.media.AudioAttributes
import android.media.SoundPool
import android.util.Log
import com.guitmcode.ujiseabattle.Model.SoundPlayer
import es.uji.vj1229.framework.AnimatedBitmap
import es.uji.vj1229.framework.Graphics
import es.uji.vj1229.framework.IGameController
import es.uji.vj1229.framework.TouchHandler
import es.uji.vj1229.framework.TouchHandler.TouchEvent
import java.lang.Float.min

private const val TOTAL_CELLS_WIDTH = 24
private const val TOTAL_CELLS_HEIGHT = 14
private const val BOARD_SIZE = 10
private val CELL_OFFSET = Pair(1, 2)

class Controller(width: Int, height: Int, context: Context) : IGameController, SoundPlayer {
	companion object {

		private const val BACKGROUND_COLOR = -0xbf0fc0
		private const val LINE_COLOR = -0x1000000
		private const val WIN_COLOR = -0xfc000

		private const val MARGIN_FRACTION = 0.1f
		private const val LINEWIDTH_FRACTION = 0.01f
		private const val BALL_FRACTION = (1 - 2 * MARGIN_FRACTION - 2 * LINEWIDTH_FRACTION) / 3
	}


	private val cellSide : Int = min(width.toFloat() / TOTAL_CELLS_WIDTH, height.toFloat() / TOTAL_CELLS_HEIGHT).toInt()
	private val xOffset : Float = (width - TOTAL_CELLS_WIDTH * cellSide) / 2.0f
	private val yOffset : Float = (height - TOTAL_CELLS_HEIGHT * cellSide) / 2.0f

	private val board = Board(BOARD_SIZE, CELL_OFFSET, cellSide.toFloat())
	private val computerBoard = Board(BOARD_SIZE, Pair(13, 2), cellSide.toFloat())

	private lateinit var ships: Array<Ship>
	private var dragged: Ship? = null

	//val originX : Float = board.origin.first * cellSide + xOffset
	//val originY : Float = board.origin.second * cellSide + yOffset


	private val lineWidth = (width * LINEWIDTH_FRACTION).toInt()


	private val graphics: Graphics
	private val model: Model
	private lateinit var soundPool: SoundPool
	private var victoryId = 0
	private var moveId = 0

	private var animation: AnimatedBitmap? = null

	init {
		//fillCellCoordinates()
		initShips(context)

		graphics = Graphics(width, height)
		//prepareSoundPool(context)
		model = Model(this, board, computerBoard, ships)
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

		for (event in touchEvents) {

			when (event.type) {
				TouchHandler.TouchType.TOUCH_DOWN -> onTouchDown(event)
				TouchHandler.TouchType.TOUCH_DRAGGED -> onTouchDragged(event)
				TouchHandler.TouchType.TOUCH_UP -> onTouchUp(event)
			}

		}
	}

	private fun onTouchDown(event: TouchEvent) {
		dragged = null
		for (ship in ships) {
			if (ship.clicked(event) && !ship.set) {
				dragged = ship
				dragged!!.coords = Pair(event.x, event.y)
				return
			}
		}
	}

	private fun onTouchDragged(event: TouchEvent) {
		if (dragged == null) return

		dragged!!.coords = Pair(event.x, event.y)


	}

	private fun onTouchUp(event: TouchEvent) {
		var (col, row) = getTouchCells(event) // col: from 0 to TOTAL_CELLS_WIDTH, row: from 0 to TOTAL_CELLS_HEIGTH



		if (model.state == Model.SeaBattleAction.PLACE_SHIPS) {

			if (board.inBoard(col, row)) { // Soltamos click dentro del tablero del jugador
				val rCol = col - board.oI.first // col: from 0 to board.numCells - 1
				val rRow = row - board.oI.second // row: from 0 to board.numCells - 1

				if (dragged != null) {

					if (dragged!!.fits(rCol, rRow, board)) { // El barco cabe en el tablero
						dragged!!.coords = Pair(col * cellSide, row * cellSide)
						dragged!!.set = true
						dragged = null
						model.updateGameState()
					} else { // El barco no cabe en el tablero
						dragged?.coords = dragged!!.OCoords
					}

				}
			} else { // Hemos soltado el barco fuera del tablero
				dragged?.coords = dragged!!.OCoords
			}

			dragged = null // Not necessary, just in case
			return

		} else if (model.state == Model.SeaBattleAction.PLAYER_TURN) {
			if (computerBoard.inBoard(col, row)) { // Soltamos click dentro del tablero del oponente
				val rCol = col - computerBoard.oI.first // col: from 0 to board.numCells - 1
				val rRow = row - computerBoard.oI.second // row: from 0 to board.numCells - 1

				val touched = model.bomb(rCol, rRow)
			}
		}





	}

	fun getTouchCells(event: TouchEvent): Pair<Int, Int> {
		return Pair(Math.floorDiv(event.x - xOffset.toInt(), cellSide), Math.floorDiv(event.y - yOffset.toInt(), cellSide))
	}

	override fun onDrawingRequested(): Bitmap? {
		// if (!updated) return null
		graphics.clear(BACKGROUND_COLOR)
		drawBoard(board)
		drawShips()

		if (model.state != Model.SeaBattleAction.PLACE_SHIPS) {
			drawBoard(computerBoard)
		}


		//drawPieces()
		//if (model.winner != SquareColor.EMPTY)
			//drawWinnerLine()
		return graphics.frameBuffer
	}

	private fun drawBoard(board: Board) {

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

			// Drawing bombed cells
			for (bombedCell in board.bombedCells) {
				drawLine(originX + (bombedCell.first * step), originY + (bombedCell.second * step), originX + ((bombedCell.first + 1) * step), originY + ((bombedCell.second + 1) * step), lineWidth.toFloat(), LINE_COLOR)
			}
		}
	}


	override fun playVictory() {
		//soundPool.play(victoryId, 0.6f, 0.8f, 0, 0, 1f)
	}

	override fun playMove() {
		//soundPool.play(moveId, 0.6f, 0.8f, 0, 0, 1f)
	}

	fun initShips(context: Context) {
		Assets.createAssets(context, cellSide.toInt())

		val occupedCells = 3

		ships = arrayOf(Ship(Assets.ship!!, occupedCells, cellSide, 0), Ship(Assets.ship!!, occupedCells, cellSide,1), Ship(Assets.ship!!, occupedCells, cellSide,2))


	}

	fun drawShips() {

		for (ship in ships) {
			graphics.drawDrawable(ship.drawable, ship.coords.first.toFloat(), ship.coords.second.toFloat(), cellSide.toFloat() * ship.occupedCells, cellSide.toFloat())
		}
	}

}