package org.hyperhawks.itantra.ui.screens

import androidx.compose.animation.core.RepeatMode.Reverse
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement.Center
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
import androidx.compose.material.icons.filled.Emergency
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.text.font.FontWeight.Companion.SemiBold
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
fun SosBeaconScreen(
  viewModel: TransceiverViewModel,
  modifier: Modifier = Modifier
) {
  val isSosActive: Boolean by viewModel.isEmergencySosActive.collectAsState()
  val callsign: String by viewModel.userCallsign.collectAsState()

  val infiniteTransition = rememberInfiniteTransition(label = "sos_pulse_transition")
  val pulseScale: State<Float> = infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.25f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 600),
      repeatMode = Reverse
    ),
    label = "sos_pulse_scale"
  )

  val quickDistressTemplates: List<String> = listOf(
    "तत्काल चिकित्सा सहायता आवश्यक है - 4 घायल",
    "बाढ़ में 15 ग्रामीण फंसे हैं - नाव की आवश्यकता",
    "भूस्खलन के कारण मुख्य मार्ग अवरुद्ध है",
    "शिशुओं और वृद्धों के लिए पेयजल और भोजन की कमी",
    "हवाई बचाव दल (Air Rescue) की तत्काल आवश्यकता"
  )

  LazyColumn(
    modifier = modifier
      .fillMaxSize()
      .background(color = TacticalBackground)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = spacedBy(space = 16.dp),
    horizontalAlignment = CenterHorizontally
  ) {
    item {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = spacedBy(space = 4.dp)
      ) {
        Text(
          text = "112 NATIONAL EMERGENCY BRIDGE",
          style = typography.titleLarge,
          color = TacticalRed,
          fontWeight = Bold
        )
        Text(
          text = "ISRO SATCOM & Multi-Hop LoRa Mesh Priority Override",
          style = typography.labelSmall,
          color = TextSecondary
        )
      }
    }

    item {
      Box(
        contentAlignment = Center,
        modifier = Modifier.padding(vertical = 12.dp)
      ) {
        if (isSosActive) {
          Box(
            modifier = Modifier
              .size(size = 150.dp)
              .scale(scale = pulseScale.value)
              .clip(shape = CircleShape)
              .background(color = TacticalRed.copy(alpha = 0.25f))
          )
        }

        Box(
          contentAlignment = Center,
          modifier = Modifier
            .size(size = 120.dp)
            .clip(shape = CircleShape)
            .background(color = if (isSosActive) TacticalRed else TacticalRed.copy(alpha = 0.2f))
            .border(width = 3.dp, color = TacticalRed, shape = CircleShape)
            .clickable { viewModel.triggerEmergencySos(activate = !isSosActive) }
        ) {
          Column(
            horizontalAlignment = CenterHorizontally,
            verticalArrangement = spacedBy(space = 4.dp)
          ) {
            Icon(
              imageVector = if (isSosActive) Default.NotificationsActive else Default.Emergency,
              contentDescription = "SOS Beacon",
              tint = White,
              modifier = Modifier.size(size = 42.dp)
            )
            Text(
              text = if (isSosActive) "TRANSMITTING" else "112 SOS",
              color = White,
              fontSize = 12.sp,
              fontWeight = Bold
            )
          }
        }
      }
    }

    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(shape = RoundedCornerShape(size = 12.dp))
          .background(color = TacticalSurface)
          .border(width = 1.dp, color = TacticalBorder, shape = RoundedCornerShape(size = 12.dp))
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
            horizontalArrangement = spacedBy(space = 6.dp)
          ) {
            Icon(
              imageVector = Default.LocationOn,
              contentDescription = "GPS",
              tint = TacticalOrangePrimary,
              modifier = Modifier.size(size = 18.dp)
            )
            Text(
              text = "FIELD COORDINATES (ISRO SAC)",
              style = typography.titleMedium,
              color = TextPrimary,
              fontWeight = Bold
            )
          }

          Text(
            text = "AIR-GAP GPS",
            color = TacticalCyan,
            fontSize = 11.sp,
            fontWeight = Bold
          )
        }

        Text(
          text = "Latitude: 28.6139° N • Longitude: 77.2090° E\nAltitude: 216m AMSL • Callsign: $callsign",
          style = typography.bodyMedium,
          color = TextPrimary
        )
      }
    }

    item {
      Column(
        modifier = Modifier
          .fillMaxWidth()
          .clip(shape = RoundedCornerShape(size = 12.dp))
          .background(color = TacticalSurface)
          .border(width = 1.dp, color = TacticalBorder, shape = RoundedCornerShape(size = 12.dp))
          .padding(all = 14.dp),
        verticalArrangement = spacedBy(space = 10.dp)
      ) {
        Text(
          text = "RAPID FIELD DISTRESS BROADCASTS",
          style = typography.titleMedium,
          color = TacticalAmber,
          fontWeight = Bold
        )

        for (template: String in quickDistressTemplates) {
          Box(
            modifier = Modifier
              .fillMaxWidth()
              .clip(shape = RoundedCornerShape(size = 8.dp))
              .background(color = TacticalSurfaceVariant)
              .border(width = 0.5.dp, color = TacticalBorder, shape = RoundedCornerShape(size = 8.dp))
              .clickable { viewModel.sendTextMessage(text = template) }
              .padding(horizontal = 12.dp, vertical = 10.dp)
          ) {
            Row(
              modifier = Modifier.fillMaxWidth(),
              horizontalArrangement = SpaceBetween,
              verticalAlignment = CenterVertically
            ) {
              Text(
                text = template,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = Medium,
                modifier = Modifier.weight(weight = 1.0f)
              )
              Text(
                text = "TRANSMIT",
                color = TacticalRed,
                fontSize = 11.sp,
                fontWeight = Bold
              )
            }
          }
        }
      }
    }
  }
}
