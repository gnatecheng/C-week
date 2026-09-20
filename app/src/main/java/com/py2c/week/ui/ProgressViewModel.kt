package com.py2c.week.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.py2c.week.data.AppContainer
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.data.ProgressStore
import com.py2c.week.data.WeekCurriculum
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

/** Shared progress + curriculum for Compose screens. */
class ProgressViewModel(
    val curriculum: WeekCurriculum,
    private val store: ProgressStore,
) : ViewModel() {
    val progress: StateFlow<ProgressSnapshot> = store.progress.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5_000),
        ProgressSnapshot(),
    )

    fun markLesson(id: String) = viewModelScope.launch { store.markLesson(id) }
    fun markLab(id: String) = viewModelScope.launch { store.markLab(id) }
    fun markQuiz(dayId: Int, score: Int) = viewModelScope.launch { store.markQuiz(dayId, score) }
    fun bumpLabAttempt(id: String) = viewModelScope.launch { store.bumpLabAttempt(id) }
    fun revealSolution(id: String) = viewModelScope.launch { store.revealSolution(id) }
    fun checkInToday() = viewModelScope.launch { store.checkInToday() }
    fun checkInCourseDay(dayId: Int) = viewModelScope.launch { store.checkInCourseDay(dayId) }
    fun syncCompletedCourseDays(ids: Set<Int>) = viewModelScope.launch { store.syncCompletedCourseDays(ids) }
    fun resetAll() = viewModelScope.launch { store.resetAll() }

    companion object {
        fun factory(container: AppContainer): ViewModelProvider.Factory =
            object : ViewModelProvider.Factory {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return ProgressViewModel(container.curriculum, container.progressStore) as T
                }
            }
    }
}
