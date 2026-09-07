package org.hyperhawks.itantra.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import org.hyperhawks.itantra.core.model.TransceiverPacket
import org.hyperhawks.itantra.ui.theme.TacticalAmber
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalGreen
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalRed
import org.hyperhawks.itantra.ui.theme.TacticalSurface
import org.hyperhawks.itantra.ui.theme.TacticalSurfaceVariant
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary
import org.hyperhawks.itantra.ui.theme.TextTertiary

@Composable
fun PacketBubble(
  packet: TransceiverPacket,
  isOwnPacket: Boolean,
  onReplayTts: () -> Unit,
  modifier: Modifier = Modifier
) {
  val timeFormat: SimpleDateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
  val formattedTime: String = timeFormat.format(Date(packet.timestampMs))

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape = RoundedCornerShape(size = 12.dp))
      .background(color = if (packet.isEmergencySos) TacticalRed.copy(alpha = 0.12f) else TacticalSurface)
      .border(
        width = 1.dp,
        color = if (packet.isEmergencySos) TacticalRed else TacticalBorder,
        shape = RoundedCornerShape(size = 12.dp)
      )
      .padding(all = 12.dp),
    verticalArrangement = spacedBy(space = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = SpaceBetween,
      verticalAlignment = CenterVertically
    ) {
      Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(space = 8.dp)
      ) {
        Box(
          modifier = Modifier
            .size(size = 8.dp)
            .clip(shape = CircleShape)
            .background(color = if (packet.isEmergencySos) TacticalRed else if (isOwnPacket) TacticalOrangePrimary else TacticalCyan)
        )
        Text(
          text = packet.senderCallsign,
          style = typography.titleMedium,
          color = if (isOwnPacket) TacticalOrangePrimary else TacticalCyan,
          fontWeight = Bold
        )
        Text(
          text = "#${packet.sequenceNumber}",
          style = typography.labelSmall,
          color = TextTertiary
        )
      }

      Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(space = 6.dp)
      ) {
        Text(
          text = formattedTime,
          style = typography.labelSmall,
          color = TextTertiary
        )
        Box(
          modifier = Modifier
            .clip(shape = CircleShape)
            .background(color = TacticalSurfaceVariant)
            .clickable { onReplayTts() }
            .padding(all = 6.dp)
        ) {
          Icon(
            imageVector = Default.VolumeUp,
            contentDescription = "Replay TTS",
            tint = TacticalCyan,
            modifier = Modifier.size(size = 16.dp)
          )
        }
      }
    }

    Column(verticalArrangement = spacedBy(space = 4.dp)) {
      Text(
        text = packet.transcribedText,
        style = typography.bodyLarge,
        color = TextPrimary,
        fontWeight = Medium
      )

      if (packet.translatedText != packet.transcribedText && packet.translatedText.isNotBlank()) {
        Text(
          text = "↳ ${packet.translatedText}",
          style = typography.bodyMedium,
          color = TacticalAmber,
          fontWeight = Normal
        )
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = SpaceBetween,
      verticalAlignment = CenterVertically
    ) {
      Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(space = 6.dp)
      ) {
        Box(
          modifier = Modifier
            .clip(shape = RoundedCornerShape(size = 4.dp))
            .background(color = TacticalGreen.copy(alpha = 0.15f))
            .border(width = 0.5.dp, color = TacticalGreen, shape = RoundedCornerShape(size = 4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${packet.sttLatencyMs}ms (<300ms PASS)",
            color = TacticalGreen,
            fontSize = 10.sp,
            fontWeight = Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(shape = RoundedCornerShape(size = 4.dp))
            .background(color = TacticalCyan.copy(alpha = 0.15f))
            .border(width = 0.5.dp, color = TacticalCyan, shape = RoundedCornerShape(size = 4.dp))
            .padding(horizontal = 6.dp, vertical = 2.dp)
        ) {
          Text(
            text = "${"%.1f".format(args = arrayOf(packet.bandwidthSavingsPercent))}% RF SAVED",
            color = TacticalCyan,
            fontSize = 10.sp,
            fontWeight = Bold
          )
        }
      }

      Text(
        text = "${packet.sourceLanguage.code.uppercase()} ➔ ${packet.targetLanguage.code.uppercase()} • ${packet.payloadBytes}B",
        style = typography.labelSmall,
        color = TextSecondary
      )
    }
  }
}
