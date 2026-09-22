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

internal fun day2(): CourseDay = CourseDay(
    id = 2,
    title = "类型、变量与 I/O",
    subtitle = "静态类型 · printf/scanf · 没有自动回收",
    outcome = "能声明 int/double/char，用 printf/scanf 读写，理解溢出与栈上变量生命周期。",
    minutes = 100,
    todayFocus = "盒子上先贴标签才能装东西：int 盒子装不下无限大的数。函数返回像退房，房间里的东西不能再拿。",
    lessons = listOf(
        Lesson(
            id = "d2-l1",
            title = "声明先于使用",
            minutes = 12,
            summary = "类型写在名字前面；初始化；const。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：贴了标签的盒子",
                    "int x 是一只印着「整数」的盒子，编译器拒绝往里塞文字。声明就是先做盒子再装东西；类型错了，编译期就会拦下来，而不是等到程序跑到一半。",
                ),
                Example(
                    title = "同一个整数",
                    source = "int x = 3;\n/* x = \"hi\";  非法：类型系统拒绝 */",
                    note = "C 在编译期检查类型。你必须先想清楚数据长什么样，编译器才能帮你挡住一整类运行到一半才爆的错误。",
                ),
                Heading("常用标量"),
                Bullets(
                    listOf(
                        "int：有符号整数，教学默认用它。宽度依平台，常见 32 位。",
                        "long long：更大的整数，竞赛常用来避免溢出。",
                        "double：双精度浮点。",
                        "char：一个字节，通常用来存字符或小整数。",
                        "_Bool / stdbool.h 的 bool：C99 才有，0 为假，非 0 为真。",
                    ),
                ),
                Callout(
                    CalloutKind.KEY,
                    "没有内置「字符串类型」当一等公民",
                    "C 的「字符串」是 char 数组 + 末尾 '\\0'。Day 4 才会真正讲。今天先把数字和单个字符用顺。",
                ),
            ),
        ),
        Lesson(
            id = "d2-l2",
            title = "整数会溢出",
            minutes = 12,
            summary = "模 2^n 回绕；为什么算法题要开 long long。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：存钱罐满了",
                    "int 像容量固定的存钱罐。再塞会挤爆：无符号数往往回绕成很小的数，有符号溢出则是未定义行为（标准不保证结果）。最短路的 INF 选 1e9 还是 4e18，取决于罐子有多大（int 还是 long long）。",
                ),
                Paragraph("C 的 int 满了就回绕（无符号）或变成未定义行为（有符号溢出）。写最短路时 INF 选 1e9 还是 4e18，直接取决于你用的类型。"),
                Example(
                    title = "大数要用对类型",
                    source = "#include <stdio.h>\nint main(void) {\n    int a = 1000000000;\n    printf(\"%d\\n\", a + a); /* 可能溢出 */\n    long long b = 1000000000LL;\n    printf(\"%lld\\n\", b + b); /* 2000000000 */\n    return 0;\n}",
                    note = "字面量后面的 LL 告诉编译器这是 long long。printf 用 %lld 匹配。",
                ),
                Callout(
                    CalloutKind.WARN,
                    "有符号溢出是未定义行为",
                    "编译器可以「假设不会发生」，于是优化掉你的溢出检查。竞赛里习惯用 long long 做加法和距离。",
                ),
            ),
        ),
        Lesson(
            id = "d2-l3",
            title = "printf 与 scanf",
            minutes = 15,
            summary = "格式串是合同：%d %lf %s 必须和参数类型一致。",
            blocks = listOf(
                Heading("输出"),
                Example(
                    title = "格式化",
                    source = "char name[] = \"Ada\";\nint age = 21;\nprintf(\"%s is %d\\n\", name, age);",
                    note = "格式串像表格的格子：%s 这一格必须是门牌指向的字符串，%d 必须是 int。格子和货物对不上，打印垃圾甚至崩溃。",
                ),
                Heading("输入"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：快递要写门牌号",
                    "scanf 要把读到的数放进变量这间「房间」。你必须把房间地址（&n，门牌号）交给它，而不能把房间里现有的东西（n 的值）当成地址。漏写 &，等于让快递员按一个随机号码上门——典型段错误。",
                ),
                Paragraph("scanf 要把值写进变量，所以传入地址：&n。这是你第一次被迫看见「指针就在日常 I/O 里」。读入失败时返回值小于期望项数，初学至少要检查。"),
                Example(
                    title = "读一个整数",
                    source = "int n;\nif (scanf(\"%d\", &n) != 1) {\n    return 1; /* 输入失败 */\n}",
                    note = "漏写 & 时，scanf 把 n 的值当成地址去写，典型段错误。",
                ),
                Callout(
                    CalloutKind.TIP,
                    "教学建议",
                    "本周算法题用 scanf/printf。不要用 gets（已废弃、必爆）。读行用 fgets，Day 5 文件课会再见。",
                ),
            ),
        ),
        Lesson(
            id = "d2-l4",
            title = "运算符与短路",
            minutes = 10,
            summary = "整数除法、取模、++、逻辑与位运算。",
            blocks = listOf(
                Bullets(
                    listOf(
                        "5 / 2 在两个 int 之间是 2，不是 2.5。要小数就 5.0 / 2 或先转 double。",
                        "a && b：a 为假则不计算 b。可用来 if (p && p->next)。",
                        "a & b 是按位与，别和 && 混用。最短路里有时用位运算压状态，本周不用。",
                        "++i 与 i++ 在单独一行时常等价；写在更大表达式里会把人绕晕，本课禁止炫技。",
                    ),
                ),
                Example(
                    title = "除法跟随操作数类型",
                    source = "printf(\"%d\\n\", 5 / 2);     /* 2 */\nprintf(\"%f\\n\", 5.0 / 2);  /* 2.5 */",
                    note = "两个 int 相除截断向 0。一边是浮点，结果才是浮点。",
                ),
            ),
        ),
        Lesson(
            id = "d2-l5",
            title = "没有自动回收：作用域就是寿命",
            minutes = 12,
            summary = "栈帧直觉；为什么不能返回局部数组的指针（预告 Day 4/5）。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：酒店退房",
                    "进函数像入住：前台当场给你几间房（栈上局部变量）。函数 return 就是退房，钥匙作废。你若把房间钥匙（指向局部数组的指针）带出酒店，下一对客人可能已经住进去——读到的是别人的东西，或直接崩。",
                ),
                Paragraph("进入函数时，局部变量在栈上分配；函数返回，这块内存视为失效。C 没有引用计数跟着你走。"),
                Example(
                    title = "危险预告",
                    source = "int *make(void) {\n    int xs[3] = {1, 2, 3};\n    return xs; /* 危险：xs 随函数结束而消失 */\n}",
                    note = "今天只要记住结论：需要离开函数还活着的数据，要么让调用者提供缓冲区，要么 Day 5 用 malloc。",
                ),
                Callout(
                    CalloutKind.KEY,
                    "心智模型",
                    "先问三个问题：它是什么类型？它住在栈还是堆？谁负责释放？C 要你自己答。",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d2",
        title = "实验：摄氏 ↔ 华氏",
        brief = "读入一个整数摄氏度，输出华氏度（整数算术）。",
        task = "公式 F = C * 9 / 5 + 32。输入已写好 scanf。用整数运算，对 0 → 32、100 → 212。",
        starterCode = """
#include <stdio.h>

int main(void) {
    int c;
    if (scanf("%d", &c) != 1) return 1;
    int f = 0; /* TODO: 计算华氏度 */
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
        expectedOutput = "输入 100 时输出：\n212\n",
        testCases = listOf(
            LabTestCase("沸水", "输入 100（10 的倍数，先 /5 碰巧也对）", "212", input = "100"),
            LabTestCase("冰点", "输入 0", "32", input = "0"),
            LabTestCase("同值点", "输入 -40", "-40", input = "-40"),
            LabTestCase("体温", "输入 37（不能被 5 整除，先 /5 会截断）", "98", input = "37"),
            LabTestCase("一摄氏", "输入 1", "33", input = "1"),
        ),
        checks = listOf(
            LabCheck("scanf", "请用 scanf 读入，并传 &c。", CheckRule.Contains("&c")),
            LabCheck("formula", "需要用到 * 9 和 / 5。注意先乘后除，减少整数截断误差。", CheckRule.ContainsRegex("""\*\s*9""")),
            LabCheck("div", "公式里应有 / 5。", CheckRule.ContainsRegex("""/\s*5""")),
            LabCheck("printf", "用 printf 输出结果。", CheckRule.Contains("printf")),
        ),
        hints = listOf(
            "先乘 9 再除 5：c * 9 / 5，不要先 /5 再 *9（0 度以外会更不准）。",
            "scanf(\"%d\", &c) 的 & 不能省。",
            "本模拟器不真正读键盘；检查的是代码形态。电脑上用 echo 100 | ./temp 测。",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d2-q1",
            "scanf(\"%d\", n) 漏了 &，最可能的后果？",
            listOf("自动补上地址", "把 n 的值当作指针写入，未定义行为/崩溃", "编译器改成更大的整数类型", "只会少读一位小数"),
            1,
            "scanf 需要 int*。漏 & 是 Day 2 的经典段错误。",
        ),
        QuizQuestion(
            "d2-q2",
            "两个 int 做 5/2 的结果是？",
            listOf("2.5", "2", "3（四舍五入）", "编译错误"),
            1,
            "整数除法截断向 0。",
        ),
        QuizQuestion(
            "d2-q3",
            "printf(\"%d\", 3.14) 为什么危险？",
            listOf("格式与参数类型不符，属于未定义行为", "3.14 不能出现在 C 里", "应写成 puts", "%d 会自动转换所以安全"),
            0,
            "合同必须对齐。正确是 %f 并传 double。",
        ),
        QuizQuestion(
            "d2-q4",
            "局部 int x 在函数返回后？",
            listOf("由运行时决定何时释放", "栈帧结束，不应再取它的地址", "永远在堆上", "会自动变成全局变量"),
            1,
            "没有自动回收把寿命延长。",
        ),
    ),
)
