package com.py2c.week.data

import android.content.Context
import com.py2c.week.data.curriculum.buildCurriculum

class AppContainer(context: Context) {
    private val appContext = context.applicationContext
    val curriculum: WeekCurriculum = buildCurriculum()
    val progressStore = ProgressStore(appContext)
    val userPreferencesStore = UserPreferencesStore(appContext)
    val videos: Map<String, VideoDemo> = VideoCatalog(appContext).byId
}
