package com.py2c.week.data.curriculum

import com.py2c.week.data.WeekCurriculum

fun buildCurriculum(): WeekCurriculum = WeekCurriculum(
    days = listOf(day1(), day2(), day3(), day4(), day5(), day6(), day7()),
    glossary = glossaryTerms(),
    demos = vsCodeDemos(),
)
