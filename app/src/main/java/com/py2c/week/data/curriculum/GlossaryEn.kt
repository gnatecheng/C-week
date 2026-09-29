package com.py2c.week.data.curriculum

import com.py2c.week.data.GlossaryTerm

internal fun glossaryTermsEn(): List<GlossaryTerm> = listOf(
    GlossaryTerm(
        "path", "path 路径", "absolute / relative path", "Files",
        "Where a file lives on the directory tree. Absolute paths start at the root; relative paths depend on cwd.",
        "Street address vs. “next door”: an absolute path is the full city/street/number so mail finds you anywhere; a relative path only makes sense from where you stand now. Example: /home/ada/c-week/hello.c vs ./hello. The folder you open in VS Code is the workspace root; the integrated terminal’s default cwd is that folder. Home (~) is the whole house—don’t scatter homework outside the course folder.",
    ),
    GlossaryTerm(
        "cwd", "cwd 当前工作目录", "current working directory", "Files",
        "The directory the process is “standing in” right now. pwd prints it.",
        "Which room you’re in. pwd reads the nameplate. Relative paths, fopen(\"in.txt\"), and launch.json cwd all depend on it. If lost, pwd first, then cd to c-week.",
    ),
    GlossaryTerm(
        "perm", "permission 权限", "rwx / chmod", "Files",
        "Read, write, execute. Executables need x or ./hello gets Permission denied.",
        "Three keys on the lock: read = look, write = change contents, execute = run as a program. x on a directory means you may enter. ls -l shows them. Don’t chmod 777 the whole disk.",
    ),
    GlossaryTerm(
        "bash", "bash 命令", "pwd ls cd mkdir cp mv rm cat echo", "Terminal",
        "File and compile operations in the VS Code integrated terminal.",
        "pwd = read the room name; ls = scan the desk; cd = change rooms; mkdir = add a closet; touch = empty notebook; cp = photocopy; mv = move/rename; rm = shredder (no recycle bin); cat = read aloud; echo = say a line; clear = wipe the board. See Day 1 lessons and the cheat sheet.",
    ),
    GlossaryTerm(
        "redirect", "重定向与管道", "> >> |", "Terminal",
        "> writes a file (overwrite), >> appends, | pipes left output into right.",
        "Plate, box, conveyor: default serves on the table; > puts food in a box (throws away old contents); >> adds more; | hands output to the next step. echo 100 | ./temp feeds one line to your program. Never echo over your .c source.",
    ),
    GlossaryTerm(
        "launch", "launch.json", "type / request / program / cwd", "Debug",
        "Tells VS Code’s C/C++ extension what F5 should launch.",
        "Kitchen manual: which stove (type=cppdbg), which dish (program points at the binary, not .c), which counter (cwd usually \${workspaceFolder}), prep before cooking (preLaunchTask runs gcc), where gdb lives (miDebuggerPath).",
    ),
    GlossaryTerm(
        "tasks", "tasks.json", "preLaunchTask / gcc -g", "Debug",
        "Compile before F5 so you debug the binary you just built.",
        "Prep steps. label must match preLaunchTask exactly. Put -g in args so breakpoints line up with source lines.",
    ),
    GlossaryTerm(
        "breakpoint", "breakpoint 断点", "F9 / F5 / F10 / F11", "Debug",
        "Pause at a line to inspect variables and step.",
        "Checkpoint on the road: red dot = stop. F9 toggles; F5 run/continue; F10 step over; F11 step into; Shift+F11 step out; Shift+F5 stop. VARIABLES is the trunk at the stop.",
    ),
    GlossaryTerm(
        "pointer", "pointer 指针", "address / dereference", "Memory",
        "A variable that holds an address. & takes address, * dereferences.",
        "Room number vs. contents: x is the room, &x is the number, p is a card copying that number, *p opens the door. p = NULL is “no such address”—dereferencing is undefined behavior.",
    ),
    GlossaryTerm(
        "stack-heap", "stack / heap 栈与堆", "automatic vs dynamic storage", "Memory",
        "Locals usually live on the stack; malloc uses the heap and needs free.",
        "Hotel room vs. rented warehouse: entering a function checks in; returning checks out and invalidates the key. Heap is rented storage—you must return the key (free).",
    ),
    GlossaryTerm(
        "array-decay", "array decay 数组退化", "array-to-pointer conversion", "Arrays",
        "When passed to a function, an array becomes a pointer to its first element; length is lost.",
        "You hand over only the first locker’s address—the callee doesn’t know how many follow. Signature (int *a, int n). sizeof inside the function does not count elements.",
    ),
    GlossaryTerm(
        "struct", "struct 结构体", "record / field layout", "Data",
        "Fixed field layout decided at compile time.",
        "Student ID card with welded columns: id, name, score positions fixed at compile time. Field access is offset math. Use -> through pointers.",
    ),
    GlossaryTerm(
        "malloc", "malloc / free", "dynamic allocation", "Memory",
        "Heap allocation and release. Failure returns NULL.",
        "Rent a shelf: you need the key to use it; NULL means rent failed. Double-free or use-after-free are crashes. Who allocates frees.",
    ),
    GlossaryTerm(
        "ub", "undefined behavior 未定义行为", "UB", "Safety",
        "The standard guarantees nothing: out of bounds, null deref, signed overflow…",
        "Like jaywalking: might work once, another compiler or optimization breaks you. -Wall and sanitizers are insurance.",
    ),
    GlossaryTerm(
        "pass-by-value", "pass by value 传值", "copy of the argument", "Functions",
        "Parameters are copies; changing the copy doesn’t affect the caller.",
        "Photocopied homework: editing the copy doesn’t change the original on your desk. To change the original, pass its address (pointer). scanf already does this.",
    ),
    GlossaryTerm(
        "nul", "NUL terminator '\\0'", "C string", "Strings",
        "C strings end with a zero byte.",
        "Blank card at the shelf end meaning “stop here”. printf(\"%s\") and strlen rely on it; without it you read into the next locker.",
    ),
    GlossaryTerm(
        "header", "header / 源文件", "#include", "Toolchain",
        "#include inserts declarations; .c files provide definitions.",
        "Menu vs. kitchen: headers are names and ingredient lists (declarations); .c is the recipe (definition). include is preprocessing pasting the menu page in.",
    ),
    GlossaryTerm(
        "compile", "compile + link 编译链接", "translation + linking", "Toolchain",
        "Source → object files → executable.",
        "Recipe vs. plated dish: .c isn’t served directly. Compile turns source into steps; link wires printf and friends. Missing symbols = link error.",
    ),
    GlossaryTerm(
        "segfault", "segmentation fault", "invalid memory access", "Debug",
        "Accessing memory you’re not allowed to touch.",
        "Using a blank or expired room key. Common: NULL, wild pointers, use-after-return, writing string literals. Use the debugger on pointer values at the crash.",
    ),
    GlossaryTerm(
        "const", "const", "read-only view", "Types",
        "Promise not to modify through this view.",
        "Read-only ticket: const char *s = won’t change the chars; char * const p = won’t swap which address the pointer holds.",
    ),
    GlossaryTerm(
        "typedef", "typedef", "type alias", "Types",
        "Shorter name for an existing type.",
        "Nickname on the ID card—not a new document, fewer characters. typedef struct Node Node; then Node *p is shorter.",
    ),
    GlossaryTerm(
        "adj", "adjacency list 邻接表", "edge lists per vertex", "Graphs",
        "Each vertex keeps its outgoing edges.",
        "Contacts list: each person followed by friend numbers. Saves space on sparse graphs. In C: linked lists or parallel arrays to/nxt/head.",
    ),
    GlossaryTerm(
        "dijkstra", "Dijkstra", "non-negative shortest path", "Graphs",
        "Single-source shortest paths with non-negative weights.",
        "GPS by minutes not intersections. Direct edge may be slower. All weights 1 degenerates to BFS. Teaching uses O(V²) scan for minimum; settled nodes don’t change.",
    ),
    GlossaryTerm(
        "prototype", "function prototype", "forward declaration", "Functions",
        "Declare the signature before the definition.",
        "Menu lists dish names before cooking. The compiler reads top-down; unknown names error without a prototype.",
    ),
)
