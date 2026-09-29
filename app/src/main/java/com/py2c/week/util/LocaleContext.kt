package com.py2c.week.util

import android.content.Context
import android.content.res.Configuration
import com.py2c.week.data.AppLocale
import java.util.Locale

fun Context.withAppLocale(locale: AppLocale): Context {
    val config = Configuration(resources.configuration)
    val javaLocale = when (locale) {
        AppLocale.EN -> Locale.ENGLISH
        AppLocale.ZH -> Locale.CHINESE
    }
    config.setLocale(javaLocale)
    return createConfigurationContext(config)
}
