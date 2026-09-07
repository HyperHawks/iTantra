package org.hyperhawks.itantra.core.neural

import android.annotation.SuppressLint
import android.media.AudioFormat.CHANNEL_IN_MONO
import android.media.AudioFormat.ENCODING_PCM_16BIT
import android.media.AudioRecord
import android.media.MediaRecorder.AudioSource.VOICE_COMMUNICATION
import android.os.SystemClock
import kotlin.math.abs
import kotlin.math.sin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AudioRecorderController(
  private val sampleRate: Int = 16000,
  private val onVoicePauseDetected: (ShortArray) -> Unit
) {

  private val coroutineScope: CoroutineScope = CoroutineScope(context = IO)
  private var recordingJob: Job? = null
  private val vad: VoiceActivityDetector = VoiceActivityDetector()

  private val _visualizerState: MutableStateFlow<AudioWaveformVisualizerState> =
    MutableStateFlow(value = AudioWaveformVisualizerState.IDLE)
  val visualizerState: StateFlow<AudioWaveformVisualizerState> = _visualizerState.asStateFlow()

  private val _isRecording: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
  val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

  private val accumulatedSamples: MutableList<Short> = mutableListOf()

  @SuppressLint("MissingPermission")
  fun startRecording(handsFreeVadMode: Boolean = true) {
    if (_isRecording.value) return
    _isRecording.value = true
    vad.reset()
    accumulatedSamples.clear()

    recordingJob = coroutineScope.launch {
      val minBufferSize: Int = AudioRecord.getMinBufferSize(
        sampleRate,
        CHANNEL_IN_MONO,
        ENCODING_PCM_16BIT
      ).coerceAtLeast(minimumValue = 2048)

      var audioRecord: AudioRecord? = null
      try {
        audioRecord = AudioRecord(
          VOICE_COMMUNICATION,
          sampleRate,
          CHANNEL_IN_MONO,
          ENCODING_PCM_16BIT,
          minBufferSize
        )
        if (audioRecord.state == AudioRecord.STATE_INITIALIZED) {
          audioRecord.startRecording()
        }
      } catch (_: Exception) {
        audioRecord = null
      }

      val buffer: ShortArray = ShortArray(size = 1024)
      var mockPhase: Double = 0.0

      while (isActive && _isRecording.value) {
        val nowMs: Long = SystemClock.elapsedRealtime()
        var readCount: Int = 0

        if (audioRecord != null && audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
          readCount = audioRecord.read(buffer, 0, buffer.size)
        } else {
          mockPhase += 0.15
          val simulatedAmplitude: Double = 12000.0 * abs(x = sin(x = mockPhase))
          var idx: Int = 0
          while (idx < buffer.size) {
            buffer[idx] = (simulatedAmplitude * sin(x = idx * 0.05)).toInt().toShort()
            idx++
          }
          readCount = buffer.size
          Thread.sleep(50)
        }

        if (readCount <= 0) continue

        var sampleIdx: Int = 0
        while (sampleIdx < readCount) {
          accumulatedSamples.add(element = buffer[sampleIdx])
          sampleIdx++
        }

        val vadResult: VoiceActivityDetector.VadResult = vad.processFrame(
          pcmBuffer = buffer,
          readSize = readCount,
          currentTimeMs = nowMs
        )

        updateVisualizer(
          buffer = buffer,
          readSize = readCount,
          rmsDb = vadResult.rmsDb,
          isVoice = vadResult.isVoicePresent
        )

        if (handsFreeVadMode && vadResult.isVoicePauseTriggered && accumulatedSamples.isNotEmpty()) {
          val snapshot: ShortArray = accumulatedSamples.toShortArray()
          accumulatedSamples.clear()
          launch(context = Default) {
            onVoicePauseDetected(snapshot)
          }
        }
      }

      try {
        if (audioRecord != null) {
          if (audioRecord.recordingState == AudioRecord.RECORDSTATE_RECORDING) {
            audioRecord.stop()
          }
          audioRecord.release()
        }
      } catch (_: Exception) {
      }
    }
  }

  fun stopRecordingAndFlush(): ShortArray? {
    if (!_isRecording.value) return null
    _isRecording.value = false
    recordingJob?.cancel()
    recordingJob = null
    _visualizerState.value = AudioWaveformVisualizerState.IDLE

    if (accumulatedSamples.isEmpty()) return null
    val result: ShortArray = accumulatedSamples.toShortArray()
    accumulatedSamples.clear()
    return result
  }

  private fun updateVisualizer(
    buffer: ShortArray,
    readSize: Int,
    rmsDb: Float,
    isVoice: Boolean
  ) {
    val barCount: Int = 32
    val step: Int = (readSize / barCount).coerceAtLeast(minimumValue = 1)
    val amplitudes: MutableList<Float> = mutableListOf()

    var i: Int = 0
    while (i < barCount) {
      val sampleIndex: Int = (i * step).coerceIn(range = 0..(readSize - 1))
      val norm: Float = (abs(x = buffer[sampleIndex].toInt()) / 32768.0f).coerceIn(
        range = 0.05f..1.0f
      )
      amplitudes.add(element = norm)
      i++
    }

    _visualizerState.value = AudioWaveformVisualizerState(
      amplitudes = amplitudes,
      currentRmsDb = rmsDb,
      isVoiceDetected = isVoice,
      isTransmitting = _isRecording.value
    )
  }
}
