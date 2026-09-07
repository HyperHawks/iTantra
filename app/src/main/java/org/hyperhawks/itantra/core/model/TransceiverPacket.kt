package org.hyperhawks.itantra.core.model

data class TransceiverPacket(
  val packetId: String,
  val sequenceNumber: Long,
  val senderCallsign: String,
  val channelId: String,
  val sourceLanguage: Language,
  val targetLanguage: Language,
  val transcribedText: String,
  val translatedText: String,
  val timestampMs: Long,
  val sttLatencyMs: Long,
  val rawAudioBytesEstimated: Int,
  val payloadBytes: Int,
  val linkType: MeshLinkType,
  val isEmergencySos: Boolean,
  val latitude: Double = 0.0,
  val longitude: Double = 0.0
) {
  val bandwidthSavingsPercent: Double
    get() {
      if (rawAudioBytesEstimated <= 0) return 98.0
      val ratio: Double = payloadBytes.toDouble() / rawAudioBytesEstimated.toDouble()
      val savings: Double = (1.0 - ratio) * 100.0
      if (savings > 99.5) return 99.5
      if (savings < 0.0) return 0.0
      return savings
    }
}
