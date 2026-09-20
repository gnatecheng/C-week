package com.py2c.week.data

/** Categorized, student-facing reason a lab check failed. */
enum class HintKind {
    TODO,
    INCLUDE,
    PYTHON,
    OFF_BY_ONE,
    POINTER,
    FORMULA,
    BFS_VS_DIJKSTRA,
    STUB,
    NEWLINE,
    RELAX,
    QUEUE,
    CHECK,
}

fun HintKind.title(): String = when (this) {
    HintKind.TODO -> "还留着 TODO"
    HintKind.INCLUDE -> "缺头文件"
    HintKind.PYTHON -> "写成了别的语言"
    HintKind.OFF_BY_ONE -> "差一错误（off-by-one）"
    HintKind.POINTER -> "指针用法不对"
    HintKind.FORMULA -> "公式或运算顺序不对"
    HintKind.BFS_VS_DIJKSTRA -> "和 BFS 搞混了"
    HintKind.STUB -> "函数还是空壳"
    HintKind.NEWLINE -> "少了换行"
    HintKind.RELAX -> "松弛条件"
    HintKind.QUEUE -> "忘记入队"
    HintKind.CHECK -> "未通过的检查"
}

data class GradeHint(
    val kind: HintKind,
    val title: String,
    val detail: String,
) {
    constructor(kind: HintKind, detail: String) : this(kind, kind.title(), detail)
}

data class CheckOutcome(
    val id: String,
    val passed: Boolean,
    val failHint: String,
)

/**
 * Simulated auto-grader for in-app C labs.
 * Does not compile; it checks required shapes plus common Chinese-explained mistakes.
 */
fun gradeLab(code: String, lab: CodeLab): LabEvaluation {
    val extras = extraHints(code, lab)
    val outcomes = lab.checks.map { check ->
        CheckOutcome(check.id, check.rule.passes(code), check.failHint)
    }
    val failedChecks = outcomes.filter { !it.passed }
    val checkHints = failedChecks.map { outcome ->
        GradeHint(kindForCheck(lab, outcome.id), hintTitleForCheck(lab, outcome.id), outcome.failHint)
    }
    val syntaxNotes = extras.filter { it.kind == HintKind.PYTHON }.map { it.detail }
    val stillTodo = code.contains("TODO")
    val passed = failedChecks.isEmpty() && extras.isEmpty() && !stillTodo
    val allHints = buildList {
        addAll(extras)
        addAll(checkHints)
        if (stillTodo && extras.none { it.kind == HintKind.TODO }) {
            add(GradeHint(HintKind.TODO, "还留着 TODO：先把标记处补全再检查，模拟器不会替你填空。"))
        }
    }.distinctBy { it.kind to it.detail }
    return LabEvaluation(
        passed = passed,
        failedHints = allHints.map { it.detail },
        simulatedOutput = if (passed) lab.expectedOutput else null,
        syntaxNotes = syntaxNotes,
        gradeHints = allHints,
        checkOutcomes = outcomes,
        passedChecks = outcomes.count { it.passed },
        totalChecks = outcomes.size,
    )
}

fun evaluateLab(code: String, lab: CodeLab): LabEvaluation = gradeLab(code, lab)

private fun kindForCheck(lab: CodeLab, checkId: String): HintKind = when {
    checkId == "todo" -> HintKind.TODO
    checkId == "inc" || checkId == "include" -> HintKind.INCLUDE
    checkId == "nl" -> HintKind.NEWLINE
    checkId == "nobfs" || checkId == "relax" && lab.id == "lab-d7" -> HintKind.BFS_VS_DIJKSTRA
    checkId == "cmp" || checkId == "assign" || checkId == "relax" -> HintKind.RELAX
    checkId == "enq" -> HintKind.QUEUE
    checkId == "scanf" || checkId == "sig" && lab.id == "lab-d4" -> HintKind.POINTER
    checkId == "formula" || checkId == "div" -> HintKind.FORMULA
    checkId == "lt2" || checkId == "len" -> HintKind.OFF_BY_ONE
    else -> HintKind.CHECK
}

private fun hintTitleForCheck(lab: CodeLab, checkId: String): String =
    kindForCheck(lab, checkId).title()

