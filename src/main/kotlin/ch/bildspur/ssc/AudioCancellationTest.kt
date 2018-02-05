package ch.bildspur.ssc

import ch.bildspur.ssc.sound.AudioCancellationListener
import ch.bildspur.ssc.sound.CircularBuffer
import ddf.minim.AudioPlayer
import ddf.minim.AudioRecorder
import ddf.minim.AudioSource
import ddf.minim.Minim
import processing.core.PApplet

class AudioCancellationTest(applet : PApplet) {
    companion object {
        @JvmStatic
        val DEFAULT_BUFFER_SIZE = 2048

        @JvmStatic
        val DEFAULT_CIRCULAR_BUFFER_SIZE = DEFAULT_BUFFER_SIZE * 50
    }

    val minim = Minim(applet)

    lateinit var backgroundAudio : AudioPlayer
    val backgroundBuffer = CircularBuffer(DEFAULT_CIRCULAR_BUFFER_SIZE)

    lateinit var input : AudioSource
    val inputBuffer = CircularBuffer(DEFAULT_CIRCULAR_BUFFER_SIZE)

    lateinit var recorder : AudioRecorder

    val cancellationListener = AudioCancellationListener(backgroundBuffer)

    fun setup()
    {
        //backgroundAudio = minim.loadFile("SAVERNE_Hendrix.mp3", DEFAULT_BUFFER_SIZE)
        backgroundAudio = minim.loadFile("clip.wav", DEFAULT_BUFFER_SIZE)
        //backgroundAudio.skip(1000 * 10)
        backgroundAudio.play()

        input = minim.getLineIn(Minim.MONO, DEFAULT_BUFFER_SIZE)
        input.addListener(cancellationListener)

        recorder = minim.createRecorder(input, "data/recorded.wav")
    }

    fun update()
    {
        backgroundBuffer += backgroundAudio.mix.toArray()
        inputBuffer += input.mix.toArray()
    }
}