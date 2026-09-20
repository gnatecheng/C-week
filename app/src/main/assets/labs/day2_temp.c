#include <stdio.h>

int main(void) {
    int c;
    if (scanf("%d", &c) != 1) return 1;
    int f = c * 9 / 5 + 32;
    printf("%d\n", f);
    return 0;
}
