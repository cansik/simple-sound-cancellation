package ch.bildspur.ssc.sound

import ch.bildspur.ssc.util.incMod

class CircularBuffer(val size : Int) : Iterable<Float> {
    override fun iterator(): Iterator<Float> {
        return CircularBufferIterator(this)
    }

    private var index = -1

    var count = 0
    val buffer = FloatArray(size)

    fun add(value: Float)
    {
        index = index.incMod(buffer.size)
        buffer[index] = value

        count = Math.min(count + 1, buffer.size)
    }

    fun add(values: FloatArray)
    {
        values.forEach { add(it) }
    }

    operator fun plusAssign(value: Float) {
        add(value)
    }

    operator fun plusAssign(values: FloatArray) {
        add(values)
    }

    class CircularBufferIterator(private val buffer: CircularBuffer) : Iterator<Float>
    {
        private var index = 0

        override fun hasNext(): Boolean {
            return buffer.count > index
        }

        override fun next(): Float {
            val ci = buffer.index.incMod(buffer.count, -(index++))
            return buffer.buffer[ci]
        }
    }
}