package com.py2c.week.data

/** Locale-neutral lab wrong-book payload (stored in [WrongItem.userAnswer]). */
internal const val LAB_SUMMARY_PREFIX = "labstat:"

fun buildLabSummary(eval: LabEvaluation, hintKind: HintKind?): String = buildString {
    append(LAB_SUMMARY_PREFIX)
    append("c=${eval.passedCases}/${eval.totalCases}")
    append("|k=${eval.passedChecks}/${eval.totalChecks}")
    append("|s=${eval.scorePercent}")
    hintKind?.let { append("|h=${it.name}") }
}

fun parseLabSummary(raw: String): LabSummaryParts? {
    if (!raw.startsWith(LAB_SUMMARY_PREFIX)) return null
    val body = raw.removePrefix(LAB_SUMMARY_PREFIX)
    val map = body.split('|').mapNotNull { part ->
        val idx = part.indexOf('=')
        if (idx <= 0) null else part.substring(0, idx) to part.substring(idx + 1)
    }.toMap()
    val cases = map["c"]?.split('/')?.mapNotNull { it.toIntOrNull() }
    val checks = map["k"]?.split('/')?.mapNotNull { it.toIntOrNull() }
    val score = map["s"]?.toIntOrNull()
    val hint = map["h"]?.let { name -> HintKind.entries.find { it.name == name } }
    return LabSummaryParts(
        passedCases = cases?.getOrNull(0),
        totalCases = cases?.getOrNull(1),
        passedChecks = checks?.getOrNull(0),
        totalChecks = checks?.getOrNull(1),
        scorePercent = score,
        hintKind = hint,
    )
}

data class LabSummaryParts(
    val passedCases: Int?,
    val totalCases: Int?,
    val passedChecks: Int?,
    val totalChecks: Int?,
    val scorePercent: Int?,
    val hintKind: HintKind?,
)

data class WrongItemView(
    val prompt: String,
    val userAnswer: String,
    val correctAnswer: String,
    val hintCategory: String,
)

fun WrongItem.toView(curriculum: WeekCurriculum, locale: AppLocale): WrongItemView {
    return when (source) {
        WrongSource.QUIZ -> {
            val q = curriculum.days.firstOrNull { it.id == dayId }
                ?.quiz?.find { it.id == questionId }
            if (q != null) {
                val selected = userChoiceIndex?.takeIf { it >= 0 } ?: userAnswer.toIntOrNull()
                val userText = selected?.let { q.choices.getOrElse(it) { userAnswer } } ?: userAnswer
                WrongItemView(
                    prompt = q.prompt,
                    userAnswer = userText,
                    correctAnswer = q.choices.getOrElse(q.correctIndex) { correctAnswer },
                    hintCategory = q.hintCategory(locale),
                )
            } else {
                WrongItemView(prompt, userAnswer, correctAnswer, hintCategory)
            }
        }
        WrongSource.LAB -> {
            val lab = curriculum.days.firstOrNull { it.id == dayId }?.lab
                ?.takeIf { it.id == questionId }
            val summary = parseLabSummary(userAnswer)
            val userText = if (summary != null) {
                formatLabSummary(summary, locale)
            } else {
                userAnswer
            }
            val category = summary?.hintKind?.title(locale)
                ?: labWrongCategory(hintCategory, locale)
            WrongItemView(
                prompt = lab?.title ?: prompt,
                userAnswer = userText,
                correctAnswer = lab?.expectedOutput?.trim() ?: correctAnswer,
                hintCategory = category,
            )
        }
    }
}

private fun formatLabSummary(parts: LabSummaryParts, locale: AppLocale): String = buildString {
    if (parts.totalCases != null && parts.passedCases != null) {
        append(if (locale == AppLocale.EN) "Cases " else "用例 ")
        append("${parts.passedCases}/${parts.totalCases}")
    }
    if (parts.totalChecks != null && parts.passedChecks != null && parts.totalChecks > 0) {
        if (isNotEmpty()) append(if (locale == AppLocale.EN) " · " else " · ")
        append(if (locale == AppLocale.EN) "Checks " else "检查 ")
        append("${parts.passedChecks}/${parts.totalChecks}")
    }
    parts.scorePercent?.takeIf { it > 0 }?.let {
        if (isNotEmpty()) append(if (locale == AppLocale.EN) " · " else " · ")
        append(if (locale == AppLocale.EN) "Score $it%" else "得分 $it%")
    }
}.ifBlank {
    if (locale == AppLocale.EN) "Simulation failed" else "模拟评测未通过"
}

private fun labWrongCategory(stored: String, locale: AppLocale): String {
    HintKind.entries.find { it.name == stored }?.let { return it.title(locale) }
    val kind = HintKind.entries.find { it.title(AppLocale.ZH) == stored || it.title(AppLocale.EN) == stored }
    if (kind != null) return kind.title(locale)
    if (stored == "LAB_FAIL") {
        return if (locale == AppLocale.EN) "Lab not passed" else "实验未通过"
    }
    return stored
}
