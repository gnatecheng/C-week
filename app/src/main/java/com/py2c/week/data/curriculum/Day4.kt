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

internal fun day4(): CourseDay = CourseDay(
    id = 4,
    title = "数组、字符串、指针",
    subtitle = "连续内存 · 地址 · '\\0'",
    outcome = "能画指针图，正确用数组与 '\\0' 字符串，避开越界和数组退化陷阱。",
    minutes = 120,
    todayFocus = "数组是一排连号储物柜。指针是门牌号，不是房间里的东西。字符串在最后一格夹一张「到此为止」的空白卡。",
    lessons = listOf(
        Lesson(
            id = "d4-l1",
            title = "数组是连续的格子",
            minutes = 14,
            summary = "长度编译期固定（VLA 先别用）；下标从 0；没有自动扩容。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：一排连号储物柜",
                    "int a[3] 是三只并排、号码 0、1、2 的柜子，格子数编译时就焊死，不会自己变长。a[1] 是开 1 号柜。去开 a[3] 等于撬开隔壁别人的柜子：C 常常不报警，只是默默读/写别人的字节。柜子自己不记得「我有几格」，长度要你另存一个 n。",
                ),
                Example(
                    title = "一组整数",
                    source = "int a[3] = {10, 20, 30};\n/* 没有自动 append；长度 3 写死 */\nprintf(\"%d\\n\", a[1]);  /* 20 */",
                    note = "需要变长 → Day 5 malloc。需要知道长度 → 自己另存一个 n，C 数组不会携带 len。",
                ),
                Bullets(
                    listOf(
                        "int a[5]; 未初始化则栈上是垃圾值（局部）。",
                        "int a[5] = {0}; 全部置 0。",
                        "访问 a[5] 当长度为 5 时越界。合法下标 0..4。",
                    ),
                ),
                Callout(
                    CalloutKind.WARN,
                    "sizeof 的一半陷阱",
                    "在声明处 sizeof a / sizeof a[0] 能得到元素个数。一旦数组作为参数传入函数，它会退化成指针，sizeof 变成指针宽度。所以函数都要额外传 n。",
                ),
            ),
        ),
        Lesson(
            id = "d4-l2",
            title = "指针：地址、&、*、NULL",
            minutes = 18,
            summary = "动画：变量、地址、指针格子、解引用写入。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：门牌号 vs 房间里的东西",
                    "变量 x 是一间房间，里面放着值 5。&x 是这间房的门牌号。int *p = &x 是把门牌号抄在一张借书卡上。*p 表示拿着卡去开门、读写屋里的东西——所以 *p = 7 会把 x 变成 7。p 和 x 不是同一个盒子：一个装地址，一个装整数。p = NULL 是把卡片涂成「无此地址」，再去开门就是未定义行为。",
                ),
                Paragraph("int x = 5; 在栈上占一块放 5 的内存，假设地址是 0xA0。int *p = &x; 让 p 这个格子里存放 0xA0。*p 表示「跟着门牌走，读写那栋房子」。"),
                MemoryViz("pointer_basic"),
                Example(
                    title = "指向整数",
                    source = "int x = 5;\nint *p = &x;\n*p = 7;        /* x 变成 7 */\np = NULL;      /* 现在谁都别解引用 p */",
                    note = "* 在声明里表示「这是指针」，在表达式里表示「解引用」。同一符号两种角色，读的时候看它在不在类型位置。",
                ),
                Code(
                    "c",
                    "void swap(int *a, int *b) {\n    int t = *a;\n    *a = *b;\n    *b = t;\n}\n/* 调用：swap(&x, &y); */",
                    "昨天失败的 swap，今天的标准写法。",
                ),
                Callout(
                    CalloutKind.KEY,
                    "NULL",
                    "空指针表示卡片上写着「无此地址」。解引用 NULL 是未定义行为。图算法里孩子指针、malloc 失败都要先判断。",
                ),
            ),
        ),
        Lesson(
            id = "d4-l3",
            title = "数组退化：a 与 &a[0]",
            minutes = 12,
            summary = "函数参数 int a[] 其实是 int *a。",
            blocks = listOf(
                MemoryViz("array_decay"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：只把第一格的门牌号交给别人",
                    "把整排储物柜传进函数时，C 实际只递出 0 号柜的门牌。对方看不见「后面还有几格」，sizeof 在函数里变成一张门牌的宽度。所以约定永远写成 (int *a, int n)：地址 + 你亲手报的格数。",
                ),
                Paragraph("在表达式里，数组名通常转换成指向首元素的指针。所以 foo(a) 和 foo(&a[0]) 一样。例外：sizeof a、&a（指向整个数组的指针）。教学上记住：一进函数，你就只剩指针 + 自己传入的长度。"),
                Code(
                    "c",
                    "int sum(int *a, int n) {\n    int s = 0;\n    for (int i = 0; i < n; i++) s += a[i]; /* a[i] 即 *(a+i) */\n    return s;\n}",
                    "a[i] 与 *(a+i) 等价。指针加减的单位是元素大小，不是字节（除非是 char*）。",
                ),
            ),
        ),
        Lesson(
            id = "d4-l4",
            title = "C 字符串：以 '\\0' 结尾的 char 数组",
            minutes = 16,
            summary = "长度要自己数或 strlen。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：书架末尾的空白卡",
                    "\"cat\" 不是三个字那么简单：柜子里实际是 c、a、t，再加一张写着「到此为止」的空白卡 '\\0'。printf(\"%s\") 和 strlen 都靠这张卡停下来。漏做这张卡，阅读会一直走到隔壁柜子，直到碰巧遇到 0。字面量本身常在只读区，当成可变缓冲去改可能崩溃。",
                ),
                Example(
                    title = "一段文字",
                    source = "char s[] = \"cat\";  /* 实际 4 字节: c a t \\0 */\ns[0] = 'C';        /* 可变，变成 Cat */\nprintf(\"%s\\n\", s);",
                    note = "字面量 \"cat\" 本身通常在只读区。写成 char *p = \"cat\"; 再 p[0]='C' 可能崩溃。可变缓冲用数组或 malloc。",
                ),
                Bullets(
                    listOf(
                        "strlen 不计 '\\0'。缓冲区要至少 strlen+1。",
                        "strcpy 不检查目标大小，容易溢出；教学可用自己的循环，或了解 snprintf。",
                        "比较内容用 strcmp，不要用 ==（那是比指针地址）。",
                    ),
                ),
                MemoryViz("cstring"),
            ),
        ),
        Lesson(
            id = "d4-l5",
            title = "常见陷阱清单",
            minutes = 12,
            summary = "越界、野指针、off-by-one、把数组当值拷贝。",
            blocks = listOf(
                Bullets(
                    listOf(
                        "使用未初始化指针（野指针）——先让它指向有效对象或 NULL。",
                        "循环写成 i <= n 访问 a[n]。",
                        "忘记给字符串留 '\\0'，printf(\"%s\") 会刷到别人内存直到碰巧遇到 0。",
                        "以为 int a[3] 赋给 int b[3] 会拷贝三个元素——数组不能整体赋值，要循环或 memcpy。",
                        "返回局部数组的指针（Day 2 预告的那个）。",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "调试手法",
                    "越界往往「看起来能跑」。用 -Wall、有条件开 -fsanitize=address（gcc/clang）。VS Code 断点看指针的值是不是 0x0 或明显垃圾。",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d4",
        title = "实验：原地反转字符串",
        brief = "用双指针交换 char 数组，不要申请新串。",
        task = "实现 void reverse(char *s)。假设 s 是合法 C 字符串。main 会打印结果。示例输入 hello → olleh。",
        starterCode = """
#include <stdio.h>
#include <string.h>

void reverse(char *s) {
    /* TODO: 原地反转 s */
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
            LabTestCase("单字符", "s = \"a\"", "a", input = "a"),
            LabTestCase("偶数长度", "s = \"ab\"", "ba", input = "ab"),
        ),
        checks = listOf(
            LabCheck("sig", "保持 void reverse(char *s)。", CheckRule.ContainsRegex("""void\s+reverse\s*\(\s*char\s*\*""")),
            LabCheck("len", "需要知道长度，strlen 或自己数到 '\\0'。", CheckRule.ContainsRegex("""strlen\s*\(|s\[i\]\s*==\s*'\\0'|s\[i\]\s*!=\s*'\\0'""")),
            LabCheck("swap", "应交换字符（临时变量或等价写法）。", CheckRule.ContainsRegex("""s\[""")),
            LabCheck("inc", "用到 char* 字符串时请包含 string.h 或自己实现长度。", CheckRule.Contains("#include")),
        ),
        hints = listOf(
            "i 从 0，j 从 strlen-1，交换直到 i>=j。不要动末尾的 '\\0'。",
            "空串：strlen 为 0，j=-1，循环不进，正好。",
            "越界多半是 j 写成 strlen 没 -1。",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d4-q1",
            "int a[10]; 作为参数传入 void f(int a[]) 后，函数内 sizeof a 通常是？",
            listOf("10 * sizeof(int)", "指针的大小（如 8）", "10", "0"),
            1,
            "数组退化成指针。长度要另传。",
        ),
        QuizQuestion(
            "d4-q2",
            "char s[] = \"ab\"; 占用多少字节（含结束符）？",
            listOf("2", "3", "1", "由 strlen 决定，不含结束符所以是 2 字节"),
            1,
            "a、b、\\0 共 3。",
        ),
        QuizQuestion(
            "d4-q3",
            "* 在 int *p 与 *p = 1 中分别表示？",
            listOf("都是乘号", "声明：指针类型；表达式：解引用", "都是解引用", "都是地址"),
            1,
            "看它出现在类型位置还是表达式位置。",
        ),
        QuizQuestion(
            "d4-q4",
            "a[i] 等价于？",
            listOf("a + i", "*(a + i)", "&a + i", "a[0] * i"),
            1,
            "指针运算按元素步长。",
        ),
    ),
)
