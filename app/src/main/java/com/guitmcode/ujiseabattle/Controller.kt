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
private const val RESET_COL = 11f
private const val RESET_ROW = 12.5f
private const val RESET_SIDE = 2f
private val CELL_OFFSET = Pair(1, 2)

class Controller(width: Int, height: Int, val context: Context, val sound: Float) : IGameController, SoundPlayer {
	companion object {

		private const val BACKGROUND_COLOR = -0xa64f1f
		private const val LINE_COLOR = -0x1000000
		private const val WIN_COLOR = -0xfc000

		private const val LINEWIDTH_FRACTION = 0.01f
	}


	private val cellSide : Int = min(width.toFloat() / TOTAL_CELLS_WIDTH, height.toFloat() / TOTAL_CELLS_HEIGHT).toInt()
	private val xOffset : Float = (width - TOTAL_CELLS_WIDTH * cellSide) / 2.0f
	private val yOffset : Float = (height - TOTAL_CELLS_HEIGHT * cellSide) / 2.0f

	private var board = Board(BOARD_SIZE, CELL_OFFSET, cellSide.toFloat())
	private var computerBoard = Board(BOARD_SIZE, Pair(13, 2), cellSide.toFloat())

	private lateinit var ships: Array<Ship>
	private lateinit var computerShips: Array<Ship>
	private var dragged: Ship? = null
	private var arrastrando : Boolean = false

	//val originX : Float = board.origin.first * cellSide + xOffset
	//val originY : Float = board.origin.second * cellSide + yOffset


	private val lineWidth = (width * LINEWIDTH_FRACTION).toInt()


	private val graphics: Graphics
	private lateinit var model: Model
	private lateinit var soundPool: SoundPool
	private var bombId = 0
	private var waterId = 0

	private var animation: AnimatedBitmap? = null

	init {
		graphics = Graphics(width, height)
		initShips(context)
		model = Model(this, board, computerBoard, ships)
		model.createComputerBoard(computerShips)

		prepareSoundPool(context)
	}

	fun restart(context: Context) {

		initShips(context)

		board = Board(BOARD_SIZE, CELL_OFFSET, cellSide.toFloat())
		computerBoard = Board(BOARD_SIZE, Pair(13, 2), cellSide.toFloat())

		dragged = null

		prepareSoundPool(context)
		//model = Model(this, board, computerBoard, ships)
		model.reset(this, board, computerBoard, ships)
		model.createComputerBoard(computerShips)

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
		bombId = soundPool.load(context,
			R.raw.explosion, 0)
		waterId = soundPool.load(context, R.raw.splash1, 0)
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

		arrastrando = true
	}

