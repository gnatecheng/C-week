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

internal fun day5En(): CourseDay = CourseDay(
    id = 5,
    title = "Structs, heap memory, files",
    subtitle = "struct · malloc/free · fopen",
    outcome = "Define structs, allocate adjacency-list nodes on the heap, free correctly, and do basic file I/O.",
    minutes = 110,
    todayFocus = "Student ID fields are welded in place (struct). Renting warehouse shelves means returning the key (malloc/free). Open a drawer, close it (fclose).",
    lessons = listOf(
        Lesson(
            id = "d5-l1",
            title = "struct: a fixed field layout",
            minutes = 14,
            summary = "Field names fixed at compile time; access with . and ->.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: student ID with printed slots",
                    "struct Student is like an ID card with printed slots: id, name, score—the layout is fixed at compile time; you can’t add a “hobby” field at runtime. stu.name fills the name slot. p->score on a pointer reads the score slot; (*p).score is the same thing.",
                ),
                Example(
                    title = "One student",
                    source = "struct Student {\n    int id;\n    char name[32];\n    int score;\n};\nstruct Student stu = {1, \"Ada\", 95};\nprintf(\"%s\\n\", stu.name);",
                    note = "You can’t add fields at runtime. Name length must fit name[32], including '\\0'.",
                ),
                Paragraph("typedef struct Student Student; lets you drop the struct keyword. Pointer field access uses p->score, same as (*p).score."),
                Code(
                    "c",
                    "void print_stu(const struct Student *p) {\n    printf(\"%d %s %d\\n\", p->id, p->name, p->score);\n}",
                    "Pass large structs by pointer to avoid copying the whole thing; const means you won’t modify it.",
                ),
            ),
        ),
        Lesson(
            id = "d5-l2",
            title = "malloc / free: you are the garbage collector",
            minutes = 16,
            summary = "Heap lifetime crosses functions; failure returns NULL; whoever allocates frees.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: renting a shelf in a warehouse",
                    "malloc rents a shelf: success gives you a key (pointer); the shelf stays in the warehouse after the function returns. Failure returns NULL—don’t force the door. free returns the key; freeing twice or using the shelf after free is a disaster. Hotel rooms (stack) are reclaimed at checkout; warehouse shelves you must return yourself.",
                ),
                MemoryViz("heap"),
                Code(
                    "c",
                    "#include <stdlib.h>\n\nint *make_n(int n) {\n    int *a = malloc(sizeof(int) * (size_t)n);\n    if (a == NULL) return NULL;\n    for (int i = 0; i < n; i++) a[i] = 0;\n    return a; /* caller must free(a) */\n}",
                    "sizeof(int) * n—not sizeof(n). n is an int value; sizeof(n) is always 4 or 8.",
                ),
                Bullets(
                    listOf(
                        "free(NULL) is safe. free the same block twice is not.",
                        "After free, set p = NULL to reduce use-after-free mistakes.",
                        "Leak: malloc without free. Short homework exits hide it; graph loops eat all memory.",
                    ),
                ),
                Callout(
                    CalloutKind.KEY,
                    "Three steps, none optional",
                    "Heap arrays need: malloc, initialization, and an ownership rule (who frees). Skip any step and you pay later with crashes.",
                ),
            ),
        ),
        Lesson(
            id = "d5-l3",
            title = "Linked-list nodes: prep for adjacency lists",
            minutes = 14,
            summary = "struct Edge { int to, w; Edge *next; }",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: next stop on a shipping label",
                    "A list node is a label: destination (to), cost (w), and the next label’s reference (next). Head insert puts the new label on top; older ones chain behind. Throwing away only the top binder without walking the chain to return each shelf leaks the warehouse.",
                ),
                Paragraph("An adjacency list is “one linked list per vertex.” Days 6/7 use array or list versions. Start with the node:"),
                Code(
                    "c",
                    "struct Node {\n    int to;\n    int w;\n    struct Node *next;\n};\n\nstruct Node *add(struct Node *head, int to, int w) {\n    struct Node *e = malloc(sizeof *e);\n    if (!e) return head;\n    e->to = to;\n    e->w = w;\n    e->next = head;\n    return e; /* new head insert */\n}",
                    "Head insert O(1). Undirected graphs: add on both endpoints.",
                ),
                Callout(
                    CalloutKind.WARN,
                    "Freeing the whole graph",
                    "Walk each list and free nodes. Freeing only the vertex array leaks every edge node.",
                ),
            ),
        ),
        Lesson(
            id = "d5-l4",
            title = "File I/O basics",
            minutes = 12,
            summary = "fopen / fprintf / fscanf / fclose; don’t skip close.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: open a drawer, close it when you leave",
                    "fopen pulls a drawer open. fprintf slips a note inside. C won’t close it when you leave the room (return)—buffers may still be in your hand—so every exit path after a successful open needs fclose. \"w\" clears the drawer then writes; \"a\" appends below existing notes.",
                ),
                Example(
                    title = "Write one line",
                    source = "FILE *fp = fopen(\"out.txt\", \"w\");\nif (!fp) return 1;\nfprintf(fp, \"%d\\n\", 95);\nfclose(fp);",
                    note = "C doesn’t auto-close files when scope ends. fclose on early return too, or buffers may not hit disk.",
                ),
                Bullets(
                    listOf(
                        "\"r\" read text, \"w\" write (truncate), \"a\" append.",
                        "On read failure check fscanf’s return value, like scanf.",
                        "Binary uses \"rb\"/\"wb\"; this week’s algorithm tasks use text.",
                    ),
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d5",
        title = "Lab: highest score in a struct array",
        brief = "Three Students—find the max score and print the name.",
        task = "After filling students, scan with index or pointer, find the highest score, printf only the name plus newline.",
        starterCode = """
#include <stdio.h>
#include <string.h>

struct Student {
    int id;
    char name[32];
    int score;
};

int main(void) {
    struct Student a[3] = {
        {1, "Ada", 90},
        {2, "Ben", 95},
        {3, "Cara", 88},
    };
    int best = 0; /* TODO: index of highest score */
    printf("%s\n", a[best].name);
    return 0;
}
""".trimIndent(),
        solutionCode = """
#include <stdio.h>
#include <string.h>

struct Student {
    int id;
    char name[32];
    int score;
};

int main(void) {
    struct Student a[3] = {
        {1, "Ada", 90},
        {2, "Ben", 95},
        {3, "Cara", 88},
    };
    int best = 0;
    for (int i = 1; i < 3; i++) {
        if (a[i].score > a[best].score) best = i;
    }
    printf("%s\n", a[best].name);
    return 0;
}
""".trimIndent(),
        expectedOutput = "Ben\n",
        testCases = listOf(
            LabTestCase("sample trio", "Ada 90, Ben 95, Cara 88", "Ben"),
            LabTestCase(
                "must compare scores",
                "Don’t hard-code printf(\"Ben\"); loop over .score",
                "Ben",
                extraChecks = listOf(
                    LabCheck("loop-case", "Should iterate the array comparing score.", CheckRule.ContainsRegex("""for\s*\(""")),
                    LabCheck("score-case", "Compare the score field.", CheckRule.Contains(".score")),
                ),
            ),
        ),
        checks = listOf(
            LabCheck("field", "Compare the score field.", CheckRule.Contains(".score")),
            LabCheck("loop", "Should iterate the array.", CheckRule.ContainsRegex("""for\s*\(""")),
            LabCheck("print", "Print the name.", CheckRule.Contains(".name")),
        ),
        hints = listOf(
            "Start best at 0, compare i from 1 through 2.",
            "On a tie, keep the earlier index.",
            "Field access is a[i].score; use -> only with pointers.",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d5-q1",
            "After malloc(n), what must you check?",
            listOf("Return value is NULL", "File is read-only", "int overflowed to double", "Compiler version"),
            0,
            "Failure returns NULL; dereferencing crashes.",
        ),
        QuizQuestion(
            "d5-q2",
            "p->x is equivalent to?",
            listOf("p.x", "(*p).x", "*p.x", "p[x]"),
            1,
            "Arrow is for struct pointers.",
        ),
        QuizQuestion(
            "d5-q3",
            "sizeof *p when p is struct Node*?",
            listOf("Always 8", "Size of struct Node—good for malloc", "Pointer width", "Illegal"),
            1,
            "sizeof *p doesn’t dereference; it uses the pointed-to type. That’s the malloc(sizeof *p) idiom.",
        ),
        QuizQuestion(
            "d5-q4",
            "Main risk of forgetting fclose after successful fopen?",
            listOf("Syntax error", "Buffers not flushed, handle leak", "File becomes read-only automatically", "Struct fields misaligned"),
            1,
            "OS reclaims on short program exit, but pairing must be habit.",
        ),
    ),
)
