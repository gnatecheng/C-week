package com.py2c.week.data

/** Per-question Chinese 错因, used when the selected choice is wrong. */
fun QuizQuestion.wrongReason(selectedIndex: Int): String {
    if (selectedIndex == correctIndex) return explanation
    return WRONG_REASONS[id] ?: explanation
}

fun QuizQuestion.verdict(selectedIndex: Int): String {
    val ok = selectedIndex == correctIndex
    return if (ok) "判断：正确" else "判断：错误"
}

private val WRONG_REASONS: Map<String, String> = mapOf(
    "d1-q1" to "code / cat / chmod 都不编译。要把 .c 变成可执行文件，本课用 gcc hello.c -o hello。",
    "d1-q2" to "C/C++ 扩展只是高亮、补全和调试胶水，里面没有 gcc。编译器要在系统里另外安装。",
    "d1-q3" to "缺 \\n 仍然合法，只是不换行。不会因此编译失败或段错误。",
    "d1-q4" to "单文件模式没有稳定的工作区，终端 cwd、相对路径和 launch.json 都容易指错地方。",
    "d1-q5" to "./hello 相对的是当前工作目录（pwd），不是家目录，也不是磁盘根。",
    "d1-q6" to "调试器启动的是编译出来的二进制，不是 .c 源文件，更不是 stdio.h。",
    "d1-q7" to "Step Over 是「走完这一行、不进函数」；进函数是 Step Into。它不会结束程序。",
    "d1-q8" to "> 是覆盖写入；追加才是 >>。echo 100 > in.txt 会把原内容丢掉。",
    "d1-q9" to "mv 是搬走/改名，原位置不再有文件。要备份且留原件用 cp。",
    "d1-q10" to "clear 只擦屏幕，像擦黑板。删文件才是 rm。",
    "d1-q11" to "echo hello.c 只会打印这几个字；看源码内容要用 cat 或打开编辑器。",

    "d2-q1" to "scanf 要的是 int*。漏 & 等于把变量里的数字当成地址去写，典型未定义行为。",
    "d2-q2" to "两个 int 相除是截断，5/2 得 2 不是 2.5，也不会四舍五入。",
    "d2-q3" to "%d 约定收 int，却塞了 double，属于格式合同对不上，是未定义行为。",
    "d2-q4" to "局部变量活在栈帧里，函数一返回格子就回收，不能再拿它的地址当礼物送出去。",

    "d3-q1" to "C 默认传值：函数里改参数改的是副本。要改调用者必须传指针。",
    "d3-q2" to "i <= n 会多走一格碰到 a[n]。访问 a[0..n-1] 应用 i < n，这是经典 off-by-one。",
    "d3-q3" to "main 若写在前面，编译器还没见过 foo 的类型。文件上部的原型就是提前报户口。",
    "d3-q4" to "if (x = 0) 是赋值，条件变成恒假。比较相等要写 ==。",

    "d4-q1" to "参数里的数组已经退化成指针，sizeof a 是指针宽度，不是 10*sizeof(int)。长度要另传。",
    "d4-q2" to "\"ab\" 实际是 a、b、\\0 三格，占 3 字节。strlen 不计结束符，但数组本身含结束符。",
    "d4-q3" to "声明里的 * 表示「这是指针」；表达式里的 *p 才是顺着箭头取格子。",
    "d4-q4" to "a[i] 就是 *(a + i)。a + i 只是地址，还没取内容。",

    "d5-q1" to "malloc 失败返回 NULL。不检查就解引用会直接崩。",
    "d5-q2" to "p->x 等价于 (*p).x。p.x 要求 p 不是指针；*p.x 会先点后星，优先级错。",
    "d5-q3" to "sizeof *p 看的是 Node 的大小，不解引用。malloc(sizeof *p) 才和结构体匹配。",
    "d5-q4" to "忘记 fclose 会让缓冲没刷完、占用句柄。短程序退出时系统会收，但习惯必须配对。",

    "d6-q1" to "BFS 把每条边当成 1。边权不全相等时，先到达不代表代价最小，要改 Dijkstra。",
    "d6-q2" to "qh 是出队下标，qt 是下一个入队位置。队列非空当且仅当 qh < qt。",
    "d6-q3" to "选择排序两层循环，平均 O(n²)，不是 n log n。",
    "d6-q4" to "邻接表每条边出现常数次，遍历是 O(V+E)，不是必然 O(V²)。",

    "d7-q1" to "本课 Dijkstra 要求非负权。负权会破坏「已确定的点不再改」；边也不必全是 1。",
    "d7-q2" to "每一轮在未 used 的点里选 dist 最小者，再松弛它的出边。不是按输入顺序，更不是 BFS。",
    "d7-q3" to "主测里 0→1 直边权 4，比 0→2→1 的 2 更早但更贵。BFS 数边数，不能替换加权最短路。",
    "d7-q4" to "int 溢出后可能变成很小的数，错误地通过 dist[v] > dist[u]+w 比较。INF 和类型要一起设计。",
)
