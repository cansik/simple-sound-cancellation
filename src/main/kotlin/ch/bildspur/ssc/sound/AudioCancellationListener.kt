package ch.bildspur.ssc.sound

import ddf.minim.AudioListener

class AudioCancellationListener(private val buffer : CircularBuffer) : AudioListener {
    val result = CircularBuffer(buffer.buffer.size)

    override fun samples(p0: FloatArray?) {
        result.add(p0!!)
    }

    override fun samples(p0: FloatArray?, p1: FloatArray?) {
        println("processing stereo")
    }
}