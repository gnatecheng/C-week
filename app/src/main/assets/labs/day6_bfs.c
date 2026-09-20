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
