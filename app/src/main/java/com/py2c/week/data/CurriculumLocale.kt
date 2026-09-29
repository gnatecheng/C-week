package com.py2c.week.data

import com.py2c.week.data.curriculum.buildCurriculum

fun WeekCurriculum.forLocale(locale: AppLocale): WeekCurriculum {
    if (locale == AppLocale.ZH) return this
    return buildCurriculum(locale).copy(videos = videos)
}

fun CourseDay.forLocale(locale: AppLocale): CourseDay =
    if (locale == AppLocale.ZH) this else buildCurriculum(locale).days.first { it.id == id }
