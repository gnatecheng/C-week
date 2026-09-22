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
    HARDCODE,
    OUTPUT,
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
    HintKind.HARDCODE -> "写死了答案"
    HintKind.OUTPUT -> "输出和用例对不上"
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
 * Does not compile; it checks required shapes, runs multiple offline test cases,
 * and explains common mistakes in Chinese. No gcc is required.
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
    val cases = evaluateTestCases(code, lab)
    val caseHints = cases.filter { !it.passed }.mapNotNull { c ->
        c.hint?.let { GradeHint(HintKind.OUTPUT, "用例「${c.name}」", it) }
    }
    val syntaxNotes = extras.filter { it.kind == HintKind.PYTHON }.map { it.detail }
    val stillTodo = code.contains("TODO")
    val allHints = buildList {
        addAll(extras)
        addAll(checkHints)
        addAll(caseHints)
        if (stillTodo && extras.none { it.kind == HintKind.TODO }) {
            add(GradeHint(HintKind.TODO, "还留着 TODO：先把标记处补全再检查，模拟器不会替你填空。"))
        }
    }.distinctBy { it.kind to it.detail }

    val passedChecks = outcomes.count { it.passed }
    val totalChecks = outcomes.size
    val passedCases = cases.count { it.passed }
    val totalCases = cases.size
    val extrasBlock = extras.isNotEmpty() || stillTodo
    val rawScore = scorePercent(passedChecks, totalChecks, passedCases, totalCases)
    val passed = failedChecks.isEmpty() &&
        (totalCases == 0 || passedCases == totalCases) &&
        !extrasBlock
    val scorePercent = when {
        passed -> 100
        extrasBlock && rawScore >= 100 -> 90
        else -> rawScore
    }

    val simulated = when {
        passed && totalCases > 0 -> cases.first().actual ?: lab.expectedOutput
        passed -> lab.expectedOutput
        totalCases > 0 -> cases.firstOrNull { it.actual != null }?.actual
        else -> null
    }

    return LabEvaluation(
        passed = passed,
        failedHints = allHints.map { it.detail },
        simulatedOutput = simulated,
        syntaxNotes = syntaxNotes,
        gradeHints = allHints,
        checkOutcomes = outcomes,
        passedChecks = passedChecks,
        totalChecks = totalChecks,
        caseOutcomes = cases,
        passedCases = passedCases,
        totalCases = totalCases,
        scorePercent = if (passed) 100 else scorePercent,
    )
}

fun evaluateLab(code: String, lab: CodeLab): LabEvaluation = gradeLab(code, lab)

internal fun scorePercent(
    passedChecks: Int,
    totalChecks: Int,
    passedCases: Int,
    totalCases: Int,
): Int {
    val parts = mutableListOf<Float>()
    if (totalChecks > 0) parts += passedChecks / totalChecks.toFloat()
    if (totalCases > 0) parts += passedCases / totalCases.toFloat()
    if (parts.isEmpty()) return 0
    return ((parts.average() * 100.0).toInt()).coerceIn(0, 100)
}

internal fun stdoutMatches(actual: String, expected: String): Boolean {
    fun norm(s: String) = s.replace("\r\n", "\n").trim().replace(Regex("[ \t]+"), " ")
    return norm(actual) == norm(expected)
}

internal fun evaluateTestCases(code: String, lab: CodeLab): List<CaseOutcome> {
    if (lab.testCases.isEmpty()) return emptyList()
    return lab.testCases.map { tc ->
        val extraFailed = tc.extraChecks.filter { !it.rule.passes(code) }
        val (actual, simHint) = simulateCase(code, lab, tc)
        val stdoutOk = actual != null && stdoutMatches(actual, tc.expected)
        val passed = stdoutOk && extraFailed.isEmpty()
        val hint = when {
            extraFailed.isNotEmpty() -> extraFailed.first().failHint
            stdoutOk -> null
            actual == null -> simHint?.detail ?: "模拟器看不出这组输入会打印什么。对照错因把关键语句写出来再测。"
            else -> buildString {
                append("期望「${tc.expected.trim()}」，模拟 stdout 是「${actual.trim()}」")
                simHint?.detail?.let { append("。$it") }
            }
        }
        CaseOutcome(
            name = tc.name,
            inputDesc = tc.inputDesc,
            expected = tc.expected,
            actual = actual,
            passed = passed,
            hint = hint,
        )
    }
}

