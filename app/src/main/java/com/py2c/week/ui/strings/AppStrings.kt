package com.py2c.week.ui.strings

import androidx.compose.runtime.Composable
import androidx.compose.runtime.staticCompositionLocalOf
import com.py2c.week.data.AppLocale
import com.py2c.week.data.LanguagePreference
import com.py2c.week.data.ThemePreference
import com.py2c.week.data.WrongSource

interface AppStrings {
    val brandName: String
    val brandSubtitle: String
    val tagline: String

    val navHome: String
    val navLabs: String
    val navWrongs: String
    val navGlossary: String

    val back: String
    val cancel: String
    val clear: String
    val reset: String
    val passed: String
    val notPassed: String
    val complete: String
    val incomplete: String
    val lessonDone: String
    val labPassed: String

    val settingsTitle: String
    val settingsLanguage: String
    val settingsTheme: String
    val settingsAbout: String
    val languageSystem: String
    val languageZh: String
    val languageEn: String
    val themeSystem: String
    val themeLight: String
    val themeDark: String

    val aboutVersion: String
    val aboutLastUpdate: String
    val aboutSource: String
    val aboutOpenRepo: String
    val aboutDescription: String

    fun homeProgressPercent(percent: Int): String
    val homeProgressBlurb: String
    val homeWrongBookTitle: String
    fun homeWrongBookBody(openWrongs: Int): String
    val homeReportTitle: String
    fun homeReportBody(streak: Int, percent: Int): String
    val homeClearProgress: String
    val homeClearProgressTitle: String
    val homeClearProgressMessage: String
    fun homeDayMeta(dayId: Int, minutes: Int): String
    fun homeDayProgress(done: Int, total: Int): String
    fun homeDayContentDescription(dayId: Int, title: String): String
    fun overallProgressDescription(percent: Int): String

    val calendarTitle: String
    fun calendarStreak(streak: Int, litCount: Int): String
    fun calendarNudgeAllDone(): String
    fun calendarNudgeNext(dayId: Int, title: String): String
    val calendarNudgeFinish: String
    val calendarWeekLabel: String
    val calendarCheckInToday: String
    val calendarCheckedInToday: String
    val calendarCheckInHintChecked: String
    val calendarCheckInHintUnchecked: String
    val calendarCellOn: String
    val calendarCellOff: String
    fun calendarDayDone(dayId: Int): String
    fun calendarDayNotDone(dayId: Int): String

    val wrongBookTitle: String
    val wrongBookIntro: String
    fun wrongBookOpenCount(open: Int): String
    fun wrongBookResolvedCount(done: Int): String
    val wrongBookOpenReport: String
    val wrongBookEmptyTitle: String
    val wrongBookEmptyBody: String
    fun wrongBookDayHeader(dayId: Int, title: String): String
    fun wrongBookDayResolved(dayId: Int): String
    val wrongBookHideResolved: String
    fun wrongBookShowResolved(count: Int): String
    fun wrongSourceLabel(source: WrongSource): String
    val wrongResolvedSuffix: String
    val wrongYourAnswer: String
    val wrongCorrectAnswer: String
    val wrongRedoQuiz: String
    val wrongRedoLab: String

    val reportTitle: String
    fun reportStreak(streak: Int): String
    fun reportCheckins(checkinDays: Int, percent: Int): String
    val reportQuizAccuracy: String
    val reportLabsPassed: String
    val reportQuizNotTaken: String
    fun reportQuizSubtitle(correct: Int, asked: Int): String
    fun reportOpenWrongs(count: Int): String
    val reportDayBreakdown: String
    fun reportDayLine(dayId: Int, title: String): String
    fun reportDayDetail(percent: Int, done: Int, total: Int, quizPart: String, labPart: String): String
    val reportQuizNotSubmitted: String
    fun reportQuizScore(score: Int, total: Int): String
    val reportLabDone: String
    val reportLabNotDone: String
    val reportShare: String
    val reportShareHint: String
    val reportShareChooser: String
    fun reportShareSubject(brand: String): String

    val labsListTitle: String
    val labsListIntro: String
    fun labsListDay(dayId: Int, capstone: Boolean): String
    val labsCapstoneBadge: String

    val labTitle: String
    val labCapstoneTitle: String
    fun labDayLabel(dayId: Int): String
    fun labCompileHint(capstone: Boolean): String
    fun labTestCasesHeader(count: Int): String
    val labSimulatedInput: String
    val labExpected: String
    val labEditor: String
    val labRunCheck: String
    fun labHintsTitle(attempts: Int): String
    val labExpectedOutput: String
    val labSolutionShown: String
    val labRevealSolution: String
    fun labRevealAfterAttempts(remaining: Int): String
    val labReferenceSolution: String

