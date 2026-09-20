package com.py2c.week.data.curriculum

import com.py2c.week.data.CalloutKind
import com.py2c.week.data.ContentBlock.Bullets
import com.py2c.week.data.ContentBlock.Callout
import com.py2c.week.data.ContentBlock.Code
import com.py2c.week.data.ContentBlock.Heading
import com.py2c.week.data.ContentBlock.Paragraph
import com.py2c.week.data.Lesson

/** Dedicated bash command intros + cheat-sheet. Inserted after Day 1 file-management lessons. */
internal fun bashCommandLessons(): List<Lesson> = listOf(
    Lesson(
        id = "d1-l4",
        title = "bash 命令逐条：在 VS Code 终端里练",
        minutes = 28,
        summary = "pwd、ls、cd、mkdir、touch、cp、mv、rm、cat、echo、clear，以及 | > >>。每条：干什么、常用参数、例子、易错点。",
        blocks = listOf(
            Heading("先打开集成终端"),
            Paragraph("VS Code 快捷键 Ctrl+`（反引号，Esc 下面）打开面板。下面默认你已经「打开文件夹」c-week，提示符前面的路径应是课程目录，不是家。每条请在电脑上敲一遍。"),
            Callout(
                CalloutKind.KEY,
                "练习约定",
                "先 pwd 确认位置。例子里的 hello.c 是工作区里的源文件。Windows 的 PowerShell 有少量差异，本课按 bash / VS Code 终端写；若提示符是 PS，可用 Git Bash 或 MSYS2。",
            ),

            Heading("pwd：我现在站在哪"),
            Callout(
                CalloutKind.TIP,
                "生活类比：抬头看门牌",
                "人在房子里走动，偶尔会忘了自己在哪一间。pwd 就是抬头看门牌，把完整地址打印出来。",
            ),
            Paragraph("pwd = print working directory。打印当前工作目录（cwd）的绝对路径。没有常用参数，初学直接敲 pwd。"),
            Code(
                "bash",
                "pwd\n# 期望类似：/home/ada/c-week   或   /Users/ada/c-week\n# Windows Git Bash 也可能是 /c/Users/Ada/c-week",
                "输出必须是你打开的工作区。若是 /home/ada 或 C:\\Users\\Ada，说明站在家目录，相对路径会找错文件。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "不先 pwd 就 gcc hello.c：文件明明在资源管理器里，终端却报 No such file。先看门牌，再用 cd 走进 c-week。",
            ),

            Heading("ls：这个房间里有什么"),
            Callout(
                CalloutKind.TIP,
                "生活类比：扫一眼桌面",
                "ls 是转一圈看桌上放了哪些本子，不打开内容。想看封面细节（权限、大小）用 ls -l。",
            ),
            Paragraph("ls = list。列出当前目录（或你指出的路径）里的文件和子目录。初学常用：ls、ls -l（长格式）、ls -a（含隐藏文件，以 . 开头的也显示）。"),
            Code(
                "bash",
                "ls\nls -l\nls hello.c          # 只确认这一个文件在不在\nls -l hello.c src    # 可以跟多个名字",
                "-l 能看见权限列（-rw-r--r--）和是否可执行。找不到 hello.c 时先 ls，不要猜。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "把 ls 当成「打开文件」。看内容用 cat，编辑用 VS Code。ls 空目录看起来像没反应，其实是「这里没有可见条目」。",
            ),

            Heading("cd：走进另一间房"),
            Callout(
                CalloutKind.TIP,
                "生活类比：人走过去",
                "cd 是你整个人换房间，不是把文件搬走。之后的相对路径都相对新房间。",
            ),
            Paragraph("cd = change directory。换当前工作目录。常用：cd 子目录、cd ..（上一级）、cd ~（家目录）、cd -（回到刚才那间，部分 shell 支持）、cd ~/c-week（用绝对/家路径一次到位）。"),
            Code(
                "bash",
                "pwd\ncd src              # 走进子目录 src（要先有这个目录）\npwd                 # 应变成 .../c-week/src\ncd ..               # 回到上一级，也就是 c-week\ncd ~                # 回到家\ncd ~/c-week         # 再回书房，不依赖你刚才在哪",
                "迷路了：pwd 看门牌，cd ~/c-week 回家做作业。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "cd src 但 src 不存在 → No such file or directory，先 mkdir 或 ls 看真名。cd /c-week 少写了家目录，那是从根找，通常不对。cd 单独敲一下（无参数）在 bash 里等于 cd ~，会离开工作区。",
            ),

            Heading("mkdir：新开一间房间"),
            Callout(
                CalloutKind.TIP,
                "生活类比：在书房里隔出储藏间",
                "mkdir 只造空房间，不会自动放文件。-p 表示「中间楼层没有也一并砌上」，重复执行不会报错。",
            ),
            Paragraph("mkdir = make directory。创建目录。初学：mkdir src；一次建多层用 mkdir -p notes/day1。"),
            Code(
                "bash",
                "pwd                  # 确认在 ~/c-week\nmkdir src\nmkdir -p notes/day1\nls",
                "-p：父目录不存在就创建；目录已存在也不报错。适合写进脚本。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "mkdir src 时已经有 src → 报 File exists（没加 -p）。mkdir src/foo 但 src 还不在 → 需要 -p。名字里尽量不要空格；有空格要加引号。",
            ),

            Heading("touch：放一本空白练习本"),
            Callout(
                CalloutKind.TIP,
                "生活类比：先放空本再写字",
                "touch 造一个空文件（若已存在则只更新「最后改过」的时间）。真正写内容用编辑器或 echo/重定向。",
            ),
            Paragraph("初学几乎不用参数：touch notes.txt。一次可以 touch a.c b.c。"),
            Code(
                "bash",
                "touch notes.txt\nls -l notes.txt     # 大小经常是 0\n# 已存在时再 touch 一次：内容不变，时间戳会更新",
                "空文件可以占位，提醒自己「这里还要写」。源码更常见的是在 VS Code 里 New File 再保存。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "touch 不会创建中间目录：touch src/a.c 若 src 不存在会失败。它也不会打开编辑器。",
            ),

            Heading("cp：复印一份"),
            Callout(
                CalloutKind.TIP,
                "生活类比：复印，原件还在",
                "cp 留下原件，多出一份拷贝。备份交实验前的代码很有用。",
            ),
            Paragraph("cp = copy。cp 源 目标。目标可以是新文件名，或已有目录（拷进目录、名字不变）。常用：cp -i（覆盖前询问）、cp -r 目录（复制整棵目录树）。"),
            Code(
                "bash",
                "cp hello.c hello.bak.c\nls\nmkdir -p backup\ncp hello.c backup/          # 拷进 backup/hello.c\n# cp -r src src-copy        # 复制整个目录时必须 -r",
                "先 ls 确认源文件在。拷完再 ls 目标位置。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "cp 目录却忘了 -r → 报 omitting directory。目标已存在且是文件时，默认覆盖，初学可加 -i。cp a.c b.c 若 b.c 已有内容会被换成 a.c 的拷贝。",
            ),

            Heading("mv：搬走或改名"),
            Callout(
                CalloutKind.TIP,
                "生活类比：搬家，原地不再留一份",
                "mv 不是复印：旧位置的那份消失。改名其实也是 mv，只是「搬到同一房间但换门牌」。",
            ),
            Paragraph("mv = move。mv 旧名 新名 改名；mv 文件 目录/ 搬进目录。同样有 -i 防止误覆盖。"),
            Code(
                "bash",
                "mv notes.txt readme.txt     # 改名\nmkdir -p src\nmv readme.txt src/          # 搬进 src/\nls src",
                "改名和移动是同一条命令，差别只在目标是新文件名还是已有目录。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "mv hello.c src 若 src 不是目录而是普通文件，会把 hello.c 的内容覆盖进名叫 src 的文件，原 src 丢掉。先 ls -ld src 看它是不是目录。",
            ),

            Heading("rm：扔掉，没有回收站"),
            Callout(
                CalloutKind.TIP,
                "生活类比：碎纸机，不是废纸篓",
                "图形界面删除往往进回收站。rm 是直接碎掉。删之前 ls 看清名字。",
            ),
            Paragraph("rm = remove。rm 文件。删目录要用 rm -r（本课尽量少用）。-i 删除前询问。千万不要复制来路不明的 rm -rf /。"),
            Code(
                "bash",
                "ls\nrm notes.bak          # 只删确认过的文件\n# rm -i hello.bak.c   # 会问一声\n# rm -r tmp           # 删目录，本课作业尽量不用",
                "gcc 编错的可执行文件 hello 可以 rm hello 再重新编译。源码 hello.c 删了就没了，除非你有备份或 git。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "rm hello 和 rm hello.c 不是同一个文件。通配符 rm *.o 会删当前目录所有 .o。空格写成 rm hello .c 会试图删 hello 和 .c 两个名字。",
            ),

            Heading("cat：把本子摊开给自己看"),
            Callout(
                CalloutKind.TIP,
                "生活类比：朗读全文",
                "cat 把文件内容从头到尾印到终端。短文件合适；很长的源码更适合在 VS Code 里打开。",
            ),
            Paragraph("cat = concatenate（拼接输出）。初学：cat hello.c。多个文件 cat a.c b.c 会连着打印。"),
            Code(
                "bash",
                "cat hello.c\ncat notes.txt hello.c     # 两个文件依次输出到屏幕",
                "用来快速确认源码是否保存成功、重定向后的文本对不对。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "cat 大文件会刷屏，用 VS Code 打开更好。cat 没有文件会报错。它不会进入编辑模式。",
            ),

            Heading("echo：把一句话念出来"),
            Callout(
                CalloutKind.TIP,
                "生活类比：自己先说一遍",
                "echo 把后面的文字打到屏幕（或经重定向写进文件）。用来造一小段测试输入，或确认 shell 怎么理解你写的字。",
            ),
            Paragraph("echo 文字。加引号可保留空格。没有「读取文件」的功能，那是 cat。"),
            Code(
                "bash",
                "echo Hello, C\necho \"Hello, C\"           # 有空格时建议加引号\necho 100                   # 后面可以喂给程序：echo 100 | ./temp",
                "默认在末尾加换行。只是打印，不会编译、不会保存，除非你用 >。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "echo hello.c 只是把四个字 hello.c 印出来，不是显示文件内容。看文件请 cat。特殊字符（*、$）在无引号时可能被 shell 先展开。",
            ),

            Heading("clear：擦黑板，不扔本子"),
            Callout(
                CalloutKind.TIP,
                "生活类比：擦黑板",
                "屏幕上命令堆太多时，clear 把可视区域清空。文件一个都不会删。",
            ),
            Paragraph("无参数。Ctrl+L 在很多终端里同样清屏。"),
            Code(
                "bash",
                "clear\n# 光标回到顶部。滚动历史里旧输出可能还在，但眼前干净了",
                "清屏不是撤销，也不是删除文件。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "把 clear 当成「清空文件夹」。那是 rm。clear 只影响显示。",
            ),

            Heading(">、>> 与 |：装盒、追加、传送带"),
            Callout(
                CalloutKind.TIP,
                "生活类比：餐桌、保鲜盒、传送带",
                "命令默认把结果端上桌（屏幕）。> 是装进保鲜盒并盖上（文件已有内容会被倒掉重装）。>> 是往盒里再塞一份。| 是传送带：左边做好的东西直接递给右边，不上桌。",
            ),
            Paragraph("> 覆盖写入文件；>> 追加；| 把左边的标准输出接到右边的标准输入。本周最常见：给即将写的 C 程序喂一行数字。"),
            Code(
                "bash",
                "echo Hello, C > hello.txt      # 覆盖写入\ncat hello.txt\necho more >> hello.txt         # 追加一行\ncat hello.txt\necho 100 | ./temp              # 把 100 当作程序的键盘输入（程序要先编译好）\ngcc hello.c -o hello -Wall && ./hello\n# && 表示左边成功才执行右边，不是管道",
                "| 传递数据；> 写入文件；&& 串联「成功才继续」的步骤。三者不要混。",
            ),
            Callout(
                CalloutKind.WARN,
                "常见错误",
                "echo hi > hello.c 会把源码覆盖成两个字母 hi，极危险。> 目标要对，不要指向你的 .c。想保留旧内容用 >>。",
            ),
        ),
    ),
    Lesson(
        id = "d1-l4s",
        title = "bash 速查表与练习清单",
        minutes = 10,
        summary = "何时用哪条命令、对照表、以及在 c-week 里走一遍的检查单。",
        blocks = listOf(
            Heading("何时用哪一条"),
            Bullets(
                listOf(
                    "迷路了 → pwd；确认桌上有什么 → ls / ls -l。",
                    "换房间 → cd；新开房间 → mkdir；放空本 → touch。",
                    "要留原件 → cp；改名或搬走 → mv；扔掉 → rm（先 ls）。",
                    "朗读文件 → cat；把一句话说出来 → echo。",
                    "屏幕太乱 → clear（不删文件）。",
                    "结果写进文件 → >（覆盖）或 >>（追加）；交给下一命令 → |。",
                ),
            ),
            Heading("速查表"),
            Code(
                "text",
                "命令      干什么                 初学参数         例子\n---------------------------------------------------------------\npwd       打印当前目录             （无）            pwd\nls        列出文件                 -l  -a           ls -l\ncd        换目录                   ..  ~            cd ~/c-week\nmkdir     建目录                   -p               mkdir -p src\ntouch     建空文件 / 更新时间       （无）            touch notes.txt\ncp        复制（留原件）            -i  -r           cp hello.c hello.bak.c\nmv        移动或改名               -i               mv notes.txt src/\nrm        删除（无回收站）          -i  -r 慎用       rm notes.bak\ncat       打印文件内容             （无）            cat hello.c\necho      打印文字                 （无）            echo Hello, C\nclear     清屏                     （无）            clear\n>         覆盖写入文件                              echo hi > out.txt\n>>        追加写入文件                              echo more >> out.txt\n|         管道，接到下一命令                         echo 100 | ./temp",
                "打印出来对照。参数以本课够用为限，不必一次记全手册。",
            ),
            Heading("在 c-week 里走一遍（练习清单）"),
            Paragraph("打开 VS Code 集成终端，按顺序做。全部打勾再进下一课。"),
            Bullets(
                listOf(
                    "pwd 输出里包含 c-week（否则 cd ~/c-week）。",
                    "ls 能看见你的文件夹内容；ls -l 能读出权限列。",
                    "mkdir -p drill && cd drill && pwd 显示 .../c-week/drill。",
                    "touch memo.txt，echo hello > memo.txt，cat memo.txt 看到 hello。",
                    "echo more >> memo.txt，再 cat，应有两行。",
                    "cp memo.txt memo.bak，mv memo.bak ../，cd ..，ls 能看见 memo.bak。",
                    "rm memo.bak（先 ls 确认名字），clear 清屏后再 pwd，文件还在、屏幕干净。",
                    "cd drill 若还在，rm memo.txt，cd ..，rmdir drill（空目录才能 rmdir；或留下也行）。",
                ),
            ),
            Callout(
                CalloutKind.KEY,
                "过关标准",
                "能不看表说出：pwd/ls/cd 管「我在哪、有什么、怎么走」；cp 留原件、mv 不留、rm 不可恢复；> 会覆盖源码所以千万别对 .c 乱用。下一课测验会抽查。",
            ),
        ),
    ),
)
