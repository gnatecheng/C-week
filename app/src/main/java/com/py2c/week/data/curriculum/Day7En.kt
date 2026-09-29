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

internal fun day7En(): CourseDay = CourseDay(
    id = 7,
    title = "Capstone: Dijkstra shortest paths",
    subtitle = "Non-negative weights · adjacency list · O(V²) teaching implementation",
    outcome = "Complete the relaxation loop on your own, explain how it differs from BFS, and compile and test in VS Code.",
    minutes = 130,
    todayFocus = "Navigation counts minutes, not intersections. A longer route can be cheaper. Once a stop is stamped, its shortest time does not change.",
    lessons = listOf(
        Lesson(
            id = "d7-l1",
            title = "Problem: weighted shortest paths",
            minutes = 12,
            summary = "Minimum sum of edge weights from s to each vertex; weights non-negative.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: GPS by minutes, not corner count",
                    "BFS counts how many streets you cross (each edge is 1). Dijkstra counts minutes on the road. A direct highway might be 40 minutes in traffic; a longer but clear side route 2+1 minutes can win. Negative \"time travel\" breaks \"stamped stops never change\"—this algorithm does not handle negative weights.",
                ),
                Paragraph("Input: a directed or undirected graph with positive edge weights (0 allowed here, negatives not). Compute dist[u]: minimum cost from start s to u. Unreachable stays INF."),
                Bullets(
                    listOf(
                        "BFS counts edges, treating each as 1.",
                        "Dijkstra sums weights. The first time you \"finalize\" a vertex, its dist is final.",
                        "Negative weights break \"finalize then never change\"—that is not this algorithm's fault.",
                    ),
                ),
                Code(
                    "text",
                    "Edge 0→1 weight 1, 0→2 weight 4, 1→2 weight 1\nBFS edge count: 0→2 direct is 1 step if it exists\nDijkstra weight: 0→1→2 = 2, beats direct 4",
                    "Using BFS on a weighted graph gives wrong answers. The lab has a deliberate contrast.",
                ),
            ),
        ),
        Lesson(
            id = "d7-l2",
            title = "Algorithm steps (O(V²) version)",
            minutes = 18,
            summary = "used[] marks finalized vertices; each round pick the unsettled vertex with smallest dist, relax outgoing edges.",
            blocks = listOf(
                Heading("Pseudocode"),
                Code(
                    "text",
                    "dist[s] = 0, others INF\nRepeat V times:\n    pick unsettled u with minimum dist (stop if none)\n    used[u] = 1\n    for each edge u→v, weight w:\n        if dist[v] > dist[u] + w:\n            dist[v] = dist[u] + w",
                    "Relaxation: update when you find a shorter path.",
                ),
                Paragraph("A heap drops \"pick minimum u\" from O(V) to O(log V), overall O((V+E) log V). For teaching, get relaxation right first. With ≤400 vertices, O(V²) is fine."),
                Callout(
                    CalloutKind.WARN,
                    "Choosing INF",
                    "INF must exceed any real path but keep dist[u] + w from overflowing. int often uses 1e9; for larger needs use long long and 4e18.",
                ),
            ),
        ),
        Lesson(
            id = "d7-l3",
            title = "Weighted adjacency list",
            minutes = 10,
            summary = "Add w[] on top of Day 6's to/nxt.",
            blocks = listOf(
                Code(
                    "c",
                    "int head[MAXN], to[MAXM], w[MAXM], nxt[MAXM], eid;\nvoid add(int u, int v, int c) {\n    to[eid] = v; w[eid] = c; nxt[eid] = head[u]; head[u] = eid++;\n}",
                    "On relax, read w[e], not default 1.",
                ),
                VsCode("new_source_dijkstra"),
            ),
        ),
        Lesson(
            id = "d7-l4",
            title = "Correctness intuition & practice",
            minutes = 12,
            summary = "With non-negative weights, the current smallest unsettled vertex cannot be improved by a detour.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: stamping a route book",
                    "When every leg takes ≥0 time: among unstamped stops, the one with smallest time so far cannot be beaten by going elsewhere first—any continuation is longer or equal. Stamp it (used); dist freezes. Cousin to BFS layering, but \"layers\" are numeric distance.",
                ),
                Paragraph("Because all edge weights are ≥0, any path still in progress can only get longer or stay equal—it cannot beat the current minimum dist. So after used[u]=1, dist[u] is frozen. Same family as BFS layer freezing, but layers are numeric distances."),
                Heading("Hands-on (on your machine)"),
                Bullets(
                    listOf(
                        "Problem 1: n=3, edges 0→1 weight 5, 0→2 weight 1, 2→1 weight 1. Source 0. Answer dist = 0, 2, 1.",
                        "Problem 2: Add 1→2 weight 10; should not change dist to 2.",
                        "Problem 3: Isolated vertex keeps INF. Print as -1 by convention.",
                    ),
                ),
                Callout(
                    CalloutKind.KEY,
                    "Graduation bar for this week",
                    "Write relaxation without peeking; explain why BFS cannot replace this on weighted graphs; compile with -Wall in VS Code. No obvious pointer/array out-of-bounds.",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d7",
        title = "Capstone: complete Dijkstra relaxation",
        brief = "O(V²) template is ready. You only write relaxation over the adjacency list after picking u.",
        task = "Graph: 0→1 (4), 0→2 (1), 2→1 (1), 1→3 (1), 2→3 (5). Source 0. Expected dist: 0 2 1 3. After three failed attempts you may reveal the answer.",
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
            /* TODO: if dist[u] + w[e] is better, update dist[v] */
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
                "Main test",
                "Edges 0→1:4, 0→2:1, 2→1:1, 1→3:1, 2→3:5; source 0",
                "0 2 1 3",
            ),
            LabTestCase(
                "Not edge counting",
                "dist[u]+1 gives 0 1 1 2; cost 0→2→1 is 2 not 1",
                "0 2 1 3",
                extraChecks = listOf(
                    LabCheck(
                        "nobfs-case",
                        "Do not write dist[v] = dist[u] + 1—that is BFS.",
                        CheckRule.NotContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1"""),
                    ),
                ),
            ),
            LabTestCase(
                "Compare before assign",
                "Relaxation needs if (dist[v] > dist[u] + w[e]); blind overwrite breaks shorter routes",
                "0 2 1 3",
                extraChecks = listOf(
                    LabCheck(
                        "cmp-case",
                        "Relax condition should look like dist[v] > dist[u] + w[e].",
                        CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*>\s*dist\s*\[\s*u\s*\]\s*\+\s*w"""),
                    ),
                ),
            ),
        ),
        checks = listOf(
            LabCheck(
                "cmp",
                "Relax condition like dist[v] > dist[u] + w[e] (update only when strictly shorter).",
                CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*>\s*dist\s*\[\s*u\s*\]\s*\+\s*w\s*\[\s*e\s*\]"""),
            ),
            LabCheck(
                "assign",
                "Update: dist[v] = dist[u] + w[e].",
                CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*w\s*\[\s*e\s*\]"""),
            ),
            LabCheck("todo", "Remove the TODO comment or complete it.", CheckRule.NotContains("TODO")),
            LabCheck("nobfs", "Do not write dist[v] = dist[u] + 1—that is BFS.", CheckRule.NotContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""")),
        ),
        hints = listOf(
            "Relaxation uses edge weight w[e], not 1.",
            "After used[u]=1, do not put u back in the candidate pool—the template already handles that.",
            "On your machine: gcc dijkstra.c -o dijkstra -Wall && ./dijkstra",
            "If dist[1] is still 4, you did not relax the 2→1 edge.",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d7-q1",
            "Dijkstra requires edge weights to be?",
            listOf("Any integers, including negative", "Non-negative (this course)", "All exactly 1", "Acyclic only"),
            1,
            "Negative weights use Bellman-Ford, etc. Cycles are OK if weights are non-negative.",
        ),
        QuizQuestion(
            "d7-q2",
            "What does each round of the O(V²) implementation do?",
            listOf("Delete a random edge", "Pick unsettled vertex with smallest dist, then relax", "Pick vertices in input order", "Run BFS only"),
            1,
            "Heap version uses a priority queue instead of linear scan.",
        ),
        QuizQuestion(
            "d7-q3",
            "Why can't BFS replace this problem?",
            listOf("BFS cannot use adjacency lists", "When weights differ, arriving first ≠ minimum cost", "Dijkstra is shorter so syntax differs", "C forbids queues"),
            1,
            "Main test: direct 0→1 is reached early but costs more.",
        ),
        QuizQuestion(
            "d7-q4",
            "If dist[u] + w overflows, what can happen?",
            listOf("Auto-promote to a wider type", "Wrap to a small number and pass relax checks wrongly", "Compiler inserts checks", "Only affects printf"),
            1,
            "Design INF and integer type together.",
        ),
    ),
)
