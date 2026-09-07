package org.hyperhawks.itantra.core.model

enum class MeshLinkType(
  val label: String,
  val nominalBandwidthKbps: Double,
  val rangeMeters: Int,
  val protocolHeader: String
) {
  LORA_MESH(
    label = "LoRa (865-867 MHz)",
    nominalBandwidthKbps = 5.4,
    rangeMeters = 15000,
    protocolHeader = "LR-ITN"
  ),
  BLE_MESH(
    label = "BLE 5.0 Mesh",
    nominalBandwidthKbps = 125.0,
    rangeMeters = 100,
    protocolHeader = "BLE-ITN"
  ),
  WIFI_DIRECT(
    label = "WiFi Direct P2P",
    nominalBandwidthKbps = 1000.0,
    rangeMeters = 200,
    protocolHeader = "WFD-ITN"
  ),
  BSNL_2G(
    label = "BSNL 2G Fallback",
    nominalBandwidthKbps = 9.6,
    rangeMeters = 35000,
    protocolHeader = "2G-ITN"
  ),
  ISRO_SATCOM(
    label = "ISRO SATCOM Bridge",
    nominalBandwidthKbps = 2.4,
    rangeMeters = 800000,
    protocolHeader = "SAT-ITN"
  );
}
