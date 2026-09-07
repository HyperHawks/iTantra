package org.hyperhawks.itantra.core.network

import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.DataInputStream
import java.io.DataOutputStream
import org.hyperhawks.itantra.core.model.Language
import org.hyperhawks.itantra.core.model.MeshLinkType
import org.hyperhawks.itantra.core.model.TransceiverPacket

object BinaryPacketSerializer {

  private const val PROTOCOL_VERSION: Byte = 1

  fun serialize(packet: TransceiverPacket): ByteArray {
    val byteStream: ByteArrayOutputStream = ByteArrayOutputStream()
    val out: DataOutputStream = DataOutputStream(byteStream)

    out.writeByte(PROTOCOL_VERSION.toInt())
    out.writeUTF(packet.packetId)
    out.writeLong(packet.sequenceNumber)
    out.writeUTF(packet.senderCallsign)
    out.writeUTF(packet.channelId)
    out.writeUTF(packet.sourceLanguage.code)
    out.writeUTF(packet.targetLanguage.code)
    out.writeUTF(packet.transcribedText)
    out.writeUTF(packet.translatedText)
    out.writeLong(packet.timestampMs)
    out.writeLong(packet.sttLatencyMs)
    out.writeInt(packet.rawAudioBytesEstimated)
    out.writeUTF(packet.linkType.name)
    out.writeBoolean(packet.isEmergencySos)
    out.writeDouble(packet.latitude)
    out.writeDouble(packet.longitude)

    out.flush()
    return byteStream.toByteArray()
  }

  fun deserialize(bytes: ByteArray): TransceiverPacket? {
    if (bytes.isEmpty()) return null

    try {
      val byteStream: ByteArrayInputStream = ByteArrayInputStream(bytes)
      val inStream: DataInputStream = DataInputStream(byteStream)

      val version: Byte = inStream.readByte()
      if (version != PROTOCOL_VERSION) return null

      val packetId: String = inStream.readUTF()
      val seqNum: Long = inStream.readLong()
      val callsign: String = inStream.readUTF()
      val channelId: String = inStream.readUTF()
      val srcCode: String = inStream.readUTF()
      val tgtCode: String = inStream.readUTF()
      val transcribed: String = inStream.readUTF()
      val translated: String = inStream.readUTF()
      val timestamp: Long = inStream.readLong()
      val latency: Long = inStream.readLong()
      val rawAudioBytes: Int = inStream.readInt()
      val linkTypeName: String = inStream.readUTF()
      val isSos: Boolean = inStream.readBoolean()
      val lat: Double = inStream.readDouble()
      val lon: Double = inStream.readDouble()

      val sourceLang: Language = Language.fromCode(code = srcCode)
      val targetLang: Language = Language.fromCode(code = tgtCode)
      val linkType: MeshLinkType = try {
        MeshLinkType.valueOf(value = linkTypeName)
      } catch (_: Exception) {
        MeshLinkType.LORA_MESH
      }

      return TransceiverPacket(
        packetId = packetId,
        sequenceNumber = seqNum,
        senderCallsign = callsign,
        channelId = channelId,
        sourceLanguage = sourceLang,
        targetLanguage = targetLang,
        transcribedText = transcribed,
        translatedText = translated,
        timestampMs = timestamp,
        sttLatencyMs = latency,
        rawAudioBytesEstimated = rawAudioBytes,
        payloadBytes = bytes.size,
        linkType = linkType,
        isEmergencySos = isSos,
        latitude = lat,
        longitude = lon
      )
    } catch (_: Exception) {
      return null
    }
  }
}
