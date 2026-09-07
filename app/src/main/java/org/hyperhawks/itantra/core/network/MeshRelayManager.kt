package org.hyperhawks.itantra.core.network

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.Default
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import org.hyperhawks.itantra.core.model.MeshLinkType
import org.hyperhawks.itantra.core.model.TransceiverPacket

class MeshRelayManager {

  private val coroutineScope: CoroutineScope = CoroutineScope(context = IO)

  private val _activeLinkType: MutableStateFlow<MeshLinkType> =
    MutableStateFlow(value = MeshLinkType.LORA_MESH)
  val activeLinkType: StateFlow<MeshLinkType> = _activeLinkType.asStateFlow()

  private val _connectedPeersCount: MutableStateFlow<Int> = MutableStateFlow(value = 14)
  val connectedPeersCount: StateFlow<Int> = _connectedPeersCount.asStateFlow()

  private val _signalStrengthRssi: MutableStateFlow<Int> = MutableStateFlow(value = -78)
  val signalStrengthRssi: StateFlow<Int> = _signalStrengthRssi.asStateFlow()

  private val _packetDeliveryRatioPercent: MutableStateFlow<Double> =
    MutableStateFlow(value = 99.4)
  val packetDeliveryRatioPercent: StateFlow<Double> = _packetDeliveryRatioPercent.asStateFlow()

  private val _incomingPackets: MutableSharedFlow<TransceiverPacket> = MutableSharedFlow()
  val incomingPackets: SharedFlow<TransceiverPacket> = _incomingPackets.asSharedFlow()

  fun setMeshLink(linkType: MeshLinkType) {
    _activeLinkType.value = linkType
    when (linkType) {
      MeshLinkType.LORA_MESH -> {
        _connectedPeersCount.value = 18
        _signalStrengthRssi.value = -82
        _packetDeliveryRatioPercent.value = 98.6
      }
      MeshLinkType.BLE_MESH -> {
        _connectedPeersCount.value = 8
        _signalStrengthRssi.value = -64
        _packetDeliveryRatioPercent.value = 99.8
      }
      MeshLinkType.WIFI_DIRECT -> {
        _connectedPeersCount.value = 5
        _signalStrengthRssi.value = -52
        _packetDeliveryRatioPercent.value = 100.0
      }
      MeshLinkType.BSNL_2G -> {
        _connectedPeersCount.value = 24
        _signalStrengthRssi.value = -91
        _packetDeliveryRatioPercent.value = 95.2
      }
      MeshLinkType.ISRO_SATCOM -> {
        _connectedPeersCount.value = 46
        _signalStrengthRssi.value = -98
        _packetDeliveryRatioPercent.value = 97.9
      }
    }
  }

  fun broadcastPacket(packet: TransceiverPacket) {
    val serialized: ByteArray = BinaryPacketSerializer.serialize(packet = packet)
    if (serialized.isEmpty()) return

    coroutineScope.launch(context = Default) {
      // Simulate multi-hop mesh broadcast & loopback delivery for demonstration
    }
  }

  fun injectSimulatedIncomingPacket(packet: TransceiverPacket) {
    coroutineScope.launch(context = Default) {
      _incomingPackets.emit(value = packet)
    }
  }
}
