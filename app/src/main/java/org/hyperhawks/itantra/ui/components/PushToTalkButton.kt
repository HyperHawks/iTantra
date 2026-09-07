package org.hyperhawks.itantra.ui.components

import androidx.compose.animation.core.RepeatMode.Reverse
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectTapGestures
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
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.Center
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color.Companion.Transparent
import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperhawks.itantra.ui.theme.TacticalAmber
import org.hyperhawks.itantra.ui.theme.TacticalBackground
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalGreen
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalSurface
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary
import org.hyperhawks.itantra.ui.theme.TextTertiary

@Composable
fun PushToTalkControl(
  isRecording: Boolean,
  isHandsFreeVad: Boolean,
  onVadToggled: (Boolean) -> Unit,
  onPttPressed: () -> Unit,
  onPttReleased: () -> Unit,
  onSendTextMessage: (String) -> Unit,
  modifier: Modifier = Modifier
) {
  var manualInputText: String by remember { mutableStateOf(value = "") }

  val infiniteTransition = rememberInfiniteTransition(label = "ptt_pulse_transition")
  val pulseScale: State<Float> = infiniteTransition.animateFloat(
    initialValue = 1.0f,
    targetValue = 1.15f,
    animationSpec = infiniteRepeatable(
      animation = tween(durationMillis = 800),
      repeatMode = Reverse
    ),
    label = "ptt_pulse_scale"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .background(color = TacticalSurface)
      .border(width = 1.dp, color = TacticalBorder)
      .padding(horizontal = 16.dp, vertical = 12.dp),
    verticalArrangement = spacedBy(space = 12.dp),
    horizontalAlignment = CenterHorizontally
  ) {
    Row(
      modifier = Modifier.fillMaxWidth(),
      horizontalArrangement = SpaceBetween,
      verticalAlignment = CenterVertically
    ) {
      Column {
        Text(
          text = "HANDS-FREE DUPLEX VAD",
          style = typography.titleMedium,
          color = TextPrimary,
          fontWeight = Bold
        )
        Text(
          text = if (isHandsFreeVad) "Continuous speech detection (no PTT needed)" else "Manual Push-To-Talk active",
          style = typography.labelSmall,
          color = TextSecondary
        )
      }

      Switch(
        checked = isHandsFreeVad,
        onCheckedChange = { checked: Boolean -> onVadToggled(checked) },
        colors = SwitchDefaults.colors(
          checkedThumbColor = TacticalOrangePrimary,
          checkedTrackColor = TacticalOrangePrimary.copy(alpha = 0.3f),
          uncheckedThumbColor = TextTertiary,
          uncheckedTrackColor = TacticalBorder
        )
      )
    }

    Box(
      contentAlignment = Center,
      modifier = Modifier.padding(vertical = 4.dp)
    ) {
      if (isRecording) {
        Box(
          modifier = Modifier
            .size(size = 96.dp)
            .scale(scale = pulseScale.value)
            .clip(shape = CircleShape)
            .background(color = TacticalOrangePrimary.copy(alpha = 0.2f))
        )
      }

      Box(
        contentAlignment = Center,
        modifier = Modifier
          .size(size = 80.dp)
          .clip(shape = CircleShape)
          .background(
            color = if (isRecording) TacticalOrangePrimary else TacticalBorder.copy(alpha = 0.6f)
          )
          .border(
            width = 2.dp,
            color = if (isRecording) White else TacticalOrangePrimary,
            shape = CircleShape
          )
          .pointerInput(key1 = isHandsFreeVad) {
            detectTapGestures(
              onPress = {
                if (!isHandsFreeVad) {
                  onPttPressed()
                  tryAwaitRelease()
                  onPttReleased()
                }
              }
            )
          }
      ) {
        Icon(
          imageVector = if (isHandsFreeVad) Default.GraphicEq else Default.Mic,
          contentDescription = "Push To Talk",
          tint = if (isRecording) White else TacticalOrangePrimary,
          modifier = Modifier.size(size = 36.dp)
        )
      }
    }

    Text(
      text = if (isHandsFreeVad) "AUTOMATIC VOICE RELAY ACTIVE" else "HOLD PTT BUTTON TO TALK",
      style = typography.labelSmall,
      color = if (isRecording) TacticalOrangePrimary else TextSecondary,
      fontWeight = Bold
    )

    Row(
      modifier = Modifier.fillMaxWidth(),
      verticalAlignment = CenterVertically,
      horizontalArrangement = spacedBy(space = 8.dp)
    ) {
      OutlinedTextField(
        value = manualInputText,
        onValueChange = { newValue: String -> manualInputText = newValue },
        placeholder = {
          Text(
            text = "Type text to transmit over RF...",
            color = TextTertiary,
            fontSize = 13.sp
          )
        },
        modifier = Modifier.weight(weight = 1.0f),
        colors = OutlinedTextFieldDefaults.colors(
          focusedContainerColor = TacticalBackground,
          unfocusedContainerColor = TacticalBackground,
          focusedBorderColor = TacticalCyan,
          unfocusedBorderColor = TacticalBorder,
          focusedTextColor = TextPrimary,
          unfocusedTextColor = TextPrimary
        ),
        shape = RoundedCornerShape(size = 8.dp),
        singleLine = true
      )

      IconButton(
        onClick = {
          if (manualInputText.isNotBlank()) {
            onSendTextMessage(manualInputText)
            manualInputText = ""
          }
        },
        modifier = Modifier
          .clip(shape = RoundedCornerShape(size = 8.dp))
          .background(color = TacticalCyan.copy(alpha = 0.15f))
          .border(width = 1.dp, color = TacticalCyan, shape = RoundedCornerShape(size = 8.dp))
      ) {
        Icon(
          imageVector = Default.Send,
          contentDescription = "Send Text",
          tint = TacticalCyan,
          modifier = Modifier.size(size = 20.dp)
        )
      }
    }
  }
}
