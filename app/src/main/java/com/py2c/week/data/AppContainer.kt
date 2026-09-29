package com.py2c.week.data

import android.content.Context
import com.py2c.week.data.curriculum.buildCurriculum

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val progressStore = ProgressStore(appContext)
    val userPreferencesStore = UserPreferencesStore(appContext)
    val videos: Map<String, VideoDemo> = VideoCatalog(appContext).byId

    fun curriculumFor(locale: AppLocale): WeekCurriculum =
        buildCurriculum(locale).copy(videos = videosForLocale(locale))

    private fun videosForLocale(locale: AppLocale): Map<String, VideoDemo> =
        if (locale == AppLocale.EN) videos.mapValues { (_, demo) -> demo.withEnglishCaptions() } else videos
}