private fun extraHints(code: String, lab: CodeLab): List<GradeHint> = buildList {
    if (code.contains("TODO")) {
        add(GradeHint(HintKind.TODO, "代码里还有 TODO 注释。模拟评测把未填空当成失败——删掉标记并写出真正的语句。"))
    }
    if (Regex("""\bpass\b""").containsMatchIn(code) && !code.contains("password")) {
        add(GradeHint(HintKind.PYTHON, "看到了 pass：那是别的语言的空语句。C 里空函数体是 { }，不能写 pass。"))
    }
    if (Regex("""\bprint\s*\(""").containsMatchIn(code) && !code.contains("printf")) {
        add(GradeHint(HintKind.PYTHON, "输出请用 printf(\"...\\n\")，并 #include <stdio.h>。C 没有 print()。"))
    }
    if (code.contains("def ") || Regex("""\bimport\s+""").containsMatchIn(code)) {
        add(GradeHint(HintKind.PYTHON, "C 用函数声明/定义和 #include，不要写 def 或 import。"))
    }
    if (Regex("""for\s+\w+\s+in\s+""").containsMatchIn(code)) {
        add(GradeHint(HintKind.PYTHON, "C 的 for 典型写法：for (int i = 0; i < n; i++)，没有 for x in xs。"))
    }
    if (code.contains("None") || Regex("""\bTrue\b""").containsMatchIn(code) || Regex("""\bFalse\b""").containsMatchIn(code)) {
        add(GradeHint(HintKind.PYTHON, "C 用 NULL / 1 / 0（或 stdbool.h 的 true/false），不要写 True/False/None。"))
    }
    if (Regex("""\brange\s*\(""").containsMatchIn(code)) {
        add(GradeHint(HintKind.PYTHON, "C 没有 range()。用 for (int i = 0; i < n; i++) 数格子。"))
    }

    val usesIo = code.contains("printf") || code.contains("scanf") || code.contains("puts")
    val hasStdio = Regex("""#include\s*<stdio\.h>""").containsMatchIn(code)
    if (usesIo && !hasStdio) {
        add(GradeHint(HintKind.INCLUDE, "用了 printf/scanf 却没有 #include <stdio.h>。真实 gcc 往往会报隐式声明，手机模拟器也按缺头文件判错。"))
    }

    if (Regex("""scanf\s*\(\s*"[^"]*"\s*,\s*[A-Za-z_]\w*\s*\)""").containsMatchIn(code)) {
        add(GradeHint(HintKind.POINTER, "scanf 的参数必须是地址。写成 scanf(\"%d\", n) 会把 n 的值当成指针乱写内存。应传 &n。"))
    }

    when (lab.id) {
        "lab-d1" -> addAll(hintsDay1(code))
        "lab-d2" -> addAll(hintsDay2(code))
        "lab-d3" -> addAll(hintsDay3(code))
        "lab-d4" -> addAll(hintsDay4(code))
        "lab-d5" -> addAll(hintsDay5(code))
        "lab-d6" -> addAll(hintsDay6(code))
        "lab-d7" -> addAll(hintsDay7(code))
    }
}

private fun hintsDay1(code: String): List<GradeHint> = buildList {
    if (code.contains("printf") && code.contains("Hello, Ada") && !code.contains("\\n")) {
        add(GradeHint(HintKind.NEWLINE, "格式串里要写 \\n，否则「Hello, Ada」后面没有换行，和黄金输出对不上。"))
    }
    if (code.contains("print(") && !code.contains("printf")) {
        add(GradeHint(HintKind.PYTHON, "这是 C 实验：用 printf(\"Hello, Ada\\n\"); 而不是 print。"))
    }
}

private fun hintsDay2(code: String): List<GradeHint> = buildList {
    if (Regex("""/\s*5[\s\n]*\*[\s\n]*9""").containsMatchIn(code)) {
        add(GradeHint(HintKind.FORMULA, "先 /5 再 *9 会先做整数截断。应写成 c * 9 / 5 + 32，让乘法走在除法前面。"))
    }
    if (Regex("""\*\s*9""").containsMatchIn(code) && Regex("""/\s*5""").containsMatchIn(code) &&
        !Regex("""\+\s*32""").containsMatchIn(code)
    ) {
        add(GradeHint(HintKind.FORMULA, "华氏度还要 + 32。只乘 9/5 会得到 180 而不是 212（以 100℃ 为例）。"))
    }
    if (Regex("""9\s*/\s*5""").containsMatchIn(code) && !Regex("""c\s*\*\s*9|9\s*\*\s*c""").containsMatchIn(code)) {
        add(GradeHint(HintKind.FORMULA, "9/5 在整数里等于 1，整段公式会变成 c*1+32。请写 c * 9 / 5 + 32。"))
    }
    if (code.contains("&c").not() && code.contains("scanf")) {
        add(GradeHint(HintKind.POINTER, "scanf(\"%d\", &c) 的 & 不能省，否则没有把「格子的门牌号」交给 scanf。"))
    }
}

private fun hintsDay3(code: String): List<GradeHint> = buildList {
    val body = functionBody(code, "is_prime")
    if (body != null && Regex("""return\s+0\s*;""").containsMatchIn(body) &&
        !Regex("""return\s+1\s*;""").containsMatchIn(body) &&
        !body.contains("%")
    ) {
        add(GradeHint(HintKind.STUB, "is_prime 现在几乎总是返回 0。需要：n<2 返回 0，试除发现整除返回 0，否则返回 1。"))
    }
    if (Regex("""for\s*\([^;]*;\s*i\s*<=\s*n\s*;""").containsMatchIn(code) &&
        !Regex("""i\s*\*\s*i\s*<=\s*n""").containsMatchIn(code)
    ) {
        add(GradeHint(HintKind.OFF_BY_ONE, "试除写成 i <= n 会把 n 自己除进去，任何 n>1 都会被判成合数。用 i * i <= n（或 i <= n / i），从 2 起跳。"))
    }
}

