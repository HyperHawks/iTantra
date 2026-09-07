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
import androidx.compose.material.icons.filled.CellTower
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperhawks.itantra.core.model.Channel
import org.hyperhawks.itantra.core.model.MeshLinkType
import org.hyperhawks.itantra.ui.theme.TacticalAmber
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalGreen
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalRed
import org.hyperhawks.itantra.ui.theme.TacticalSurface
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary

@Composable
fun TacticalTopBar(
  activeChannel: Channel,
  meshLinkType: MeshLinkType,
  signalStrengthRssi: Int,
  connectedPeersCount: Int,
  isEmergencySosActive: Boolean,
  onChannelClick: () -> Unit,
  onSosToggle: () -> Unit,
  modifier: Modifier = Modifier
) {
  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(color = TacticalSurface)
      .border(width = 1.dp, color = TacticalBorder)
      .padding(horizontal = 16.dp, vertical = 10.dp),
    verticalArrangement = spacedBy(space = 8.dp)
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = SpaceBetween,
      verticalAlignment = CenterVertically
    ) {
      Column {
        Row(
          verticalAlignment = CenterVertically,
          horizontalArrangement = spacedBy(space = 6.dp)
        ) {
          Text(
            text = "iTANTRA",
            style = typography.titleLarge,
            color = TacticalOrangePrimary,
            fontWeight = Bold
          )
          Box(
            modifier = Modifier
              .clip(shape = RoundedCornerShape(size = 4.dp))
              .background(color = TacticalCyan.copy(alpha = 0.15f))
              .border(width = 1.dp, color = TacticalCyan, shape = RoundedCornerShape(size = 4.dp))
              .padding(horizontal = 6.dp, vertical = 2.dp)
          ) {
            Text(
              text = "SIH 2025",
              color = TacticalCyan,
              fontSize = 10.sp,
              fontWeight = Bold
            )
          }
        }
        Text(
          text = "NEURAL TRANSCEIVER • 10 INDIC LANGUAGES",
          style = typography.labelSmall,
          color = TextSecondary
        )
      }

      Box(
        modifier = Modifier
          .clip(shape = RoundedCornerShape(size = 8.dp))
          .background(color = if (isEmergencySosActive) TacticalRed else TacticalRed.copy(alpha = 0.15f))
          .border(
            width = 1.5.dp,
            color = TacticalRed,
            shape = RoundedCornerShape(size = 8.dp)
          )
          .clickable { onSosToggle() }
          .padding(horizontal = 12.dp, vertical = 6.dp)
      ) {
        Row(
          verticalAlignment = CenterVertically,
          horizontalArrangement = spacedBy(space = 4.dp)
        ) {
          Icon(
            imageVector = Default.Warning,
            contentDescription = "SOS",
            tint = if (isEmergencySosActive) White else TacticalRed,
            modifier = Modifier.size(size = 14.dp)
          )
          Text(
            text = if (isEmergencySosActive) "SOS ACTIVE" else "112 SOS",
            color = if (isEmergencySosActive) White else TacticalRed,
            fontSize = 11.sp,
            fontWeight = Bold
          )
        }
      }
    }

    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = SpaceBetween,
      verticalAlignment = CenterVertically
    ) {
      Box(
        modifier = Modifier
          .clip(shape = RoundedCornerShape(size = 6.dp))
          .background(color = TacticalBorder.copy(alpha = 0.5f))
          .clickable { onChannelClick() }
          .padding(horizontal = 8.dp, vertical = 4.dp)
      ) {
        Row(
          verticalAlignment = CenterVertically,
          horizontalArrangement = spacedBy(space = 6.dp)
        ) {
          Box(
            modifier = Modifier
              .size(size = 8.dp)
              .clip(shape = CircleShape)
              .background(color = TacticalGreen)
          )
          Text(
            text = "${activeChannel.id} • ${activeChannel.name}",
            color = TextPrimary,
            fontSize = 12.sp,
            fontWeight = SemiBold
          )
        }
      }

      Row(
        verticalAlignment = CenterVertically,
        horizontalArrangement = spacedBy(space = 8.dp)
      ) {
        Row(
          verticalAlignment = CenterVertically,
          horizontalArrangement = spacedBy(space = 3.dp)
        ) {
          Icon(
            imageVector = Default.CellTower,
            contentDescription = "RF Link",
            tint = TacticalCyan,
            modifier = Modifier.size(size = 13.dp)
          )
          Text(
            text = "${meshLinkType.protocolHeader} (${signalStrengthRssi} dBm)",
            color = TacticalCyan,
            fontSize = 11.sp,
            fontWeight = Bold
          )
        }

        Box(
          modifier = Modifier
            .clip(shape = RoundedCornerShape(size = 4.dp))
            .background(color = TacticalGreen.copy(alpha = 0.15f))
            .border(width = 1.dp, color = TacticalGreen, shape = RoundedCornerShape(size = 4.dp))
            .padding(horizontal = 5.dp, vertical = 2.dp)
        ) {
          Row(
            verticalAlignment = CenterVertically,
            horizontalArrangement = spacedBy(space = 3.dp)
          ) {
            Icon(
              imageVector = Default.Security,
              contentDescription = "Air-Gapped",
              tint = TacticalGreen,
              modifier = Modifier.size(size = 10.dp)
            )
            Text(
              text = "AIR-GAP",
              color = TacticalGreen,
              fontSize = 9.sp,
              fontWeight = Bold
            )
          }
        }
      }
    }
  }
}
