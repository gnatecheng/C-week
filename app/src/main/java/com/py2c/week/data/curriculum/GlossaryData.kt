package com.py2c.week.data.curriculum

import com.py2c.week.data.GlossaryTerm

internal fun glossaryTerms(): List<GlossaryTerm> = listOf(
    GlossaryTerm(
        "path", "path 路径", "absolute / relative path", "文件",
        "文件在目录树上的地址。绝对从根写起，相对相对 cwd。",
        "门牌号：绝对路径写全「市/区/路/号」，不管你人在哪都能寄到；相对路径像说「隔壁」「上一层」，完全取决于你现在站在哪。例如 /home/ada/c-week/hello.c 与 ./hello。VS Code 打开的文件夹是工作区根，集成终端默认 cwd 就是它。家目录 ~ 是整栋房子，别把作业散落在家里。",
    ),
    GlossaryTerm(
        "cwd", "cwd 当前工作目录", "current working directory", "文件",
        "进程「现在所在」的目录。pwd 打印它。",
        "你站在哪间房间。pwd 是抬头看门牌。相对路径、fopen(\"in.txt\")、调试配置里的 cwd 都相对这里。迷路了先 pwd，再 cd 到 c-week。",
    ),
    GlossaryTerm(
        "perm", "permission 权限", "rwx / chmod", "文件",
        "读、写、执行。可执行文件需要 x，否则 ./hello 会 Permission denied。",
        "门锁上的三种钥匙：读是翻看，写是改内容，执行是当程序跑。目录的 x 表示能走进这扇门。ls -l 能看见。不要对整盘 chmod 777。",
    ),
    GlossaryTerm(
        "bash", "bash 命令", "pwd ls cd mkdir cp mv rm cat echo", "终端",
        "在 VS Code 集成终端里操作文件与编译。",
        "pwd 看门牌，ls 扫桌面，cd 换房间，mkdir 隔储藏间，touch 放空本，cp 复印，mv 搬家/改名，rm 碎纸机（无回收站），cat 朗读，echo 把一句话说出来，clear 擦黑板。详见第 1 天逐条课与速查表。",
    ),
    GlossaryTerm(
        "redirect", "重定向与管道", "> >> |", "终端",
        "> 写入文件（覆盖），>> 追加，| 把左边输出交给右边。",
        "餐桌、保鲜盒、传送带：默认端上桌；> 盖盒（倒掉旧的再装）；>> 往盒里再塞；| 直接递给下一道工序。echo 100 | ./temp 给程序喂一行。千万别 echo 覆盖你的 .c。",
    ),
    GlossaryTerm(
        "launch", "launch.json", "type / request / program / cwd", "调试",
        "告诉 VS Code 的 C/C++ 扩展：F5 启动哪个程序。",
        "厨房说明书：用哪口锅（type=cppdbg）、炒哪道菜（program 指向可执行文件不是 .c）、在哪个灶台（cwd 通常是 \${workspaceFolder}）、动手前先备菜（preLaunchTask 跑 gcc）、gdb 在哪（miDebuggerPath）。",
    ),
    GlossaryTerm(
        "tasks", "tasks.json", "preLaunchTask / gcc -g", "调试",
        "F5 之前先编译，保证调试的是刚生成的二进制。",
        "备菜步骤。label 必须和说明书上的 preLaunchTask 一字不差。args 里记得 -g，否则检查站（断点）对不齐行。",
    ),
    GlossaryTerm(
        "breakpoint", "breakpoint 断点", "F9 / F5 / F10 / F11", "调试",
        "让程序停在指定行，以便看变量和单步。",
        "路上的检查站：红点停车。F9 设/清站，F5 启动或开到下一站，F10 沿路走一格，F11 拐进路边店，Shift+F11 从店里出来，Shift+F5 收车。VARIABLES 是停车时翻后备箱。",
    ),
    GlossaryTerm(
        "pointer", "pointer 指针", "address / dereference", "内存",
        "存放地址的变量。& 取址，* 解引用。",
        "门牌号 vs 房间里的东西：x 是房间，&x 是门牌，p 是抄了门牌的借书卡，*p 是拿卡去开门。p = NULL 是卡片写「无此地址」，再开门就是未定义行为。",
    ),
    GlossaryTerm(
        "stack-heap", "stack / heap 栈与堆", "automatic vs dynamic storage", "内存",
        "局部变量默认在栈；malloc 在堆，需 free。",
        "酒店房间 vs 租的仓库：函数进入像入住，返回像退房、钥匙作废。堆是向库房租货架，必须自己还钥匙（free）。",
    ),
    GlossaryTerm(
        "array-decay", "array decay 数组退化", "array-to-pointer conversion", "数组",
        "数组传入函数后变成指向首元素的指针，丢失长度。",
        "只把第一格储物柜的门牌交给别人，对方不知道后面还有几格。所以函数写成 (int *a, int n)。sizeof 在函数内测不到元素个数。",
    ),
    GlossaryTerm(
        "struct", "struct 结构体", "record / field layout", "数据",
        "编译期固定的字段布局。",
        "学生证栏目焊死：学号、姓名、分数的位置编译期定好，不能运行时加栏。字段访问是偏移量。指针用 ->。",
    ),
    GlossaryTerm(
        "malloc", "malloc / free", "dynamic allocation", "内存",
        "堆分配与释放。失败返回 NULL。",
        "向仓库租货架：拿到钥匙才能用；租不到是 NULL。还两次或还完再开锁都是事故。谁租谁还。",
    ),
    GlossaryTerm(
        "ub", "undefined behavior 未定义行为", "UB", "安全",
        "标准不保证结果：越界、空指针、有符号溢出…",
        "像违章穿越：这次可能没事，换个路口（编译器/优化等级）就出事。-Wall 与 sanitizer 是保险。",
    ),
    GlossaryTerm(
        "pass-by-value", "pass by value 传值", "copy of the argument", "函数",
        "参数是副本。改副本不影响调用者。",
        "复印作业：同学改复印件，你桌上的原件不动。要改原件，把课桌门牌号递过去（传指针）。scanf 已经这么干了。",
    ),
    GlossaryTerm(
        "nul", "NUL terminator '\\0'", "C string", "字符串",
        "C 字符串以字节 0 结尾。",
        "书架末尾的空白卡「到此为止」。printf(\"%s\") 和 strlen 靠它停。没这张卡会读到隔壁柜子。",
    ),
    GlossaryTerm(
        "header", "header / 源文件", "#include", "工具链",
        "#include 插入声明；.c 提供定义。",
        "菜单 vs 后厨：头文件是菜名和配料表（声明），.c 是真正做法（定义）。include 是预处理把菜单页夹进来。",
    ),
    GlossaryTerm(
        "compile", "compile + link 编译链接", "translation + linking", "工具链",
        "源码 → 目标文件 → 可执行文件。",
        "食谱 vs 做好的菜：.c 不能直接上桌。编译把食谱变成工序，链接把厨具（printf 等）配齐。缺厨具是链接错误。",
    ),
    GlossaryTerm(
        "segfault", "segmentation fault", "invalid memory access", "调试",
        "访问了无权访问的地址。",
        "拿着作废或空白的门牌去撞门。常见：NULL、野指针、退房后还用酒店钥匙、改只读字面量。用调试器看当时的指针值。",
    ),
    GlossaryTerm(
        "const", "const", "read-only view", "类型",
        "承诺不通过这个视图修改对象。",
        "只读参观券：const char *s 是答应不改屋里的字；char * const p 是答应不换手上这张门牌。",
    ),
    GlossaryTerm(
        "typedef", "typedef", "type alias", "类型",
        "给已有类型起短名。",
        "给学生证印简称：不发明新证件，只是少写几个字。typedef struct Node Node; 之后 Node *p 更短。",
    ),
    GlossaryTerm(
        "adj", "adjacency list 邻接表", "edge lists per vertex", "图",
        "每个点保存出边列表。",
        "通讯录：每人后面跟一串朋友电话。稀疏图省纸。C 用链表或平行数组 to/nxt/head。",
    ),
    GlossaryTerm(
        "dijkstra", "Dijkstra", "non-negative shortest path", "图",
        "非负权单源最短路。",
        "导航按分钟不按路口数。直达可能更堵。边权全 1 时退化为 BFS。教学用 O(V²) 扫最小；盖章后不再改。",
    ),
    GlossaryTerm(
        "prototype", "function prototype", "forward declaration", "函数",
        "先声明签名，后给定义。",
        "菜单先写菜名，后厨再做。编译器从上往下读，没见过的名字会报错。",
    ),
)
