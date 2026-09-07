package org.hyperhawks.itantra.core.network

import android.content.Context
import java.util.UUID
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.hyperhawks.itantra.core.model.AcousticMetrics
import org.hyperhawks.itantra.core.model.Channel
import org.hyperhawks.itantra.core.model.Channel.Companion.DEFAULT_CHANNELS
import org.hyperhawks.itantra.core.model.Language
import org.hyperhawks.itantra.core.model.Language.ENGLISH
import org.hyperhawks.itantra.core.model.Language.HINDI
import org.hyperhawks.itantra.core.model.TransceiverPacket
import org.hyperhawks.itantra.core.neural.AudioRecorderController
import org.hyperhawks.itantra.core.neural.AudioWaveformVisualizerState
import org.hyperhawks.itantra.core.neural.IndicNlpTransliteration
import org.hyperhawks.itantra.core.neural.IndicSpeechToTextEngine
import org.hyperhawks.itantra.core.neural.IndicSpeechToTextEngine.SttResult
import org.hyperhawks.itantra.core.neural.IndicTextToSpeechEngine

class TransceiverManager(private val context: Context) {

  private val coroutineScope: CoroutineScope = CoroutineScope(context = Default)

  val sttEngine: IndicSpeechToTextEngine = IndicSpeechToTextEngine()
  val ttsEngine: IndicTextToSpeechEngine = IndicTextToSpeechEngine(context = context)
  val meshRelay: MeshRelayManager = MeshRelayManager()

  private val _activeChannel: MutableStateFlow<Channel> =
    MutableStateFlow(value = DEFAULT_CHANNELS[0])
  val activeChannel: StateFlow<Channel> = _activeChannel.asStateFlow()

  private val _userCallsign: MutableStateFlow<String> =
    MutableStateFlow(value = "EAGLE-ALPHA-01")
  val userCallsign: StateFlow<String> = _userCallsign.asStateFlow()

  private val _sourceLanguage: MutableStateFlow<Language> = MutableStateFlow(value = HINDI)
  val sourceLanguage: StateFlow<Language> = _sourceLanguage.asStateFlow()

  private val _targetLanguage: MutableStateFlow<Language> = MutableStateFlow(value = ENGLISH)
  val targetLanguage: StateFlow<Language> = _targetLanguage.asStateFlow()

  private val _isHandsFreeVadMode: MutableStateFlow<Boolean> = MutableStateFlow(value = true)
  val isHandsFreeVadMode: StateFlow<Boolean> = _isHandsFreeVadMode.asStateFlow()

  private val _packetsHistory: MutableStateFlow<List<TransceiverPacket>> =
    MutableStateFlow(value = createInitialSeedPackets())
  val packetsHistory: StateFlow<List<TransceiverPacket>> = _packetsHistory.asStateFlow()

  private val _latestMetrics: MutableStateFlow<AcousticMetrics> =
    MutableStateFlow(value = AcousticMetrics.DEFAULT_BENCHMARK)
  val latestMetrics: StateFlow<AcousticMetrics> = _latestMetrics.asStateFlow()

  private val _isEmergencySosActive: MutableStateFlow<Boolean> = MutableStateFlow(value = false)
  val isEmergencySosActive: StateFlow<Boolean> = _isEmergencySosActive.asStateFlow()

  private var sequenceCounter: Long = 100L

  val audioRecorder: AudioRecorderController = AudioRecorderController(
    sampleRate = 16000,
    onVoicePauseDetected = { pcmAudio: ShortArray ->
      processAndTransmitAudio(audio = pcmAudio, isSos = _isEmergencySosActive.value)
    }
  )

  init {
    coroutineScope.launch {
      meshRelay.incomingPackets.collect { packet: TransceiverPacket ->
        handleIncomingPacket(packet = packet)
      }
    }
  }

  fun setChannel(channel: Channel) {
    _activeChannel.value = channel
    meshRelay.setMeshLink(linkType = channel.activeMesh)
  }

  fun setSourceLanguage(language: Language) {
    _sourceLanguage.value = language
  }

  fun setTargetLanguage(language: Language) {
    _targetLanguage.value = language
  }

  fun toggleHandsFreeVad(enabled: Boolean) {
    _isHandsFreeVadMode.value = enabled
    if (!enabled && audioRecorder.isRecording.value) {
      audioRecorder.stopRecordingAndFlush()
    }
  }

  fun startPushToTalk() {
    audioRecorder.startRecording(handsFreeVadMode = false)
  }

  fun releasePushToTalk() {
    val audio: ShortArray? = audioRecorder.stopRecordingAndFlush()
    if (audio != null && audio.isNotEmpty()) {
      processAndTransmitAudio(audio = audio, isSos = _isEmergencySosActive.value)
    }
  }

  fun triggerEmergencySos(activate: Boolean) {
    _isEmergencySosActive.value = activate
    if (!activate) {
      ttsEngine.stop()
      return
    }

    val sosPacket: TransceiverPacket = TransceiverPacket(
      packetId = UUID.randomUUID().toString(),
      sequenceNumber = ++sequenceCounter,
      senderCallsign = _userCallsign.value,
      channelId = "CH-SOS-112",
      sourceLanguage = _sourceLanguage.value,
      targetLanguage = _targetLanguage.value,
      transcribedText = "MAYDAY MAYDAY! आपातकालीन एसओएस संकेत - सेक्टर 9 में तत्काल बचाव दल भेजें",
      translatedText = "MAYDAY MAYDAY! Emergency SOS signal - dispatch rescue team immediately to Sector 9",
      timestampMs = System.currentTimeMillis(),
      sttLatencyMs = 184L,
      rawAudioBytesEstimated = 48000,
      payloadBytes = 42,
      linkType = meshRelay.activeLinkType.value,
      isEmergencySos = true,
      latitude = 28.6139,
      longitude = 77.2090
    )

    meshRelay.broadcastPacket(packet = sosPacket)
    val currentList: MutableList<TransceiverPacket> = _packetsHistory.value.toMutableList()
    currentList.add(element = sosPacket)
    _packetsHistory.value = currentList

    ttsEngine.speak(
      text = "Emergency SOS Alert broadcasted across all mesh nodes. Transceiver active.",
      language = ENGLISH,
      isEmergencyAlert = true
    )
  }

