package com.py2c.week.data

/** One week of beginner C lessons, labs, quizzes, and VS Code demos. */
data class WeekCurriculum(
    val brand: String = "C一周通",
    val brandEn: String = "C Week",
    val tagline: String = "7 天，从零基础走到能用 C 写出 Dijkstra 最短路",
    val days: List<CourseDay>,
    val glossary: List<GlossaryTerm>,
    val demos: Map<String, VsCodeDemo>,
    val videos: Map<String, VideoDemo> = emptyMap(),
)

data class CourseDay(
    val id: Int,
    val title: String,
    val subtitle: String,
    val outcome: String,
    val minutes: Int,
    val todayFocus: String,
    val lessons: List<Lesson>,
    val lab: CodeLab,
    val quiz: List<QuizQuestion>,
)

data class Lesson(
    val id: String,
    val title: String,
    val minutes: Int,
    val summary: String,
    val blocks: List<ContentBlock>,
)

sealed class ContentBlock {
    data class Heading(val text: String) : ContentBlock()
    data class Paragraph(val text: String) : ContentBlock()
    data class Callout(val kind: CalloutKind, val title: String, val text: String) : ContentBlock()
    data class Example(
        val title: String,
        val source: String,
        val note: String,
        val language: String = "c",
    ) : ContentBlock()
    data class Code(val language: String, val source: String, val caption: String? = null) : ContentBlock()
    data class Bullets(val items: List<String>) : ContentBlock()
    data class VsCode(val demoId: String) : ContentBlock()
    data class MemoryViz(val variant: String) : ContentBlock()
}

enum class CalloutKind { TIP, WARN, KEY }

data class QuizQuestion(
    val id: String,
    val prompt: String,
    val choices: List<String>,
    val correctIndex: Int,
    val explanation: String,
)

data class CodeLab(
    val id: String,
    val title: String,
    val brief: String,
    val task: String,
    val starterCode: String,
    val solutionCode: String,
    val expectedOutput: String,
    val checks: List<LabCheck>,
    val hints: List<String>,
    val testCases: List<LabTestCase> = emptyList(),
    val attemptsBeforeReveal: Int = 3,
    val isCapstone: Boolean = false,
)

data class LabTestCase(
    val name: String,
    val inputDesc: String,
    val expected: String,
    /** Simulated stdin or function argument the offline runner feeds this case. */
    val input: String = "",
    val extraChecks: List<LabCheck> = emptyList(),
)

data class LabCheck(
    val id: String,
    val failHint: String,
    val rule: CheckRule,
)

sealed class CheckRule {
    data class Contains(val needle: String, val ignoreCase: Boolean = false) : CheckRule()
    data class ContainsRegex(val pattern: String) : CheckRule()
    data class NotContains(val needle: String, val ignoreCase: Boolean = true) : CheckRule()
    data class NotContainsRegex(val pattern: String) : CheckRule()
}

data class GlossaryTerm(
    val id: String,
    val termC: String,
    val termEn: String,
    val category: String,
    val summary: String,
    val detail: String,
)

data class VsCodeDemo(
    val id: String,
    val title: String,
    val subtitle: String,
    val steps: List<VsCodeStep>,
)

data class VideoCaption(
    val atMs: Long,
    val text: String,
)

data class VideoDemo(
    val id: String,
    val title: String,
    val subtitle: String,
    val file: String,
    val captions: List<VideoCaption>,
)

data class VsCodeStep(
    val caption: String,
    val durationMs: Int = 2400,
    val activity: VsActivity = VsActivity.EXPLORER,
    val explorerFiles: List<String> = listOf("c-week"),
    val selectedFile: String? = null,
    val tabName: String = "",
    val editorLines: List<String> = emptyList(),
    val cursorLine: Int = 0,
    val highlightLine: Int? = null,
    val breakpointLine: Int? = null,
    val terminalLines: List<String> = emptyList(),
    val pointer: PointerTarget = PointerTarget.EDITOR,
    val statusText: String = "gcc 就绪",
    val extensionQuery: String? = null,
    val menuLabel: String? = null,
    val click: Boolean = true,
    val typing: String? = null,
)

enum class VsActivity { EXPLORER, EXTENSIONS, RUN, DEBUG, SEARCH }

enum class PointerTarget {
    ACTIVITY_EXPLORER,
    ACTIVITY_EXTENSIONS,
    ACTIVITY_RUN,
    ACTIVITY_DEBUG,
    EXPLORER_NEW,
    EXPLORER_FILE,
    EDITOR,
    GUTTER,
    TERMINAL,
    MENU,
    STATUS,
    EXTENSION_INSTALL,
    RUN_BUTTON,
}

fun CheckRule.passes(code: String): Boolean = when (this) {
    is CheckRule.Contains ->
        if (ignoreCase) code.contains(needle, ignoreCase = true) else code.contains(needle)
    is CheckRule.ContainsRegex -> Regex(pattern).containsMatchIn(code)
    is CheckRule.NotContains ->
        if (ignoreCase) !code.contains(needle, ignoreCase = true) else !code.contains(needle)
    is CheckRule.NotContainsRegex -> !Regex(pattern).containsMatchIn(code)
}

data class CaseOutcome(
    val name: String,
    val inputDesc: String,
    val expected: String,
    val actual: String?,
    val passed: Boolean,
    val hint: String? = null,
)

data class LabEvaluation(
    val passed: Boolean,
    val failedHints: List<String>,
    val simulatedOutput: String?,
    val syntaxNotes: List<String>,
    val gradeHints: List<GradeHint> = emptyList(),
    val checkOutcomes: List<CheckOutcome> = emptyList(),
    val passedChecks: Int = 0,
    val totalChecks: Int = 0,
    val caseOutcomes: List<CaseOutcome> = emptyList(),
    val passedCases: Int = 0,
    val totalCases: Int = 0,
    /** 0–100 partial credit from test cases and structural checks. */
    val scorePercent: Int = 0,
) {
    val hasPartialCredit: Boolean get() = !passed && scorePercent in 1..99
}

fun CourseDay.itemCount(): Int = lessons.size + 2 /* lab + quiz */

fun CourseDay.completedCount(progress: ProgressSnapshot): Int {
    val lessonsDone = lessons.count { progress.completedLessons.contains(it.id) }
    val labDone = if (progress.completedLabs.contains(lab.id)) 1 else 0
    val quizDone = if (progress.completedQuizzes.contains(id)) 1 else 0
    return lessonsDone + labDone + quizDone
}
