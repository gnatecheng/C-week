package com.py2c.week.ui.home

import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onRoot
import androidx.test.core.app.ApplicationProvider
import com.github.takahirom.roborazzi.captureRoboImage
import com.py2c.week.Py2CApplication
import com.py2c.week.data.AppContainer
import com.py2c.week.data.AppLocale
import com.py2c.week.data.CheckInClock
import com.py2c.week.data.ProgressSnapshot
import com.py2c.week.ui.navigation.LocalContainer
import com.py2c.week.ui.strings.LocalAppLocale
import com.py2c.week.ui.strings.LocalStrings
import com.py2c.week.ui.strings.stringsFor
import com.py2c.week.ui.theme.Py2CTheme
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config
import org.robolectric.annotation.GraphicsMode
import java.time.LocalDate

@RunWith(RobolectricTestRunner::class)
@GraphicsMode(GraphicsMode.Mode.NATIVE)
@Config(sdk = [33], application = Py2CApplication::class, qualifiers = "w411dp-h891dp-420dpi")
class HomeScreenRoborazziTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val fixedToday = LocalDate.of(2026, 9, 28)

    @Before
    fun fixClock() {
        CheckInClock.fixedToday = fixedToday
    }

    @After
    fun resetClock() {
        CheckInClock.clearFixedToday()
    }

    @Test
    fun homeZhLight() {
        captureHome(
            locale = AppLocale.ZH,
            darkTheme = false,
            streakDays = 1,
            output = "/opt/cursor/artifacts/cweek-home-zh-light-streak1.png",
        )
    }

    @Test
    fun homeZhDark() {
        captureHome(
            locale = AppLocale.ZH,
            darkTheme = true,
            streakDays = 1,
            output = "/opt/cursor/artifacts/cweek-home-zh-dark-streak1.png",
        )
    }

    @Test
    fun homeEnLightStreak1() {
        captureHome(
            locale = AppLocale.EN,
            darkTheme = false,
            streakDays = 1,
            litDays = 1,
            output = "/opt/cursor/artifacts/cweek-home-en-light-streak1.png",
        )
    }

    @Test
    fun homeEnDarkStreak1() {
        captureHome(
            locale = AppLocale.EN,
            darkTheme = true,
            streakDays = 1,
            litDays = 1,
            output = "/opt/cursor/artifacts/cweek-home-en-dark-streak1.png",
        )
    }

    @Test
    fun homeEnLightStreak2() {
        captureHome(
            locale = AppLocale.EN,
            darkTheme = false,
            streakDays = 2,
            litDays = 2,
            output = "/opt/cursor/artifacts/cweek-home-en-light-streak2.png",
        )
    }

    @Test
    fun homeEnDarkStreak2() {
        captureHome(
            locale = AppLocale.EN,
            darkTheme = true,
            streakDays = 2,
            litDays = 2,
            output = "/opt/cursor/artifacts/cweek-home-en-dark-streak2.png",
        )
    }

    private fun captureHome(
        locale: AppLocale,
        darkTheme: Boolean,
        streakDays: Int,
        litDays: Int = streakDays,
        output: String,
    ) {
        val context = ApplicationProvider.getApplicationContext<android.content.Context>()
        val container = AppContainer(context)
        val curriculum = container.curriculumFor(locale)
        val checkins = (0 until streakDays).map { fixedToday.minusDays(it.toLong()).toString() }.toSet()
        val checkedCourseDays = (1..litDays).toSet()
        val progress = ProgressSnapshot(
            checkinDates = checkins,
            checkedCourseDays = checkedCourseDays,
        )
        val strings = stringsFor(locale, context.resources)

        composeRule.setContent {
            Py2CTheme(darkTheme = darkTheme) {
                CompositionLocalProvider(
                    LocalStrings provides strings,
                    LocalAppLocale provides locale,
                    LocalContainer provides container,
                ) {
                    HomeScreen(
                        curriculum = curriculum,
                        progress = progress,
                        onOpenDay = {},
                        onOpenWrongBook = {},
                        onOpenReport = {},
                        onOpenSettings = {},
                        onReset = {},
                    )
                }
            }
        }
        composeRule.waitForIdle()
        composeRule.onRoot().captureRoboImage(output)
    }
}