internal fun simulateCase(code: String, lab: CodeLab, tc: LabTestCase): Pair<String?, GradeHint?> = when (lab.id) {
    "lab-d1" -> simulateHello(code)
    "lab-d2" -> simulateCelsius(code, tc.input.toIntOrNull() ?: 100)
    "lab-d3" -> simulatePrime(code, tc.input.toIntOrNull() ?: 13)
    "lab-d4" -> simulateReverse(code, tc.input.ifBlank { "hello" })
    "lab-d5" -> simulateTopScore(code)
    "lab-d6" -> simulateBfs(code)
    "lab-d7" -> simulateDijkstra(code)
    else -> null to GradeHint(HintKind.CHECK, "这组用例还没有对应的离线模拟器。")
}

private fun simulateHello(code: String): Pair<String?, GradeHint?> {
    val hasText = code.contains("Hello, Ada")
    val hasPrintf = code.contains("printf")
    val hasNl = code.contains("\\n")
    return when {
        hasPrintf && hasText && hasNl -> "Hello, Ada\n" to null
        hasPrintf && hasText -> "Hello, Ada" to GradeHint(HintKind.NEWLINE, "打印了 Hello, Ada，但格式串里没有 \\n，黄金输出对不上换行。")
        code.contains("print(") && !hasPrintf ->
            null to GradeHint(HintKind.PYTHON, "C 没有 print()。请写 printf(\"Hello, Ada\\n\");")
        else -> null to GradeHint(HintKind.STUB, "还没看到 printf(\"Hello, Ada\\n\")。把 TODO 换成这一句即可过标准用例。")
    }
}

private fun compact(code: String): String = code.replace(Regex("\\s+"), "")

private fun assignedFormula(code: String, name: String = "f"): String {
    val match = Regex("""\b$name\s*=\s*([^;]+)""").find(code) ?: return compact(code)
    return compact(match.groupValues[1])
}

private fun simulateCelsius(code: String, c: Int): Pair<String?, GradeHint?> {
    val body = assignedFormula(code, "f")
    when {
        body.contains("c/5*9+32") || body.contains("(c/5)*9+32") -> {
            val wrong = c / 5 * 9 + 32
            return wrong.toString() to GradeHint(
                HintKind.FORMULA,
                "你写的是先 /5 再 *9。整数下输入 $c 会得到 $wrong，而不是 ${c * 9 / 5 + 32}。改成 c * 9 / 5 + 32。",
            )
        }
        body.contains("c*9/5+32") || body.contains("9*c/5+32") || body.contains("c*9/5.0+32") -> {
            return (c * 9 / 5 + 32).toString() to null
        }
        body.contains("9/5") && body.contains("+32") && !body.contains("c*9") && !body.contains("9*c") -> {
            val wrong = c * (9 / 5) + 32
            return wrong.toString() to GradeHint(
                HintKind.FORMULA,
                "9/5 在整数里等于 1，输入 $c 会变成 $wrong。请写 c * 9 / 5 + 32，让乘法走在整数除法前面。",
            )
        }
        (body.contains("c*9/5") || body.contains("9*c/5")) && !body.contains("+32") -> {
            val wrong = c * 9 / 5
            return wrong.toString() to GradeHint(
                HintKind.FORMULA,
                "少了 + 32。输入 $c 现在得到 $wrong，华氏度应是 ${c * 9 / 5 + 32}。",
            )
        }
        else -> return null to GradeHint(HintKind.STUB, "还看不出华氏度公式。在 TODO 处写 int f = c * 9 / 5 + 32;")
    }
}

private fun isPrimeKotlin(n: Int): Boolean {
    if (n < 2) return false
    var i = 2
    while (i * i <= n) {
        if (n % i == 0) return false
        i++
    }
    return true
}

