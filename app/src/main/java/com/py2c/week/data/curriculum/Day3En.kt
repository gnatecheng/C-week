package com.py2c.week.data.curriculum

import com.py2c.week.data.CalloutKind
import com.py2c.week.data.CheckRule
import com.py2c.week.data.CodeLab
import com.py2c.week.data.ContentBlock.Bullets
import com.py2c.week.data.ContentBlock.Callout
import com.py2c.week.data.ContentBlock.Code
import com.py2c.week.data.ContentBlock.Example
import com.py2c.week.data.ContentBlock.Heading
import com.py2c.week.data.ContentBlock.Paragraph
import com.py2c.week.data.CourseDay
import com.py2c.week.data.LabCheck
import com.py2c.week.data.LabTestCase
import com.py2c.week.data.Lesson
import com.py2c.week.data.QuizQuestion

internal fun day3En(): CourseDay = CourseDay(
    id = 3,
    title = "Control flow and functions",
    subtitle = "if/for/while · prototypes · pass by value",
    outcome = "Write functions with prototypes, understand C pass-by-value; know why swap needs pointers (setup for Day 4).",
    minutes = 95,
    todayFocus = "Put dish names on the menu first (function prototype), then cook. Pass-by-value is photocopying homework; to change the original, hand over its address.",
    lessons = listOf(
        Lesson(
            id = "d3-l1",
            title = "if, else, and braces",
            minutes = 10,
            summary = "Conditions are integers; braces are not decoration.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: recipe steps in a box",
                    "{} is like boxing steps in a recipe: only what's inside counts as \"if this, do these.\" Relying on indentation without braces lets the compiler attach the next line its own way—skip or duplicate steps. == compares; = stores into the box; if (x = 0) is legal but almost always stores 0 then asks \"is the box empty?\"",
                ),
                Paragraph("C's if (x) treats non-zero as true. There is no elif keyword—write else if. Compare with == not =; a single = is assignment, and if (x = 0) is legal but almost always wrong."),
                Example(
                    title = "Conditions",
                    source = "if (score >= 60) {\n    printf(\"pass\\n\");\n} else if (score >= 0) {\n    printf(\"fail\\n\");\n} else {\n    printf(\"bad\\n\");\n}",
                    note = "Brace even a single statement. C uses {} for boundaries. Mixed indentation without braces is a famous source of goto fail–class bugs.",
                ),
            ),
        ),
        Lesson(
            id = "d3-l2",
            title = "for / while: indices start at 0",
            minutes = 12,
            summary = "i from 0 to n-1; off-by-one preview.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: shelf slots numbered from 0",
                    "n lockers in a row, labels 0 through n-1. for (i = 0; i < n; i++) opens each in turn. i <= n tries locker n, which does not exist—out of bounds. Forgetting i++ stays on one slot forever—a dead loop.",
                ),
                Example(
                    title = "Loop 0..n-1",
                    source = "for (int i = 0; i < n; i++) {\n    printf(\"%d\\n\", i);\n}",
                    note = "Since C99 you can declare int i in the for. Use i < n, not i <= n—when array length is n, <= goes one past the end.",
                ),
                Code(
                    "c",
                    "int i = 0;\nwhile (i < n) {\n    /* body */\n    i++;\n}",
                    "Equivalent while form. Forgetting i++ is an infinite loop.",
                ),
                Callout(
                    CalloutKind.WARN,
                    "Do not write for i in n",
                    "C has no iterator syntax like that. Typical pattern: for (int i = 0; i < n; i++). The lab simulator checks for this.",
                ),
            ),
        ),
        Lesson(
            id = "d3-l3",
            title = "Functions: declaration, definition, return",
            minutes = 14,
            summary = "Prototypes tell the compiler parameter types; void means no return value.",
            blocks = listOf(
                Heading("Minimal split"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: menu lists dish names first",
                    "Prototype int square(int x); is like printing \"square: takes one int\" on the menu. The compiler reads top to bottom; unknown names error. The definition is the kitchen recipe; you can put the recipe before main and skip the menu, but multi-file projects need prototypes in headers.",
                ),
                Code(
                    "c",
                    "#include <stdio.h>\n\nint square(int x);          /* prototype: for main below */\n\nint main(void) {\n    printf(\"%d\\n\", square(5));\n    return 0;\n}\n\nint square(int x) {          /* definition: the real body */\n    return x * x;\n}",
                    "You can also put the definition before main and omit the prototype. Multi-file projects rely on prototypes in headers.",
                ),
                Example(
                    title = "No default args, no overloading by name",
                    source = "int add2(int x, int y) { return x + y; }\nint add1(int x) { return add2(x, 0); }",
                    note = "C function names are unique at link time. For \"defaults,\" write two functions. C++ allows overloading.",
                ),
            ),
        ),
        Lesson(
            id = "d3-l4",
            title = "Pass by value: why swap fails",
            minutes = 14,
            summary = "Parameters are copies; pass addresses to change outer variables.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: photocopy vs giving the original's address",
                    "Pass-by-value is handing a photocopy to a classmate: they edit the copy, your original on the desk stays put. To edit the original, give the desk number (pass a pointer). swap(int a, int b) swaps two copies, so x and y outside stay 1 and 2.",
                ),
                Paragraph("C copies every argument into the function. Changing parameter x does not change the caller's variable."),
                Example(
                    title = "Failed swap",
                    source = "void swap(int a, int b) {\n    int t = a; a = b; b = t;\n}\nint x = 1, y = 2;\nswap(x, y); /* x,y still 1,2 */",
                    note = "Tomorrow: void swap(int *a, int *b) { int t = *a; *a = *b; *b = t; } and call swap(&x, &y).",
                ),
                Callout(
                    CalloutKind.KEY,
                    "To change the caller's variable, pass its address",
                    "scanf already does this. Pointers are not magic syntax—they mean \"pass the door number, not a photocopy of the whole house.\"",
                ),
            ),
        ),
        Lesson(
            id = "d3-l5",
            title = "Recursion and stack frames (intuition)",
            minutes = 10,
            summary = "Each call adds a stack layer; too deep overflows the stack.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: stacking plates",
                    "Each recursive call is another plate on the pile. C's tray (the stack) is small; too high and the stack collapses (stack overflow)—often without a polite message. Recursion fits trees; for worst-case graphs, an explicit array stack or BFS is safer.",
                ),
                Paragraph("Recursion pushes one stack frame per call. C's stack is usually small; too deep crashes, not always with a friendly message. DFS recursion is fine on trees; for adversarial graphs, prefer an explicit stack or BFS."),
                Code(
                    "c",
                    "int fact(int n) {\n    if (n <= 1) return 1;\n    return n * fact(n - 1);\n}",
                    "Factorial for teaching. Large n risks both stack depth and int overflow.",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d3",
        title = "Lab: write is_prime",
        brief = "Decide whether a positive integer is prime; return 1 or 0.",
        task = "Implement int is_prime(int n). Return 0 if n < 2. Trial-divide while i*i <= n. main already reads n and prints Yes/No.",
        starterCode = """
#include <stdio.h>

int is_prime(int n) {
    /* TODO: return 1 if prime, 0 if not */
    return 0;
}

int main(void) {
    int n;
    if (scanf("%d", &n) != 1) return 1;
    printf("%s\n", is_prime(n) ? "Yes" : "No");
    return 0;
}
""".trimIndent(),
        solutionCode = """
#include <stdio.h>

int is_prime(int n) {
    if (n < 2) return 0;
    for (int i = 2; i * i <= n; i++) {
        if (n % i == 0) return 0;
    }
    return 1;
}

int main(void) {
    int n;
    if (scanf("%d", &n) != 1) return 1;
    printf("%s\n", is_prime(n) ? "Yes" : "No");
    return 0;
}
""".trimIndent(),
        expectedOutput = "Input 13 → Yes\nInput 1 → No\nInput 9 → No\n",
        testCases = listOf(
            LabTestCase("Prime 13", "Input 13", "Yes", input = "13"),
            LabTestCase("1 is not prime", "Input 1", "No", input = "1"),
            LabTestCase("Composite 9", "Input 9", "No", input = "9"),
            LabTestCase("Smallest prime 2", "Input 2", "Yes", input = "2"),
        ),
        checks = listOf(
            LabCheck("sig", "Keep the signature int is_prime(int n).", CheckRule.ContainsRegex("""int\s+is_prime\s*\(\s*int""")),
            LabCheck("lt2", "Handle n < 2 first.", CheckRule.ContainsRegex("""n\s*<\s*2""")),
            LabCheck("mod", "Use % to test divisibility.", CheckRule.Contains("%")),
            LabCheck("loop", "You need a trial-division loop.", CheckRule.ContainsRegex("""for\s*\(""")),
        ),
        hints = listOf(
            "i * i <= n avoids sqrt. Note i*i can overflow for huge i; this problem stays in teaching int range.",
            "2 is prime: loop starts at 2, body never runs, return 1.",
            "On a machine: echo 13 | ./prime",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d3-q1",
            "How are C function parameters passed by default?",
            listOf("By reference; assignment inside changes the caller", "By value; parameters are copies", "Compiler decides at random", "Arrays follow the opposite rule from everything else"),
            1,
            "To change the caller you must pass a pointer (address).",
        ),
        QuizQuestion(
            "d3-q2",
            "for (int i = 0; i <= n; i++) accessing a[0..n-1]?",
            listOf("Just right", "One extra access at a[n], out-of-bounds risk", "One short", "Syntax error"),
            1,
            "Classic off-by-one. Use i < n.",
        ),
        QuizQuestion(
            "d3-q3",
            "Why write int foo(int); at the top of a file?",
            listOf("So main can call before the definition and the compiler still knows types", "C requires every function declared twice", "Linker only accepts semicolons", "Optional; any compiler accepts deletion"),
            0,
            "That is a function prototype.",
        ),
        QuizQuestion(
            "d3-q4",
            "What's wrong with if (x = 0)?",
            listOf("Syntax error, won't compile", "Assigns 0 to x, condition always false—usually meant ==", "Throws an exception", "Same as if (x == 0)"),
            1,
            "Assignment expression value is what was assigned. -Wall often warns.",
        ),
    ),
)
