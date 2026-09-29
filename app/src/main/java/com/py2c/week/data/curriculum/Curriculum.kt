package com.py2c.week.data.curriculum

import com.py2c.week.data.AppLocale
import com.py2c.week.data.WeekCurriculum

fun buildCurriculum(locale: AppLocale = AppLocale.ZH): WeekCurriculum = when (locale) {
    AppLocale.ZH -> buildCurriculumZh()
    AppLocale.EN -> buildCurriculumEn()
}

fun buildCurriculumZh(): WeekCurriculum = WeekCurriculum(
    days = listOf(day1(), day2(), day3(), day4(), day5(), day6(), day7()),
    glossary = glossaryTerms(),
    demos = vsCodeDemos(),
)

fun buildCurriculumEn(): WeekCurriculum = WeekCurriculum(
    brand = "C Week",
    brandEn = "C一周通",
    tagline = "Seven days from zero to implementing Dijkstra in C",
    days = listOf(day1En(), day2En(), day3En(), day4En(), day5En(), day6En(), day7En()),
    glossary = glossaryTermsEn(),
    demos = vsCodeDemosEn(),
)
