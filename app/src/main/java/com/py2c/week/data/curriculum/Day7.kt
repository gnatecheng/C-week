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

internal fun day7(): CourseDay = CourseDay(
    id = 7,
    title = "大作业：Dijkstra 最短路",
    subtitle = "正权图 · 邻接表 · O(V²) 教学实现",
    outcome = "独立补全松弛循环，能解释与 BFS 的差别，并在 VS Code 里编译运行测试。",
    minutes = 130,
    todayFocus = "导航不算「过几条街」，而算分钟。近路可能绕一圈更便宜。盖章的最短路不再改。",
    lessons = listOf(
        Lesson(
            id = "d7-l1",
            title = "问题：加权最短路",
            minutes = 12,
            summary = "从 s 到每个点的最小边权之和；边权非负。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：导航按分钟，不按路口数",
                    "BFS 数的是过了几条街（每条边当 1）。Dijkstra 数的是路上花的分钟。直达可能堵车 40 分钟，绕一条畅通的小路 2+1 分钟反而更快。负的「时间倒流」会破坏「已经确定的站点不再改」——本算法不管负权。",
                ),
                Paragraph("输入一张有向或无向图，边有正权重（本课允许 0，不允许负）。求 dist[u]：从起点 s 走到 u 的最小代价。到不了则保持 INF。"),
                Bullets(
                    listOf(
                        "BFS 数的是边数，把每条边看成 1。",
                        "Dijkstra 数的是权重和。第一次「确定」某个点时，它的 dist 就是最终答案。",
                        "负权会破坏「确定后不再变」——那不是本算法的锅。",
                    ),
                ),
                Code(
                    "text",
                    "边 0→1 权 1，0→2 权 4，1→2 权 1\nBFS 边数：0→2 若有直边是 1 步\nDijkstra 权：0→1→2 = 2，优于直边 4",
                    "若你用 BFS 做加权图，答案会错。实验里有专门对照。",
                ),
            ),
        ),
        Lesson(
            id = "d7-l2",
            title = "算法步骤（O(V²) 版）",
            minutes = 18,
            summary = "used[] 标记已确定；每轮选 dist 最小的未确定点，松弛出边。",
            blocks = listOf(
                Heading("伪代码"),
                Code(
                    "text",
                    "dist[s] = 0，其余 INF\n重复 V 次:\n    选一个未 used 且 dist 最小的 u（没有则停）\n    used[u] = 1\n    对 u 的每条边 u→v, w:\n        if dist[v] > dist[u] + w:\n            dist[v] = dist[u] + w",
                    "松弛：发现一条更短路径就更新。",
                ),
                Paragraph("用堆可以把「选最小 u」从 O(V) 降到 O(log V)，总体 O((V+E) log V)。教学先把松弛写对。顶点 ≤ 400 时 O(V²) 很稳。"),
                Callout(
                    CalloutKind.WARN,
                    "INF 的选择",
                    "INF 要大到超过任何合法路径，又要保证 dist[u] + w 不溢出。int 可用 1e9；需要更大就用 long long 和 4e18。",
                ),
            ),
        ),
        Lesson(
            id = "d7-l3",
            title = "邻接表带权",
            minutes = 10,
            summary = "在 Day 6 的 to/nxt 上加 w[]。",
            blocks = listOf(
                Code(
                    "c",
                    "int head[MAXN], to[MAXM], w[MAXM], nxt[MAXM], eid;\nvoid add(int u, int v, int c) {\n    to[eid] = v; w[eid] = c; nxt[eid] = head[u]; head[u] = eid++;\n}",
                    "松弛时读 w[e]，不是默认 1。",
                ),
                VsCode("new_source_dijkstra"),
            ),
        ),
        Lesson(
            id = "d7-l4",
            title = "正确性直觉与练习题",
            minutes = 12,
            summary = "非负权下，当前最小未确定点不可能再被更短路径赶上。",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "生活类比：路书盖章",
                    "所有路段耗时都 ≥ 0 时：当前「未盖章里耗时最小」的那个点，不可能再被别的绕路追上——再走只会长或持平。于是给它盖章（used），dist 冻结。这和 BFS 按层盖章是亲戚，只是「层」换成了分钟数。",
                ),
                Paragraph("因为所有边权 ≥ 0，任何还在路上的路径再走下去只会更长或持平，不可能比「当前最小 dist」更小。所以把 u 标 used 后 dist[u] 冻结。这和 BFS 分层冻结是亲戚，只是「层」换成了数值距离。"),
                Heading("动手题（电脑上做）"),
                Bullets(
                    listOf(
                        "题 1：n=3，边 0→1 权 5，0→2 权 1，2→1 权 1。源 0。答案 dist = 0, 2, 1。",
                        "题 2：加一条 1→2 权 10，不应改变 2 的距离。",
                        "题 3：孤立点 dist 保持 INF。打印时约定输出 -1。",
                    ),
                ),
                Callout(
                    CalloutKind.KEY,
                    "本周毕业标准",
                    "能不看答案写出松弛；能解释为何不能把 BFS 直接用于加权；能在 VS Code 里 -Wall 编译通过。指针/数组没有明显越界。",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d7",
        title = "大作业：补全 Dijkstra 松弛",
        brief = "O(V²) 模板已搭好。你只需要写出「选 u 之后」对邻接表的松弛。",
        task = "图：0→1 (4), 0→2 (1), 2→1 (1), 1→3 (1), 2→3 (5)。源点 0。期望 dist：0 2 1 3。完成后可对照测试用例，三次失败后允许揭晓答案。",
        starterCode = """
#include <stdio.h>
#define N 4
#define M 16
#define INF 1000000000
int head[N], to[M], w[M], nxt[M], eid;
int dist[N], used[N];

void add(int u, int v, int c) {
    to[eid] = v; w[eid] = c; nxt[eid] = head[u]; head[u] = eid++;
}

int main(void) {
    for (int i = 0; i < N; i++) { head[i] = -1; dist[i] = INF; used[i] = 0; }
    eid = 0;
    add(0,1,4); add(0,2,1); add(2,1,1); add(1,3,1); add(2,3,5);
    dist[0] = 0;
    for (int it = 0; it < N; it++) {
        int u = -1;
        for (int i = 0; i < N; i++) {
            if (!used[i] && (u < 0 || dist[i] < dist[u])) u = i;
        }
        if (u < 0 || dist[u] >= INF) break;
        used[u] = 1;
        for (int e = head[u]; e != -1; e = nxt[e]) {
            int v = to[e];
            /* TODO: 若 dist[u] + w[e] 更优，更新 dist[v] */
        }
    }
    for (int i = 0; i < N; i++) {
        if (dist[i] >= INF) printf("-1 ");
        else printf("%d ", dist[i]);
    }
    printf("\n");
    return 0;
}
""".trimIndent(),
        solutionCode = """
#include <stdio.h>
#define N 4
#define M 16
#define INF 1000000000
int head[N], to[M], w[M], nxt[M], eid;
int dist[N], used[N];

void add(int u, int v, int c) {
    to[eid] = v; w[eid] = c; nxt[eid] = head[u]; head[u] = eid++;
}

int main(void) {
    for (int i = 0; i < N; i++) { head[i] = -1; dist[i] = INF; used[i] = 0; }
    eid = 0;
    add(0,1,4); add(0,2,1); add(2,1,1); add(1,3,1); add(2,3,5);
    dist[0] = 0;
    for (int it = 0; it < N; it++) {
        int u = -1;
        for (int i = 0; i < N; i++) {
            if (!used[i] && (u < 0 || dist[i] < dist[u])) u = i;
        }
        if (u < 0 || dist[u] >= INF) break;
        used[u] = 1;
        for (int e = head[u]; e != -1; e = nxt[e]) {
            int v = to[e];
            if (dist[v] > dist[u] + w[e]) {
                dist[v] = dist[u] + w[e];
            }
        }
    }
    for (int i = 0; i < N; i++) {
        if (dist[i] >= INF) printf("-1 ");
        else printf("%d ", dist[i]);
    }
    printf("\n");
    return 0;
}
""".trimIndent(),
        expectedOutput = "0 2 1 3 \n",
        attemptsBeforeReveal = 3,
        isCapstone = true,
        testCases = listOf(
            LabTestCase(
                "主测",
                "边 0→1:4, 0→2:1, 2→1:1, 1→3:1, 2→3:5；源 0",
                "0 2 1 3",
            ),
            LabTestCase(
                "不是数边",
                "写成 dist[u]+1 会得到 0 1 1 2；0→2→1 的代价是 2 不是 1",
                "0 2 1 3",
                extraChecks = listOf(
                    LabCheck(
                        "nobfs-case",
                        "不要写成 dist[v] = dist[u] + 1，那是 BFS。",
                        CheckRule.NotContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1"""),
                    ),
                ),
            ),
            LabTestCase(
                "先比较再赋值",
                "松弛必须带 if (dist[v] > dist[u] + w[e])，无条件覆盖会写坏更短路",
                "0 2 1 3",
                extraChecks = listOf(
                    LabCheck(
                        "cmp-case",
                        "松弛条件应类似 dist[v] > dist[u] + w[e]。",
                        CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*>\s*dist\s*\[\s*u\s*\]\s*\+\s*w"""),
                    ),
                ),
            ),
        ),
        checks = listOf(
            LabCheck(
                "cmp",
                "松弛条件应类似 dist[v] > dist[u] + w[e]（严格更短才更新即可）。",
                CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*>\s*dist\s*\[\s*u\s*\]\s*\+\s*w\s*\[\s*e\s*\]"""),
            ),
            LabCheck(
                "assign",
                "更新：dist[v] = dist[u] + w[e]。",
                CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*w\s*\[\s*e\s*\]"""),
            ),
            LabCheck("todo", "删掉 TODO 注释或把它补全。", CheckRule.NotContains("TODO")),
            LabCheck("nobfs", "不要写成 dist[v] = dist[u] + 1，那是 BFS。", CheckRule.NotContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""")),
        ),
        hints = listOf(
            "松弛只看边权 w[e]，不是 1。",
            "used[u]=1 之后不要把 u 再加入候选。模板已经做了。",
            "电脑上：gcc dijkstra.c -o dijkstra -Wall && ./dijkstra",
            "若 dist[1] 仍是 4，说明你没松弛 2→1 这条边。",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d7-q1",
            "Dijkstra 要求边权？",
            listOf("任意整数，含负", "非负（本课）", "必须全是 1", "必须无环"),
            1,
            "负权用 Bellman-Ford 等。可以有环，只要权非负。",
        ),
        QuizQuestion(
            "d7-q2",
            "O(V²) 实现每轮在干什么？",
            listOf("随机删一条边", "在未确定点中选 dist 最小者再松弛", "按输入顺序选点", "只跑 BFS"),
            1,
            "堆优化版用优先队列代替线性扫描。",
        ),
        QuizQuestion(
            "d7-q3",
            "为何 BFS 不能替换本题？",
            listOf("BFS 不能处理邻接表", "边权不全相等时，先到达不代表代价最小", "Dijkstra 更短所以语法不同", "C 语言禁止队列"),
            1,
            "主测里 0→1 直边更早但更贵。",
        ),
        QuizQuestion(
            "d7-q4",
            "dist[u] + w 溢出时可能发生？",
            listOf("自动变成更大的类型", "变成很小的数，错误地通过松弛比较", "编译器插入检查", "只影响打印格式"),
            1,
            "所以 INF 与类型要一起设计。",
        ),
    ),
)
