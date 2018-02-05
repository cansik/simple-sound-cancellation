package ch.bildspur.ssc

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
        val WINDOW_WIDTH = 520
        @JvmStatic
        val WINDOW_HEIGHT = 950

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
    lateinit var cancelPlot : GPlot

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

        backgroundPlot = GPlot(this, 25f, 15f)
        inputPlot = GPlot(this, 25f, 15f + 300f)
        cancelPlot = GPlot(this, 25f, 15f + 600f)

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

        cancelPlot.let {
            it.setTitleText("Canceled Plot")
            it.xAxis.setAxisLabelText("Time (t)")
            it.yAxis.setAxisLabelText("Amplitude (a)")
            it.setPointSize(0.0f)
            it.setLineColor(color(61.0f, 152.0f, 112.0f))
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
        val n = 300
        backgroundPlot.points = GPointsArray(cancellationTest.backgroundBuffer
                .filterIndexed { i, v -> i % n == 0}
                .reversed()
                .mapIndexed { i, v -> GPoint(i.toFloat(), v) }.toTypedArray())

        inputPlot.points = GPointsArray(cancellationTest.inputBuffer
                .filterIndexed { i, v -> i % n == 0}
                .reversed()
                .mapIndexed { i, v -> GPoint(i.toFloat(), v) }.toTypedArray())

        cancelPlot.points = GPointsArray(cancellationTest.cancellationListener.result
                .filterIndexed { i, v -> i % n == 0}
                .reversed()
                .mapIndexed { i, v -> GPoint(i.toFloat(), v) }.toTypedArray())

        backgroundPlot.defaultDraw()
        inputPlot.defaultDraw()
        cancelPlot.defaultDraw()
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