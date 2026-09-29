package com.py2c.week.data

internal fun HintKind.titleEn(): String = when (this) {
    HintKind.TODO -> "TODO still in code"
    HintKind.INCLUDE -> "Missing header"
    HintKind.PYTHON -> "Wrong language syntax"
    HintKind.OFF_BY_ONE -> "Off-by-one"
    HintKind.POINTER -> "Pointer misuse"
    HintKind.FORMULA -> "Wrong formula or order"
    HintKind.BFS_VS_DIJKSTRA -> "Confused with BFS"
    HintKind.STUB -> "Empty stub"
    HintKind.NEWLINE -> "Missing newline"
    HintKind.RELAX -> "Relaxation condition"
    HintKind.QUEUE -> "Forgot to enqueue"
    HintKind.CHECK -> "Check failed"
    HintKind.HARDCODE -> "Hard-coded answer"
    HintKind.OUTPUT -> "Output mismatch"
}

internal object LabGraderEn {
    const val NO_OFFLINE_SIM = "No offline simulator for this test case yet."
    const val HELLO_NEWLINE = "Printed Hello, Ada but the format string has no \\n—the golden output expects a newline."
    const val HELLO_PYTHON = "C has no print(). Use printf(\"Hello, Ada\\n\");"
    const val HELLO_STUB = "Missing printf(\"Hello, Ada\\n\"). Replace TODO with that line to pass the standard case."
    const val CELSIUS_STUB = "Can't see the Fahrenheit formula yet. At TODO write int f = c * 9 / 5 + 32;"
    fun primeStubAlwaysNo(n: Int) =
        "is_prime almost always returns 0, so input $n prints No. Need: n<2 → 0, divisor found → 0, else → 1."
    const val PRIME_LT2 = "n < 2 not handled. 1 and negatives aren't prime—return 0 first."
    const val PRIME_TRIAL_STUB = "Can't see trial division yet. Use % for divisibility and remember to return 1."
    fun reverseStub(s: String) =
        "reverse is empty. For \"$s\" the sim still returns the original. Swap with two pointers until i>=j."

