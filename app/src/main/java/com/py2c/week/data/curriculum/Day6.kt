package com.py2c.week.data.curriculum

import com.py2c.week.data.CalloutKind
import com.py2c.week.data.CheckRule
import com.py2c.week.data.CodeLab
import com.py2c.week.data.ContentBlock.Bullets
import com.py2c.week.data.ContentBlock.Callout
import com.py2c.week.data.ContentBlock.Code
import com.py2c.week.data.ContentBlock.Heading
import com.py2c.week.data.ContentBlock.Paragraph
import com.py2c.week.data.ContentBlock.VsCode
import com.py2c.week.data.CourseDay
import com.py2c.week.data.LabCheck
import com.py2c.week.data.LabTestCase
import com.py2c.week.data.Lesson
import com.py2c.week.data.QuizQuestion

internal fun day6(): CourseDay = CourseDay(
    id = 6,
    title = "算法热身：排序与 BFS",
    subtitle = "选择排序 · 邻接表 · 网格/图 BFS",
    outcome = "能用 C 数组实现选择排序，并用队列做无权最短路（BFS）。",
    minutes = 120,
    todayFocus = "食堂排队（队列）：先到先打饭。每条路一样长时，最先走到的就是最近（BFS）。",
    lessons = listOf(
        Lesson(
            id = "d6-l1",
            title = "复杂度直觉（给一周课够用的）",
            minutes = 10,
            summary = "O(n^2) 排序、O(V+E) 遍历；常数因缓存会变，但阶更要紧。",
            blocks = listOf(
                Bullets(
                    listOf(
                        "双重循环扫 n×n：大约 n²。n=1000 还能教着用，n=1e5 不行。",
                        "图遍历每条边看一次：O(V+E)。",
                        "BFS 无权最短路；正权用 Dijkstra（明天）。负权需要别的算法，本周不覆盖。",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "为什么还手写选择排序",
                    "标准库有更好的排序。自己写是为了把「交换、下标、边界」练会，不是为了生产。",
                ),
            ),
        ),
        Lesson(
            id = "d6-l2",
            title = "选择排序：纯数组练习",
            minutes = 14,
            summary = "每轮找最小，换到前面。",
            blocks = listOf(
                Code(
                    "c",
                    "void selection_sort(int *a, int n) {\n    for (int i = 0; i < n; i++) {\n        int m = i;\n        for (int j = i + 1; j < n; j++) {\n            if (a[j] < a[m]) m = j;\n        }\n        int t = a[i]; a[i] = a[m]; a[m] = t;\n    }\n}",
                    "注意 j 从 i+1 起，比较的是值不是下标。交换必须用临时变量。",
                ),
            ),
        ),
        Lesson(
            id = "d6-l3",
            title = "邻接表：用数组模拟链表",
            minutes = 16,
            summary = "to[] / nxt[] / head[] 是竞赛 C 的常见写法，指针版也可以。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：通讯录后面跟一串朋友电话",
                    "邻接表像每人一张通讯录卡片：head[u] 是 u 的第一位朋友，nxt 指向下一位。数组版是把所有卡片预先印好、用下标当页码，省得一张张去仓库租。无向「认识」要两边各记一次。",
                ),
                Paragraph("链表版每个边一块 malloc，教学直观。数组版预分配所有边，下标当指针，更快也更不容易漏 free。两种在本课都认。"),
                Code(
                    "c",
                    "#define MAXN 100\n#define MAXM 400\nint head[MAXN], to[MAXM], nxt[MAXM], eid;\n\nvoid init_graph(int n) {\n    for (int i = 0; i < n; i++) head[i] = -1;\n    eid = 0;\n}\nvoid add_edge(int u, int v) {\n    to[eid] = v; nxt[eid] = head[u]; head[u] = eid++;\n}",
                    "遍历 u 的出边：for (int e = head[u]; e != -1; e = nxt[e]) { int v = to[e]; }",
                ),
                Callout(
                    CalloutKind.TIP,
                    "和无向图",
                    "add_edge(u,v); add_edge(v,u); 两条有向边模拟无向边。",
                ),
            ),
        ),
        Lesson(
            id = "d6-l4",
            title = "BFS：队列 + dist[]",
            minutes = 18,
            summary = "无权图最短路；网格是四连通的特殊图。",
            blocks = listOf(
                Heading("算法"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：食堂排队 + 一样长的小路",
                    "队列像食堂打饭：qh 是队头（下一个轮到谁），qt 是队尾后面的空位。边权全是 1 时，像每条小路一样长：从家出发，先访隔壁再访更远，第一次走到某栋楼就是最少步数。小路有的要爬坡（权重大），先到的不一定最省力——那是明天 Dijkstra。",
                ),
                Bullets(
                    listOf(
                        "dist 全部置 -1 表示未访问。",
                        "起点 dist[s]=0，入队。",
                        "出队 u，对每个邻居 v，若 dist[v]<0 则 dist[v]=dist[u]+1 并入队。",
                    ),
                ),
                Code(
                    "c",
                    "int q[MAXN], qh, qt;\nint dist[MAXN];\n\nvoid bfs(int n, int s) {\n    for (int i = 0; i < n; i++) dist[i] = -1;\n    qh = qt = 0;\n    dist[s] = 0;\n    q[qt++] = s;\n    while (qh < qt) {\n        int u = q[qh++];\n        for (int e = head[u]; e != -1; e = nxt[e]) {\n            int v = to[e];\n            if (dist[v] < 0) {\n                dist[v] = dist[u] + 1;\n                q[qt++] = v;\n            }\n        }\n    }\n}",
                    "qh 是队头（下一个出队），qt 是下一个入队位置。不要每次把数组整体前移。",
                ),
                Callout(
                    CalloutKind.KEY,
                    "为什么 BFS 是最短",
                    "边权全是 1 时，第一次到达 v 的路径就是最少边数。加权之后这个结论不成立，所以明天要 Dijkstra。",
                ),
            ),
        ),
        Lesson(
            id = "d6-l5",
            title = "在 VS Code 里跑 BFS",
            minutes = 8,
            summary = "新建文件、开警告编译、用样例喂输入。",
            blocks = listOf(
                VsCode("new_source_dijkstra"),
                Paragraph("把上面演示里的 dijkstra.c 换成 bfs.c 即可。建议：gcc bfs.c -o bfs -Wall 然后用重定向提供图数据，例如 echo 数据 | ./bfs 或 ./bfs < graph.txt。"),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d6",
        title = "实验：补全 BFS 松弛",
        brief = "给定邻接表，填空让 dist 成为从 0 出发的无权距离。",
        task = "在标记处：若邻居未访问，设置 dist 并入队。图：0-1, 0-2, 1-3。期望 dist: 0 1 1 2。",
        starterCode = """
#include <stdio.h>
#define N 4
#define M 8
int head[N], to[M], nxt[M], eid;
int q[N], dist[N];

void add(int u, int v) {
    to[eid] = v; nxt[eid] = head[u]; head[u] = eid++;
}

int main(void) {
    for (int i = 0; i < N; i++) { head[i] = -1; dist[i] = -1; }
    eid = 0;
    add(0,1); add(1,0);
    add(0,2); add(2,0);
    add(1,3); add(3,1);
    int qh = 0, qt = 0;
    dist[0] = 0;
    q[qt++] = 0;
    while (qh < qt) {
        int u = q[qh++];
        for (int e = head[u]; e != -1; e = nxt[e]) {
            int v = to[e];
            /* TODO: 若 dist[v] < 0，更新 dist 并入队 */
        }
    }
    for (int i = 0; i < N; i++) printf("%d ", dist[i]);
    printf("\n");
    return 0;
}
""".trimIndent(),
        solutionCode = """
#include <stdio.h>
#define N 4
#define M 8
int head[N], to[M], nxt[M], eid;
int q[N], dist[N];

void add(int u, int v) {
    to[eid] = v; nxt[eid] = head[u]; head[u] = eid++;
}

int main(void) {
    for (int i = 0; i < N; i++) { head[i] = -1; dist[i] = -1; }
    eid = 0;
    add(0,1); add(1,0);
    add(0,2); add(2,0);
    add(1,3); add(3,1);
    int qh = 0, qt = 0;
    dist[0] = 0;
    q[qt++] = 0;
    while (qh < qt) {
        int u = q[qh++];
        for (int e = head[u]; e != -1; e = nxt[e]) {
            int v = to[e];
            if (dist[v] < 0) {
                dist[v] = dist[u] + 1;
                q[qt++] = v;
            }
        }
    }
    for (int i = 0; i < N; i++) printf("%d ", dist[i]);
    printf("\n");
    return 0;
}
""".trimIndent(),
        expectedOutput = "0 1 1 2 \n",
        testCases = listOf(
            LabTestCase("样例图", "0 连 1、2；1 连 3。源点 0", "0 1 1 2"),
            LabTestCase(
                "必须入队",
                "更新 dist 后要 q[qt++] = v，否则 3 号点走不到",
                "0 1 1 2",
                extraChecks = listOf(
                    LabCheck(
                        "enq-case",
                        "访问后要入队 q[qt++] = v。",
                        CheckRule.ContainsRegex("""q\s*\[\s*qt\s*\+\+\s*\]\s*=\s*v"""),
                    ),
                ),
            ),
            LabTestCase(
                "无权 +1",
                "第一次到达即最短，写 dist[u]+1 而不是加边权",
                "0 1 1 2",
                extraChecks = listOf(
                    LabCheck(
                        "plus1-case",
                        "无权 BFS 用 dist[v] = dist[u] + 1。",
                        CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1"""),
                    ),
                ),
            ),
        ),
        checks = listOf(
            LabCheck("if", "需要判断 dist[v] 是否未访问（<0）。", CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*<\s*0""")),
            LabCheck("relax", "新距离应是 dist[u] + 1。", CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""")),
            LabCheck("enq", "访问后要入队 q[qt++] = v 或等价。", CheckRule.ContainsRegex("""q\s*\[\s*qt\s*\+\+\s*\]\s*=\s*v""")),
        ),
        hints = listOf(
            "未访问用 dist[v] < 0，不要用 vis 数组也行。",
            "先写 dist 再入队，顺序反了也通常能过，但请保持这个习惯。",
            "无权图不要写成 dist[v] > dist[u]+1 的松弛——那是 Dijkstra 的思维，BFS 第一次到达即最短。",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d6-q1",
            "BFS 能保证最短的前提是？",
            listOf("边权都相等（教学里当作 1）", "有负权边", "图必须是树", "必须用递归"),
            0,
            "正权且不全等时改用 Dijkstra。",
        ),
        QuizQuestion(
            "d6-q2",
            "数组队列 qh、qt 的含义？",
            listOf("qh 是容量，qt 是元素值", "qh 出队下标，qt 下一个入队位置", "两个都是栈顶", "必须等于 n"),
            1,
            "qh < qt 表示队列非空。",
        ),
        QuizQuestion(
            "d6-q3",
            "选择排序平均时间阶？",
            listOf("O(n)", "O(n log n)", "O(n²)", "O(1)"),
            2,
            "两层循环。",
        ),
        QuizQuestion(
            "d6-q4",
            "邻接表遍历边的复杂度？",
            listOf("O(V²) 必然", "O(V+E)", "O(E²)", "O(1)"),
            1,
            "每条边在链表/数组里出现常数次。",
        ),
    ),
)
