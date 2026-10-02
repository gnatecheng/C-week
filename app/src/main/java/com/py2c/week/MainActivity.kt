package com.py2c.week

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.py2c.week.data.ThemePreference
import com.py2c.week.data.UserPreferencesStore
import com.py2c.week.ui.navigation.Py2CRoot
import com.py2c.week.ui.strings.LocalAppLocale
import com.py2c.week.ui.strings.LocalStrings
import com.py2c.week.ui.strings.stringsFor
import com.py2c.week.ui.theme.Py2CTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = (application as Py2CApplication).container
        setContent {
            val userPrefs by container.userPreferencesStore.preferences.collectAsState(
                initial = com.py2c.week.data.UserPreferences(),
            )
            val locale = UserPreferencesStore.resolveLocale(userPrefs.language, resources.configuration)
            val strings = stringsFor(locale, resources)
            val darkTheme = when (userPrefs.theme) {
                ThemePreference.DARK -> true
                ThemePreference.LIGHT -> false
                ThemePreference.SYSTEM -> isSystemInDarkTheme()
            }
            Py2CTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(
                    LocalStrings provides strings,
                    LocalAppLocale provides locale,
                ) {
                    Py2CRoot(
                        container = container,
                        userPreferences = userPrefs,
                    )
                }
            }
        }
    }
}