    fun reverseOob(s: String) =
        "Right pointer should be strlen(s)-1. j = strlen(s) steps past '\\0'—\"$s\" won't reverse correctly."
    const val REVERSE_NO_SWAP = "Moving pointers without writing cells won't reverse. Use a temp to swap s[i] and s[j]."
    const val TOP_HARDCODE = "Sample happens to be Ben, but a hard-coded string fails other scores. Loop on a[i].score."
    const val TOP_STUB_ADA = "best stays 0, prints Ada instead of top scorer Ben. Loop and compare score."
    const val TOP_NO_COMPARE = "Loop never compares .score, so the best index never updates."
    const val BFS_TODO = "TODO unfilled—only dist[0]=0, rest stay -1."
    const val BFS_NOT_WEIGHTED = "Unweighted BFS: dist[v] = dist[u] + 1 and enqueue—don't add edge weights."
    const val BFS_NO_ENQ = "Updated dist but didn't enqueue. Vertex 3 never reached—sim output missing the last value."
    const val BFS_RELAX_STUB = "Can't see BFS relax yet. If dist[v]<0, set dist[v]=dist[u]+1 and q[qt++]=v."
    const val DIJ_STUB = "Adjacency loop has no relax yet. Source is 0; others stay INF (-1). For each edge, if dist[u]+w[e] is smaller, update dist[v]."
    const val DIJ_BFS_OUT = "dist[v] = dist[u] + 1 counts hops. Here 0→1 direct weight 4 loses to 0→2→1 = 2. Sim stdout becomes 0 1 1 2, not 0 2 1 3."
    const val DIJ_NO_CMP = "Sample may print 0 2 1 3, but without if (dist[v] > dist[u] + w[e]) a different graph gets wrong distances."
    const val DIJ_RELAX_STUB = "Can't see relax yet. Write if (dist[v] > dist[u] + w[e]) dist[v] = dist[u] + w[e];"
    const val TODO_BLOCK = "Still TODO in code. The simulator treats blanks as failure—remove the marker and write real statements."
    const val TODO_RUN = "TODO remains: fill marked spots before checking—the simulator won't fill them for you."
    const val PYTHON_PASS = "Found pass—that's another language. In C use { } for an empty body, not pass."
    const val PYTHON_PRINT = "Use printf(\"...\\n\") and #include <stdio.h>. C has no print()."
    const val PYTHON_DEF = "C uses declarations/definitions and #include, not def or import."
    const val PYTHON_FOR_IN = "C for loop: for (int i = 0; i < n; i++)—no for x in xs."
    const val PYTHON_BOOL = "Use NULL / 1 / 0 (or stdbool.h true/false), not True/False/None."
    const val PYTHON_RANGE = "No range() in C. Use for (int i = 0; i < n; i++)."
    const val PYTHON_CONSOLE = "This is a C lab—use printf, not console.log or System.out."
    const val INCLUDE_STDIO = "printf/scanf without #include <stdio.h>. Real gcc may warn; the app treats missing headers as failure."
    const val SCANF_ADDR = "scanf needs addresses. scanf(\"%d\", n) treats n's value as a pointer. Pass &n."
    const val NO_GETS = "Don't use gets—it can't bound length and overflows easily. Use scanf or a fixed char array in class."
    const val D1_NEWLINE = "Put \\n in the format string or Hello, Ada won't match the golden line break."
    const val D1_PYTHON = "C lab: use printf(\"Hello, Ada\\n\"); not print."
    const val D1_COMMA = "Comma and space matter—print Hello, Ada (comma then space after Hello)."
    const val D2_DIV_ORDER = "Divide before multiply truncates ints. Write c * 9 / 5 + 32 so multiply comes first."
    const val D2_NO_32 = "Fahrenheit needs + 32. Only scaling gives 180 not 212 at 100°C."
    const val D2_NINE_FIFTH = "9/5 as int is 1, formula becomes c*1+32. Write c * 9 / 5 + 32."
    const val D2_SCANF = "scanf(\"%d\", &c) needs &—otherwise scanf doesn't get the variable's address."
    const val D3_PRIME_STUB = "is_prime almost always returns 0. Need n<2 → 0, divisor → 0, else 1."
    const val D3_LOOP_N =
        "Trial loop i <= n divides n itself—every n>1 looks composite. Use i * i <= n (or i <= n / i) from 2."
    const val D4_REVERSE_STUB = "reverse empty. Two pointers i=0, j=strlen(s)-1, swap until i>=j."
    const val D4_STRLEN = "Right pointer is strlen(s) - 1. j = strlen(s) steps past '\\0'—wrong char or overflow."
    const val D4_SWAP = "Swap needs a temp: char t = s[i]; s[i] = s[j]; s[j] = t;. Moving pointers alone won't reverse."
    const val D5_BEST_STUB = "best stays 0 → prints Ada not Ben. Loop and compare a[i].score."
    const val D5_ARROW = "a is a struct array, not pointer—use a[i].score; p->score only for struct Student *p."
    const val D5_BOUNDS = "Only a[0..2]. i <= 3 reads a[3] out of bounds—use i < 3."
    const val D5_HARDCODE = "Don't printf(\"Ben\") hard-coded. Other scores change the top name—compare score by index."
    const val D6_WEIGHTED = "Unweighted BFS: first arrival is shortest—dist[v]=dist[u]+1 and enqueue. Weighted relax is Day 7 Dijkstra."
    const val D6_QUEUE = "Updated dist without enqueue. BFS spreads the next layer via the queue—missing q[qt++]=v blocks later nodes."
    const val D7_BFS_HOPS = "dist[v]=dist[u]+1 counts hops. Edge weights differ—add w[e] or 0→1 becomes 1 instead of shorter 0→2→1=2."
    const val D7_RELAX = "Compare before assign: if (dist[v] > dist[u] + w[e]) dist[v] = dist[u] + w[e]; blind assign breaks shorter paths."
    const val D7_LOOP_STUB = "Adjacency loop missing relax. For edge e: v=to[e], if dist[u]+w[e] improves dist[v], update."

    fun celsiusDivFirst(c: Int, wrong: Int, expected: Int) =
        "You divide by 5 before multiply by 9. For input $c you get $wrong, not $expected. Use c * 9 / 5 + 32."

    fun celsiusNineFifth(c: Int, wrong: Int) =
        "9/5 as int is 1; input $c becomes $wrong. Write c * 9 / 5 + 32 with multiply before divide."

    fun celsiusNo32(c: Int, wrong: Int, expected: Int) =
        "Missing + 32. Input $c gives $wrong; Fahrenheit should be $expected."

    fun primeLoopN(n: Int) =
        "Trial division with i <= n includes n itself—input $n looks composite. Use i * i <= n starting at 2."

    fun caseTitle(name: String) = "Case \"$name\""

    fun stdoutMismatch(expected: String, actual: String, extra: String?) = buildString {
        append("Expected \"${expected.trim()}\", simulated stdout \"${actual.trim()}\"")
        extra?.let { append(". $it") }
    }

    fun simUnknown() =
        "Simulator can't predict stdout for this input. Write the key statements from the hint and retry."
}
