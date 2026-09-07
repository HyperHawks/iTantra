package org.hyperhawks.itantra.ui.components

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Arrangement.SpaceBetween
import androidx.compose.foundation.layout.Arrangement.spacedBy
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment.Companion.Bottom
import androidx.compose.ui.Alignment.Companion.CenterHorizontally
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperhawks.itantra.core.neural.AudioWaveformVisualizerState
import org.hyperhawks.itantra.ui.theme.TacticalAmber
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalGreen
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalSurfaceVariant
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary

@Composable
fun WaveformVisualizer(
  visualizerState: AudioWaveformVisualizerState,
  isRecording: Boolean,
  isHandsFreeVad: Boolean,
  modifier: Modifier = Modifier
) {
  val targetBarColor: Color = when {
    visualizerState.isVoiceDetected -> TacticalOrangePrimary
    visualizerState.isTransmitting -> TacticalCyan
    isRecording -> TacticalGreen
    else -> TacticalBorder
  }

  val animatedBarColor: State<Color> = animateColorAsState(
    targetValue = targetBarColor,
    animationSpec = tween(durationMillis = 150),
    label = "waveform_bar_color"
  )

  Column(
    modifier = modifier
      .fillMaxWidth()
      .clip(shape = RoundedCornerShape(size = 12.dp))
      .background(color = TacticalSurfaceVariant)
      .border(
        width = 1.dp,
        color = if (visualizerState.isVoiceDetected) TacticalOrangePrimary else TacticalBorder,
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
        horizontalArrangement = spacedBy(space = 6.dp)
      ) {
        val statusText: String = when {
          visualizerState.isVoiceDetected -> "VOICE PAUSE DETECTOR ACTIVE (VAD)"
          visualizerState.isTransmitting -> "QUANTIZED CTC STT STREAMING"
          isHandsFreeVad -> "HANDS-FREE VAD LISTENING"
          else -> "STANDBY (PUSH TO TALK)"
        }
        Text(
          text = statusText,
          style = typography.labelSmall,
          color = if (visualizerState.isVoiceDetected) TacticalOrangePrimary else TacticalCyan,
          fontWeight = Bold
        )
      }

      Text(
        text = "${"%.1f".format(args = arrayOf(visualizerState.currentRmsDb))} dB",
        style = typography.labelSmall,
        color = TextSecondary,
        fontWeight = Medium
      )
    }

    Row(
      modifier = Modifier
        .fillMaxWidth()
        .height(height = 42.dp),
      horizontalArrangement = Arrangement.SpaceEvenly,
      verticalAlignment = Bottom
    ) {
      for ((index: Int, amplitude: Float) in visualizerState.amplitudes.withIndex()) {
        val heightFraction: Float = amplitude.coerceIn(range = 0.08f..1.0f)
        Box(
          modifier = Modifier
            .width(width = 4.dp)
            .fillMaxHeight(fraction = heightFraction)
            .clip(shape = RoundedCornerShape(size = 2.dp))
            .background(color = animatedBarColor.value)
        )
      }
    }
  }
}
