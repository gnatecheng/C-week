package com.py2c.week.ui.settings

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.py2c.week.BuildConfig
import com.py2c.week.data.LanguagePreference
import com.py2c.week.data.ThemePreference
import com.py2c.week.data.UserPreferences
import com.py2c.week.data.UserPreferencesStore
import com.py2c.week.ui.strings.AppStrings
import com.py2c.week.ui.strings.languagePreferenceLabel
import com.py2c.week.ui.strings.themePreferenceLabel
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private const val REPO_URL = "https://github.com/gnatecheng/c-week"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    strings: AppStrings,
    preferences: UserPreferences,
    store: UserPreferencesStore,
    onBack: () -> Unit,
) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val buildTimeFormatted = rememberBuildTimeLabel()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.settingsTitle) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = strings.back)
                    }
                },
            )
        },
    ) { padding ->
        Column(
            Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            Text(strings.settingsLanguage, style = MaterialTheme.typography.titleMedium)
            PreferenceChipRow(
                options = LanguagePreference.entries.toList(),
                selected = preferences.language,
                label = { languagePreferenceLabel(strings, it) },
                onSelect = { scope.launch { store.setLanguage(it) } },
            )

            Text(strings.settingsTheme, style = MaterialTheme.typography.titleMedium)
            PreferenceChipRow(
                options = ThemePreference.entries.toList(),
                selected = preferences.theme,
                label = { themePreferenceLabel(strings, it) },
                onSelect = { scope.launch { store.setTheme(it) } },
            )

            Text(strings.settingsAbout, style = MaterialTheme.typography.titleMedium)
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(
                    Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    Text(strings.aboutDescription, style = MaterialTheme.typography.bodyMedium)
                    Text(
                        "${strings.aboutVersion}: ${BuildConfig.VERSION_NAME}",
                        style = MaterialTheme.typography.bodyLarge,
                    )
                    Text(
                        "${strings.aboutLastUpdate}: $buildTimeFormatted",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                    Text(
                        strings.aboutSource,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.primary,
                    )
                    Text(REPO_URL, style = MaterialTheme.typography.bodySmall)
                    OutlinedButton(
                        onClick = {
                            context.startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(REPO_URL)))
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                    ) {
                        Text(strings.aboutOpenRepo)
                    }
                }
            }
        }
    }
}

@Composable
private fun <T> PreferenceChipRow(
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        options.chunked(2).forEach { row ->
            androidx.compose.foundation.layout.Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth(),
            ) {
                row.forEach { option ->
                    FilterChip(
                        selected = selected == option,
                        onClick = { onSelect(option) },
                        label = { Text(label(option)) },
                        modifier = Modifier.weight(1f),
                    )
                }
                if (row.size == 1) {
                    androidx.compose.foundation.layout.Spacer(Modifier.weight(1f))
                }
            }
        }
    }
}

@Composable
private fun rememberBuildTimeLabel(): String {
    val raw = BuildConfig.BUILD_TIME_UTC
    val zone = ZoneId.systemDefault()
    return runCatching {
        val instant = Instant.parse(raw)
        val formatted = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z", Locale.getDefault())
            .withZone(zone)
            .format(instant)
        formatted
    }.getOrElse { raw }
}