    fun labSimPassed(): String
    fun labSimPartial(score: Int): String
    fun labSimFailed(score: Int): String
    fun labCasesProgress(passed: Int, total: Int): String
    fun labChecksProgress(passed: Int, total: Int): String
    val labSimStdout: String
    val labPartialHint: String
    val labFailHint: String
    fun labCasePassed(name: String): String
    fun labCaseFailed(name: String): String
    fun labCaseCompare(expected: String, actual: String): String
    val labNoOutput: String
    fun labCheckPassed(id: String): String
    fun labCheckFailed(id: String, hint: String): String
    fun labGradeHintTitle(title: String): String

    val dijkstraBoardTitle: String
    val dijkstraBoardUnlocked: String
    val dijkstraBoardLocked: String
    fun dijkstraStep(step: Int, total: Int, caption: String): String
    fun dijkstraDistLine(dist: List<String>): String
    val dijkstraPrevStep: String
    val dijkstraPlay: String
    val dijkstraPause: String
    val dijkstraNextStep: String
    val dijkstraReplay: String
    val dijkstraUnlockHint: String
    fun dijkstraCellLabel(node: Int): String

    val dayLessons: String
    fun dayTodayFocus(focus: String): String
    fun dayCompleteBanner(dayId: Int): String
    fun dayIncompleteBanner(dayId: Int): String
    fun dayProgressDetail(lessonsDone: Int, lessonTotal: Int, labDone: Int, quizDone: Int): String
    val dayCapstoneLab: String
    val dayTodayLab: String
    fun dayQuizLabel(count: Int): String
    fun dayQuizSubmitted(score: Int, total: Int): String
    val dayQuizPending: String
    fun dayTitle(dayId: Int): String

    val quizTitle: String
    fun quizRedoTitle(dayId: Int): String
    val quizRedoIntro: String
    fun quizLastScore(score: Int, total: Int): String
    fun quizScoreLine(score: Int, total: Int): String
    val quizResultAllCorrectRedo: String
    val quizResultAllCorrect: String
    val quizResultPartialRedo: String
    val quizResultPartial: String
    val quizSubmitRedo: String
    val quizSubmit: String
    fun quizSubmittedScore(score: Int, total: Int): String
    val quizRetryRedo: String
    val quizRetry: String
    val quizVerdictCorrect: String
    val quizVerdictWrong: String
    val quizCategory: String
    val quizYouChose: String
    val quizWrongReason: String
    val quizCorrect: String
    val quizConceptFallback: String

    fun lessonDayMeta(dayId: Int, minutes: Int): String
    fun lessonCardSubtitle(minutes: Int, summary: String): String
    val lessonVideoMissing: String
    val lessonViewSchematic: String
    val lessonMarkDoneReturn: String
    val lessonMarkDone: String
    val demoScreenTitle: String
    fun vscodeDemoContentDescription(title: String): String
    val vscodeSpeedLabel: String
    fun vscodeStepProgress(active: Int, total: Int): String
    val vscodeExplainingNow: String

    val glossaryTitle: String
    val glossaryIntro: String
    val glossaryAll: String

    fun memoryVizCaption(variant: String): String
    val memoryVizLegendVar: String
    val memoryVizLegendPtr: String
    val memoryVizLegendData: String

    fun walkthroughStepLabel(index: Int, total: Int): String
    val walkthroughNewFile: String
    val walkthroughFocus: String

    val errorContainerMissing: String
}

val LocalStrings = staticCompositionLocalOf<AppStrings> { StringsZh }

val LocalAppLocale = staticCompositionLocalOf { AppLocale.ZH }

fun stringsFor(locale: AppLocale): AppStrings = when (locale) {
    AppLocale.ZH -> StringsZh
    AppLocale.EN -> StringsEn
}

fun languagePreferenceLabel(strings: AppStrings, pref: LanguagePreference): String = when (pref) {
    LanguagePreference.SYSTEM -> strings.languageSystem
    LanguagePreference.ZH -> strings.languageZh
    LanguagePreference.EN -> strings.languageEn
}

fun themePreferenceLabel(strings: AppStrings, pref: ThemePreference): String = when (pref) {
    ThemePreference.SYSTEM -> strings.themeSystem
    ThemePreference.LIGHT -> strings.themeLight
    ThemePreference.DARK -> strings.themeDark
}

@Composable
fun rememberStrings(): AppStrings = LocalStrings.current