private fun simulatePrime(code: String, n: Int): Pair<String?, GradeHint?> {
    val body = functionBody(code, "is_prime") ?: code
    val usesMod = body.contains("%")
    val hasLt2 = Regex("""n\s*<\s*2""").containsMatchIn(body)
    val loopsToN = Regex("""i\s*<=\s*n""").containsMatchIn(body) &&
        !Regex("""i\s*\*\s*i\s*<=\s*n""").containsMatchIn(body) &&
        !Regex("""i\s*<=\s*n\s*/""").containsMatchIn(body)
    val returnsOne = Regex("""return\s+1\s*;""").containsMatchIn(body)
    val stub = body.contains("TODO") ||
        (Regex("""return\s+0\s*;""").containsMatchIn(body) && !returnsOne && !usesMod)

    fun yesNo(prime: Boolean) = if (prime) "Yes" else "No"

    when {
        stub -> {
            return "No" to GradeHint(
                HintKind.STUB,
                "is_prime 几乎总是返回 0，所以输入 $n 会打印 No。需要：n<2 返回 0，试除整除返回 0，否则返回 1。",
            )
        }
        loopsToN && usesMod -> {
            val prime = if (n < 2) false else false // n % n == 0 for n>1
            return yesNo(prime && n >= 2) to GradeHint(
                HintKind.OFF_BY_ONE,
                "试除写成 i <= n 会把 n 自己除进去。输入 $n 会被判成合数。用 i * i <= n，从 2 起跳。",
            )
        }
        usesMod && returnsOne -> {
            val predicted = if (!hasLt2 && n < 2) true else isPrimeKotlin(n)
            val hint = if (!hasLt2 && n < 2) {
                GradeHint(HintKind.OFF_BY_ONE, "没处理 n < 2。1 和负数都不是素数，应先 return 0。")
            } else {
                null
            }
            return yesNo(predicted) to hint
        }
        else -> return null to GradeHint(HintKind.STUB, "还看不出试除。用 % 判断整除，并记得返回 1。")
    }
}

private fun simulateReverse(code: String, s: String): Pair<String?, GradeHint?> {
    val body = functionBody(code, "reverse") ?: code
    val hasLoop = Regex("""(while|for)\s*\(""").containsMatchIn(body)
    val missingMinus1 = Regex("""strlen\s*\(\s*s\s*\)""").containsMatchIn(code) &&
        !Regex("""strlen\s*\(\s*s\s*\)\s*-\s*1""").containsMatchIn(code)
    val hasSwap = Regex("""char\s+\w+|tmp|\bt\s*=""").containsMatchIn(body) ||
        Regex("""s\s*\[\s*\w+\s*\]\s*=""").containsMatchIn(body)
    return when {
        !hasLoop || body.contains("TODO") ->
            s to GradeHint(HintKind.STUB, "reverse 还是空的。对「$s」模拟结果仍是原串。用双指针交换直到 i>=j。")
        missingMinus1 && s.length > 1 ->
            "越界" to GradeHint(
                HintKind.OFF_BY_ONE,
                "右指针应是 strlen(s)-1。j 写成 strlen(s) 会踩到 '\\0' 后面，串「$s」不能正确反转。",
            )
        !hasSwap ->
            s to GradeHint(HintKind.POINTER, "只移动指针、不写回格子，原串不会反转。需要临时变量交换 s[i] 与 s[j]。")
        else -> s.reversed() to null
    }
}

private fun simulateTopScore(code: String): Pair<String?, GradeHint?> {
    val hasLoop = Regex("""for\s*\(""").containsMatchIn(code)
    val comparesScore = code.contains(".score")
    val hardcoded = Regex("""printf\s*\(\s*"Ben""").containsMatchIn(code)
    return when {
        hardcoded && !hasLoop ->
            "Ben" to GradeHint(HintKind.HARDCODE, "样例碰巧是 Ben，但写死字符串换一组分数就错。请循环比较 a[i].score。")
        hasLoop && comparesScore -> "Ben" to null
        !hasLoop ->
            "Ada" to GradeHint(HintKind.STUB, "best 一直停在 0，会打印 Ada 而不是最高分 Ben。请用循环比较 score。")
        else ->
            "Ada" to GradeHint(HintKind.STUB, "循环里没有比较 .score，最高分下标不会更新。")
    }
}

