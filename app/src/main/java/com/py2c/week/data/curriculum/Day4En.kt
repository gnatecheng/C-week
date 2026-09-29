package com.py2c.week.data.curriculum

import com.py2c.week.data.CalloutKind
import com.py2c.week.data.CheckRule
import com.py2c.week.data.CodeLab
import com.py2c.week.data.ContentBlock.Bullets
import com.py2c.week.data.ContentBlock.Callout
import com.py2c.week.data.ContentBlock.Code
import com.py2c.week.data.ContentBlock.Example
import com.py2c.week.data.ContentBlock.MemoryViz
import com.py2c.week.data.ContentBlock.Paragraph
import com.py2c.week.data.CourseDay
import com.py2c.week.data.LabCheck
import com.py2c.week.data.LabTestCase
import com.py2c.week.data.Lesson
import com.py2c.week.data.QuizQuestion

internal fun day4En(): CourseDay = CourseDay(
    id = 4,
    title = "Arrays, strings, pointers",
    subtitle = "Contiguous memory · addresses · '\\0'",
    outcome = "Draw pointer diagrams, use arrays and '\\0'-terminated strings correctly, and avoid out-of-bounds and array-decay traps.",
    minutes = 120,
    todayFocus = "An array is a row of numbered lockers. A pointer is a street address, not what lives inside the room. A string slips a blank “stop here” card into the last slot.",
    lessons = listOf(
        Lesson(
            id = "d4-l1",
            title = "Arrays are contiguous slots",
            minutes = 14,
            summary = "Length fixed at compile time (skip VLAs for now); indices start at 0; no automatic growth.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: a row of numbered lockers",
                    "int a[3] is three side-by-side lockers numbered 0, 1, and 2—the count is welded shut at compile time and never grows on its own. a[1] opens locker 1. Opening a[3] is like prying into someone else’s locker: C often stays silent and quietly reads/writes someone else’s bytes. The lockers don’t remember how many there are; you keep n separately.",
                ),
                Example(
                    title = "A small set of integers",
                    source = "int a[3] = {10, 20, 30};\n/* no automatic append; length 3 is fixed */\nprintf(\"%d\\n\", a[1]);  /* 20 */",
                    note = "Need variable length → Day 5 malloc. Need the length → store n yourself; C arrays don’t carry len.",
                ),
                Bullets(
                    listOf(
                        "int a[5]; uninitialized locals on the stack hold garbage.",
                        "int a[5] = {0}; zeroes every element.",
                        "a[5] when length is 5 is out of bounds. Valid indices are 0..4.",
                    ),
                ),
                Callout(
                    CalloutKind.WARN,
                    "The sizeof half-trap",
                    "At the declaration site, sizeof a / sizeof a[0] gives the element count. Once the array is passed to a function, it decays to a pointer and sizeof becomes the pointer width. Functions always need n passed separately.",
                ),
            ),
        ),
        Lesson(
            id = "d4-l2",
            title = "Pointers: address, &, *, NULL",
            minutes = 18,
            summary = "Animation: variables, addresses, pointer cells, dereference writes.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: street address vs what’s in the room",
                    "Variable x is a room holding the value 5. &x is that room’s address. int *p = &x copies the address onto a library card. *p means follow the card, open the door, read/write what’s inside—so *p = 7 makes x become 7. p and x are not the same box: one holds an address, one holds an int. p = NULL marks the card “no such address”; dereferencing it is undefined behavior.",
                ),
                Paragraph("int x = 5; reserves stack memory for 5, say at address 0xA0. int *p = &x; stores 0xA0 in p. *p means “go to that address and read/write the house.”"),
                MemoryViz("pointer_basic"),
                Example(
                    title = "Pointing at an integer",
                    source = "int x = 5;\nint *p = &x;\n*p = 7;        /* x becomes 7 */\np = NULL;      /* don’t dereference p now */",
                    note = "* in a declaration means “this is a pointer”; in an expression it means “dereference.” Same symbol, two roles—check whether it’s in type position.",
                ),
                Code(
                    "c",
                    "void swap(int *a, int *b) {\n    int t = *a;\n    *a = *b;\n    *b = t;\n}\n/* call: swap(&x, &y); */",
                    "Yesterday’s broken swap, today’s standard form.",
                ),
                Callout(
                    CalloutKind.KEY,
                    "NULL",
                    "A null pointer means the card reads “no such address.” Dereferencing NULL is undefined behavior. In graph code, child pointers and failed malloc need checks first.",
                ),
            ),
        ),
        Lesson(
            id = "d4-l3",
            title = "Array decay: a vs &a[0]",
            minutes = 12,
            summary = "Function parameter int a[] is really int *a.",
            blocks = listOf(
                MemoryViz("array_decay"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: handing over only locker 0’s address",
                    "When you pass a whole row of lockers into a function, C actually passes the address of locker 0. The callee can’t see “how many follow,” and sizeof inside the function becomes the width of one address card. So the convention is always (int *a, int n): address plus the count you report.",
                ),
                Paragraph("In expressions, an array name usually becomes a pointer to the first element. So foo(a) and foo(&a[0]) are the same. Exceptions: sizeof a, &a (pointer to the whole array). For teaching: once inside a function, you only have a pointer plus the length you pass in."),
                Code(
                    "c",
                    "int sum(int *a, int n) {\n    int s = 0;\n    for (int i = 0; i < n; i++) s += a[i]; /* a[i] is *(a+i) */\n    return s;\n}",
                    "a[i] and *(a+i) are equivalent. Pointer arithmetic steps by element size, not bytes (unless it’s char*).",
                ),
            ),
        ),
        Lesson(
            id = "d4-l4",
            title = "C strings: char arrays ending in '\\0'",
            minutes = 16,
            summary = "You count length yourself or use strlen.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: a blank card at the end of the shelf",
                    "\"cat\" isn’t just three letters: the slots are c, a, t, plus a blank “stop here” card '\\0'. printf(\"%s\") and strlen both stop at that card. Skip the card and reading walks into the next locker until it happens to hit a 0. String literals often live in read-only memory—treating them as a mutable buffer can crash.",
                ),
                Example(
                    title = "A short text",
                    source = "char s[] = \"cat\";  /* actually 4 bytes: c a t \\0 */\ns[0] = 'C';        /* mutable → Cat */\nprintf(\"%s\\n\", s);",
                    note = "Literal \"cat\" is usually in read-only storage. char *p = \"cat\"; then p[0]='C' may crash. For mutable buffers use an array or malloc.",
                ),
                Bullets(
                    listOf(
                        "strlen does not count '\\0'. Buffers need at least strlen+1.",
                        "strcpy doesn’t check destination size—easy overflow; in class you can loop yourself or learn snprintf.",
                        "Compare contents with strcmp, not == (that compares pointer addresses).",
                    ),
                ),
                MemoryViz("cstring"),
            ),
        ),
        Lesson(
            id = "d4-l5",
            title = "Common trap checklist",
            minutes = 12,
            summary = "Out of bounds, wild pointers, off-by-one, treating arrays like value copies.",
            blocks = listOf(
                Bullets(
                    listOf(
                        "Using an uninitialized pointer (wild pointer)—point it at a valid object or NULL first.",
                        "Loop with i <= n accessing a[n].",
                        "Forgetting '\\0' on a string; printf(\"%s\") scans foreign memory until it randomly hits 0.",
                        "Thinking int b[3] = a copies three elements—arrays aren’t assignable as a whole; loop or memcpy.",
                        "Returning a pointer to a local array (the Day 2 teaser).",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "Debugging habits",
                    "Out-of-bounds often “seems fine.” Use -Wall; when you can, -fsanitize=address (gcc/clang). In VS Code breakpoints, check whether a pointer is 0x0 or obvious garbage.",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d4",
        title = "Lab: reverse a string in place",
        brief = "Use two pointers to swap chars in the array—don’t allocate a new string.",
        task = "Implement void reverse(char *s). Assume s is a valid C string. main prints the result. Example: input hello → olleh.",
        starterCode = """
#include <stdio.h>
#include <string.h>

void reverse(char *s) {
    /* TODO: reverse s in place */
}

int main(void) {
    char s[] = "hello";
    reverse(s);
    printf("%s\n", s);
    return 0;
}
""".trimIndent(),
        solutionCode = """
#include <stdio.h>
#include <string.h>

void reverse(char *s) {
    int i = 0;
    int j = (int)strlen(s) - 1;
    while (i < j) {
        char t = s[i];
        s[i] = s[j];
        s[j] = t;
        i++;
        j--;
    }
}

int main(void) {
    char s[] = "hello";
    reverse(s);
    printf("%s\n", s);
    return 0;
}
""".trimIndent(),
        expectedOutput = "olleh\n",
        testCases = listOf(
            LabTestCase("hello", "s = \"hello\"", "olleh", input = "hello"),
            LabTestCase("single char", "s = \"a\"", "a", input = "a"),
            LabTestCase("even length", "s = \"ab\"", "ba", input = "ab"),
        ),
        checks = listOf(
            LabCheck("sig", "Keep void reverse(char *s).", CheckRule.ContainsRegex("""void\s+reverse\s*\(\s*char\s*\*""")),
            LabCheck("len", "Need the length—strlen or count to '\\0'.", CheckRule.ContainsRegex("""strlen\s*\(|s\[i\]\s*==\s*'\\0'|s\[i\]\s*!=\s*'\\0'""")),
            LabCheck("swap", "Should swap characters (temp variable or equivalent).", CheckRule.ContainsRegex("""s\[""")),
            LabCheck("inc", "Include string.h or implement length yourself when using char* strings.", CheckRule.Contains("#include")),
        ),
        hints = listOf(
            "i from 0, j from strlen-1, swap until i>=j. Don’t move the trailing '\\0'.",
            "Empty string: strlen is 0, j=-1, loop never runs—that’s correct.",
            "Out of bounds is often j = strlen without the -1.",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d4-q1",
            "After int a[10] is passed to void f(int a[]), sizeof a inside f is usually?",
            listOf("10 * sizeof(int)", "Size of a pointer (e.g. 8)", "10", "0"),
            1,
            "The array decays to a pointer. Pass length separately.",
        ),
        QuizQuestion(
            "d4-q2",
            "char s[] = \"ab\"; how many bytes (including the terminator)?",
            listOf("2", "3", "1", "Determined by strlen, excluding terminator so 2 bytes"),
            1,
            "a, b, and \\0 → 3 bytes.",
        ),
        QuizQuestion(
            "d4-q3",
            "What does * mean in int *p vs *p = 1?",
            listOf("Multiplication both times", "Declaration: pointer type; expression: dereference", "Dereference both times", "Address both times"),
            1,
            "Check type position vs expression position.",
        ),
        QuizQuestion(
            "d4-q4",
            "a[i] is equivalent to?",
            listOf("a + i", "*(a + i)", "&a + i", "a[0] * i"),
            1,
            "Pointer arithmetic steps by element size.",
        ),
    ),
)
