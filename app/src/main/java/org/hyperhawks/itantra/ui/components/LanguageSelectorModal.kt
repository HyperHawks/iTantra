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
import androidx.compose.ui.text.font.FontWeight.Companion.Medium
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.hyperhawks.itantra.core.model.Language
import org.hyperhawks.itantra.ui.theme.TacticalBorder
import org.hyperhawks.itantra.ui.theme.TacticalCyan
import org.hyperhawks.itantra.ui.theme.TacticalOrangePrimary
import org.hyperhawks.itantra.ui.theme.TacticalSurface
import org.hyperhawks.itantra.ui.theme.TacticalSurfaceVariant
import org.hyperhawks.itantra.ui.theme.TextPrimary
import org.hyperhawks.itantra.ui.theme.TextSecondary
import org.hyperhawks.itantra.ui.theme.TextTertiary

@Composable
fun LanguageSelectorModal(
  title: String,
  selectedLanguage: Language,
  onLanguageSelected: (Language) -> Unit,
  onDismissRequest: () -> Unit
) {
  AlertDialog(
    onDismissRequest = onDismissRequest,
    title = {
      Text(
        text = title,
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
        items(items = Language.entries) { language: Language ->
          val isSelected: Boolean = language == selectedLanguage
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
                onLanguageSelected(language)
                onDismissRequest()
              }
              .padding(horizontal = 12.dp, vertical = 10.dp),
            horizontalArrangement = SpaceBetween,
            verticalAlignment = CenterVertically
          ) {
            Column {
              Text(
                text = language.displayName,
                style = typography.titleMedium,
                color = if (isSelected) TacticalOrangePrimary else TextPrimary,
                fontWeight = Bold
              )
              Text(
                text = "${language.nativeName} • Script: ${language.scriptCode}",
                style = typography.labelSmall,
                color = TextSecondary
              )
            }

            if (isSelected) {
              Box(
                modifier = Modifier
                  .size(size = 10.dp)
                  .clip(shape = CircleShape)
                  .background(color = TacticalOrangePrimary)
              )
            }
          }
        }
      }
    },
    confirmButton = {
      TextButton(onClick = onDismissRequest) {
        Text(text = "CLOSE", color = TacticalCyan, fontWeight = Bold)
      }
    },
    containerColor = TacticalSurface,
    tonalElevation = 8.dp
  )
}
