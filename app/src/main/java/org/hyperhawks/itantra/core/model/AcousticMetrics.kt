package org.hyperhawks.itantra.core.model

data class AcousticMetrics(
  val sttLatencyMs: Long,
  val latencyDeltaGatePassed: Boolean,
  val targetMaxLatencyMs: Long = 300L,
  val wordErrorRatePercent: Double,
  val cpuUsagePercent: Double,
  val targetMaxCpuPercent: Double = 12.0,
  val modelMemoryMb: Double,
  val targetMaxMemoryMb: Double = 150.0,
  val rfDataSavedPercent: Double,
  val isAirGappedCertInAligned: Boolean = true,
  val isZeroCloudDependency: Boolean = true
) {
  companion object {
    val DEFAULT_BENCHMARK: AcousticMetrics = AcousticMetrics(
      sttLatencyMs = 214L,
      latencyDeltaGatePassed = true,
      targetMaxLatencyMs = 300L,
      wordErrorRatePercent = 8.4,
      cpuUsagePercent = 9.8,
      targetMaxCpuPercent = 12.0,
      modelMemoryMb = 118.5,
      targetMaxMemoryMb = 150.0,
      rfDataSavedPercent = 98.4,
      isAirGappedCertInAligned = true,
      isZeroCloudDependency = true
    )
  }
}
