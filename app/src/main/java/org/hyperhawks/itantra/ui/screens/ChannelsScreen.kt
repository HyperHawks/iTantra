package org.hyperhawks.itantra.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.LockOpen
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperhawks.itantra.core.model.Channel
import org.hyperhawks.itantra.core.model.Channel.Companion.DEFAULT_CHANNELS
import org.hyperhawks.itantra.ui.theme.TacticalAmber
import org.hyperhawks.itantra.ui.theme.TacticalBackground
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
import org.hyperhawks.itantra.ui.viewmodel.TransceiverViewModel

@Composable
fun ChannelsScreen(
  viewModel: TransceiverViewModel,
  onNavigateToTransceiver: () -> Unit,
  modifier: Modifier = Modifier
) {
  val selectedChannel: Channel by viewModel.activeChannel.collectAsState()

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(color = TacticalBackground)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = spacedBy(space = 12.dp)
  ) {
    item {
      Column(verticalArrangement = spacedBy(space = 4.dp)) {
        Text(
          text = "TACTICAL RELAY CHANNELS",
          style = typography.titleLarge,
          color = TacticalOrangePrimary,
          fontWeight = Bold
        )
        Text(
          text = "LoRa, BLE 5.0, WiFi Direct & BSNL 2G frequency assignments",
          style = typography.labelSmall,
          color = TextSecondary
        )
      }
    }

    items(items = DEFAULT_CHANNELS) { channel: Channel ->
      val isSelected: Boolean = channel.id == selectedChannel.id
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(shape = RoundedCornerShape(size = 12.dp))
          .background(color = if (isSelected) TacticalOrangePrimary.copy(alpha = 0.12f) else TacticalSurface)
          .border(
            width = if (isSelected) 1.5.dp else 1.dp,
            color = if (isSelected) TacticalOrangePrimary else TacticalBorder,
            shape = RoundedCornerShape(size = 12.dp)
          )
          .clickable {
            viewModel.setChannel(channel = channel)
            onNavigateToTransceiver()
          }
          .padding(all = 14.dp),
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
                .size(size = 10.dp)
                .clip(shape = CircleShape)
                .background(color = if (channel.isEmergencyOverride) TacticalRed else if (isSelected) TacticalOrangePrimary else TacticalGreen)
            )
            Text(
              text = channel.id,
              style = typography.titleMedium,
              color = if (isSelected) TacticalOrangePrimary else TextPrimary,
              fontWeight = Bold
            )
            Icon(
              imageVector = if (channel.isEncrypted) Default.Lock else Default.LockOpen,
              contentDescription = if (channel.isEncrypted) "Encrypted" else "Open",
              tint = if (channel.isEncrypted) TacticalGreen else TextTertiary,
              modifier = Modifier.size(size = 14.dp)
            )
          }

          Text(
            text = "${channel.frequencyMhz} MHz",
            color = TacticalCyan,
            fontSize = 12.sp,
            fontWeight = Bold
          )
        }

        Text(
          text = channel.name,
          style = typography.titleMedium,
          color = TextPrimary,
          fontWeight = SemiBold
        )

        Text(
          text = channel.agency,
          style = typography.bodyMedium,
          color = TextSecondary
        )

        Row(
          modifier = Modifier.fillMaxWidth(),
          horizontalArrangement = SpaceBetween,
          verticalAlignment = CenterVertically
        ) {
          Box(
            modifier = Modifier
              .clip(shape = RoundedCornerShape(size = 4.dp))
              .background(color = TacticalSurfaceVariant)
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = channel.activeMesh.label,
              color = TacticalCyan,
              fontSize = 10.sp,
              fontWeight = Bold
            )
          }

          Text(
            text = "${channel.activePeersCount} active field nodes",
            style = typography.labelSmall,
            color = TacticalGreen,
            fontWeight = Bold
          )
        }
      }
    }
  }
}
