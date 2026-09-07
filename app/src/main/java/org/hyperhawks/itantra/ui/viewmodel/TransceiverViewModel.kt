package org.hyperhawks.itantra.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.StateFlow
import org.hyperhawks.itantra.core.model.AcousticMetrics
import org.hyperhawks.itantra.core.model.Channel
import org.hyperhawks.itantra.core.model.Language
import org.hyperhawks.itantra.core.model.MeshLinkType
import org.hyperhawks.itantra.core.model.TransceiverPacket
import org.hyperhawks.itantra.core.neural.AudioWaveformVisualizerState
import org.hyperhawks.itantra.core.network.TransceiverManager

class TransceiverViewModel(application: Application) : AndroidViewModel(application) {

  private val transceiverManager: TransceiverManager = TransceiverManager(
    context = application.applicationContext
  )

  val activeChannel: StateFlow<Channel> = transceiverManager.activeChannel
  val userCallsign: StateFlow<String> = transceiverManager.userCallsign
  val sourceLanguage: StateFlow<Language> = transceiverManager.sourceLanguage
  val targetLanguage: StateFlow<Language> = transceiverManager.targetLanguage
  val isHandsFreeVadMode: StateFlow<Boolean> = transceiverManager.isHandsFreeVadMode
  val packetsHistory: StateFlow<List<TransceiverPacket>> = transceiverManager.packetsHistory
  val latestMetrics: StateFlow<AcousticMetrics> = transceiverManager.latestMetrics
  val isEmergencySosActive: StateFlow<Boolean> = transceiverManager.isEmergencySosActive

  val visualizerState: StateFlow<AudioWaveformVisualizerState> =
    transceiverManager.audioRecorder.visualizerState
  val isRecording: StateFlow<Boolean> = transceiverManager.audioRecorder.isRecording
  val isSpeaking: StateFlow<Boolean> = transceiverManager.ttsEngine.isSpeaking

  val activeLinkType: StateFlow<MeshLinkType> = transceiverManager.meshRelay.activeLinkType
  val connectedPeersCount: StateFlow<Int> = transceiverManager.meshRelay.connectedPeersCount
  val signalStrengthRssi: StateFlow<Int> = transceiverManager.meshRelay.signalStrengthRssi

  fun setChannel(channel: Channel) {
    transceiverManager.setChannel(channel = channel)
  }

  fun setSourceLanguage(language: Language) {
    transceiverManager.setSourceLanguage(language = language)
  }

  fun setTargetLanguage(language: Language) {
    transceiverManager.setTargetLanguage(language = language)
  }

  fun toggleHandsFreeVad(enabled: Boolean) {
    transceiverManager.toggleHandsFreeVad(enabled = enabled)
  }

  fun startPushToTalk() {
    transceiverManager.startPushToTalk()
  }

  fun releasePushToTalk() {
    transceiverManager.releasePushToTalk()
  }

  fun triggerEmergencySos(activate: Boolean) {
    transceiverManager.triggerEmergencySos(activate = activate)
  }

  fun sendTextMessage(text: String) {
    transceiverManager.sendTextMessageManually(text = text)
  }

  fun replaySpeech(packet: TransceiverPacket) {
    val speechText: String = if (packet.targetLanguage == sourceLanguage.value) {
      packet.translatedText
    } else {
      packet.transcribedText
    }
    transceiverManager.ttsEngine.speak(
      text = speechText,
      language = sourceLanguage.value,
      isEmergencyAlert = packet.isEmergencySos
    )
  }

  override fun onCleared() {
    super.onCleared()
    transceiverManager.audioRecorder.stopRecordingAndFlush()
    transceiverManager.ttsEngine.shutdown()
  }
}
