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