  fun sendTextMessageManually(text: String) {
    if (text.isBlank()) return
    val srcLang: Language = _sourceLanguage.value
    val tgtLang: Language = _targetLanguage.value

    val translated: String = IndicNlpTransliteration.rescoreCodeMixedText(
      inputText = text,
      targetLanguage = tgtLang
    )

    val payloadBytes: Int = text.toByteArray(charset = Charsets.UTF_8).size + 16
    val packet: TransceiverPacket = TransceiverPacket(
      packetId = UUID.randomUUID().toString(),
      sequenceNumber = ++sequenceCounter,
      senderCallsign = _userCallsign.value,
      channelId = _activeChannel.value.id,
      sourceLanguage = srcLang,
      targetLanguage = tgtLang,
      transcribedText = text,
      translatedText = translated,
      timestampMs = System.currentTimeMillis(),
      sttLatencyMs = 195L,
      rawAudioBytesEstimated = 32000,
      payloadBytes = payloadBytes,
      linkType = meshRelay.activeLinkType.value,
      isEmergencySos = false
    )

    meshRelay.broadcastPacket(packet = packet)
    val currentList: MutableList<TransceiverPacket> = _packetsHistory.value.toMutableList()
    currentList.add(element = packet)
    _packetsHistory.value = currentList
  }

  private fun processAndTransmitAudio(
    audio: ShortArray,
    isSos: Boolean
  ) {
    val srcLang: Language = _sourceLanguage.value
    val tgtLang: Language = _targetLanguage.value

    val sttResult: SttResult = sttEngine.transcribePcmAudio(
      audioSamples = audio,
      language = srcLang
    )

    _latestMetrics.value = sttResult.metrics

    val translated: String = IndicNlpTransliteration.rescoreCodeMixedText(
      inputText = sttResult.transcribedText,
      targetLanguage = tgtLang
    )

    val packet: TransceiverPacket = TransceiverPacket(
      packetId = UUID.randomUUID().toString(),
      sequenceNumber = ++sequenceCounter,
      senderCallsign = _userCallsign.value,
      channelId = _activeChannel.value.id,
      sourceLanguage = srcLang,
      targetLanguage = tgtLang,
      transcribedText = sttResult.transcribedText,
      translatedText = translated,
      timestampMs = System.currentTimeMillis(),
      sttLatencyMs = sttResult.latencyMs,
      rawAudioBytesEstimated = sttResult.rawAudioBytes,
      payloadBytes = sttResult.payloadBytes,
      linkType = meshRelay.activeLinkType.value,
      isEmergencySos = isSos
    )

    meshRelay.broadcastPacket(packet = packet)

    val currentList: MutableList<TransceiverPacket> = _packetsHistory.value.toMutableList()
    currentList.add(element = packet)
    _packetsHistory.value = currentList
  }

  private fun handleIncomingPacket(packet: TransceiverPacket) {
    val currentList: MutableList<TransceiverPacket> = _packetsHistory.value.toMutableList()
    currentList.add(element = packet)
    _packetsHistory.value = currentList

    val speechText: String = if (packet.targetLanguage == _sourceLanguage.value) {
      packet.translatedText
    } else {
      packet.transcribedText
    }

    coroutineScope.launch(context = Main) {
      ttsEngine.speak(
        text = speechText,
        language = _sourceLanguage.value,
        isEmergencyAlert = packet.isEmergencySos
      )
    }
  }

  private fun createInitialSeedPackets(): List<TransceiverPacket> {
    val now: Long = System.currentTimeMillis()
    return listOf(
      TransceiverPacket(
        packetId = "seed-01",
        sequenceNumber = 98L,
        senderCallsign = "NDRF-BASE-CMD",
        channelId = "CH-NDRF-01",
        sourceLanguage = HINDI,
        targetLanguage = ENGLISH,
        transcribedText = "सभी यूनिट्स ध्यान दें: ब्यास नदी का जलस्तर खतरे के निशान के करीब है",
        translatedText = "All units notice: Beas river water level is near danger mark",
        timestampMs = now - 180000L,
        sttLatencyMs = 212L,
        rawAudioBytesEstimated = 38400,
        payloadBytes = 38,
        linkType = meshRelay.activeLinkType.value,
        isEmergencySos = false
      ),
      TransceiverPacket(
        packetId = "seed-02",
        sequenceNumber = 99L,
        senderCallsign = "AAPDA-UNIT-04",
        channelId = "CH-NDRF-01",
        sourceLanguage = Language.GUJARATI,
        targetLanguage = HINDI,
        transcribedText = "અમે 50 ગ્રામજનોને સુરક્ષિત કેમ્પમાં પહોંચાડી દીધા છે",
        translatedText = "हमने 50 ग्रामीणों को सुरक्षित शिविर में पहुंचा दिया है",
        timestampMs = now - 95000L,
        sttLatencyMs = 198L,
        rawAudioBytesEstimated = 42000,
        payloadBytes = 34,
        linkType = meshRelay.activeLinkType.value,
        isEmergencySos = false
      )
    )
  }
}
