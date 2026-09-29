package com.py2c.week.data

import android.content.Context
import android.content.res.Configuration
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.runBlocking
import java.util.Locale

private val Context.userPrefsDataStore: DataStore<Preferences> by preferencesDataStore(name = "py2c_user_prefs")

enum class LanguagePreference {
    SYSTEM,
    ZH,
    EN,
}

enum class ThemePreference {
    SYSTEM,
    LIGHT,
    DARK,
}

/** Resolved UI language (never SYSTEM). */
enum class AppLocale {
    ZH,
    EN,
}

data class UserPreferences(
    val language: LanguagePreference = LanguagePreference.SYSTEM,
    val theme: ThemePreference = ThemePreference.SYSTEM,
)

class UserPreferencesStore(private val context: Context) {
    private val languageKey = stringPreferencesKey("language")
    private val themeKey = stringPreferencesKey("theme")

    val preferences: Flow<UserPreferences> = context.userPrefsDataStore.data.map { prefs ->
        UserPreferences(
            language = prefs[languageKey]?.let { runCatching { LanguagePreference.valueOf(it) }.getOrNull() }
                ?: LanguagePreference.SYSTEM,
            theme = prefs[themeKey]?.let { runCatching { ThemePreference.valueOf(it) }.getOrNull() }
                ?: ThemePreference.SYSTEM,
        )
    }

    suspend fun setLanguage(preference: LanguagePreference) {
        context.userPrefsDataStore.edit { it[languageKey] = preference.name }
    }

    suspend fun setTheme(preference: ThemePreference) {
        context.userPrefsDataStore.edit { it[themeKey] = preference.name }
    }

    companion object {
        fun resolveLocale(preference: LanguagePreference, configuration: Configuration): AppLocale {
            return when (preference) {
                LanguagePreference.ZH -> AppLocale.ZH
                LanguagePreference.EN -> AppLocale.EN
                LanguagePreference.SYSTEM -> {
                    val lang = configuration.locales[0].language.lowercase(Locale.ROOT)
                    if (lang == "en") AppLocale.EN else AppLocale.ZH
                }
            }
        }

        /** Blocking read for non-Compose surfaces (e.g. fullscreen activity). */
        fun resolvedLocaleBlocking(context: Context): AppLocale = runBlocking {
            val prefs = context.applicationContext.userPrefsDataStore.data.first()
            val language = prefs[stringPreferencesKey("language")]?.let {
                runCatching { LanguagePreference.valueOf(it) }.getOrNull()
            } ?: LanguagePreference.SYSTEM
            resolveLocale(language, context.resources.configuration)
        }
    }
}
