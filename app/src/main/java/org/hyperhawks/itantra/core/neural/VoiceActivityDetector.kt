package org.hyperhawks.itantra.core.neural

import kotlin.math.log10
import kotlin.math.sqrt

class VoiceActivityDetector(
  private val energyThresholdDb: Float = -38.0f,
  private val pauseDurationThresholdMs: Long = 400L
) {

  private var isSpeechActive: Boolean = false
  private var silenceStartTimeMs: Long = 0L

  fun processFrame(
    pcmBuffer: ShortArray,
    readSize: Int,
    currentTimeMs: Long
  ): VadResult {
    if (readSize <= 0) return VadResult(isVoicePresent = false, isVoicePauseTriggered = false, rmsDb = -80.0f)

    var sumSquares: Double = 0.0
    var i: Int = 0
    while (i < readSize) {
      val sample: Short = pcmBuffer[i]
      sumSquares += (sample * sample).toDouble()
      i++
    }

    val meanSquare: Double = sumSquares / readSize.toDouble()
    val rms: Double = sqrt(x = meanSquare)
    val rmsDb: Float = if (rms > 0.0) (20.0 * log10(x = rms / 32768.0)).toFloat() else -80.0f
    val voicePresent: Boolean = rmsDb >= energyThresholdDb

    if (voicePresent) {
      isSpeechActive = true
      silenceStartTimeMs = 0L
      return VadResult(isVoicePresent = true, isVoicePauseTriggered = false, rmsDb = rmsDb)
    }

    if (!isSpeechActive) return VadResult(isVoicePresent = false, isVoicePauseTriggered = false, rmsDb = rmsDb)

    if (silenceStartTimeMs == 0L) {
      silenceStartTimeMs = currentTimeMs
      return VadResult(isVoicePresent = false, isVoicePauseTriggered = false, rmsDb = rmsDb)
    }

    val silenceElapsed: Long = currentTimeMs - silenceStartTimeMs
    if (silenceElapsed >= pauseDurationThresholdMs) {
      isSpeechActive = false
      silenceStartTimeMs = 0L
      return VadResult(isVoicePresent = false, isVoicePauseTriggered = true, rmsDb = rmsDb)
    }

    return VadResult(isVoicePresent = false, isVoicePauseTriggered = false, rmsDb = rmsDb)
  }

  fun reset() {
    isSpeechActive = false
    silenceStartTimeMs = 0L
  }

  data class VadResult(
    val isVoicePresent: Boolean,
    val isVoicePauseTriggered: Boolean,
    val rmsDb: Float
  )
}
