package com.py2c.week.data

/** One persisted mistake from a quiz question or a lab run. */
enum class WrongSource { QUIZ, LAB }

data class WrongItem(
    val source: WrongSource,
    val dayId: Int,
    val questionId: String,
    val prompt: String,
    val userAnswer: String,
    val correctAnswer: String,
    val hintCategory: String,
    val resolved: Boolean = false,
    val updatedAtMs: Long = 0L,
) {
    val key: String get() = "${source.name}:$questionId"

    val sourceLabel: String get() = if (source == WrongSource.QUIZ) "测验" else "实验"
}

private const val FS = "\u001e"

internal fun String.wrongField(): String = replace(FS, " ").replace("\r\n", "\n")

fun WrongItem.serialize(): String = listOf(
    source.name,
    dayId.toString(),
    questionId.wrongField(),
    prompt.wrongField(),
    userAnswer.wrongField(),
    correctAnswer.wrongField(),
    hintCategory.wrongField(),
    if (resolved) "1" else "0",
    updatedAtMs.toString(),
).joinToString(FS)

fun parseWrongItem(raw: String): WrongItem? {
    val p = raw.split(FS)
    if (p.size < 9) return null
    val source = WrongSource.entries.find { it.name == p[0] } ?: return null
    val dayId = p[1].toIntOrNull() ?: return null
    return WrongItem(
        source = source,
        dayId = dayId,
        questionId = p[2],
        prompt = p[3],
        userAnswer = p[4],
        correctAnswer = p[5],
        hintCategory = p[6],
        resolved = p[7] == "1",
        updatedAtMs = p[8].toLongOrNull() ?: 0L,
    )
}

fun List<WrongItem>.groupedByDay(): Map<Int, List<WrongItem>> =
    groupBy { it.dayId }.toSortedMap()
