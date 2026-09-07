package org.hyperhawks.itantra

import android.Manifest
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons.Default
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Radio
import androidx.compose.material.icons.filled.Stream
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight.Companion.Bold
import androidx.compose.ui.text.font.FontWeight.Companion.Normal
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import org.hyperhawks.itantra.ui.screens.ChannelsScreen
import org.hyperhawks.itantra.ui.screens.DiagnosticsScreen
import org.hyperhawks.itantra.ui.screens.SosBeaconScreen
import org.hyperhawks.itantra.ui.screens.TransceiverScreen
import org.hyperhawks.itantra.ui.theme.ITantraTheme
import org.hyperhawks.itantra.ui.theme.TacticalBackground
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalRed
import org.hyperhawks.itantra.ui.theme.TacticalSurface
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary
import org.hyperhawks.itantra.ui.viewmodel.TransceiverViewModel

class MainActivity : ComponentActivity() {

  private val viewModel: TransceiverViewModel by viewModels()

  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)

    requestRequiredPermissions()

    setContent {
      ITantraTheme {
        MainAppContainer(viewModel = viewModel)
      }
    }
  }

  private fun requestRequiredPermissions() {
    val permissions: MutableList<String> = mutableListOf(
      Manifest.permission.RECORD_AUDIO,
      Manifest.permission.ACCESS_FINE_LOCATION,
      Manifest.permission.ACCESS_COARSE_LOCATION
    )

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
      permissions.add(Manifest.permission.BLUETOOTH_SCAN)
      permissions.add(Manifest.permission.BLUETOOTH_ADVERTISE)
      permissions.add(Manifest.permission.BLUETOOTH_CONNECT)
    }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
      permissions.add(Manifest.permission.NEARBY_WIFI_DEVICES)
    }

    val missing: List<String> = permissions.filter { perm: String ->
      ContextCompat.checkSelfPermission(this, perm) != PackageManager.PERMISSION_GRANTED
    }

    if (missing.isNotEmpty()) {
      ActivityCompat.requestPermissions(this, missing.toTypedArray(), 101)
    }
  }
}

private enum class NavTab(
  val label: String,
  val icon: ImageVector
) {
  TRANSCEIVER(label = "Transceiver", icon = Default.Radio),
  CHANNELS(label = "Channels", icon = Default.Stream),
  DIAGNOSTICS(label = "Benchmarks", icon = Default.Assessment),
  SOS(label = "112 SOS", icon = Default.Warning)
}

@Composable
private fun MainAppContainer(viewModel: TransceiverViewModel) {
  var currentTab: NavTab by remember { mutableIntStateOf(value = 0).let { state ->
    mutableStateOf(value = NavTab.TRANSCEIVER)
  } }

  Scaffold(
    modifier = Modifier.fillMaxSize(),
    containerColor = TacticalBackground,
    bottomBar = {
      NavigationBar(
        containerColor = TacticalSurface,
        tonalElevation = 8.dp
      ) {
        for (tab: NavTab in NavTab.entries) {
          val isSelected: Boolean = tab == currentTab
          val isSos: Boolean = tab == NavTab.SOS

          NavigationBarItem(
            selected = isSelected,
            onClick = { currentTab = tab },
            icon = {
              Icon(
                imageVector = tab.icon,
                contentDescription = tab.label,
                modifier = Modifier.size(size = 22.dp)
              )
            },
            label = {
              Text(
                text = tab.label,
                fontSize = 11.sp,
                fontWeight = if (isSelected) Bold else Normal
              )
            },
            colors = NavigationBarItemDefaults.colors(
              selectedIconColor = if (isSos) TacticalRed else TacticalOrangePrimary,
              selectedTextColor = if (isSos) TacticalRed else TacticalOrangePrimary,
              unselectedIconColor = TextSecondary,
              unselectedTextColor = TextSecondary,
              indicatorColor = TacticalBorder.copy(alpha = 0.5f)
            )
          )
        }
      }
    }
  ) { innerPadding ->
    when (currentTab) {
      NavTab.TRANSCEIVER -> TransceiverScreen(
        viewModel = viewModel,
        modifier = Modifier.padding(paddingValues = innerPadding)
      )
      NavTab.CHANNELS -> ChannelsScreen(
        viewModel = viewModel,
        onNavigateToTransceiver = { currentTab = NavTab.TRANSCEIVER },
        modifier = Modifier.padding(paddingValues = innerPadding)
      )
      NavTab.DIAGNOSTICS -> DiagnosticsScreen(
        viewModel = viewModel,
        modifier = Modifier.padding(paddingValues = innerPadding)
      )
      NavTab.SOS -> SosBeaconScreen(
        viewModel = viewModel,
        modifier = Modifier.padding(paddingValues = innerPadding)
      )
    }
  }
}
