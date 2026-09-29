package com.py2c.week.data.curriculum

import com.py2c.week.data.CalloutKind
import com.py2c.week.data.CheckRule
import com.py2c.week.data.CodeLab
import com.py2c.week.data.ContentBlock.Bullets
import com.py2c.week.data.ContentBlock.Callout
import com.py2c.week.data.ContentBlock.Example
import com.py2c.week.data.ContentBlock.Heading
import com.py2c.week.data.ContentBlock.Paragraph
import com.py2c.week.data.CourseDay
import com.py2c.week.data.LabCheck
import com.py2c.week.data.LabTestCase
import com.py2c.week.data.Lesson
import com.py2c.week.data.QuizQuestion

internal fun day2En(): CourseDay = CourseDay(
    id = 2,
    title = "Types, Variables & I/O",
    subtitle = "Static typing · printf/scanf · no automatic reclamation",
    outcome = "Declare int/double/char, read and write with printf/scanf, and understand overflow and stack variable lifetime.",
    minutes = 100,
    todayFocus = "Label the box before you put things in: an int box cannot hold arbitrarily large numbers. Returning from a function is like checking out—you cannot use what was in the room anymore.",
    lessons = listOf(
        Lesson(
            id = "d2-l1",
            title = "Declare before use",
            minutes = 12,
            summary = "Type before name; initialization; const.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: a labeled box",
                    "int x is a box stamped \"integer\"; the compiler refuses to put text inside. A declaration makes the box first, then you fill it. Wrong type? The compiler stops you at compile time, not halfway through a run.",
                ),
                Example(
                    title = "One integer",
                    source = "int x = 3;\n/* x = \"hi\";  illegal: type system rejects */",
                    note = "C checks types at compile time. You must decide what the data looks like first; then the compiler can block a whole class of errors that would only blow up mid-run.",
                ),
                Heading("Common scalars"),
                Bullets(
                    listOf(
                        "int: signed integer; we use it by default in teaching. Width is platform-dependent; often 32 bits.",
                        "long long: wider integer; common in contests to avoid overflow.",
                        "double: double-precision floating point.",
                        "char: one byte, usually for a character or small integer.",
                        "_Bool / bool from stdbool.h: C99; 0 is false, non-zero is true.",
                    ),
                ),
                Callout(
                    CalloutKind.KEY,
                    "No built-in string type as a first-class citizen",
                    "C \"strings\" are char arrays + trailing '\\0'. Day 4 covers that properly. Today, get comfortable with numbers and single characters.",
                ),
            ),
        ),
        Lesson(
            id = "d2-l2",
            title = "Integers overflow",
            minutes = 12,
            summary = "Wrap modulo 2^n; why algorithm problems use long long.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: piggy bank is full",
                    "int is a fixed-size piggy bank. Keep stuffing and it bursts: unsigned values often wrap to small numbers; signed overflow is undefined behavior (the standard does not guarantee a result). Whether shortest-path INF is 1e9 or 4e18 depends on how big the bank is (int vs long long).",
                ),
                Paragraph("When int is full, values wrap (unsigned) or signed overflow becomes undefined behavior. Whether you pick INF 1e9 or 4e18 for shortest paths depends directly on the type you use."),
                Example(
                    title = "Use the right type for big numbers",
                    source = "#include <stdio.h>\nint main(void) {\n    int a = 1000000000;\n    printf(\"%d\\n\", a + a); /* may overflow */\n    long long b = 1000000000LL;\n    printf(\"%lld\\n\", b + b); /* 2000000000 */\n    return 0;\n}",
                    note = "The LL suffix tells the compiler this is long long. printf uses %lld to match.",
                ),
                Callout(
                    CalloutKind.WARN,
                    "Signed overflow is undefined behavior",
                    "The compiler may assume it never happens and optimize away your overflow checks. In contests, people habitually use long long for sums and distances.",
                ),
            ),
        ),
        Lesson(
            id = "d2-l3",
            title = "printf and scanf",
            minutes = 15,
            summary = "The format string is a contract: %d %lf %s must match argument types.",
            blocks = listOf(
                Heading("Output"),
                Example(
                    title = "Formatting",
                    source = "char name[] = \"Ada\";\nint age = 21;\nprintf(\"%s is %d\\n\", name, age);",
                    note = "The format string is like slots on a form: %s must be a string (via its address), %d must be an int. Mismatch prints garbage or crashes.",
                ),
                Heading("Input"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: delivery needs a street address",
                    "scanf must put the read value into the variable's \"room.\" You pass the room's address (&n, the door number), not the contents already in the room (n's value). Omitting & is like giving the courier a random number—classic segfault.",
                ),
                Paragraph("scanf writes into variables, so you pass addresses: &n. This is your first forced encounter with \"pointers show up in everyday I/O.\" When input fails, the return value is less than the number of items you expected; beginners should at least check that."),
                Example(
                    title = "Read one integer",
                    source = "int n;\nif (scanf(\"%d\", &n) != 1) {\n    return 1; /* input failed */\n}",
                    note = "Without &, scanf treats n's value as an address to write to—typical segfault.",
                ),
                Callout(
                    CalloutKind.TIP,
                    "Teaching note",
                    "Use scanf/printf for this week's algorithm problems. Do not use gets (removed, unsafe). Use fgets for lines; Day 5 on files revisits that.",
                ),
            ),
        ),
        Lesson(
            id = "d2-l4",
            title = "Operators and short-circuit",
            minutes = 10,
            summary = "Integer division, modulo, ++, logic and bitwise ops.",
            blocks = listOf(
                Bullets(
                    listOf(
                        "5 / 2 between two ints is 2, not 2.5. For decimals use 5.0 / 2 or cast to double.",
                        "a && b: if a is false, b is not evaluated. Useful for if (p && p->next).",
                        "a & b is bitwise AND; do not confuse with &&. Shortest-path sometimes packs state with bits; not needed this week.",
                        "++i and i++ are often equivalent on their own line; in larger expressions they confuse people—no showing off in this lesson.",
                    ),
                ),
                Example(
                    title = "Division follows operand types",
                    source = "printf(\"%d\\n\", 5 / 2);     /* 2 */\nprintf(\"%f\\n\", 5.0 / 2);  /* 2.5 */",
                    note = "Two ints divide with truncation toward zero. With a floating operand, the result is floating point.",
                ),
            ),
        ),
        Lesson(
            id = "d2-l5",
            title = "No automatic reclamation: scope is lifetime",
            minutes = 12,
            summary = "Stack frame intuition; why you cannot return a pointer to a local array (preview Day 4/5).",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: hotel checkout",
                    "Entering a function is check-in: the desk gives you rooms on the spot (stack locals). return is checkout—keys are void. Handing out a key to a local array is leaving the hotel with a room key; the next guest may already be there—you read someone else's data or crash.",
                ),
                Paragraph("When you enter a function, locals are allocated on the stack; when the function returns, that memory is invalid. C has no reference counting extending lifetime for you."),
                Example(
                    title = "Danger preview",
                    source = "int *make(void) {\n    int xs[3] = {1, 2, 3};\n    return xs; /* dangerous: xs vanishes when the function ends */\n}",
                    note = "For today, remember the rule: data that must outlive the function needs a caller-provided buffer, or malloc on Day 5.",
                ),
                Callout(
                    CalloutKind.KEY,
                    "Mental model",
                    "Ask three questions first: What type is it? Stack or heap? Who frees it? In C, you answer yourself.",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d2",
        title = "Lab: Celsius ↔ Fahrenheit",
        brief = "Read an integer Celsius temperature; print Fahrenheit (integer arithmetic).",
        task = "Formula F = C * 9 / 5 + 32. Input is already read with scanf. Use integer math; 0 → 32, 100 → 212.",
        starterCode = """
#include <stdio.h>

int main(void) {
    int c;
    if (scanf("%d", &c) != 1) return 1;
    int f = 0; /* TODO: compute Fahrenheit */
    printf("%d\n", f);
    return 0;
}
""".trimIndent(),
        solutionCode = """
#include <stdio.h>

int main(void) {
    int c;
    if (scanf("%d", &c) != 1) return 1;
    int f = c * 9 / 5 + 32;
    printf("%d\n", f);
    return 0;
}
""".trimIndent(),
        expectedOutput = "When input is 100:\n212\n",
        testCases = listOf(
            LabTestCase("Boiling", "Input 100 (multiple of 10; /5 first happens to work)", "212", input = "100"),
            LabTestCase("Freezing", "Input 0", "32", input = "0"),
            LabTestCase("Same value", "Input -40", "-40", input = "-40"),
            LabTestCase("Body temp", "Input 37 (not divisible by 5; /5 first truncates)", "98", input = "37"),
            LabTestCase("One degree", "Input 1", "33", input = "1"),
        ),
        checks = listOf(
            LabCheck("scanf", "Use scanf to read, and pass &c.", CheckRule.Contains("&c")),
            LabCheck("formula", "Use * 9 and / 5. Multiply before divide to reduce integer truncation error.", CheckRule.ContainsRegex("""\*\s*9""")),
            LabCheck("div", "The formula should include / 5.", CheckRule.ContainsRegex("""/\s*5""")),
            LabCheck("printf", "Print the result with printf.", CheckRule.Contains("printf")),
        ),
        hints = listOf(
            "Multiply by 9 then divide by 5: c * 9 / 5, not /5 then *9 (less accurate except at 0°C).",
            "Do not omit & in scanf(\"%d\", &c).",
            "This simulator does not read the keyboard; it checks code shape. On a real machine: echo 100 | ./temp.",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d2-q1",
            "scanf(\"%d\", n) without &—most likely consequence?",
            listOf("Address added automatically", "Treats n's value as a pointer to write to—UB/crash", "Compiler picks a larger integer type", "Only loses one decimal digit"),
            1,
            "scanf needs int*. Missing & is the classic Day 2 segfault.",
        ),
        QuizQuestion(
            "d2-q2",
            "Two ints: result of 5/2?",
            listOf("2.5", "2", "3 (rounded)", "Compile error"),
            1,
            "Integer division truncates toward zero.",
        ),
        QuizQuestion(
            "d2-q3",
            "Why is printf(\"%d\", 3.14) dangerous?",
            listOf("Format and argument type mismatch—undefined behavior", "3.14 cannot appear in C", "Should use puts", "%d auto-converts so it's safe"),
            0,
            "The contract must match. Correct: %f with double.",
        ),
        QuizQuestion(
            "d2-q4",
            "Local int x after the function returns?",
            listOf("Runtime decides when to free", "Stack frame ends; do not take its address", "Always on the heap", "Becomes global automatically"),
            1,
            "Nothing automatically extends its lifetime.",
        ),
    ),
)
