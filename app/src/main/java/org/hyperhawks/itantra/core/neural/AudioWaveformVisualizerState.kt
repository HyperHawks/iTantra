package org.hyperhawks.itantra.core.neural

data class AudioWaveformVisualizerState(
  val amplitudes: List<Float> = List(size = 32) { 0.05f },
  val currentRmsDb: Float = -60.0f,
  val isVoiceDetected: Boolean = false,
  val isTransmitting: Boolean = false
) {
  companion object {
    val IDLE: AudioWaveformVisualizerState = AudioWaveformVisualizerState()
  }
}
