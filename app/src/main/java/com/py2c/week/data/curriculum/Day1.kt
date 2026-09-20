package com.py2c.week.data.curriculum

import com.py2c.week.data.CalloutKind
import com.py2c.week.data.CheckRule
import com.py2c.week.data.CodeLab
import com.py2c.week.data.ContentBlock.Bullets
import com.py2c.week.data.ContentBlock.Callout
import com.py2c.week.data.ContentBlock.Code
import com.py2c.week.data.ContentBlock.Example
import com.py2c.week.data.ContentBlock.Heading
import com.py2c.week.data.ContentBlock.Paragraph
import com.py2c.week.data.ContentBlock.VsCode
import com.py2c.week.data.CourseDay
import com.py2c.week.data.LabCheck
import com.py2c.week.data.Lesson
import com.py2c.week.data.QuizQuestion

internal fun day1(): CourseDay = CourseDay(
    id = 1,
    title = "环境、文件与 Hello World",
    subtitle = "路径 · bash · VS Code · 第一次编译",
    outcome = "能分清路径与工作区，用终端做基本文件操作，在 VS Code 里新建 .c、用 gcc 编译运行，并看懂 launch.json 与断点调试。",
    minutes = 165,
    todayFocus = "食谱（.c）要先做成菜（可执行文件）才能上桌。终端「你站在哪间房间」决定相对路径怎么走。",
    lessons = listOf(
        Lesson(
            id = "d1-l1",
            title = "这一周你要走到哪里",
            minutes = 8,
            summary = "给编程初学者：C 要学什么、本周地图、以及为什么必须动手敲命令。",
            blocks = listOf(
                Heading("从零学 C，七天走到最短路"),
                Paragraph("本课面向大学新生和没有写过 C 的同学。我们不假设你会别的语言。第 7 天的目标不是背 API，而是能亲手实现加权图最短路（Dijkstra），并且数组、指针、结构体用法是对的。"),
                Bullets(
                    listOf(
                        "Day 1：文件、终端（每条 bash 命令单独讲）、工具链、Hello World、launch.json 与调试。",
                        "Day 2–3：类型、输入输出、if/for、函数与传值。",
                        "Day 4–5：指针、数组、字符串、结构体与堆内存。",
                        "Day 6–7：BFS 与 Dijkstra，把语言落到算法题。",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：酒店房间 vs 租的仓库",
                    "函数里的局部变量像入住时发的酒店房间：退房（函数返回）前台立刻收回，钥匙作废。堆上的内存像向库房租的货架：不还钥匙（free）就一直占着。后面每天都用这套图。",
                ),
                Callout(
                    CalloutKind.KEY,
                    "两套机器模型（精确说法）",
                    "局部变量默认住在栈上，函数返回后这块内存失效。堆上的内存必须你自己 malloc/free。写错不是「风格问题」，而是崩溃。",
                ),
                Callout(
                    CalloutKind.KEY,
                    "本周纪律",
                    "每天在电脑上用 VS Code 跟做一遍。手机 App 负责讲解、测验和模拟评测；真正的 gcc 在电脑上。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l2",
            title = "路径、目录与工作区",
            minutes = 14,
            summary = "绝对路径、相对路径、cwd、家目录 vs VS Code 打开的文件夹。",
            blocks = listOf(
                Heading("文件在树里，不在桌面图标里"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：门牌号",
                    "路径就是门牌号。绝对路径像写全「市/区/路/号/房间」，不管你人在哪都能寄到。相对路径像说「隔壁房间」「上一层」：完全取决于你现在站在哪。",
                ),
                Paragraph("操作系统把文件放在一棵目录树里。每个文件有一条路径（path）：从根走到它经过的一串文件夹名字。目录（directory，也叫文件夹）是树上的节点，里面可以再放文件或子目录。"),
                Bullets(
                    listOf(
                        "Linux / macOS 根是 /。例如 /home/ada/c-week/hello.c。",
                        "Windows 盘符是根的一种，例如 C:\\Users\\Ada\\c-week\\hello.c。VS Code 终端里也可以写成 /c/Users/Ada/c-week。",
                        "家目录（home）：你的「私人根」。Linux 常是 /home/用户名，macOS 是 /Users/用户名，简写 ~。",
                    ),
                ),
                Heading("当前工作目录 cwd"),
                Paragraph("把终端想成你本人站在房子里的某个房间。pwd 就是抬头看门牌：我现在在哪。说「去拿 hello.c」时，若只报文件名，系统只在你站的这个房间里找。"),
                Paragraph("术语：这个「我现在站在哪」叫 current working directory，简称 cwd。相对路径相对 cwd；绝对路径从根（或盘符）写起，和你站在哪无关。"),
                Code(
                    "bash",
                    "pwd\n# 假设输出 /home/ada/c-week\n\nls hello.c          # 相对：cwd 下的 hello.c\nls /home/ada/c-week/hello.c   # 绝对：从根写全\nls ./hello.c        # ./ 明确表示「就在 cwd」\nls ../readme.md     # .. 是上一级目录",
                    "同一文件可以用相对或绝对两种写法。调试配置里的 program、cwd 也是路径，写错就会找不到可执行文件。",
                ),
                Heading("工作区文件夹 ≠ 家目录"),
                Paragraph("家目录 ~ 是整栋房子（下载、照片、别的课的作业都堆在里面）。工作区是你专门辟出的一间书房：本课请建 ~/c-week，用 VS Code「打开文件夹」走进这间书房。集成终端默认就站在书房门口，gcc hello.c 才找得到文件。"),
                Callout(
                    CalloutKind.WARN,
                    "不要只拖一个 .c 进 VS Code",
                    "打开单文件时，终端 cwd 往往是家目录或上次残留的位置。相对路径、调试、新建文件都会「跑丢」。永远打开文件夹。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l3",
            title = "创建、复制、移动、删除与权限",
            minutes = 12,
            summary = "mkdir/touch/cp/mv/rm；读-写-执行权限的入门含义。",
            blocks = listOf(
                Heading("对文件做什么"),
                Paragraph("编辑器保存是「写入」。除此之外，你还经常在终端里创建空文件、复制一份备份、改名、挪到子目录、删掉编错的可执行文件。这些动作和图形界面拖拽是同一件事，只是写成命令后可以复现、可以写进脚本。"),
                Code(
                    "bash",
                    "mkdir -p ~/c-week/src     # 创建目录；-p 表示中间层不存在也一并创建\ncd ~/c-week\ntouch notes.txt          # 创建空文件（若已存在则只更新时间）\ncp notes.txt notes.bak   # 复制\nmv notes.txt src/        # 移动（目标是目录则放进去）\nmv src/notes.txt readme.txt   # 改名其实也是 mv\nrm notes.bak             # 删除文件\n# rm -r src              # 删除目录（本课尽量少用，删错不可恢复）",
                    "cp 留下原件；mv 不留原件。删之前 ls 看一眼。",
                ),
                Heading("权限：谁能读、写、跑"),
                Paragraph("权限像门锁上的三种钥匙：读 r 是能翻看，写 w 是能改内容，执行 x 是能当程序跑起来。源码只要读写；gcc 生成的 hello 必须有执行钥匙，否则 ./hello 就是 Permission denied——门在，但钥匙不对。"),
                Paragraph("每个文件有三组权限：所有者、同组、其他人。目录的 x 表示「能走进这扇门」；没有它，即使有 r 也可能 cd 不进去。"),
                Code(
                    "bash",
                    "ls -l hello.c hello\n# -rw-r--r--  1 ada ada  120  hello.c     # 文件，所有者可读写\n# -rwxr-xr-x  1 ada ada  16k hello        # 可执行\n\nchmod +x hello           # 给当前用户加上执行权限\n# gcc 重新编译通常会直接生成带 x 的文件，不必每次 chmod",
                    "目录的 x 表示「能进入这个目录」。没有它，即使有 r 也可能 cd 不进去。",
                ),
                Callout(
                    CalloutKind.TIP,
                    "初学够用的三条",
                    "1) 源码文件要能读写。2) 程序文件要能执行。3) 不要对整盘 chmod 777。课程作业在自己的 c-week 里操作即可。",
                ),
            ),
        ),
    ) + bashCommandLessons() + listOf(
        Lesson(
            id = "d1-l5",
            title = "安装 VS Code",
            minutes = 10,
            summary = "下载编辑器、选好中文包、终端就在编辑器里。",
            blocks = listOf(
                Heading("为什么指定 VS Code"),
                Paragraph("它跨平台、终端就在编辑器里、C/C++ 扩展能提供跳转和调试。CLion / Visual Studio 也可以，但本课所有点击路径按 VS Code 演示。"),
                Bullets(
                    listOf(
                        "打开 https://code.visualstudio.com/ 下载对应系统的安装包。",
                        "Windows：用安装器，勾选「Add to PATH」。",
                        "macOS：把 VS Code 拖进应用程序；若终端没有 code 命令，在命令面板运行 Shell Command: Install 'code' command。",
                        "Linux：用发行版包或官方 .deb/.rpm。",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "中文界面",
                    "扩展市场搜索 Chinese (Simplified) Language Pack，安装后按提示切换。本课录像用英文菜单名，字幕给中文。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l6",
            title = "安装 C/C++ 扩展",
            minutes = 8,
            summary = "Microsoft C/C++ 扩展：高亮、补全、调试。扩展不是编译器。",
            blocks = listOf(
                Paragraph("扩展本身不包含 gcc。它只是把编辑器和本机的 gcc/clang/gdb 接起来。没装编译器时，扩展会报「无法找到 cl.exe / gcc」。下面是真实 VS Code 扩展市场录像。"),
                VsCode("install_cpptools"),
                Callout(
                    CalloutKind.WARN,
                    "不要先装一堆主题",
                    "先只装 C/C++。所谓「一键运行」插件会掩盖你到底调用了哪条编译命令。本周用集成终端亲手敲 gcc。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l7",
            title = "安装编译器",
            minutes = 15,
            summary = "Windows MinGW / macOS clang / Linux gcc。验证 gcc --version。",
            blocks = listOf(
                Heading("编译器才是 C 的「运行时」"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：食谱 vs 做好的菜",
                    ".c 源码是食谱：写得再工整也不能直接上桌。gcc/clang 是厨师，按食谱做出可执行文件这盘菜。没装编译器，你手里只有一张字。链接则是把「用到的厨具」（printf 等标准库）配齐；缺厨具就是链接错误。",
                ),
                Paragraph("C 语言标准只规定语法和库，可执行文件要靠编译器生成。我们用 gcc（Linux / Windows MinGW）或 clang（macOS 默认）。没有编译器，.c 文件只是文本。"),
                Heading("Windows"),
                Bullets(
                    listOf(
                        "推荐：安装 MSYS2，再 pacman -S mingw-w64-ucrt-x86_64-gcc。",
                        "或者安装 WinLibs / w64devkit，把 bin 目录加到系统 PATH。",
                        "PowerShell 新开窗口，运行 gcc --version，应打印版本号而不是「无法识别」。",
                    ),
                ),
                Heading("macOS"),
                Paragraph("终端运行 xcode-select --install，等待命令行工具装完。clang --version 有输出即可。clang 对教学用的 C11 足够。"),
                Heading("Linux"),
                Code("bash", "sudo apt install build-essential   # Debian/Ubuntu\nsudo dnf install gcc gdb         # Fedora", "用发行版包管理器安装 gcc、make、gdb"),
                Callout(
                    CalloutKind.KEY,
                    "验收命令",
                    "gcc --version 或 clang --version 成功，就算工具链过关。VS Code 状态栏有时会提示配置 compilerPath，选你刚才那个 gcc 即可。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l8",
            title = "打开文件夹并写下 Hello World",
            minutes = 12,
            summary = "工作区、.c 文件、main 与 printf。两段点击录像。",
            blocks = listOf(
                Paragraph("先在家目录建空文件夹 c-week。用「打开文件夹」而不是只拖一个文件——终端 cwd、调试 cwd 都跟工作区走。下面两段是实机录像。"),
                VsCode("open_folder"),
                VsCode("create_hello"),
                Example(
                    title = "最小的 C 程序",
                    source = "#include <stdio.h>\n\nint main(void) {\n    printf(\"Hello, C\\n\");\n    return 0;\n}",
                    note = "stdio.h 提供 printf。main 是程序入口，返回 0 表示正常结束。\\n 是换行；没有它，光标会停在同一行。",
                ),
                Callout(
                    CalloutKind.WARN,
                    "常见第一坑",
                    "文件如果叫 hello.c.txt（资源管理器隐藏扩展名），gcc 会拒绝。在 VS Code 资源管理器里看清全名。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l9",
            title = "在终端里编译并运行",
            minutes = 12,
            summary = "gcc -o、./hello、以及最常见的报错。",
            blocks = listOf(
                VsCode("compile_run"),
                Heading("发生了什么"),
                Paragraph("gcc 做两件事：把 hello.c 编译成目标文件（机器指令），再链接上 C 标准库里 printf 的实现，输出可执行文件 hello。所以你会见到两类错误：编译错误（语法/类型）和链接错误（缺 main、缺库）。"),
                Code(
                    "bash",
                    "gcc hello.c -o hello -Wall -Wextra -g\n./hello\necho $?    # 打印退出码，0 表示成功",
                    "-Wall 打开警告；-g 写入调试信息，下一步 F5 才有行号。",
                ),
                Bullets(
                    listOf(
                        "fatal error: stdio.h: No such file or directory → 编译器/头文件没装好。",
                        "undefined reference to `main' → 空文件或 main 名字写错。",
                        "error: expected ';' → C 几乎每条语句都要分号。",
                        "Permission denied 去运行 ./hello → chmod +x hello，或重新 gcc。",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "建议永远加上警告",
                    "gcc hello.c -o hello -Wall -Wextra。警告常常是明日的段错误。第 4 天开始你会感谢它。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l10",
            title = "launch.json 与 tasks.json 逐字段",
            minutes = 16,
            summary = "F5 如何找到可执行文件：type、request、program、cwd、miDebuggerPath、preLaunchTask。",
            blocks = listOf(
                Heading("为什么要这两份文件"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：厨房说明书",
                    "手敲 gcc 像自己炒菜。F5 调试则是请人代做：必须写清用哪口锅（调试器 type）、炒哪道菜（program 指向可执行文件）、在哪个灶台开工（cwd）、动手前要不要先备菜（preLaunchTask 去跑 gcc）。launch.json 是这份说明书；tasks.json 是「备菜」那一步。",
                ),
                Paragraph("终端里手敲 gcc 最清楚。但调试器（F5）必须知道：1) 用哪种调试器；2) 运行哪一个程序；3) 从哪个目录启动；4) 启动前要不要先编译。这些写在工作区的 .vscode/launch.json 和 tasks.json 里，随文件夹一起走，换电脑也能用。"),
                VsCode("launch_json"),
                Heading("launch.json：告诉调试器怎么启动"),
                Code(
                    "json",
                    "{\n  \"version\": \"0.2.0\",\n  \"configurations\": [\n    {\n      \"name\": \"调试 hello\",\n      \"type\": \"cppdbg\",\n      \"request\": \"launch\",\n      \"program\": \"\${workspaceFolder}/hello\",\n      \"args\": [],\n      \"cwd\": \"\${workspaceFolder}\",\n      \"stopAtEntry\": false,\n      \"miDebuggerPath\": \"gdb\",\n      \"preLaunchTask\": \"C: gcc build hello\"\n    }\n  ]\n}",
                    "Windows MinGW 把 miDebuggerPath 换成 gdb.exe 的完整路径；macOS 常用 lldb，type 仍常是 cppdbg。",
                ),
                Bullets(
                    listOf(
                        "type：调试器适配器。C/C++ 扩展提供 cppdbg（gdb/lldb）。写错 type，F5 找不到调试器。",
                        "request：launch 表示「启动这个程序」；attach 是「挂到已经在跑的进程」，本课只用 launch。",
                        "program：要调试的可执行文件，必须是编译产物路径，不是 hello.c。\${workspaceFolder} 就是你打开的文件夹。",
                        "cwd：程序启动时的当前目录。相对路径、fopen(\"in.txt\") 都相对它。通常等于工作区根。",
                        "args：传给 main 的命令行参数，相当于 ./hello a b。本课 Hello World 留空数组。",
                        "miDebuggerPath：gdb 可执行文件。Linux 一般写 gdb；若 VS Code 找不到，改成 which gdb 的绝对路径。",
                        "preLaunchTask：F5 之前先跑 tasks.json 里同名任务，保证你调试的是刚刚编译的二进制，而不是昨天的旧文件。",
                        "stopAtEntry：true 会在进入 main 时立刻停。初学可先 false，靠自己打的断点停。",
                    ),
                ),
                Heading("tasks.json：F5 之前的 gcc"),
                Code(
                    "json",
                    "{\n  \"version\": \"2.0.0\",\n  \"tasks\": [\n    {\n      \"label\": \"C: gcc build hello\",\n      \"type\": \"shell\",\n      \"command\": \"gcc\",\n      \"args\": [\"hello.c\", \"-o\", \"hello\", \"-Wall\", \"-g\"],\n      \"group\": { \"kind\": \"build\", \"isDefault\": true },\n      \"problemMatcher\": [\"\$gcc\"]\n    }\n  ]\n}",
                    "label 必须和 preLaunchTask 字符串完全一致。-g 不能省，否则断点对不准行。",
                ),
                Callout(
                    CalloutKind.KEY,
                    "对不上号时先查这三处",
                    "1) program 指向的文件不存在：先在终端 gcc 一次。2) cwd 指错：相对路径全飞。3) preLaunchTask 名字抄错：F5 直接跑旧二进制，改代码却「没变化」。",
                ),
            ),
        ),
        Lesson(
            id = "d1-l11",
            title = "断点调试：从 F9 到看变量",
            minutes = 16,
            summary = "设/清断点、F5 启动、继续、单步、调用栈、停止。对照实机录像。",
            blocks = listOf(
                Heading("调试器解决什么问题"),
                Callout(
                    CalloutKind.TIP,
                    "生活类比：路上的检查站",
                    "printf 像到终点才看成绩。断点是你在路中间设的检查站：程序开到红点必须停车。F10 单步跳过是「沿这条路往前走一格」；F11 单步进入是「拐进路边那家店看看」；Shift+F11 是从店里走出来；F5 继续则开到下一站。VARIABLES 是停车时翻看后备箱里装了什么。",
                ),
                Paragraph("printf 能看结果，但看不到「走到哪一行、当时变量是多少」。调试器让程序在你指定的行停住，你可以单步往前走，并在变量窗格里读内存里的值。下面录像是在真实 VS Code 里打断点、F5、单步。"),
                VsCode("debug_bp"),
                Heading("逐步操作（请在电脑上对照快捷键）"),
                Bullets(
                    listOf(
                        "设断点：光标放到要停的那一行（例如循环体），按 F9，行号旁出现红点。再按 F9 清除。也可以点行号左侧水槽。",
                        "启动调试：F5，或左侧「运行和调试」。若配置正确，会先跑 preLaunchTask 再启动 program。",
                        "命中断点：编辑器当前行高亮。左侧 VARIABLES 列出局部变量；找不到就展开「局部」。",
                        "继续 Continue：F5（调试过程中再按）。跑到下一个断点或结束。",
                        "单步跳过 Step Over：F10。执行完当前行，不进入函数内部。适合看自己的循环。",
                        "单步进入 Step Into：F11。当前行若调用了函数，走进去。标准库函数初学不必进。",
                        "单步跳出 Step Out：Shift+F11。从当前函数返回到调用者。",
                        "调用栈 Call Stack：显示「谁调用了谁」。递归或多层函数时，点一层就能看见那一层的变量。",
                        "停止：Shift+F5，或红色方块。程序被杀掉，不是自然 return。",
                    ),
                ),
                Heading("建议你对着循环练一次"),
                Code(
                    "c",
                    "int main(void) {\n    int s = 0;\n    for (int i = 1; i <= 3; i++) {\n        s += i;   /* 在这一行打断点 */\n    }\n    printf(\"%d\\n\", s);\n    return 0;\n}",
                    "每次停下看 i 和 s：第一次 1 和 1，第二次 2 和 3，第三次 3 和 6。这比猜循环边界快。",
                ),
                Callout(
                    CalloutKind.WARN,
                    "断点是灰色 / 打不上",
                    "多半是：没加 -g 重新编译；program 指向另一份没有调试信息的二进制；或断在空行/宏展开后对不齐。先终端 gcc -g，确认 launch.json 的 program 就是这个文件。",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d1",
        title = "实验：打印你的名字",
        brief = "改 Hello World，用 printf 输出一行指定格式。",
        task = "编写完整 C 程序：输出 Hello, Ada 然后换行。本实验用固定名字方便自动对照。",
        starterCode = """
#include <stdio.h>

int main(void) {
    /* TODO: 打印 Hello, Ada 并换行 */
    return 0;
}
""".trimIndent(),
        solutionCode = """
#include <stdio.h>

int main(void) {
    printf("Hello, Ada\n");
    return 0;
}
""".trimIndent(),
        expectedOutput = "Hello, Ada\n",
        checks = listOf(
            LabCheck("inc", "缺少 #include <stdio.h>。没有它，printf 可能无法编译。", CheckRule.Contains("#include <stdio.h>")),
            LabCheck("main", "需要 int main", CheckRule.ContainsRegex("""int\s+main\s*\(""")),
            LabCheck("printf", "请用 printf。", CheckRule.Contains("printf")),
            LabCheck("text", "输出里必须包含 Hello, Ada。", CheckRule.Contains("Hello, Ada")),
            LabCheck("nl", "记得在格式串里写 \\n，否则行尾没有换行。", CheckRule.Contains("\\n")),
        ),
        hints = listOf(
            "printf 的第一个参数是格式字符串，普通文字原样输出。",
            "换行是两个字符的转义：反斜杠 + n。",
            "真实机器上：gcc hello.c -o hello && ./hello",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d1-q1",
            "把 hello.c 变成可执行文件，最贴近本课的命令是？",
            listOf("code hello.c", "gcc hello.c -o hello", "cat hello.c", "chmod hello.c"),
            1,
            "gcc 编译并链接；-o 指定输出名。",
        ),
        QuizQuestion(
            "d1-q2",
            "VS Code 的 C/C++ 扩展主要提供什么？",
            listOf("一个完整的 Linux 内核", "高亮、补全和调试适配，但不包含编译器本身", "云端评测机", "自动生成可执行文件、无需 gcc"),
            1,
            "扩展是胶水。编译器要另外安装。",
        ),
        QuizQuestion(
            "d1-q3",
            "printf(\"hi\") 没有 \\n，运行时通常会？",
            listOf("编译失败", "输出 hi 且不换行", "自动补上换行", "段错误"),
            1,
            "合法，只是光标停在 hi 后面。",
        ),
        QuizQuestion(
            "d1-q4",
            "打开单个文件 vs 打开文件夹，教学上为什么坚持文件夹？",
            listOf("文件夹占内存更小", "终端 cwd、相对路径和调试都相对工作区，新建文件也有固定落点", "C 语言语法要求必须有文件夹", "VS Code 不能打开单文件"),
            1,
            "工作区让 gcc hello.c 的路径可预期。",
        ),
        QuizQuestion(
            "d1-q5",
            "相对路径 ./hello 是相对于什么解析的？",
            listOf("永远相对于家目录", "相对于当前工作目录 cwd（终端里就是 pwd 的位置）", "相对于磁盘根目录", "相对于 gcc 安装目录"),
            1,
            "cwd 变了，同一条 ./hello 就会找错地方。",
        ),
        QuizQuestion(
            "d1-q6",
            "launch.json 里 program 应该指向？",
            listOf("hello.c 源文件", "编译得到的可执行文件（如 \${workspaceFolder}/hello）", "stdio.h", "VS Code 本身"),
            1,
            "调试器启动的是二进制，不是源码文本。",
        ),
        QuizQuestion(
            "d1-q7",
            "调试时 Step Over（F10）的含义？",
            listOf("结束程序", "执行完当前行，不进入该行调用的函数内部", "跳过编译", "删除断点"),
            1,
            "要进函数用 Step Into（F11）。",
        ),
        QuizQuestion(
            "d1-q8",
            "echo 100 > in.txt 会？",
            listOf("把 100 追加到 in.txt 末尾", "把 100 写入 in.txt（文件已存在则覆盖）", "启动程序 in.txt", "删除 in.txt"),
            1,
            "覆盖用 >，追加用 >>。",
        ),
        QuizQuestion(
            "d1-q9",
            "要把 hello.c 备份一份且原件留下，应该用？",
            listOf("mv hello.c hello.bak.c", "cp hello.c hello.bak.c", "rm hello.c", "clear"),
            1,
            "cp 复印留原件；mv 是搬走或改名，原位置不再有。",
        ),
        QuizQuestion(
            "d1-q10",
            "clear 会做什么？",
            listOf("删除当前目录所有文件", "只清屏幕显示，不删文件", "关闭终端", "卸载 gcc"),
            1,
            "擦黑板，不是碎纸机。碎纸机是 rm。",
        ),
        QuizQuestion(
            "d1-q11",
            "cat hello.c 和 echo hello.c 的差别？",
            listOf("完全一样", "cat 打印文件内容；echo 只把「hello.c」这几个字打到屏幕", "echo 会编译", "cat 会删除文件"),
            1,
            "看源码用 cat（或编辑器），echo 只是复述你写的文字。",
        ),
    ),
)