	private fun onTouchUp(event: TouchEvent) {
		var (col, row) = getTouchCells(event) // col: from 0 to TOTAL_CELLS_WIDTH, row: from 0 to TOTAL_CELLS_HEIGTH

		val halfLineWidth = 0.5f * lineWidth
		val originX = board.origin.first - halfLineWidth + xOffset
		val originY = board.origin.second - halfLineWidth + yOffset

		if (clickOnReset(col, row)) {
			restart(context)
			return
		}


		if (model.state == Model.SeaBattleAction.PLACE_SHIPS) {

			for (ship in ships) {
				if (ship.clicked(event) && !ship.set) {
					if (arrastrando == false) {
						ship.isHorizontal = !ship.isHorizontal
					}
				}
			}

			if (board.inBoard(col, row)) { // Soltamos click dentro del tablero del jugador
				val rCol = col - board.oI.first // col: from 0 to board.numCells - 1
				val rRow = row - board.oI.second // row: from 0 to board.numCells - 1

				Log.d("marselo", Pair(rCol, rRow).toString())
				if (dragged != null) {

					if (dragged!!.fits(rCol, rRow, board)) { // El barco cabe en el tablero
						//dragged!!.coords = Pair(col * cellSide, row * cellSide)
						dragged!!.coords = Pair((originX + cellSide * rCol).toInt(), (originY + cellSide * rRow).toInt())
						dragged!!.coordsTablero = Pair(rCol, rRow)
						dragged!!.set = true
						board.setShipOnBoard(dragged!!)
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
			arrastrando = false
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
		if (model.state != Model.SeaBattleAction.PLACE_SHIPS) {
			drawBoard(computerBoard)
		}
		drawShips()
		drawText()
		drawResetButton()


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

		var lineColor = LINE_COLOR

		with(graphics) {

			for (i in 0 until board.numCells + 1) {
				drawLine(originX + i * step, originY, originX + i * step, originY + boardSize, lineWidth.toFloat(), LINE_COLOR)
				drawLine(originX, originY + i * step, originX + boardSize, originY + i * step, lineWidth.toFloat(), LINE_COLOR)

			}

			// Drawing bombed cells
			for (bombedCell in board.bombedCells) {
				var lineColor = LINE_COLOR
				if (bombedCell.third) {
					lineColor = WIN_COLOR
				}
				drawLine(originX + (bombedCell.first * step), originY + (bombedCell.second * step), originX + ((bombedCell.first + 1) * step), originY + ((bombedCell.second + 1) * step), lineWidth.toFloat(), lineColor)
			}
		}
	}


	override fun playBomb() {
		soundPool.play(bombId, sound, sound, 0, 0, 1f)
	}

	override fun playWater() {
		soundPool.play(waterId, sound, sound, 0, 0, 1f)
	}

	fun initShips(context: Context) {
		Assets.createAssets(context, cellSide)

		val occupedCells = 3
		ships = arrayOf(Ship(Assets.ship!!, 1, cellSide, 0), Ship(Assets.ship!!, 2, cellSide,1), Ship(Assets.ship!!, 3, cellSide,2),
						Ship(Assets.ship!!, 1, cellSide, 3), Ship(Assets.ship!!, 2, cellSide,4), Ship(Assets.ship!!, 3, cellSide,5),
						Ship(Assets.ship!!, 1, cellSide, 6), Ship(Assets.ship!!, 2, cellSide,7), Ship(Assets.ship!!, 4, cellSide,8),
						Ship(Assets.ship!!, 1, cellSide, 9))

		computerShips = arrayOf(Ship(Assets.ship!!, 1, cellSide, 0), Ship(Assets.ship!!, 2, cellSide,1), Ship(Assets.ship!!, 3, cellSide,2),
				Ship(Assets.ship!!, 1, cellSide, 3), Ship(Assets.ship!!, 2, cellSide,4), Ship(Assets.ship!!, 3, cellSide,5),
				Ship(Assets.ship!!, 1, cellSide, 6), Ship(Assets.ship!!, 2, cellSide,7), Ship(Assets.ship!!, 4, cellSide,8),
				Ship(Assets.ship!!, 1, cellSide, 9))
	}

	fun drawShips() {
		if (model.splash.third == true) {
			Assets.waterSplash!!.update(0.025f)
			graphics.drawBitmap(Assets.waterSplash!!.currentFrame, model.splash.first.toFloat() * cellSide, model.splash.second.toFloat() * cellSide)
		}
		for (ship in ships) {
			var indice = 0
			//graphics.drawDrawable(ship.drawable, ship.coords.first.toFloat(), ship.coords.second.toFloat(), cellSide.toFloat() * ship.occupedCells, cellSide.toFloat())
			//graphics.drawBitmap(Assets.todosShips[0], ship.coords.first.toFloat(), ship.coords.second.toFloat(), cellSide.toFloat() * ship.occupedCells, cellSide.toFloat())
			if (ship.isHorizontal && !ship.isSank()) {
				indice = 0
			}
			else if (!ship.isHorizontal && !ship.isSank()) {
				indice = 1
			}
			else if (ship.isHorizontal && ship.isSank()) {
				indice = 2
			}
			else if (!ship.isHorizontal && ship.isSank()) {
				indice = 3
			}
			graphics.drawBitmap(Assets.todosShips[(ship.occupedCells - 1) * 4 + indice], ship.coords.first.toFloat(), ship.coords.second.toFloat())
		}
		for (ship in computerShips) {
			var indiceComputer = 0
			//graphics.drawDrawable(ship.drawable, ship.coords.first.toFloat(), ship.coords.second.toFloat(), cellSide.toFloat() * ship.occupedCells, cellSide.toFloat())
			//graphics.drawBitmap(Assets.todosShips[0], ship.coords.first.toFloat(), ship.coords.second.toFloat(), cellSide.toFloat() * ship.occupedCells, cellSide.toFloat())

			if (ship.isHorizontal) {
				indiceComputer = 2
			}
			else if (!ship.isHorizontal) {
				indiceComputer = 3
			}
			if (ship.isSank()) {
				graphics.drawBitmap(Assets.todosShips[(ship.occupedCells - 1) * 4 + indiceComputer], ship.coords.first.toFloat(), ship.coords.second.toFloat())
			}
		}
	}

	fun drawText() {
		if (model.state == Model.SeaBattleAction.PLACE_SHIPS) {
			graphics.drawBitmap(Assets.todosTextos[0], 0f, 0f)
		}
		else if (model.state == Model.SeaBattleAction.PLAYER_TURN) {
			graphics.drawBitmap(Assets.todosTextos[1], 0f, 0f)
		}
		else if (model.state == Model.SeaBattleAction.COMPUTER_TURN) {
			graphics.drawBitmap(Assets.todosTextos[2], 0f, 0f)
		}
		else if (model.state == Model.SeaBattleAction.END && model.victoriaJugador == true) {
			graphics.drawBitmap(Assets.todosTextos[3], 0f, 0f)
		}
		else if (model.state == Model.SeaBattleAction.END && model.victoriaJugador == false) {
			graphics.drawBitmap(Assets.todosTextos[4], 0f, 0f)
		}
	}

	fun drawResetButton() {
		graphics.drawDrawable(Assets.reset, RESET_COL * cellSide, RESET_ROW * cellSide, RESET_SIDE * cellSide, RESET_SIDE * cellSide)
	}

	fun clickOnReset(col: Int, row: Int): Boolean {
		return col.toFloat() in RESET_COL .. (RESET_COL + RESET_SIDE) && row.toFloat() in RESET_ROW .. (RESET_ROW + RESET_SIDE)
	}
}