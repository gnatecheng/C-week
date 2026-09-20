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
    val wrongItems: List<WrongItem> = emptyList(),
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
    private val wrongItemsKey = stringSetPreferencesKey("wrong_items")

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
            wrongItems = prefs[wrongItemsKey].orEmpty().mapNotNull(::parseWrongItem)
                .sortedWith(compareBy({ it.resolved }, { it.dayId }, { -it.updatedAtMs })),
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

    suspend fun recordQuizResults(day: CourseDay, answers: Map<String, Int>) {
        val now = System.currentTimeMillis()
        context.progressDataStore.edit { prefs ->
            val current = prefs[wrongItemsKey].orEmpty().mapNotNull(::parseWrongItem).toMutableList()
            answers.forEach { (qid, selected) ->
                val q = day.quiz.find { it.id == qid } ?: return@forEach
                val key = "${WrongSource.QUIZ.name}:$qid"
                if (selected == q.correctIndex) {
                    val idx = current.indexOfFirst { it.key == key }
                    if (idx >= 0) {
                        current[idx] = current[idx].copy(resolved = true, updatedAtMs = now)
                    }
                } else {
                    current.removeAll { it.key == key }
                    current.add(
                        WrongItem(
                            source = WrongSource.QUIZ,
                            dayId = day.id,
                            questionId = q.id,
                            prompt = q.prompt,
                            userAnswer = q.choices.getOrElse(selected) { selected.toString() },
                            correctAnswer = q.choices.getOrElse(q.correctIndex) { "" },
                            hintCategory = q.hintCategory(),
                            resolved = false,
                            updatedAtMs = now,
                        ),
                    )
                }
            }
            prefs[wrongItemsKey] = current.map { it.serialize() }.toSet()
        }
    }

    suspend fun recordLabResult(dayId: Int, lab: CodeLab, eval: LabEvaluation) {
        val now = System.currentTimeMillis()
        val key = "${WrongSource.LAB.name}:${lab.id}"
        context.progressDataStore.edit { prefs ->
            val current = prefs[wrongItemsKey].orEmpty().mapNotNull(::parseWrongItem).toMutableList()
            if (eval.passed) {
                val idx = current.indexOfFirst { it.key == key }
                if (idx >= 0) {
                    current[idx] = current[idx].copy(resolved = true, updatedAtMs = now)
                }
            } else {
                current.removeAll { it.key == key }
                val summary = buildString {
                    if (eval.totalCases > 0) append("用例 ${eval.passedCases}/${eval.totalCases}")
                    if (eval.totalChecks > 0) {
                        if (isNotEmpty()) append(" · ")
                        append("检查 ${eval.passedChecks}/${eval.totalChecks}")
                    }
                    if (eval.scorePercent > 0) {
                        if (isNotEmpty()) append(" · ")
                        append("得分 ${eval.scorePercent}%")
                    }
                    eval.failedHints.firstOrNull()?.let {
                        if (isNotEmpty()) append(" · ")
                        append(it)
                    }
                }.take(400).ifBlank { "模拟评测未通过" }
                current.add(
                    WrongItem(
                        source = WrongSource.LAB,
                        dayId = dayId,
                        questionId = lab.id,
                        prompt = lab.title,
                        userAnswer = summary,
                        correctAnswer = lab.expectedOutput.trim(),
                        hintCategory = eval.gradeHints.firstOrNull()?.title ?: "实验未通过",
                        resolved = false,
                        updatedAtMs = now,
                    ),
                )
            }
            prefs[wrongItemsKey] = current.map { it.serialize() }.toSet()
        }
    }

    suspend fun resetAll() {
        context.progressDataStore.edit { it.clear() }
    }

    private fun MutablePreferences.stampToday() {
        this[checkinDatesKey] = this[checkinDatesKey].orEmpty() + todayIso()
    }
}