private fun hintsDay4(code: String): List<GradeHint> = buildList {
    val body = functionBody(code, "reverse") ?: code
    val hasLoop = Regex("""(while|for)\s*\(""").containsMatchIn(body)
    if (!hasLoop) {
        add(GradeHint(HintKind.STUB, "reverse 还是空的。用双指针 i=0、j=strlen(s)-1，交换 s[i] 与 s[j] 直到 i>=j。"))
    }
    if (Regex("""strlen\s*\(\s*s\s*\)""").containsMatchIn(code) &&
        !Regex("""strlen\s*\(\s*s\s*\)\s*-\s*1""").containsMatchIn(code)
    ) {
        add(GradeHint(HintKind.OFF_BY_ONE, "右指针应是 strlen(s) - 1。j 写成 strlen(s) 会踩到 '\\0' 后面，轻则少反一个字符，重则越界。"))
    }
    if (hasLoop && !Regex("""char\s+\w+|tmp|t\s*=""").containsMatchIn(body) &&
        !Regex("""s\s*\[\s*\w+\s*\]\s*=""").containsMatchIn(body)
    ) {
        add(GradeHint(HintKind.POINTER, "交换两个格子需要临时变量：char t = s[i]; s[i] = s[j]; s[j] = t;。只移动指针不写回，原串不会反转。"))
    }
}

private fun hintsDay5(code: String): List<GradeHint> = buildList {
    if (Regex("""int\s+best\s*=\s*0\s*;""").containsMatchIn(code) &&
        !Regex("""a\s*\[\s*\w+\s*\]\s*\.\s*score""").containsMatchIn(code)
    ) {
        add(GradeHint(HintKind.STUB, "best 一直停在 0，会打印 Ada 而不是最高分 Ben。请用循环比较 a[i].score。"))
    }
    if (code.contains("->score") && !code.contains(".score")) {
        add(GradeHint(HintKind.POINTER, "a 是结构体数组不是指针。字段用 a[i].score；只有 struct Student *p 才写 p->score。"))
    }
    if (Regex("""for\s*\([^;]*;\s*i\s*<=\s*3\s*;""").containsMatchIn(code)) {
        add(GradeHint(HintKind.OFF_BY_ONE, "数组只有 a[0..2]。写成 i <= 3 会读 a[3]，越界。条件用 i < 3。"))
    }
}

private fun hintsDay6(code: String): List<GradeHint> = buildList {
    if (Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*w""").containsMatchIn(code) ||
        Regex("""dist\s*\[\s*v\s*\]\s*>\s*dist\s*\[\s*u\s*\]\s*\+\s*w""").containsMatchIn(code)
    ) {
        add(GradeHint(HintKind.BFS_VS_DIJKSTRA, "这是无权 BFS：第一次到达就是最短，写 dist[v] = dist[u] + 1 并入队。带权松弛是第 7 天 Dijkstra 的事。"))
    }
    if (Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""").containsMatchIn(code) &&
        !Regex("""q\s*\[\s*qt\s*\+\+\s*\]\s*=\s*v""").containsMatchIn(code)
    ) {
        add(GradeHint(HintKind.QUEUE, "更新了 dist 却没入队。BFS 靠队列把「下一层」铺开，漏写 q[qt++] = v 后面的点走不到。"))
    }
}

private fun hintsDay7(code: String): List<GradeHint> = buildList {
    if (Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""").containsMatchIn(code)) {
        add(GradeHint(HintKind.BFS_VS_DIJKSTRA, "dist[v] = dist[u] + 1 是按「过几条街」数的 BFS。本题边权不同：必须加 w[e]，否则 0→1 会被算成 1 而不是更短的 0→2→1 = 2。"))
    }
    if (Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*w""").containsMatchIn(code) &&
        !Regex("""dist\s*\[\s*v\s*\]\s*>\s*dist\s*\[\s*u\s*\]\s*\+\s*w""").containsMatchIn(code) &&
        !Regex("""if\s*\(""").containsMatchIn(code.substringAfter("for (int e = head[u]"))
    ) {
        add(GradeHint(HintKind.RELAX, "松弛要先比较再赋值：if (dist[v] > dist[u] + w[e]) dist[v] = dist[u] + w[e]; 无条件覆盖会把更短的路写坏。"))
    }
    val relaxLoop = code.substringAfter("for (int e = head[u]", missingDelimiterValue = "")
    if (relaxLoop.contains("TODO") || (relaxLoop.isNotEmpty() && !relaxLoop.contains("dist[v]"))) {
        add(GradeHint(HintKind.STUB, "邻接表循环里还没写松弛。对每条边 e：v = to[e]，若 dist[u] + w[e] 更小就更新 dist[v]。"))
    }
}

/** Rough extraction of a C function body by brace matching. */
private fun functionBody(code: String, name: String): String? {
    val header = Regex("""\b$name\s*\([^)]*\)\s*\{""").find(code) ?: return null
    val start = header.range.last
    var depth = 1
    var i = start + 1
    while (i < code.length && depth > 0) {
        when (code[i]) {
            '{' -> depth++
            '}' -> depth--
        }
        i++
    }
    if (depth != 0) return code.substring(start + 1)
    return code.substring(start + 1, i - 1)
}
