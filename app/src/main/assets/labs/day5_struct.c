#include <stdio.h>
#include <string.h>

struct Student {
    int id;
    char name[32];
    int score;
};

int main(void) {
    struct Student a[3] = {
        {1, "Ada", 90},
        {2, "Ben", 95},
        {3, "Cara", 88},
    };
    int best = 0;
    for (int i = 1; i < 3; i++) {
        if (a[i].score > a[best].score) best = i;
    }
    printf("%s\n", a[best].name);
    return 0;
}
