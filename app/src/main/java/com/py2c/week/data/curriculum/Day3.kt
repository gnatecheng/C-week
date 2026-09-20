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

internal fun day3(): CourseDay = CourseDay(
    id = 3,
    title = "控制流与函数",
    subtitle = "if/for/while · 原型 · 传值",
    outcome = "能写带原型的函数，理解 C 传值；知道为什么 swap 必须用指针（为 Day 4 铺路）。",
    minutes = 95,
    todayFocus = "菜单上先写菜名（函数原型），后厨再做。传值是复印一份作业；要改原件，得把原件的门牌号递进去。",
    lessons = listOf(
        Lesson(
            id = "d3-l1",
            title = "if、else 与花括号",
            minutes = 10,
            summary = "条件是整数；花括号不是装饰。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：食谱步骤用括号框住",
                    "{} 像食谱里用框圈起来的步骤：框住的才算「如果…就做这些」。只靠缩进、不写括号，厨师（编译器）会按自己的规则理解下一行，容易漏做或多做。== 是比较，= 是往盒子里装东西；if (x = 0) 合法，但几乎总是把 0 装进去再问「盒子是不是空」。",
                ),
                Paragraph("C 的 if (x) 把非 0 当真。没有 elif 关键字，写成 else if。比较用 == 不是 =；一个 = 是赋值，if (x = 0) 合法但几乎总是错。"),
                Example(
                    title = "条件",
                    source = "if (score >= 60) {\n    printf(\"pass\\n\");\n} else if (score >= 0) {\n    printf(\"fail\\n\");\n} else {\n    printf(\"bad\\n\");\n}",
                    note = "即使只有一条语句也建议打括号。C 用 {} 定边界。混用缩进不打括号，是著名的 goto fail 类漏洞来源。",
                ),
            ),
        ),
        Lesson(
            id = "d3-l2",
            title = "for / while：索引从 0 开始",
            minutes = 12,
            summary = "i 从 0 到 n-1；off-by-one 预告。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：书架格子从 0 号数",
                    "一排 n 个储物柜，标签从 0 贴到 n-1。for (i = 0; i < n; i++) 是逐个打开。写成 i <= n 就会去开不存在的第 n 号柜——越界。忘记 i++ 等于永远停在同一格，死循环。",
                ),
                Example(
                    title = "遍历 0..n-1",
                    source = "for (int i = 0; i < n; i++) {\n    printf(\"%d\\n\", i);\n}",
                    note = "C99 起可以在 for 里声明 int i。循环条件是 i < n 不是 i <= n——后面数组长度是 n 时，<= 就会越界。",
                ),
                Code(
                    "c",
                    "int i = 0;\nwhile (i < n) {\n    /* body */\n    i++;\n}",
                    "while 等价形式。忘记 i++ 就是死循环。",
                ),
                Callout(
                    CalloutKind.WARN,
                    "不要写成 for i in n",
                    "C 没有那种迭代器写法。典型就是 for (int i = 0; i < n; i++)。实验模拟器会检查。",
                ),
            ),
        ),
        Lesson(
            id = "d3-l3",
            title = "函数：声明、定义、返回值",
            minutes = 14,
            summary = "原型让编译器知道参数类型；void 表示不返回。",
            blocks = listOf(
                Heading("一份最小拆分"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：菜单先写菜名",
                    "函数原型 int square(int x); 像菜单上先印「平方：要一个整数」。编译器从上往下读，没见过的名字会报错。定义是后厨真正的做法；也可以把做法写在 main 前面从而省略菜单，多文件项目则必须靠头文件里的原型。",
                ),
                Code(
                    "c",
                    "#include <stdio.h>\n\nint square(int x);          /* 原型：告诉后面的 main */\n\nint main(void) {\n    printf(\"%d\\n\", square(5));\n    return 0;\n}\n\nint square(int x) {          /* 定义：真正的身体 */\n    return x * x;\n}",
                    "也可以把定义写在 main 前面，从而省略原型。多文件项目必须靠头文件里的原型。",
                ),
                Example(
                    title = "没有默认参数、不能靠同名重载",
                    source = "int add2(int x, int y) { return x + y; }\nint add1(int x) { return add2(x, 0); }",
                    note = "C 函数名在链接期是唯一的。需要「缺省值」就自己写两个函数。C++ 才允许重载。",
                ),
            ),
        ),
        Lesson(
            id = "d3-l4",
            title = "传值：swap 为什么换不了",
            minutes = 14,
            summary = "参数是副本；要改外面的变量就传地址。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：复印作业 vs 把原件地址给人",
                    "传值像把作业复印一份交给同学改：他改的是复印件，你桌上的原件不动。要让他改原件，就得把原件所在课桌的门牌号递过去（传指针）。swap(int a, int b) 换的是两份复印件，所以外面的 x、y 不变。",
                ),
                Paragraph("C 所有实参都按值拷贝进函数。修改参数 x 不会改调用者的变量。"),
                Example(
                    title = "失败的 swap",
                    source = "void swap(int a, int b) {\n    int t = a; a = b; b = t;\n}\nint x = 1, y = 2;\nswap(x, y); /* x,y 仍是 1,2 */",
                    note = "明天用 void swap(int *a, int *b) { int t = *a; *a = *b; *b = t; } 并调用 swap(&x, &y)。",
                ),
                Callout(
                    CalloutKind.KEY,
                    "要改外面的变量，就传它的地址",
                    "scanf 已经这么干了。指针不是神秘语法，是「把房子门牌号递进去，而不是把整栋房子复印一份」。",
                ),
            ),
        ),
        Lesson(
            id = "d3-l5",
            title = "递归与栈帧（直觉）",
            minutes = 10,
            summary = "每次调用一层栈；太深会栈溢出。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：叠盘子",
                    "每次递归调用像再叠一只盘子。C 的托盘（栈）不大，叠太高会整摞倒下（栈溢出），不一定有客气提示。树可以递归；最坏的图更稳妥的是自己用数组当盘子堆，或改 BFS。",
                ),
                Paragraph("递归每次调用压一层栈。C 的栈通常不大，太深会直接崩，不一定有友好提示。图算法里 DFS 递归可以对树，对最坏图更稳妥的是显式栈或改 BFS。"),
                Code(
                    "c",
                    "int fact(int n) {\n    if (n <= 1) return 1;\n    return n * fact(n - 1);\n}",
                    "教学用阶乘。注意 n 大时既栈深又 int 溢出。",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d3",
        title = "实验：写一个 is_prime",
        brief = "判断正整数是否为素数，返回 1 或 0。",
        task = "实现 int is_prime(int n)。n < 2 返回 0。用试除到 i*i <= n。main 已读取 n 并打印 Yes/No。",
        starterCode = """
#include <stdio.h>

int is_prime(int n) {
    /* TODO: 返回 1 表示素数，0 表示不是 */
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
        expectedOutput = "输入 13 → Yes\n输入 1 → No\n输入 9 → No\n",
        testCases = listOf(
            LabTestCase("素数 13", "输入 13", "Yes", input = "13"),
            LabTestCase("1 不是素数", "输入 1", "No", input = "1"),
            LabTestCase("合数 9", "输入 9", "No", input = "9"),
            LabTestCase("最小素数 2", "输入 2", "Yes", input = "2"),
        ),
        checks = listOf(
            LabCheck("sig", "请保持函数签名 int is_prime(int n)。", CheckRule.ContainsRegex("""int\s+is_prime\s*\(\s*int""")),
            LabCheck("lt2", "先处理 n < 2。", CheckRule.ContainsRegex("""n\s*<\s*2""")),
            LabCheck("mod", "用 % 判断整除。", CheckRule.Contains("%")),
            LabCheck("loop", "需要循环试除。", CheckRule.ContainsRegex("""for\s*\(""")),
        ),
        hints = listOf(
            "i * i <= n 可避免写 sqrt。注意 i*i 在 i 很大时可能溢出，本题 n 按 int 教学范围即可。",
            "2 是素数：循环从 2 开始，进不去循环，返回 1。",
            "电脑上测：echo 13 | ./prime",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d3-q1",
            "C 函数参数默认如何传递？",
            listOf("传引用，函数内赋值会改调用者", "传值，参数是副本", "由编译器随机决定", "数组和其他类型规则完全相反"),
            1,
            "要改调用者必须传指针（地址）。",
        ),
        QuizQuestion(
            "d3-q2",
            "for (int i = 0; i <= n; i++) 访问 a[0..n-1] 时？",
            listOf("正好", "多访问一次 a[n]，越界风险", "少访问一次", "语法非法"),
            1,
            "经典 off-by-one。条件用 i < n。",
        ),
        QuizQuestion(
            "d3-q3",
            "为什么常在文件上部写 int foo(int);？",
            listOf("为了让 main 先调用、定义放后面时编译器仍知道类型", "C 强制所有函数必须声明两遍", "链接器只认分号", "可有可无，删了也能过任何编译器"),
            0,
            "这就是函数原型。",
        ),
        QuizQuestion(
            "d3-q4",
            "if (x = 0) 的问题是？",
            listOf("语法错误，不能编译", "把 0 赋给 x，条件恒假，通常是误写 ==", "会抛异常", "等价于 if (x == 0)"),
            1,
            "赋值表达式的值是赋进去的值。开 -Wall 往往会警告。",
        ),
    ),
)
