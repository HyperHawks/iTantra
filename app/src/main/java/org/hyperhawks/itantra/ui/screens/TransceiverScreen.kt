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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment.Companion.CenterVertically
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperhawks.itantra.core.model.Channel
import org.hyperhawks.itantra.core.model.Language
import org.hyperhawks.itantra.core.model.MeshLinkType
import org.hyperhawks.itantra.core.model.TransceiverPacket
import org.hyperhawks.itantra.core.neural.AudioWaveformVisualizerState
import org.hyperhawks.itantra.ui.components.ChannelSelectorModal
import org.hyperhawks.itantra.ui.components.LanguageSelectorModal
import org.hyperhawks.itantra.ui.components.PacketBubble
import org.hyperhawks.itantra.ui.components.PushToTalkControl
import org.hyperhawks.itantra.ui.components.TacticalTopBar
import org.hyperhawks.itantra.ui.components.WaveformVisualizer
import org.hyperhawks.itantra.ui.theme.TacticalBackground
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalSurface
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary
import org.hyperhawks.itantra.ui.viewmodel.TransceiverViewModel

@Composable
fun TransceiverScreen(
  viewModel: TransceiverViewModel,
  modifier: Modifier = Modifier
) {
  val activeChannel: Channel by viewModel.activeChannel.collectAsState()
  val activeLinkType: MeshLinkType by viewModel.activeLinkType.collectAsState()
  val signalRssi: Int by viewModel.signalStrengthRssi.collectAsState()
  val peerCount: Int by viewModel.connectedPeersCount.collectAsState()
  val isSosActive: Boolean by viewModel.isEmergencySosActive.collectAsState()
  val sourceLanguage: Language by viewModel.sourceLanguage.collectAsState()
  val targetLanguage: Language by viewModel.targetLanguage.collectAsState()
  val isHandsFreeVad: Boolean by viewModel.isHandsFreeVadMode.collectAsState()
  val packets: List<TransceiverPacket> by viewModel.packetsHistory.collectAsState()
  val visualizerState: AudioWaveformVisualizerState by viewModel.visualizerState.collectAsState()
  val isRecording: Boolean by viewModel.isRecording.collectAsState()
  val userCallsign: String by viewModel.userCallsign.collectAsState()

  var showChannelModal: Boolean by remember { mutableStateOf(value = false) }
  var showSourceLangModal: Boolean by remember { mutableStateOf(value = false) }
  var showTargetLangModal: Boolean by remember { mutableStateOf(value = false) }

  val listState = rememberLazyListState()

  LaunchedEffect(key1 = packets.size) {
    if (packets.isNotEmpty()) {
      listState.animateScrollToItem(index = packets.size - 1)
    }
  }

  Scaffold(
    modifier = modifier.fillMaxSize(),
    containerColor = TacticalBackground,
    topBar = {
      TacticalTopBar(
        activeChannel = activeChannel,
        meshLinkType = activeLinkType,
        signalStrengthRssi = signalRssi,
        connectedPeersCount = peerCount,
        isEmergencySosActive = isSosActive,
        onChannelClick = { showChannelModal = true },
        onSosToggle = { viewModel.triggerEmergencySos(activate = !isSosActive) }
      )
    },
    bottomBar = {
      Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = spacedBy(space = 8.dp)
      ) {
        WaveformVisualizer(
          visualizerState = visualizerState,
          isRecording = isRecording,
          isHandsFreeVad = isHandsFreeVad,
          modifier = Modifier.padding(horizontal = 12.dp)
        )
        PushToTalkControl(
          isRecording = isRecording,
          isHandsFreeVad = isHandsFreeVad,
          onVadToggled = { enabled: Boolean -> viewModel.toggleHandsFreeVad(enabled = enabled) },
          onPttPressed = { viewModel.startPushToTalk() },
          onPttReleased = { viewModel.releasePushToTalk() },
          onSendTextMessage = { msg: String -> viewModel.sendTextMessage(text = msg) }
        )
      }
    }
  ) { paddingValues ->
    Column(
      modifier = Modifier
        .fillMaxSize()
        .padding(paddingValues = paddingValues)
        .padding(horizontal = 12.dp, vertical = 8.dp),
      verticalArrangement = spacedBy(space = 8.dp)
    ) {
      Row(
        modifier = Modifier
          .fillMaxWidth()
          .clip(shape = RoundedCornerShape(size = 8.dp))
          .background(color = TacticalSurface)
          .border(width = 1.dp, color = TacticalBorder, shape = RoundedCornerShape(size = 8.dp))
          .padding(horizontal = 12.dp, vertical = 8.dp),
        horizontalArrangement = SpaceBetween,
        verticalAlignment = CenterVertically
      ) {
        Column(
          modifier = Modifier.clickable { showSourceLangModal = true }
        ) {
          Text(text = "SPEAK", style = typography.labelSmall, color = TextSecondary)
          Text(
            text = "${sourceLanguage.displayName} (${sourceLanguage.nativeName})",
            color = TacticalOrangePrimary,
            fontWeight = Bold,
            fontSize = 13.sp
          )
        }

        Box(
          modifier = Modifier
            .clip(shape = RoundedCornerShape(size = 6.dp))
            .background(color = TacticalBorder)
            .clickable {
              val currentSrc: Language = sourceLanguage
              viewModel.setSourceLanguage(language = targetLanguage)
              viewModel.setTargetLanguage(language = currentSrc)
            }
            .padding(all = 6.dp)
        ) {
          Icon(
            imageVector = Default.SwapHoriz,
            contentDescription = "Swap Languages",
            tint = TacticalCyan,
            modifier = Modifier.size(size = 18.dp)
          )
        }

        Column(
          modifier = Modifier.clickable { showTargetLangModal = true }
        ) {
          Text(text = "RECEIVE AS", style = typography.labelSmall, color = TextSecondary)
          Text(
            text = "${targetLanguage.displayName} (${targetLanguage.nativeName})",
            color = TacticalCyan,
            fontWeight = Bold,
            fontSize = 13.sp
          )
        }
      }

      LazyColumn(
        state = listState,
        modifier = Modifier
          .fillMaxWidth()
          .weight(weight = 1.0f),
        verticalArrangement = spacedBy(space = 8.dp)
      ) {
        items(items = packets) { packet: TransceiverPacket ->
          PacketBubble(
            packet = packet,
            isOwnPacket = packet.senderCallsign == userCallsign,
            onReplayTts = { viewModel.replaySpeech(packet = packet) }
          )
        }
      }
    }
  }

  if (showChannelModal) {
    ChannelSelectorModal(
      selectedChannel = activeChannel,
      onChannelSelected = { ch: Channel -> viewModel.setChannel(channel = ch) },
      onDismissRequest = { showChannelModal = false }
    )
  }

  if (showSourceLangModal) {
    LanguageSelectorModal(
      title = "SELECT INPUT LANGUAGE",
      selectedLanguage = sourceLanguage,
      onLanguageSelected = { lang: Language -> viewModel.setSourceLanguage(language = lang) },
      onDismissRequest = { showSourceLangModal = false }
    )
  }

  if (showTargetLangModal) {
    LanguageSelectorModal(
      title = "SELECT TARGET LANGUAGE",
      selectedLanguage = targetLanguage,
      onLanguageSelected = { lang: Language -> viewModel.setTargetLanguage(language = lang) },
      onDismissRequest = { showTargetLangModal = false }
    )
  }
}
