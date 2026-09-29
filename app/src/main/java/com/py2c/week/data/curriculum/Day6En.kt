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

internal fun day6En(): CourseDay = CourseDay(
    id = 6,
    title = "Algorithm warm-up: sorting & BFS",
    subtitle = "Selection sort · adjacency lists · grid/graph BFS",
    outcome = "Implement selection sort on a C array and run BFS for unweighted shortest paths with a queue.",
    minutes = 120,
    todayFocus = "Cafeteria line (queue): first in, first served. When every edge costs the same, whoever you reach first is closest (BFS).",
    lessons = listOf(
        Lesson(
            id = "d6-l1",
            title = "Complexity intuition (enough for this week)",
            minutes = 10,
            summary = "O(n²) sort, O(V+E) traversal; constants shift with cache, but order matters more.",
            blocks = listOf(
                Bullets(
                    listOf(
                        "Double loop over n×n: about n². Fine for n=1000 in teaching; not for n=1e5.",
                        "Visit each graph edge once: O(V+E).",
                        "BFS for unweighted shortest paths; positive weights → Dijkstra (tomorrow). Negative weights need other algorithms—not covered this week.",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "Why hand-write selection sort",
                    "The standard library has better sorts. You write this yourself to drill swaps, indices, and bounds—not for production.",
                ),
            ),
        ),
        Lesson(
            id = "d6-l2",
            title = "Selection sort: pure array practice",
            minutes = 14,
            summary = "Each round, find the minimum and swap it to the front.",
            blocks = listOf(
                Code(
                    "c",
                    "void selection_sort(int *a, int n) {\n    for (int i = 0; i < n; i++) {\n        int m = i;\n        for (int j = i + 1; j < n; j++) {\n            if (a[j] < a[m]) m = j;\n        }\n        int t = a[i]; a[i] = a[m]; a[m] = t;\n    }\n}",
                    "Note j starts at i+1; compare values, not indices. Swaps need a temporary variable.",
                ),
            ),
        ),
        Lesson(
            id = "d6-l3",
            title = "Adjacency list: linked lists with arrays",
            minutes = 16,
            summary = "to[] / nxt[] / head[] is common contest C; a pointer version works too.",
            blocks = listOf(
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: address book with a friend chain",
                    "An adjacency list is like a card per person: head[u] is u's first friend, nxt points to the next. The array version pre-prints every card and uses indices as page numbers—no malloc per edge. For undirected \"knows,\" record both directions.",
                ),
                Paragraph("The linked-list version malloc's each edge—intuitive to teach. The array version pre-allocates all edges and uses indices as pointers—faster and easier to free correctly. Know both in this course."),
                Code(
                    "c",
                    "#define MAXN 100\n#define MAXM 400\nint head[MAXN], to[MAXM], nxt[MAXM], eid;\n\nvoid init_graph(int n) {\n    for (int i = 0; i < n; i++) head[i] = -1;\n    eid = 0;\n}\nvoid add_edge(int u, int v) {\n    to[eid] = v; nxt[eid] = head[u]; head[u] = eid++;\n}",
                    "Walk u's outgoing edges: for (int e = head[u]; e != -1; e = nxt[e]) { int v = to[e]; }",
                ),
                Callout(
                    CalloutKind.TIP,
                    "Undirected graphs",
                    "add_edge(u,v); add_edge(v,u); — two directed edges simulate one undirected edge.",
                ),
            ),
        ),
        Lesson(
            id = "d6-l4",
            title = "BFS: queue + dist[]",
            minutes = 18,
            summary = "Shortest paths on unweighted graphs; a grid is a graph with four-way moves.",
            blocks = listOf(
                Heading("Algorithm"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: cafeteria line + equal-length paths",
                    "The queue is like the lunch line: qh is the front (who is served next), qt is the slot after the tail. When every edge costs 1, each path is the same length: from home you visit neighbors before farther buildings—the first time you reach a building is the fewest steps. Some paths \"cost more\" (weighted)—first arrival is not always cheapest; that's Dijkstra tomorrow.",
                ),
                Bullets(
                    listOf(
                        "Set all dist to -1 meaning unvisited.",
                        "dist[s]=0 at the source; enqueue s.",
                        "Dequeue u; for each neighbor v, if dist[v]<0 then dist[v]=dist[u]+1 and enqueue v.",
                    ),
                ),
                Code(
                    "c",
                    "int q[MAXN], qh, qt;\nint dist[MAXN];\n\nvoid bfs(int n, int s) {\n    for (int i = 0; i < n; i++) dist[i] = -1;\n    qh = qt = 0;\n    dist[s] = 0;\n    q[qt++] = s;\n    while (qh < qt) {\n        int u = q[qh++];\n        for (int e = head[u]; e != -1; e = nxt[e]) {\n            int v = to[e];\n            if (dist[v] < 0) {\n                dist[v] = dist[u] + 1;\n                q[qt++] = v;\n            }\n        }\n    }\n}",
                    "qh is the dequeue index; qt is the next enqueue slot. Do not shift the whole array forward each time.",
                ),
                Callout(
                    CalloutKind.KEY,
                    "Why BFS is shortest here",
                    "When every edge weight is 1, the first time you reach v is the minimum edge count. With general weights that fails—hence Dijkstra tomorrow.",
                ),
            ),
        ),
        Lesson(
            id = "d6-l5",
            title = "Run BFS in VS Code",
            minutes = 8,
            summary = "New file, compile with warnings, feed sample input.",
            blocks = listOf(
                VsCode("new_source_dijkstra"),
                Paragraph("Swap dijkstra.c from the demo for bfs.c. Suggested: gcc bfs.c -o bfs -Wall then redirect graph data, e.g. echo data | ./bfs or ./bfs < graph.txt."),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d6",
        title = "Lab: complete BFS relaxation",
        brief = "Given an adjacency list, fill in the blanks so dist is unweighted distance from 0.",
        task = "At the marker: if a neighbor is unvisited, set dist and enqueue. Graph: 0-1, 0-2, 1-3. Expected dist: 0 1 1 2.",
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
            /* TODO: if dist[v] < 0, update dist and enqueue */
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
            LabTestCase("Sample graph", "0 linked to 1 and 2; 1 linked to 3. Source 0", "0 1 1 2"),
            LabTestCase(
                "Must enqueue",
                "After updating dist, you need q[qt++] = v or vertex 3 never gets reached",
                "0 1 1 2",
                extraChecks = listOf(
                    LabCheck(
                        "enq-case",
                        "Enqueue after visiting: q[qt++] = v.",
                        CheckRule.ContainsRegex("""q\s*\[\s*qt\s*\+\+\s*\]\s*=\s*v"""),
                    ),
                ),
            ),
            LabTestCase(
                "Unweighted +1",
                "First arrival is shortest; write dist[u]+1, not edge weights",
                "0 1 1 2",
                extraChecks = listOf(
                    LabCheck(
                        "plus1-case",
                        "Unweighted BFS uses dist[v] = dist[u] + 1.",
                        CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1"""),
                    ),
                ),
            ),
        ),
        checks = listOf(
            LabCheck("if", "Check whether dist[v] is unvisited (<0).", CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*<\s*0""")),
            LabCheck("relax", "New distance should be dist[u] + 1.", CheckRule.ContainsRegex("""dist\s*\[\s*v\s*\]\s*=\s*dist\s*\[\s*u\s*\]\s*\+\s*1""")),
            LabCheck("enq", "After visiting, enqueue q[qt++] = v or equivalent.", CheckRule.ContainsRegex("""q\s*\[\s*qt\s*\+\+\s*\]\s*=\s*v""")),
        ),
        hints = listOf(
            "Unvisited means dist[v] < 0; you can skip a separate vis[] array.",
            "Write dist before enqueue; reversed order often still works, but keep this habit.",
            "On an unweighted graph do not relax like dist[v] > dist[u]+1—that is Dijkstra thinking; first arrival is shortest in BFS.",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d6-q1",
            "BFS guarantees shortest paths when?",
            listOf("All edge weights are equal (we use 1 in teaching)", "There are negative edges", "The graph must be a tree", "You must use recursion"),
            0,
            "When weights are positive and not all equal, use Dijkstra.",
        ),
        QuizQuestion(
            "d6-q2",
            "Array queue qh and qt mean?",
            listOf("qh is capacity, qt is element value", "qh dequeue index, qt next enqueue slot", "Both are stack tops", "Must equal n"),
            1,
            "qh < qt means the queue is non-empty.",
        ),
        QuizQuestion(
            "d6-q3",
            "Selection sort average time order?",
            listOf("O(n)", "O(n log n)", "O(n²)", "O(1)"),
            2,
            "Two nested loops.",
        ),
        QuizQuestion(
            "d6-q4",
            "Complexity to traverse edges in an adjacency list?",
            listOf("Always O(V²)", "O(V+E)", "O(E²)", "O(1)"),
            1,
            "Each edge appears a constant number of times in the list/array.",
        ),
    ),
)
