package com.py2c.week.ui.strings

import androidx.test.core.app.ApplicationProvider
import com.py2c.week.data.AppLocale
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [33])
class StringsEnPluralsTest {
    private val strings: AppStrings by lazy {
        val resources = ApplicationProvider.getApplicationContext<android.content.Context>().resources
        stringsFor(AppLocale.EN, resources)
    }

    @Test
    fun streakUsesSingularForOne() {
        assertEquals("Streak 1 day", strings.reportStreak(1))
        assertEquals("Streak 1 day · 1 / 7 day lit", strings.calendarStreak(1, 1))
        assertEquals("Streak 1 day · Overall 0% · Share via system sheet", strings.homeReportBody(1, 0))
    }

    @Test
    fun streakUsesPluralForTwo() {
        assertEquals("Streak 2 days", strings.reportStreak(2))
        assertEquals("Streak 2 days · 2 / 7 days lit", strings.calendarStreak(2, 2))
        assertEquals("1 day with activity · Overall 5%", strings.reportCheckins(1, 5))
        assertEquals("2 days with activity · Overall 5%", strings.reportCheckins(2, 5))
    }

    @Test
    fun quantityLabelsUseCorrectGrammar() {
        assertEquals("0 / 1 item", strings.homeDayProgress(0, 1))
        assertEquals("1 / 3 items", strings.homeDayProgress(1, 3))
        assertEquals("Today's quiz · 1 question", strings.dayQuizLabel(1))
        assertEquals("Today's quiz · 5 questions", strings.dayQuizLabel(5))
        assertEquals("Test cases (1 group, not just one golden output)", strings.labTestCasesHeader(1))
        assertEquals("Test cases (3 groups, not just one golden output)", strings.labTestCasesHeader(3))
        assertEquals("Try 1 more time to unlock solution", strings.labRevealAfterAttempts(1))
        assertEquals("Try 2 more times to unlock solution", strings.labRevealAfterAttempts(2))
        assertEquals("Hints (attempt 1)", strings.labHintsTitle(1))
        assertEquals("Hints (attempts 2)", strings.labHintsTitle(2))
        assertEquals("0/1 lesson · 0/1 lab · 0/1 quiz", strings.dayProgressDetail(0, 1, 0, 0))
        assertEquals("1/2 lessons · 1/1 lab · 0/1 quiz", strings.dayProgressDetail(1, 2, 1, 0))
    }
}
