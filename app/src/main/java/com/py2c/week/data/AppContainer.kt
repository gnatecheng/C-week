package com.py2c.week.data

import android.content.Context
import com.py2c.week.data.curriculum.buildCurriculum

class AppContainer(context: Context) {
    val curriculum: WeekCurriculum = buildCurriculum()
    val progressStore = ProgressStore(context.applicationContext)
    val videos: Map<String, VideoDemo> = VideoCatalog(context.applicationContext).byId
}
