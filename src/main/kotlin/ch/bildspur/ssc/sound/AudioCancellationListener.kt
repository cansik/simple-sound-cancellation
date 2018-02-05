package ch.bildspur.ssc.sound

import ddf.minim.AudioListener

class AudioCancellationListener(private val buffer : CircularBuffer) : AudioListener {
    val result = CircularBuffer(buffer.buffer.size)

    override fun samples(samples: FloatArray) {
        val data = buffer.reversed().toFloatArray()
        val correlation = samples.crossCorrelate(data)

        // filter
        for(i in 0 until samples.size) {
            val index = samples.size - i - 1
            //samples[index] = samples[index] - data[Math.floorMod(index, data.size)]
        }

        result.add(samples)
    }

    override fun samples(leftSamples: FloatArray, rightSamples: FloatArray) {
        println("processing stereo")
    }
}