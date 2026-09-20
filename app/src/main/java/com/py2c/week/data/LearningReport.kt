package com.py2c.week.data

data class DayReport(
    val dayId: Int,
    val title: String,
    val percent: Int,
    val done: Int,
    val total: Int,
    val quizScore: Int?,
    val quizTotal: Int,
    val labDone: Boolean,
)

data class LearningReport(
    val streak: Int,
    val checkinDays: Int,
    val overallPercent: Int,
    val days: List<DayReport>,
    val quizCorrect: Int,
    val quizAsked: Int,
    val quizAccuracyPercent: Int,
    val labsPassed: Int,
    val labsTotal: Int,
    val openWrongs: Int,
) {
    fun shareText(brand: String = "C一周通"): String = buildString {
        appendLine("$brand · 学习报告")
        appendLine("连续打卡：$streak 天（累计 ${checkinDays} 天有学习记录）")
        appendLine("总体进度：$overallPercent%")
        appendLine("实验通过：$labsPassed / $labsTotal")
        appendLine(
            if (quizAsked == 0) "测验正确率：还没交过卷"
            else "测验正确率：$quizAccuracyPercent%（$quizCorrect / $quizAsked）",
        )
        appendLine("待订正错题：$openWrongs")
        appendLine()
        appendLine("分天完成度：")
        days.forEach { d ->
            val quiz = d.quizScore?.let { " · 测验 $it/${d.quizTotal}" } ?: " · 测验未交"
            val lab = if (d.labDone) " · 实验过" else " · 实验未过"
            appendLine("  第${d.dayId}天 ${d.title}  ${d.percent}%（${d.done}/${d.total}）$quiz$lab")
        }
        appendLine()
        appendLine("离线自学，手机上模拟评测；真正的 gcc 请在电脑 VS Code 里跑。")
    }
}

fun ProgressSnapshot.learningReport(curriculum: WeekCurriculum): LearningReport {
    val days = curriculum.days.map { day ->
        val done = day.completedCount(this)
        val total = day.itemCount()
        DayReport(
            dayId = day.id,
            title = day.title,
            percent = if (total == 0) 0 else ((done * 100f) / total).toInt(),
            done = done,
            total = total,
            quizScore = quizScores[day.id],
            quizTotal = day.quiz.size,
            labDone = completedLabs.contains(day.lab.id),
        )
    }
    var quizCorrect = 0
    var quizAsked = 0
    curriculum.days.forEach { day ->
        val score = quizScores[day.id]
        if (score != null && completedQuizzes.contains(day.id)) {
            quizCorrect += score
            quizAsked += day.quiz.size
        }
    }
    val accuracy = if (quizAsked == 0) 0 else ((quizCorrect * 100f) / quizAsked).toInt()
    return LearningReport(
        streak = streak,
        checkinDays = checkinDates.size,
        overallPercent = overallPercent(curriculum),
        days = days,
        quizCorrect = quizCorrect,
        quizAsked = quizAsked,
        quizAccuracyPercent = accuracy,
        labsPassed = completedLabs.size,
        labsTotal = curriculum.days.size,
        openWrongs = wrongItems.count { !it.resolved },
    )
}
