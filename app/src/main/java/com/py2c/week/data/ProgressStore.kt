package com.py2c.week.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

private val Context.progressDataStore: DataStore<Preferences> by preferencesDataStore(name = "py2c_progress")

data class ProgressSnapshot(
    val completedLessons: Set<String> = emptySet(),
    val completedLabs: Set<String> = emptySet(),
    val completedQuizzes: Set<Int> = emptySet(),
    val quizScores: Map<Int, Int> = emptyMap(),
    val labAttempts: Map<String, Int> = emptyMap(),
    val revealedSolutions: Set<String> = emptySet(),
    val checkinDates: Set<String> = emptySet(),
    val checkedCourseDays: Set<Int> = emptySet(),
) {
    fun overallPercent(curriculum: WeekCurriculum): Int {
        val total = curriculum.days.sumOf { it.itemCount() }
        if (total == 0) return 0
        val done = curriculum.days.sumOf { it.completedCount(this) }
        return ((done * 100f) / total).toInt()
    }

    val streak: Int get() = streakCount(checkinDates)
}

class ProgressStore(private val context: Context) {
    private val lessonsKey = stringSetPreferencesKey("lessons")
    private val labsKey = stringSetPreferencesKey("labs")
    private val quizzesKey = stringSetPreferencesKey("quizzes")
    private val revealedKey = stringSetPreferencesKey("revealed")
    private val checkinDatesKey = stringSetPreferencesKey("checkin_dates")
    private val checkedCourseDaysKey = stringSetPreferencesKey("checked_course_days")

    val progress: Flow<ProgressSnapshot> = context.progressDataStore.data.map { prefs ->
        val quizzes = prefs[quizzesKey].orEmpty().mapNotNull { it.toIntOrNull() }.toSet()
        val quizScores = quizzes.associateWith { day ->
            prefs[intPreferencesKey("quiz_score_$day")] ?: 0
        }
        val attempts = prefs.asMap().mapNotNull { (key, value) ->
            val name = key.name
            if (name.startsWith("lab_attempts_") && value is Int) {
                name.removePrefix("lab_attempts_") to value
            } else {
                null
            }
        }.toMap()
        ProgressSnapshot(
            completedLessons = prefs[lessonsKey].orEmpty(),
            completedLabs = prefs[labsKey].orEmpty(),
            completedQuizzes = quizzes,
            quizScores = quizScores,
            labAttempts = attempts,
            revealedSolutions = prefs[revealedKey].orEmpty(),
            checkinDates = prefs[checkinDatesKey].orEmpty(),
            checkedCourseDays = prefs[checkedCourseDaysKey].orEmpty().mapNotNull { it.toIntOrNull() }.toSet(),
        )
    }

    suspend fun markLesson(id: String) {
        context.progressDataStore.edit {
            it[lessonsKey] = it[lessonsKey].orEmpty() + id
            it.stampToday()
        }
    }

    suspend fun markLab(id: String) {
        context.progressDataStore.edit {
            it[labsKey] = it[labsKey].orEmpty() + id
            it.stampToday()
        }
    }

    suspend fun markQuiz(dayId: Int, score: Int) {
        context.progressDataStore.edit { prefs ->
            prefs[quizzesKey] = prefs[quizzesKey].orEmpty() + dayId.toString()
            prefs[intPreferencesKey("quiz_score_$dayId")] = score
            prefs.stampToday()
        }
    }

    suspend fun bumpLabAttempt(labId: String): Int {
        val key = intPreferencesKey("lab_attempts_$labId")
        var next = 1
        context.progressDataStore.edit { prefs ->
            next = (prefs[key] ?: 0) + 1
            prefs[key] = next
            prefs.stampToday()
        }
        return next
    }

    suspend fun revealSolution(labId: String) {
        context.progressDataStore.edit { it[revealedKey] = it[revealedKey].orEmpty() + labId }
    }

    /** Stamp today's calendar date as a learning check-in. */
    suspend fun checkInToday() {
        context.progressDataStore.edit { it.stampToday() }
    }

    /** Light up a course day (Day 1–7) on the week board. */
    suspend fun checkInCourseDay(dayId: Int) {
        context.progressDataStore.edit { prefs ->
            prefs.stampToday()
            prefs[checkedCourseDaysKey] = prefs[checkedCourseDaysKey].orEmpty() + dayId.toString()
        }
    }

    /** Keep persisted course-day stamps in sync with fully completed days. */
    suspend fun syncCompletedCourseDays(completedDayIds: Set<Int>) {
        context.progressDataStore.edit { prefs ->
            val current = prefs[checkedCourseDaysKey].orEmpty()
            val merged = current + completedDayIds.map { it.toString() }
            if (merged != current) {
                prefs[checkedCourseDaysKey] = merged
            }
        }
    }

    suspend fun resetAll() {
        context.progressDataStore.edit { it.clear() }
    }

    private fun MutablePreferences.stampToday() {
        this[checkinDatesKey] = this[checkinDatesKey].orEmpty() + todayIso()
    }
}
