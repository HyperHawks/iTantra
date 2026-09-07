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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperhawks.itantra.core.model.Channel
import org.hyperhawks.itantra.core.model.Channel.Companion.DEFAULT_CHANNELS
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalGreen
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalRed
import org.hyperhawks.itantra.ui.theme.TacticalSurface
import org.hyperhawks.itantra.ui.theme.TacticalSurfaceVariant
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary

@Composable
fun ChannelSelectorModal(
  selectedChannel: Channel,
  onChannelSelected: (Channel) -> Unit,
  onDismissRequest: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(
        text = "TACTICAL CHANNELS",
        style = typography.titleLarge,
        color = TacticalOrangePrimary,
        fontWeight = Bold
      )
    },
    text = {
      LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = spacedBy(space = 8.dp)
      ) {
        items(items = DEFAULT_CHANNELS) { channel: Channel ->
          val isSelected: Boolean = channel.id == selectedChannel.id
          Row(
            modifier = Modifier
              .fillMaxWidth()
              .clip(shape = RoundedCornerShape(size = 8.dp))
              .background(color = if (isSelected) TacticalOrangePrimary.copy(alpha = 0.15f) else TacticalSurfaceVariant)
              .border(
                width = 1.dp,
                color = if (isSelected) TacticalOrangePrimary else TacticalBorder,
                shape = RoundedCornerShape(size = 8.dp)
              )
              .clickable {
                onChannelSelected(channel)
                onDismissRequest()
              }
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically
          ) {
            Column(verticalArrangement = spacedBy(space = 2.dp)) {
              Text(
                text = "${channel.id} • ${channel.name}",
                style = typography.titleMedium,
                color = if (channel.isEmergencyOverride) TacticalRed else if (isSelected) TacticalOrangePrimary else TextPrimary,
                fontWeight = Bold
              )
              Text(
                text = channel.agency,
                style = typography.labelSmall,
                color = TextSecondary
              )
              Text(
                text = "${channel.frequencyMhz} MHz • ${channel.activeMesh.label} • ${channel.activePeersCount} peers",
                fontSize = 11.sp,
                color = TacticalCyan
              )
            }

            if (isSelected) {
              Box(
                modifier = Modifier
                  .size(size = 10.dp)
                  .clip(shape = CircleShape)
                  .background(color = TacticalGreen)
              )
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismissRequest) {
        Text(text = "CANCEL", color = TacticalCyan, fontWeight = Bold)
      }
    },
    containerColor = TacticalSurface,
    tonalElevation = 8.dp
  )
}
