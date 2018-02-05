package ch.bildspur.ssc.sound

data class CorrelationResult(val delay : Int, val confidence : Float)

fun FloatArray.crossCorrelate(x2: FloatArray): CorrelationResult {
    // define the size of the resulting correlation field
    val corrSize = 2 * this.size
    // create correlation vector
    val out = FloatArray(corrSize)
    // shift variable
    var shift = this.size
    var value: Float
    var maxIndex = 0
    var maxVal = 0f

    // we have push the signal from the left to the right
    for (i in 0 until corrSize) {
        value = 0f
        // multiply sample by sample and sum up
        for (k in this.indices) {
            // x2 has reached his end - abort
            if (k + shift > x2.size - 1) {
                break
            }

            // x2 has not started yet - continue
            if (k + shift < 0) {
                continue
            }

            // multiply sample with sample and sum up
            value += this[k] * x2[k + shift]
        }
        // save the sample
        out[i] = value
        shift--
        // save highest correlation index
        if (out[i] > maxVal) {
            maxVal = out[i]
            maxIndex = i
        }
    }

    // set the delay and confidence
    return CorrelationResult(maxIndex - this.size, maxVal)
}

fun FloatArray.linearWeightedAverage(): Double {
    // gauss
    val n = (this.size + 1).toDouble()
    val divider = (Math.pow(n, 2.0) + n) / 2.0

    // average
    var sum = 0.0
    for ((index, value) in this.withIndex())
        sum += ((index + 1).toDouble() / divider) * value

    return sum
}