private fun simulateBfs(code: String): Pair<String?, GradeHint?> {
    val hasRelax = Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""").containsMatchIn(code)
    val hasEnq = Regex("""q\s*\[\s*qt\s*\+\+\s*\]\s*=\s*v""").containsMatchIn(code)
    val dijkstra = Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*w""").containsMatchIn(code)
    return when {
        code.contains("TODO") && !hasRelax ->
            "0 -1 -1 -1" to GradeHint(HintKind.STUB, "TODO 没补，只有源点 dist[0]=0，其余仍是 -1。")
        dijkstra && !hasRelax ->
            null to GradeHint(HintKind.BFS_VS_DIJKSTRA, "这是无权 BFS：写 dist[v] = dist[u] + 1 并入队，不要加边权。")
        hasRelax && !hasEnq ->
            "0 1 1 -1" to GradeHint(HintKind.QUEUE, "更新了 dist 却没入队。3 号点走不到，模拟结果缺了最后一格。")
        hasRelax && hasEnq -> "0 1 1 2" to null
        else -> null to GradeHint(HintKind.STUB, "还看不出 BFS 松弛。若 dist[v]<0，设 dist[v]=dist[u]+1 并 q[qt++]=v。")
    }
}

private fun simulateDijkstra(code: String): Pair<String?, GradeHint?> {
    val bfs = Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""").containsMatchIn(code)
    val hasCmp = Regex("""dist\s*\[\s*v\s*\]\s*>\s*dist\s*\[\s*u\s*\]\s*\+\s*w""").containsMatchIn(code)
    val hasAssign = Regex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*w""").containsMatchIn(code)
    return when {
        code.contains("TODO") && !hasAssign ->
            "0 -1 -1 -1" to GradeHint(
                HintKind.STUB,
                "邻接表循环里还没写松弛。源点是 0，其余 INF 会打成 -1。对每条边：若 dist[u]+w[e] 更小就更新 dist[v]。",
            )
        bfs ->
            "0 1 1 2" to GradeHint(
                HintKind.BFS_VS_DIJKSTRA,
                "dist[v] = dist[u] + 1 是数边数。本题 0→1 直边权 4，更短的是 0→2→1 = 2。模拟 stdout 会得到 0 1 1 2，不是 0 2 1 3。",
            )
        hasCmp && hasAssign -> "0 2 1 3" to null
        hasAssign && !hasCmp ->
            "0 2 1 3" to GradeHint(
                HintKind.RELAX,
                "这次样例碰巧能打出 0 2 1 3，但缺少 if (dist[v] > dist[u] + w[e]) 的比较，换一张图会把更短的路写坏。",
            )
        else -> null to GradeHint(HintKind.STUB, "还看不出松弛。写出 if (dist[v] > dist[u] + w[e]) dist[v] = dist[u] + w[e];")
    }
}

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
    checkId == "hard" -> HintKind.HARDCODE
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
    if (Regex("""\bconsole\.log\b|\bsystem\.out\b""", RegexOption.IGNORE_CASE).containsMatchIn(code)) {
        add(GradeHint(HintKind.PYTHON, "这是 C 实验：用 printf，不要写 console.log 或 System.out。"))
    }

    val usesIo = code.contains("printf") || code.contains("scanf") || code.contains("puts")
    val hasStdio = Regex("""#include\s*<stdio\.h>""").containsMatchIn(code)
    if (usesIo && !hasStdio) {
        add(GradeHint(HintKind.INCLUDE, "用了 printf/scanf 却没有 #include <stdio.h>。真实 gcc 往往会报隐式声明，手机模拟器也按缺头文件判错。"))
    }

    if (Regex("""scanf\s*\(\s*"[^"]*"\s*,\s*[A-Za-z_]\w*\s*\)""").containsMatchIn(code)) {
        add(GradeHint(HintKind.POINTER, "scanf 的参数必须是地址。写成 scanf(\"%d\", n) 会把 n 的值当成指针乱写内存。应传 &n。"))
    }

    if (Regex("""gets\s*\(""").containsMatchIn(code)) {
        add(GradeHint(HintKind.POINTER, "不要用 gets：它无法限制长度，容易溢出。教学里用 scanf 或已经写好的字符数组。"))
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
    if (code.contains("Hello Ada") && !code.contains("Hello, Ada")) {
        add(GradeHint(HintKind.OUTPUT, "逗号和空格也算输出的一部分。请打印 Hello, Ada（Hello 后面是逗号再空格）。"))
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
    if (Regex("""printf\s*\(\s*"Ben""").containsMatchIn(code) && !Regex("""for\s*\(""").containsMatchIn(code)) {
        add(GradeHint(HintKind.HARDCODE, "不要写死 printf(\"Ben\")。换一组分数最高分就不是 Ben 了，请用下标比较 score。"))
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
internal fun functionBody(code: String, name: String): String? {
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
