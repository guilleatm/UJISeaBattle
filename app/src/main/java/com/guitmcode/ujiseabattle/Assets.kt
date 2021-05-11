package com.guitmcode.ujiseabattle

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.drawable.Drawable
import android.util.Log
import com.guitmcode.ujiseabattle.R
import es.uji.vj1229.framework.AnimatedBitmap
import es.uji.vj1229.framework.Graphics
import es.uji.vj1229.framework.SpriteSheet

object Assets {
	private const val BALL_DURATION = 0.2f
	private const val BALL_FRAMES = 10
	private const val SPRITE_BALL_SIDE = 128
	private const val RED_BALL_INDEX = 0
	private const val BLUE_BALL_INDEX = 3
	private var ballSprites: Bitmap? = null
	private var balls: SpriteSheet? = null
	var blueBall: Bitmap? = null
	var redBall: Bitmap? = null
	var blueBallAnimated: AnimatedBitmap? = null
	var redBallAnimated: AnimatedBitmap? = null
	var reset: Drawable? = null
	var barcosMetidos : Boolean = false
	var textosMetidos : Boolean = false

	var todosTextos : ArrayList<Bitmap> = ArrayList()

	private var shipSprites: Bitmap? = null
	private var ships: SpriteSheet? = null

	private const val SHIP_BITMAP_WIDTH = 66
	private const val SHIP_BITMAP_HEIGTH = 190

	var ship: Drawable? = null
	var todosShips : ArrayList<Bitmap> = ArrayList()
	var animatedShip: AnimatedBitmap? = null

	fun createAssets(context: Context, cellSide: Int) {
		val resources = context.resources
/*
		shipSprites?.recycle()
		shipSprites = BitmapFactory.decodeResource(resources, R.drawable.barcos)

		ships = SpriteSheet(shipSprites, SHIP_BITMAP_WIDTH, SHIP_BITMAP_HEIGTH).apply {
			ship?.recycle()
			ship = getScaledSprite(0, 0, SHIP_BITMAP_WIDTH, SHIP_BITMAP_HEIGTH)
		}
*/
		if (ship == null) // No borrar
			ship = context.getDrawable(R.drawable.ship)

		if (reset == null)
			reset = context.getDrawable(R.drawable.reset)

		if (!barcosMetidos) {
			/*todosShips[0] = BitmapFactory.decodeResource(resources, R.drawable.ship)
			todosShips[1] = BitmapFactory.decodeResource(resources, R.drawable.ship2)
			todosShips[2] = BitmapFactory.decodeResource(resources, R.drawable.shiproto)
			todosShips[3] = BitmapFactory.decodeResource(resources, R.drawable.barcoroto2)*/
			/*todosShips.add(0, BitmapFactory.decodeResource(resources, R.drawable.ship))
			todosShips.add(1, BitmapFactory.decodeResource(resources, R.drawable.ship2))
			todosShips.add(2, BitmapFactory.decodeResource(resources, R.drawable.shiproto))
			todosShips.add(3, BitmapFactory.decodeResource(resources, R.drawable.barcoroto2))*/

			val SHIP_LONG = 3
			val SHIP_FAT = 1

			todosShips.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.ship1), cellSide * SHIP_LONG, cellSide * SHIP_FAT, true))
			todosShips.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.ship1v), cellSide * SHIP_FAT,cellSide * SHIP_LONG, true))
			todosShips.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.shiproto1), cellSide * SHIP_LONG, cellSide * SHIP_FAT, true))
			todosShips.add(Bitmap.createScaledBitmap(BitmapFactory.decodeResource(resources, R.drawable.shiproto1v), cellSide * SHIP_FAT,cellSide * SHIP_LONG, true))
			barcosMetidos = true
		}

		if (!textosMetidos) {
			todosTextos.add(BitmapFactory.decodeResource(resources, R.drawable.drag))
			todosTextos.add(BitmapFactory.decodeResource(resources, R.drawable.yourturn))
			todosTextos.add(BitmapFactory.decodeResource(resources, R.drawable.enemyturn))
			todosTextos.add(BitmapFactory.decodeResource(resources, R.drawable.victory))
			todosTextos.add(BitmapFactory.decodeResource(resources, R.drawable.defeat))
			textosMetidos = true
		}

		//Log.d("marselo", todosShips.toString())

		/*
        ballSprites?.recycle()
        ballSprites = BitmapFactory.decodeResource(resources, R.drawable.balls)
        balls = SpriteSheet(ballSprites,SPRITE_BALL_SIDE,SPRITE_BALL_SIDE).apply {
            blueBall?.recycle()
            blueBall = getScaledSprite(0, BLUE_BALL_INDEX, ballSide, ballSide)
            redBall?.recycle()
            redBall = getScaledSprite(0, RED_BALL_INDEX, ballSide, ballSide)
        }

        blueBallAnimated?.recycle()
        blueBallAnimated = createAnimation(BLUE_BALL_INDEX, ballSide)
        redBallAnimated?.recycle()
        redBallAnimated = createAnimation(RED_BALL_INDEX, ballSide)
*/
/*
        if (reset == null)
            reset = context.getDrawable(R.drawable.reset)
*/
	}

	private fun createAnimation(index: Int, ballSide: Int): AnimatedBitmap {
		val frames = Array<Bitmap>(BALL_FRAMES) {
			val side = ballSide * (it + 1) / BALL_FRAMES
			val sprite = balls!!.getScaledSprite(0, index, side, side)
			val x = (ballSide - side) / 2f
			with (Graphics(ballSide, ballSide)) {
				drawBitmap(sprite, x, x)
				frameBuffer
			}
		}
		return AnimatedBitmap(BALL_DURATION, false, *frames)
	}
}