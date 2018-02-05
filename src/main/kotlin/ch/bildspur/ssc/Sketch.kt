package ch.bildspur.ssc

import ch.bildspur.ssc.sound.CircularBuffer
import ch.bildspur.ssc.util.ExponentialMovingAverage
import ch.bildspur.ssc.util.format
import grafica.GPlot
import grafica.GPoint
import grafica.GPointsArray
import processing.core.PApplet
import processing.core.PConstants
import processing.core.PGraphics

class Sketch : PApplet() {
    companion object {
        @JvmStatic
        val HIGH_RES_FRAME_RATE = 30f

        @JvmStatic
        val WINDOW_WIDTH = 800
        @JvmStatic
        val WINDOW_HEIGHT = 600

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

    private val cancellationTest = AudioCancellationTest(this)

    // plots
    lateinit var backgroundPlot : GPlot
    lateinit var inputPlot : GPlot

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
        //colorMode(HSB, 360f, 100f, 100f)

        backgroundPlot = GPlot(this, 25f, 15f)
        inputPlot = GPlot(this, 25f, 15f + 250f)

        backgroundPlot.let {
            it.setTitleText("Background Noise")
            it.xAxis.setAxisLabelText("Time (t)")
            it.yAxis.setAxisLabelText("Amplitude (a)")
            it.setPointSize(0.0f)
            it.setLineColor(color(255.0f, 64.0f, 54.0f))
            it.yLim = arrayOf(-1f, 1f).toFloatArray()
            it.fixedYLim = true
        }

        inputPlot.let {
            it.setTitleText("Mic Input")
            it.xAxis.setAxisLabelText("Time (t)")
            it.yAxis.setAxisLabelText("Amplitude (a)")
            it.setPointSize(0.0f)
            it.setLineColor(color(0.0f, 31.0f, 63.0f))
            it.yLim = arrayOf(-1f, 1f).toFloatArray()
            it.fixedYLim = true
        }

        cancellationTest.setup()
    }

    override fun draw() {
        background(255f)
        cancellationTest.update()

        visualiseBuffer()
        drawFPS(g)
    }

    private fun visualiseBuffer()
    {
        backgroundPlot.points = GPointsArray(cancellationTest.backgroundBuffer.
                reversed().mapIndexed { i, v -> GPoint(i.toFloat(), v) }.toTypedArray())

        inputPlot.points = GPointsArray(cancellationTest.inputBuffer.
                reversed().mapIndexed { i, v -> GPoint(i.toFloat(), v) }.toTypedArray())

        backgroundPlot.defaultDraw()
        inputPlot.defaultDraw()
    }

    private fun drawFPS(pg: PGraphics) {
        // add and draw fps
        fpsOverTime += frameRate.toDouble()

        pg.textAlign(PApplet.LEFT, PApplet.BOTTOM)
        pg.fill(55)
        pg.textSize(12f)
        pg.text("FPS: ${frameRate.format(2)}\nFOT: ${fpsOverTime.average.format(2)}", 10f, height - 5f)
    }

    override fun keyPressed()
    {
        when(key)
        {
            ' ' -> {
                cancellationTest.recorder.let {
                    if(it.isRecording)
                    {
                        println("stop recording...")
                        it.endRecord()
                        it.save()
                    }
                    else
                    {
                        println("start recording...")
                        it.beginRecord()
                    }
                }
            }
        }
    }
}