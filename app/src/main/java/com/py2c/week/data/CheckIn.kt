package com.py2c.week.data

import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale

private val ISO: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

fun todayIso(): String = LocalDate.now().format(ISO)

fun parseIsoDate(value: String): LocalDate? = runCatching { LocalDate.parse(value, ISO) }.getOrNull()

/** Consecutive activity days. If today is empty, still count a streak that ended yesterday. */
fun streakCount(checkinDates: Set<String>): Int {
    val today = LocalDate.now()
    val start = when {
        checkinDates.contains(today.format(ISO)) -> today
        checkinDates.contains(today.minusDays(1).format(ISO)) -> today.minusDays(1)
        else -> return 0
    }
    var cursor = start
    var n = 0
    while (checkinDates.contains(cursor.format(ISO))) {
        n++
        cursor = cursor.minusDays(1)
    }
    return n
}

fun thisCalendarWeek(): List<LocalDate> {
    val today = LocalDate.now()
    val monday = today.with(DayOfWeek.MONDAY)
    return (0..6).map { monday.plusDays(it.toLong()) }
}

fun LocalDate.shortWeekdayZh(): String =
    dayOfWeek.getDisplayName(TextStyle.NARROW, Locale.CHINA)

fun CourseDay.isFullyComplete(progress: ProgressSnapshot): Boolean =
    completedCount(progress) == itemCount()

fun ProgressSnapshot.completedCourseDayIds(curriculum: WeekCurriculum): Set<Int> =
    curriculum.days.filter { it.isFullyComplete(this) }.map { it.id }.toSet()

fun ProgressSnapshot.nextOpenDay(curriculum: WeekCurriculum): CourseDay? =
    curriculum.days.firstOrNull { !it.isFullyComplete(this) }
