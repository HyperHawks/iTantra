package org.hyperhawks.itantra.core.model

data class Channel(
  val id: String,
  val name: String,
  val agency: String,
  val frequencyMhz: Double,
  val activeMesh: MeshLinkType,
  val defaultLanguage: Language,
  val isEncrypted: Boolean,
  val isEmergencyOverride: Boolean,
  val activePeersCount: Int
) {
  companion object {
    val DEFAULT_CHANNELS: List<Channel> = listOf(
      Channel(
        id = "CH-NDRF-01",
        name = "NDRF Field Ops Alpha",
        agency = "National Disaster Response Force",
        frequencyMhz = 865.25,
        activeMesh = MeshLinkType.LORA_MESH,
        defaultLanguage = Language.HINDI,
        isEncrypted = true,
        isEmergencyOverride = false,
        activePeersCount = 14
      ),
      Channel(
        id = "CH-AAPDA-02",
        name = "Aapdamitra First Responders",
        agency = "NDMA Aapdamitra Volunteer Network",
        frequencyMhz = 866.50,
        activeMesh = MeshLinkType.BLE_MESH,
        defaultLanguage = Language.GUJARATI,
        isEncrypted = false,
        isEmergencyOverride = false,
        activePeersCount = 8
      ),
      Channel(
        id = "CH-BORDER-03",
        name = "Border Patrol Tactical Relay",
        agency = "Special Operations & Border Security",
        frequencyMhz = 867.10,
        activeMesh = MeshLinkType.BSNL_2G,
        defaultLanguage = Language.BENGALI,
        isEncrypted = true,
        isEmergencyOverride = false,
        activePeersCount = 6
      ),
      Channel(
        id = "CH-HEALTH-04",
        name = "Rural Health & ASHA Grid",
        agency = "Ministry of Health & Family Welfare",
        frequencyMhz = 865.80,
        activeMesh = MeshLinkType.WIFI_DIRECT,
        defaultLanguage = Language.TELUGU,
        isEncrypted = false,
        isEmergencyOverride = false,
        activePeersCount = 12
      ),
      Channel(
        id = "CH-SOS-112",
        name = "112 National Emergency Bridge",
        agency = "Emergency Response Support System (ERSS)",
        frequencyMhz = 868.00,
        activeMesh = MeshLinkType.ISRO_SATCOM,
        defaultLanguage = Language.ENGLISH,
        isEncrypted = false,
        isEmergencyOverride = true,
        activePeersCount = 42
      )
    )
  }
}
