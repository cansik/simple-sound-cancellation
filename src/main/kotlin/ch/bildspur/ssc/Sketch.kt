package ch.bildspur.ssc

import ch.bildspur.ssc.util.ExponentialMovingAverage
import ch.bildspur.ssc.util.format
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics

class Sketch : PApplet() {
    companion object {
        @JvmStatic
        val HIGH_RES_FRAME_RATE = 60f

        @JvmStatic
        val WINDOW_WIDTH = 1024
        @JvmStatic
        val WINDOW_HEIGHT = 576

        @JvmStatic
        val NAME = "Simple Sound Cancellation"

        @JvmStatic
        val VERSION = "0.1"

        @JvmStatic
        fun map(value: Double, start1: Double, stop1: Double, start2: Double, stop2: Double): Double {
            return start2 + (stop2 - start2) * ((value - start1) / (stop1 - start1))
        }
    }

    private val fpsOverTime = ExponentialMovingAverage(0.05)

    @Volatile
    var isResetRendererProposed = false

    init {
    }

    fun run()
    {
        runSketch()
    }

    override fun settings() {
        size(WINDOW_WIDTH, WINDOW_HEIGHT, PConstants.FX2D)
        smooth()

        // retina screen
        pixelDensity = 2
    }

    override fun setup() {
        frameRate(HIGH_RES_FRAME_RATE)
        colorMode(HSB, 360f, 100f, 100f)
    }

    override fun draw() {
        background(0)

        drawFPS(g)
    }

    private fun drawFPS(pg: PGraphics) {
        // add and draw fps
        fpsOverTime += frameRate.toDouble()

        pg.textAlign(PApplet.LEFT, PApplet.BOTTOM)
        pg.fill(255)
        pg.textSize(12f)
        pg.text("FPS: ${frameRate.format(2)}\nFOT: ${fpsOverTime.average.format(2)}", 10f, height - 5f)
    }
}