package com.py2c.week.data.curriculum

import com.py2c.week.data.CalloutKind
import com.py2c.week.data.CheckRule
import com.py2c.week.data.CodeLab
import com.py2c.week.data.ContentBlock.Bullets
import com.py2c.week.data.ContentBlock.Callout
import com.py2c.week.data.ContentBlock.Code
import com.py2c.week.data.ContentBlock.Example
import com.py2c.week.data.ContentBlock.Heading
import com.py2c.week.data.ContentBlock.MemoryViz
import com.py2c.week.data.ContentBlock.Paragraph
import com.py2c.week.data.CourseDay
import com.py2c.week.data.LabCheck
import com.py2c.week.data.LabTestCase
import com.py2c.week.data.Lesson
import com.py2c.week.data.QuizQuestion

internal fun day5(): CourseDay = CourseDay(
    id = 5,
    title = "结构体、堆内存、文件",
    subtitle = "struct · malloc/free · fopen",
    outcome = "能定义 struct、在堆上分配邻接表节点、正确 free，并做基础文件读写。",
    minutes = 110,
    todayFocus = "学生证栏目焊死（struct）。向仓库租货架要还钥匙（malloc/free）。打开抽屉记得关上（fclose）。",
    lessons = listOf(
        Lesson(
            id = "d5-l1",
            title = "struct：固定字段的布局",
            minutes = 14,
            summary = "字段名编译期定死；用 . 和 -> 访问。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：学生证，栏目焊死",
                    "struct Student 像一张印好栏目的学生证：学号、姓名、分数，位置编译期就定了，不能运行时再加「爱好」一栏。stu.name 是填姓名那一格。指针访问 p->score 等于拿着证件去读分数栏，(*p).score 是同一件事。",
                ),
                Example(
                    title = "一个学生",
                    source = "struct Student {\n    int id;\n    char name[32];\n    int score;\n};\nstruct Student stu = {1, \"Ada\", 95};\nprintf(\"%s\\n\", stu.name);",
                    note = "不能运行时随便加字段。名字长度必须在 name[32] 预算内，含 '\\0'。",
                ),
                Paragraph("typedef struct Student Student; 之后可以少写 struct 关键字。指针访问字段用 p->score，等价于 (*p).score。"),
                Code(
                    "c",
                    "void print_stu(const struct Student *p) {\n    printf(\"%d %s %d\\n\", p->id, p->name, p->score);\n}",
                    "大结构体用指针传入，避免整份拷贝；const 表示不会改。",
                ),
            ),
        ),
        Lesson(
            id = "d5-l2",
            title = "malloc / free：你就是回收器",
            minutes = 16,
            summary = "堆上寿命跨函数；失败返回 NULL；谁分配谁释放。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：向仓库租货架",
                    "malloc 是向库房租一块货架：成功拿到钥匙（指针），货架在仓库里，函数返回后仍在。租不到返回 NULL，不能硬闯。free 是还钥匙；还两次或还完还去开锁，都是事故。酒店房间（栈）退房自动收；仓库货架必须你亲自还。",
                ),
                MemoryViz("heap"),
                Code(
                    "c",
                    "#include <stdlib.h>\n\nint *make_n(int n) {\n    int *a = malloc(sizeof(int) * (size_t)n);\n    if (a == NULL) return NULL;\n    for (int i = 0; i < n; i++) a[i] = 0;\n    return a; /* 调用者负责 free(a) */\n}",
                    "sizeof(int) * n，不要写成 sizeof(n)。n 是 int 值，sizeof(n) 恒为 4 或 8。",
                ),
                Bullets(
                    listOf(
                        "free(NULL) 安全。free 同一块两次不安全。",
                        "free 之后立刻 p = NULL，降低悬空指针误用。",
                        "泄漏：malloc 了忘记 free。短作业跑完就退出，泄漏不明显；图算法循环里会吃光内存。",
                    ),
                ),
                Callout(
                    CalloutKind.KEY,
                    "三步缺一不可",
                    "堆上数组要：malloc、初始化、约定所有权（谁 free）。漏任何一步都是以后的崩溃。",
                ),
            ),
        ),
        Lesson(
            id = "d5-l3",
            title = "链表节点：图邻接表的预备",
            minutes = 14,
            summary = "struct Edge { int to, w; Edge *next; }",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：快递面单上的下一站",
                    "链表节点像一张面单：写着目的地（to）、运费（w），以及下一张面单的编号（next）。头插就是把新面单放在最上面，旧的串在后面。只扔掉最上面的目录夹、不顺着面单一张张还仓库，货架就泄漏了。",
                ),
                Paragraph("邻接表就是「每个顶点一条链表」。Day 6/7 会用数组版（更缓存友好）或链表版。先看节点："),
                Code(
                    "c",
                    "struct Node {\n    int to;\n    int w;\n    struct Node *next;\n};\n\nstruct Node *add(struct Node *head, int to, int w) {\n    struct Node *e = malloc(sizeof *e);\n    if (!e) return head;\n    e->to = to;\n    e->w = w;\n    e->next = head;\n    return e; /* 新头插 */\n}",
                    "头插 O(1)。无向图加边要在两端各 add 一次。",
                ),
                Callout(
                    CalloutKind.WARN,
                    "释放整张图",
                    "遍历每条链表 free 节点。只 free 顶点数组会泄漏所有边节点。",
                ),
            ),
        ),
        Lesson(
            id = "d5-l4",
            title = "文件 I/O 基础",
            minutes = 12,
            summary = "fopen / fprintf / fscanf / fclose；别丢关闭。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：打开抽屉要记得关上",
                    "fopen 是拉开一只抽屉。fprintf 往里面放纸条。离开房间（return）时 C 不会自动关抽屉，缓冲也可能还在手里没落进柜——所以成功打开后，每一条离开路径都要 fclose。\"w\" 会清空抽屉再写，\"a\" 是往已有纸条下面追加。",
                ),
                Example(
                    title = "写一行",
                    source = "FILE *fp = fopen(\"out.txt\", \"w\");\nif (!fp) return 1;\nfprintf(fp, \"%d\\n\", 95);\nfclose(fp);",
                    note = "C 不会在离开作用域时自动关闭文件。提前 return 时也要 fclose，否则缓冲可能没刷盘。",
                ),
                Bullets(
                    listOf(
                        "\"r\" 读文本，\"w\" 写（清空），\"a\" 追加。",
                        "读失败检查 fscanf 返回值，和 scanf 一样。",
                        "二进制用 \"rb\"/\"wb\"，本周算法题用文本即可。",
                    ),
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d5",
        title = "实验：结构体数组求最高分",
        brief = "三个 Student，找出 score 最大者并打印名字。",
        task = "填充 students 后遍历，用指针或下标找到最高分，printf 只输出名字和换行。",
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
    int best = 0; /* TODO: 找到最高分下标 */
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
            LabTestCase("样例三人", "Ada 90, Ben 95, Cara 88", "Ben"),
            LabTestCase(
                "必须比较分数",
                "不能写死 printf(\"Ben\")，要循环看 .score",
                "Ben",
                extraChecks = listOf(
                    LabCheck("loop-case", "应遍历数组比较 score。", CheckRule.ContainsRegex("""for\s*\(""")),
                    LabCheck("score-case", "比较的是 score 字段。", CheckRule.Contains(".score")),
                ),
            ),
        ),
        checks = listOf(
            LabCheck("field", "比较的是 score 字段。", CheckRule.Contains(".score")),
            LabCheck("loop", "应遍历数组。", CheckRule.ContainsRegex("""for\s*\(""")),
            LabCheck("print", "打印名字。", CheckRule.Contains(".name")),
        ),
        hints = listOf(
            "best 先记 0，从 i=1 比到 2。",
            "平分时保留先出现者即可。",
            "访问字段是 a[i].score，若是指针才用 ->。",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d5-q1",
            "malloc(n) 之后必须检查什么？",
            listOf("返回值是否 NULL", "文件是否只读", "int 是否溢出成 double", "编译器版本号"),
            0,
            "失败返回 NULL，解引用即崩。",
        ),
        QuizQuestion(
            "d5-q2",
            "p->x 等价于？",
            listOf("p.x", "(*p).x", "*p.x", "p[x]"),
            1,
            "箭头用于结构体指针。",
        ),
        QuizQuestion(
            "d5-q3",
            "sizeof *p 在 p 是 struct Node* 时？",
            listOf("总是 8", "是 Node 结构体的大小，适合 malloc", "是指针宽度", "非法"),
            1,
            "sizeof *p 不解引用，只看类型。这是 malloc(sizeof *p) 的惯用写法。",
        ),
        QuizQuestion(
            "d5-q4",
            "fopen 成功后忘记 fclose 的主要风险？",
            listOf("语法错误", "缓冲未刷新、句柄泄漏", "自动变成只读", "struct 字段错位"),
            1,
            "短程序退出时 OS 会收，但习惯必须配对。",
        ),
    ),
)
