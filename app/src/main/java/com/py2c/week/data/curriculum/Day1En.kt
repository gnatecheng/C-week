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
import com.py2c.week.data.LabTestCase
import com.py2c.week.data.Lesson
import com.py2c.week.data.QuizQuestion

internal fun day1En(): CourseDay = CourseDay(
    id = 1,
    title = "Setup, files, and Hello World",
    subtitle = "Paths · bash · VS Code · first compile",
    outcome = "Tell paths apart from your workspace, do basic file ops in the terminal, create a .c in VS Code, compile and run with gcc, and read launch.json plus breakpoint debugging.",
    minutes = 165,
    todayFocus = "A recipe (.c) must become a dish (executable) before it can be served. In the terminal, which room you stand in decides how relative paths work.",
    lessons = listOf(
        Lesson(
            id = "d1-l1",
            title = "Where this week takes you",
            minutes = 8,
            summary = "For programming beginners: what C covers, the week map, and why you must type commands yourself.",
            blocks = listOf(
                Heading("Learn C from zero to shortest paths in seven days"),
                Paragraph("This course targets first-year students and anyone who has not written C before. We do not assume another language. Day 7 is not about memorizing APIs—you implement weighted-graph shortest paths (Dijkstra) with correct arrays, pointers, and structs."),
                Bullets(
                    listOf(
                        "Day 1: files, terminal (each bash command explained), toolchain, Hello World, launch.json, and debugging.",
                        "Days 2–3: types, I/O, if/for, functions and pass-by-value.",
                        "Days 4–5: pointers, arrays, strings, structs, and heap memory.",
                        "Days 6–7: BFS and Dijkstra—language skills on algorithm problems.",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: hotel room vs rented storage",
                    "Local variables in a function are like a hotel room at check-in: at checkout (return) the front desk reclaims it and the key is invalid. Heap memory is like rented warehouse shelves: without returning the key (free), you keep occupying space. We reuse this picture all week.",
                ),
                Callout(
                    CalloutKind.KEY,
                    "Two memory models (precise terms)",
                    "Locals live on the stack by default; that memory is invalid after the function returns. Heap memory requires your own malloc/free. Getting this wrong is not a style issue—it crashes.",
                ),
                Callout(
                    CalloutKind.KEY,
                    "Discipline for the week",
                    "Follow along in VS Code on your computer every day. The mobile app teaches, quizzes, and simulates grading; real gcc runs on your machine.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l2",
            title = "Paths, directories, and workspace",
            minutes = 14,
            summary = "Absolute vs relative paths, cwd, home directory vs the folder you open in VS Code.",
            blocks = listOf(
                Heading("Files live in a tree, not on desktop icons"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: street address",
                    "A path is an address. An absolute path is the full city/district/street/number/room—mail arrives no matter where you stand. A relative path is “the next room” or “one floor up”—it depends entirely on where you are now.",
                ),
                Paragraph("The OS stores files in a directory tree. Each file has a path: the sequence of folder names from the root. A directory (folder) is a node that can hold files or subdirectories."),
                Bullets(
                    listOf(
                        "On Linux / macOS the root is /. Example: /home/ada/c-week/hello.c.",
                        "On Windows a drive letter is a kind of root, e.g. C:\\Users\\Ada\\c-week\\hello.c. In VS Code’s terminal you can also write /c/Users/Ada/c-week.",
                        "Home directory: your personal root. Often /home/username on Linux, /Users/username on macOS, written ~.",
                    ),
                ),
                Heading("Current working directory (cwd)"),
                Paragraph("Think of the terminal as you standing in one room of a house. pwd is reading the nameplate: where am I? If you ask for hello.c by filename alone, the system only searches the room you are in."),
                Paragraph("Term: current working directory (cwd). Relative paths are relative to cwd; absolute paths start at the root (or drive) and ignore where you stand."),
                Code(
                    "bash",
                    "pwd\n# example output: /home/ada/c-week\n\nls hello.c          # relative: hello.c in cwd\nls /home/ada/c-week/hello.c   # absolute: full path from root\nls ./hello.c        # ./ means \"in cwd\"\nls ../readme.md     # .. is the parent directory",
                    "The same file can be named with relative or absolute paths. program and cwd in debug configs are paths too—wrong values mean the executable is not found.",
                ),
                Heading("Workspace folder ≠ home directory"),
                Paragraph("Home ~ is the whole house (downloads, photos, other courses). The workspace is a study you set aside: create ~/c-week and use VS Code Open Folder to enter it. The integrated terminal usually starts at that folder so gcc hello.c finds your file."),
                Callout(
                    CalloutKind.WARN,
                    "Do not drag only a .c file into VS Code",
                    "With a single file open, terminal cwd is often home or whatever was left over. Relative paths, debugging, and new files go missing. Always open the folder.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l3",
            title = "Create, copy, move, delete, and permissions",
            minutes = 12,
            summary = "mkdir/touch/cp/mv/rm; read-write-execute permissions in plain terms.",
            blocks = listOf(
                Heading("What you do to files"),
                Paragraph("Saving in the editor is a write. You also create empty files, copy backups, rename, move into subfolders, and delete bad binaries in the terminal. Same as drag-and-drop in a GUI, but commands can be repeated and scripted."),
                Code(
                    "bash",
                    "mkdir -p ~/c-week/src     # create dirs; -p creates parents if missing\ncd ~/c-week\ntouch notes.txt          # create empty file (or touch timestamp if it exists)\ncp notes.txt notes.bak   # copy\nmv notes.txt src/        # move (into directory if target is a dir)\nmv src/notes.txt readme.txt   # rename is also mv\nrm notes.bak             # delete file\n# rm -r src              # delete directory (use sparingly; no undo)",
                    "cp keeps the original; mv does not. ls before you rm.",
                ),
                Heading("Permissions: who can read, write, run"),
                Paragraph("Permissions are three keys on the lock: r read, w write, x execute as a program. Source needs read/write; gcc’s hello needs execute or ./hello yields Permission denied—the door exists but the key is wrong."),
                Paragraph("Each file has owner, group, and others. On directories, x means you can enter; without it you may not cd even with r."),
                Code(
                    "bash",
                    "ls -l hello.c hello\n# -rw-r--r--  1 ada ada  120  hello.c     # file, owner read/write\n# -rwxr-xr-x  1 ada ada  16k hello        # executable\n\nchmod +x hello           # add execute for you\n# gcc usually produces an executable with x; chmod not always needed",
                    "Directory x means you can enter. Without it you may not cd even with r.",
                ),
                Callout(
                    CalloutKind.TIP,
                    "Three rules for beginners",
                    "1) Source files: read and write. 2) Program files: execute. 3) Do not chmod 777 the whole disk. Keep course work inside your c-week folder.",
                ),
            ),
        ),
    ) + bashCommandLessonsEn() + listOf(
        Lesson(
            id = "d1-l5",
            title = "Install VS Code",
            minutes = 10,
            summary = "Download the editor, optional language pack, terminal inside the editor.",
            blocks = listOf(
                Heading("Why VS Code"),
                Paragraph("Cross-platform, integrated terminal, and the C/C++ extension gives navigation and debugging. CLion / Visual Studio work too; this course demos every click in VS Code."),
                Bullets(
                    listOf(
                        "Open https://code.visualstudio.com/ and download the installer for your OS.",
                        "Windows: run the installer and check Add to PATH.",
                        "macOS: drag VS Code into Applications; if code is missing in terminal, run Shell Command: Install 'code' command from the palette.",
                        "Linux: use your distro package or official .deb/.rpm.",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "Localized UI",
                    "Search the marketplace for Chinese (Simplified) Language Pack if you want it. Course screencasts use English menu names.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l6",
            title = "Install the C/C++ extension",
            minutes = 8,
            summary = "Microsoft C/C++: highlighting, completion, debugging. The extension is not the compiler.",
            blocks = listOf(
                Paragraph("The extension does not ship gcc. It connects the editor to gcc/clang/gdb on your machine. Without a compiler you see errors like cannot find cl.exe / gcc. Below is a real VS Code marketplace recording."),
                VsCode("install_cpptools"),
                Callout(
                    CalloutKind.WARN,
                    "Skip the theme pack for now",
                    "Install only C/C++ first. One-click run plugins hide which compile command ran. This week you type gcc in the integrated terminal.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l7",
            title = "Install a compiler",
            minutes = 15,
            summary = "Windows MinGW / macOS clang / Linux gcc. Verify gcc --version.",
            blocks = listOf(
                Heading("The compiler is C’s “runtime”"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: recipe vs finished dish",
                    ".c source is the recipe—it cannot be served as-is. gcc/clang is the cook producing the executable. Without a compiler you only have text. Linking gathers standard-library “tools” (printf); missing pieces are link errors.",
                ),
                Paragraph("The C standard defines syntax and libraries; executables come from a compiler. We use gcc (Linux / Windows MinGW) or clang (macOS default). Without one, .c files are just text."),
                Heading("Windows"),
                Bullets(
                    listOf(
                        "Recommended: install MSYS2, then pacman -S mingw-w64-ucrt-x86_64-gcc.",
                        "Or WinLibs / w64devkit and add bin to PATH.",
                        "Open a new PowerShell window and run gcc --version—you should see a version, not “command not found”.",
                    ),
                ),
                Heading("macOS"),
                Paragraph("Run xcode-select --install in Terminal and wait for Command Line Tools. clang --version should print. clang is fine for teaching C11."),
                Heading("Linux"),
                Code("bash", "sudo apt install build-essential   # Debian/Ubuntu\nsudo dnf install gcc gdb         # Fedora", "Install gcc, make, and gdb with your package manager"),
                Callout(
                    CalloutKind.KEY,
                    "Acceptance check",
                    "gcc --version or clang --version succeeding means the toolchain is ready. If VS Code’s status bar asks for compilerPath, pick the gcc you just installed.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l8",
            title = "Open a folder and write Hello World",
            minutes = 12,
            summary = "Workspace, .c file, main and printf. Two click-through recordings.",
            blocks = listOf(
                Paragraph("Create an empty c-week folder under home. Use Open Folder, not a single dragged file—terminal cwd and debug cwd follow the workspace. Two live recordings below."),
                VsCode("open_folder"),
                VsCode("create_hello"),
                Example(
                    title = "Smallest C program",
                    source = "#include <stdio.h>\n\nint main(void) {\n    printf(\"Hello, C\\n\");\n    return 0;\n}",
                    note = "stdio.h supplies printf. main is the entry; return 0 means normal exit. \\n is newline; without it the cursor stays on the same line.",
                ),
                Callout(
                    CalloutKind.WARN,
                    "First pitfall",
                    "If the file is hello.c.txt (Explorer hiding extensions), gcc refuses. Check the full name in VS Code’s explorer.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l9",
            title = "Compile and run in the terminal",
            minutes = 12,
            summary = "gcc -o, ./hello, and the most common errors.",
            blocks = listOf(
                VsCode("compile_run"),
                Heading("What happened"),
                Paragraph("gcc compiles hello.c to an object file (machine code), then links in the C library’s printf, producing executable hello. Expect compile errors (syntax/types) and link errors (missing main, missing libraries)."),
                Code(
                    "bash",
                    "gcc hello.c -o hello -Wall -Wextra -g\n./hello\necho $?    # print exit code; 0 means success",
                    "-Wall enables warnings; -g adds debug info so F5 can show line numbers.",
                ),
                Bullets(
                    listOf(
                        "fatal error: stdio.h: No such file or directory → compiler or headers not installed.",
                        "undefined reference to `main' → empty file or wrong main name.",
                        "error: expected ';' → almost every C statement needs a semicolon.",
                        "Permission denied running ./hello → chmod +x hello, or re-run gcc.",
                    ),
                ),
                Callout(
                    CalloutKind.TIP,
                    "Always enable warnings",
                    "gcc hello.c -o hello -Wall -Wextra. Warnings often predict tomorrow’s segfaults. From Day 4 you will be glad you kept them.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l10",
            title = "launch.json and tasks.json field by field",
            minutes = 16,
            summary = "How F5 finds the executable: type, request, program, cwd, miDebuggerPath, preLaunchTask.",
            blocks = listOf(
                Heading("Why these two files"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: kitchen instructions",
                    "Typing gcc is cooking yourself. F5 debugging is delegating: specify the pot (debugger type), the dish (program points at the executable), the stove (cwd), and whether to prep first (preLaunchTask runs gcc). launch.json is the manual; tasks.json is the prep step.",
                ),
                Paragraph("gcc in the terminal is clearest. But the debugger (F5) needs: 1) which debugger; 2) which program; 3) starting directory; 4) compile before launch or not. That lives in .vscode/launch.json and tasks.json in the workspace, portable across machines."),
                VsCode("launch_json"),
                Heading("launch.json: tell the debugger how to start"),
                Code(
                    "json",
                    "{\n  \"version\": \"0.2.0\",\n  \"configurations\": [\n    {\n      \"name\": \"Debug hello\",\n      \"type\": \"cppdbg\",\n      \"request\": \"launch\",\n      \"program\": \"\${workspaceFolder}/hello\",\n      \"args\": [],\n      \"cwd\": \"\${workspaceFolder}\",\n      \"stopAtEntry\": false,\n      \"miDebuggerPath\": \"gdb\",\n      \"preLaunchTask\": \"C: gcc build hello\"\n    }\n  ]\n}",
                    "On Windows MinGW use the full path to gdb.exe; on macOS lldb is common while type is often still cppdbg.",
                ),
                Bullets(
                    listOf(
                        "type: debugger adapter. C/C++ provides cppdbg (gdb/lldb). Wrong type means F5 cannot find a debugger.",
                        "request: launch starts the program; attach hooks a running process—this course uses launch only.",
                        "program: executable to debug, not hello.c. \${workspaceFolder} is the folder you opened.",
                        "cwd: process working directory at start. Relative paths and fopen(\"in.txt\") use it—usually the workspace root.",
                        "args: command-line arguments to main, like ./hello a b. Leave empty for Hello World.",
                        "miDebuggerPath: gdb binary. Linux often uses gdb; if VS Code cannot find it, use the absolute path from which gdb.",
                        "preLaunchTask: run the matching tasks.json entry before F5 so you debug a fresh binary, not yesterday’s build.",
                        "stopAtEntry: true stops at main entry. Beginners can leave false and rely on breakpoints.",
                    ),
                ),
                Heading("tasks.json: gcc before F5"),
                Code(
                    "json",
                    "{\n  \"version\": \"2.0.0\",\n  \"tasks\": [\n    {\n      \"label\": \"C: gcc build hello\",\n      \"type\": \"shell\",\n      \"command\": \"gcc\",\n      \"args\": [\"hello.c\", \"-o\", \"hello\", \"-Wall\", \"-g\"],\n      \"group\": { \"kind\": \"build\", \"isDefault\": true },\n      \"problemMatcher\": [\"\$gcc\"]\n    }\n  ]\n}",
                    "label must match preLaunchTask exactly. Do not drop -g or breakpoints miss lines.",
                ),
                Callout(
                    CalloutKind.KEY,
                    "When things do not line up, check these three",
                    "1) program file missing: gcc once in the terminal. 2) wrong cwd: relative paths break. 3) preLaunchTask name typo: F5 runs an old binary and your edits seem to do nothing.",
                ),
            ),
        ),
        Lesson(
            id = "d1-l11",
            title = "Breakpoint debugging: F9 to variables",
            minutes = 16,
            summary = "Set/clear breakpoints, F5 start, continue, step, call stack, stop. Follow the live recording.",
            blocks = listOf(
                Heading("What the debugger fixes"),
                Callout(
                    CalloutKind.TIP,
                    "Everyday analogy: checkpoints on the road",
                    "printf is checking the score at the finish. A breakpoint is a checkpoint: the red dot forces a stop. F10 step over is one step along this road; F11 step into is detouring into a shop; Shift+F11 step out exits the shop; F5 continue runs to the next stop. VARIABLES is what is in the trunk when you stop.",
                ),
                Paragraph("printf shows results, not which line ran or what variables held. The debugger stops where you choose so you can step and read values in the Variables pane. The recording shows real VS Code breakpoints, F5, and stepping."),
                VsCode("debug_bp"),
                Heading("Step by step (match shortcuts on your machine)"),
                Bullets(
                    listOf(
                        "Breakpoint: put the cursor on the line (e.g. loop body), press F9 for a red dot; F9 again clears. Or click the gutter.",
                        "Start debugging: F5 or Run and Debug. With a good config, preLaunchTask runs then program starts.",
                        "Hit breakpoint: current line highlights. VARIABLES lists locals; expand Locals if needed.",
                        "Continue: F5 while debugging—runs to the next breakpoint or exit.",
                        "Step Over (F10): finish the current line without entering callees—good for your own loops.",
                        "Step Into (F11): enter the function on this line. Skip stepping into the standard library at first.",
                        "Step Out (Shift+F11): return to the caller.",
                        "Call Stack: who called whom. In recursion or deep calls, pick a frame to see its variables.",
                        "Stop: Shift+F5 or the red square. The process is killed, not a normal return.",
                    ),
                ),
                Heading("Practice on a loop once"),
                Code(
                    "c",
                    "int main(void) {\n    int s = 0;\n    for (int i = 1; i <= 3; i++) {\n        s += i;   /* breakpoint on this line */\n    }\n    printf(\"%d\\n\", s);\n    return 0;\n}",
                    "Each stop, watch i and s: first 1 and 1, then 2 and 3, then 3 and 6—faster than guessing loop bounds.",
                ),
                Callout(
                    CalloutKind.WARN,
                    "Breakpoint gray / will not bind",
                    "Usually: rebuild without -g; program points at a binary without debug info; breakpoint on a blank line or after macro expansion. gcc -g in the terminal and confirm launch.json program is that binary.",
                ),
            ),
        ),
    ),
    lab = CodeLab(
        id = "lab-d1",
        title = "Lab: print your name",
        brief = "Adapt Hello World: printf one line in the required format.",
        task = "Write a complete C program that prints Hello, Ada followed by a newline. This lab uses a fixed name for automatic checking.",
        starterCode = """
#include <stdio.h>

int main(void) {
    /* TODO: print Hello, Ada with a newline */
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
        testCases = listOf(
            LabTestCase("Standard greeting", "Prints on startup, no input", "Hello, Ada"),
            LabTestCase(
                "Trailing newline",
                "Format string must include \\n or expected output will not match",
                "Hello, Ada",
                extraChecks = listOf(
                    LabCheck("nl-case", "Put \\n in the format string or there is no newline at end of line.", CheckRule.Contains("\\n")),
                ),
            ),
        ),
        checks = listOf(
            LabCheck("inc", "Missing #include <stdio.h>. printf may not compile without it.", CheckRule.Contains("#include <stdio.h>")),
            LabCheck("main", "Need int main", CheckRule.ContainsRegex("""int\s+main\s*\(""")),
            LabCheck("printf", "Use printf.", CheckRule.Contains("printf")),
            LabCheck("text", "Output must contain Hello, Ada.", CheckRule.Contains("Hello, Ada")),
            LabCheck("nl", "Put \\n in the format string or there is no newline at end of line.", CheckRule.Contains("\\n")),
        ),
        hints = listOf(
            "printf’s first argument is the format string; ordinary text is printed as-is.",
            "Newline is the two-character escape: backslash + n.",
            "On a real machine: gcc hello.c -o hello && ./hello",
        ),
    ),
    quiz = listOf(
        QuizQuestion(
            "d1-q1",
            "Which command best matches this course for turning hello.c into an executable?",
            listOf("code hello.c", "gcc hello.c -o hello", "cat hello.c", "chmod hello.c"),
            1,
            "gcc compiles and links; -o names the output.",
        ),
        QuizQuestion(
            "d1-q2",
            "What does VS Code’s C/C++ extension mainly provide?",
            listOf("A full Linux kernel", "Highlighting, completion, and debug adapters—not the compiler itself", "A cloud judge", "Automatic executables without gcc"),
            1,
            "The extension is glue. Install the compiler separately.",
        ),
        QuizQuestion(
            "d1-q3",
            "printf(\"hi\") without \\n at runtime usually?",
            listOf("Fails to compile", "Prints hi with no newline", "Adds a newline automatically", "Segfaults"),
            1,
            "Valid C; the cursor stays after hi.",
        ),
        QuizQuestion(
            "d1-q4",
            "Why insist on opening a folder instead of a single file?",
            listOf("Folders use less memory", "Terminal cwd, relative paths, and debugging are tied to the workspace; new files land predictably", "C syntax requires a folder", "VS Code cannot open single files"),
            1,
            "A workspace makes gcc hello.c paths predictable.",
        ),
        QuizQuestion(
            "d1-q5",
            "Relative path ./hello is resolved against what?",
            listOf("Always home directory", "Current working directory cwd (where pwd points in the terminal)", "Filesystem root", "gcc install directory"),
            1,
            "Change cwd and the same ./hello looks in the wrong place.",
        ),
        QuizQuestion(
            "d1-q6",
            "In launch.json, program should point to?",
            listOf("hello.c source", "The compiled executable (e.g. \${workspaceFolder}/hello)", "stdio.h", "VS Code itself"),
            1,
            "The debugger starts the binary, not source text.",
        ),
        QuizQuestion(
            "d1-q7",
            "While debugging, Step Over (F10) means?",
            listOf("Terminate the program", "Finish the current line without entering functions called on that line", "Skip compilation", "Remove breakpoints"),
            1,
            "Use Step Into (F11) to enter a function.",
        ),
        QuizQuestion(
            "d1-q8",
            "echo 100 > in.txt will?",
            listOf("Append 100 to the end of in.txt", "Write 100 into in.txt (overwriting if it exists)", "Launch program in.txt", "Delete in.txt"),
            1,
            "> overwrites; >> appends.",
        ),
        QuizQuestion(
            "d1-q9",
            "To back up hello.c and keep the original, use?",
            listOf("mv hello.c hello.bak.c", "cp hello.c hello.bak.c", "rm hello.c", "clear"),
            1,
            "cp copies and leaves the original; mv moves or renames—nothing left at the old name.",
        ),
        QuizQuestion(
            "d1-q10",
            "What does clear do?",
            listOf("Delete all files in the current directory", "Clear the screen only; files remain", "Close the terminal", "Uninstall gcc"),
            1,
            "Erases the chalkboard, not the shredder—that is rm.",
        ),
        QuizQuestion(
            "d1-q11",
            "Difference between cat hello.c and echo hello.c?",
            listOf("Exactly the same", "cat prints file contents; echo prints the literal text hello.c", "echo compiles", "cat deletes the file"),
            1,
            "Read source with cat (or the editor); echo only repeats what you typed.",
        ),
    ),
)

internal fun bashCommandLessonsEn(): List<Lesson> = listOf(
    Lesson(
        id = "d1-l4",
        title = "bash commands one by one: practice in the VS Code terminal",
        minutes = 28,
        summary = "pwd, ls, cd, mkdir, touch, cp, mv, rm, cat, echo, clear, and | > >>. For each: what it does, common flags, examples, pitfalls.",
        blocks = listOf(
            Heading("Open the integrated terminal first"),
            Paragraph("VS Code shortcut Ctrl+` (backtick, below Esc) opens the panel. Assume you Open Folder on c-week—the path before the prompt should be the course directory, not home. Type each example on your machine."),
            Callout(
                CalloutKind.KEY,
                "Practice convention",
                "Run pwd first to confirm location. hello.c in examples is source in your workspace. Windows PowerShell differs slightly; this course uses bash / VS Code terminal—if the prompt is PS, use Git Bash or MSYS2.",
            ),

            Heading("pwd: where am I standing"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: read the nameplate",
                "You walk around the house and forget which room you are in. pwd reads the nameplate and prints the full address.",
            ),
            Paragraph("pwd = print working directory. Prints the absolute path of cwd. No common flags—just type pwd."),
            Code(
                "bash",
                "pwd\n# expect something like: /home/ada/c-week   or   /Users/ada/c-week\n# Windows Git Bash may show /c/Users/Ada/c-week",
                "Output must be the workspace you opened. /home/ada or C:\\Users\\Ada means home—relative paths will miss files.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "gcc hello.c without pwd first: the file shows in Explorer but the terminal says No such file. Read the nameplate, then cd into c-week.",
            ),

            Heading("ls: what is in this room"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: glance at the desk",
                "ls looks around at which notebooks are on the desk without opening them. ls -l shows cover details (permissions, size).",
            ),
            Paragraph("ls = list. Lists files and subdirectories in cwd (or a path you give). Beginners: ls, ls -l (long), ls -a (include hidden names starting with .)."),
            Code(
                "bash",
                "ls\nls -l\nls hello.c          # check this one file exists\nls -l hello.c src    # multiple names allowed",
                "-l shows permissions (-rw-r--r--) and execute bit. If hello.c is missing, ls first—do not guess.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "Treating ls as open file. Use cat for contents, VS Code to edit. An empty directory looks like nothing happened—there are just no visible entries.",
            ),

            Heading("cd: walk into another room"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: you walk there",
                "cd moves you to another room; it does not move files. Relative paths are from the new room afterward.",
            ),
            Paragraph("cd = change directory. Common: cd subdir, cd .. (parent), cd ~ (home), cd - (previous room, some shells), cd ~/c-week (jump to course folder)."),
            Code(
                "bash",
                "pwd\ncd src              # enter subdir src (must exist)\npwd                 # should become .../c-week/src\ncd ..               # up to c-week\ncd ~                # home\ncd ~/c-week         # back to study folder from anywhere",
                "Lost: pwd for the nameplate, cd ~/c-week to get back to homework.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "cd src when src missing → No such file or directory—mkdir or ls for the real name. cd /c-week without home is wrong from root. Bare cd in bash is cd ~ and leaves the workspace.",
            ),

            Heading("mkdir: add a new room"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: partition storage in the study",
                "mkdir creates empty rooms only—no files inside. -p builds missing intermediate floors; repeating is safe.",
            ),
            Paragraph("mkdir = make directory. Beginners: mkdir src; nested paths: mkdir -p notes/day1."),
            Code(
                "bash",
                "pwd                  # confirm you are in ~/c-week\nmkdir src\nmkdir -p notes/day1\nls",
                "-p creates parents; existing directory is OK. Good for scripts.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "mkdir src when src exists → File exists (without -p). mkdir src/foo without src → need -p. Avoid spaces in names or quote them.",
            ),

            Heading("touch: place a blank notebook"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: empty notebook before writing",
                "touch creates an empty file (or updates timestamp if it exists). Use editor or echo/redirection for content.",
            ),
            Paragraph("Beginners rarely need flags: touch notes.txt. Multiple: touch a.c b.c."),
            Code(
                "bash",
                "touch notes.txt\nls -l notes.txt     # size often 0\n# touch again when file exists: content unchanged, timestamp updates",
                "Empty files mark placeholders. Source usually comes from VS Code New File + save.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "touch does not create parent directories: touch src/a.c fails if src is missing. It does not open an editor.",
            ),

            Heading("cp: make a photocopy"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: photocopy, original stays",
                "cp keeps the original and adds a copy—handy before risky edits.",
            ),
            Paragraph("cp = copy. cp source dest. Dest can be a new filename or an existing directory (file keeps its name). Common: cp -i (prompt on overwrite), cp -r for directory trees."),
            Code(
                "bash",
                "cp hello.c hello.bak.c\nls\nmkdir -p backup\ncp hello.c backup/          # becomes backup/hello.c\n# cp -r src src-copy        # -r required for directories",
                "ls to confirm source exists; ls again at the destination.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "cp on a directory without -r → omitting directory. Overwrites silently—use -i while learning. cp a.c b.c replaces b.c’s contents.",
            ),

            Heading("mv: move or rename"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: move house—nothing left at the old spot",
                "mv is not a copy: the old name vanishes. Rename is mv with a new name in the same room.",
            ),
            Paragraph("mv = move. mv old new renames; mv file dir/ moves into dir. -i prompts before overwrite."),
            Code(
                "bash",
                "mv notes.txt readme.txt     # rename\nmkdir -p src\nmv readme.txt src/          # into src/\nls src",
                "Rename and move are one command—the target is a new name or an existing directory.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "mv hello.c src when src is a regular file overwrites that file with hello.c’s bytes. ls -ld src to confirm it is a directory.",
            ),

            Heading("rm: shred—no recycle bin"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: shredder, not trash can",
                "GUI delete often goes to recycle. rm destroys immediately. ls before you rm.",
            ),
            Paragraph("rm = remove files. rm -r for directories (use sparingly here). -i asks first. Never run untrusted rm -rf /."),
            Code(
                "bash",
                "ls\nrm notes.bak          # only names you confirmed\n# rm -i hello.bak.c   # prompts\n# rm -r tmp           # remove directory—sparingly in homework",
                "Wrong binary hello: rm hello and re-gcc. hello.c gone is gone unless you have backup or git.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "rm hello vs rm hello.c are different files. rm *.o deletes all .o here. rm hello .c tries two names hello and .c.",
            ),

            Heading("cat: open the notebook and read it"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: read aloud",
                "cat prints file contents to the terminal. Fine for short files; long source belongs in VS Code.",
            ),
            Paragraph("cat = concatenate (print together). Beginners: cat hello.c. cat a.c b.c prints both in order."),
            Code(
                "bash",
                "cat hello.c\ncat notes.txt hello.c     # two files printed in sequence",
                "Quick check that source saved or redirection wrote the right text.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "Huge files flood the screen—use VS Code. Missing file errors. cat does not edit.",
            ),

            Heading("echo: say a sentence"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: say it yourself first",
                "echo prints words to the screen (or into a file via redirection). Builds tiny test input or shows how the shell parses text.",
            ),
            Paragraph("echo text. Quotes preserve spaces. It does not read files—that is cat."),
            Code(
                "bash",
                "echo Hello, C\necho \"Hello, C\"           # quote when spaces matter\necho 100                   # later: echo 100 | ./temp",
                "Adds a newline by default. Prints only—no compile or save unless you use >.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "echo hello.c prints the four letters hello.c, not file contents—use cat. * and $ may expand without quotes.",
            ),

            Heading("clear: erase the board, not the notebooks"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: wipe the chalkboard",
                "When the screen is cluttered, clear empties the visible area. No files deleted.",
            ),
            Paragraph("No arguments. Ctrl+L often does the same."),
            Code(
                "bash",
                "clear\n# cursor to top; scrollback may still hold old output but the view is clean",
                "Clearing is not undo and not deleting files.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "clear is not empty folder—that is rm. clear affects display only.",
            ),

            Heading(">, >>, and |: box, append, conveyor"),
            Callout(
                CalloutKind.TIP,
                "Everyday analogy: table, container, conveyor belt",
                "Commands default to serving on the table (screen). > packs into a container and replaces contents. >> adds another layer. | hands output to the next command without hitting the table.",
            ),
            Paragraph("> overwrite write; >> append; | connects stdout to stdin of the next command. Common this week: feed one number line to a C program."),
            Code(
                "bash",
                "echo Hello, C > hello.txt      # overwrite write\ncat hello.txt\necho more >> hello.txt         # append a line\ncat hello.txt\necho 100 | ./temp              # 100 as stdin (compile ./temp first)\ngcc hello.c -o hello -Wall && ./hello\n# && run right side only if left succeeded—not a pipe",
                "| moves data; > writes files; && chains success-only steps—do not mix them up.",
            ),
            Callout(
                CalloutKind.WARN,
                "Common mistake",
                "echo hi > hello.c replaces source with hi—dangerous. Point > at the right file, never your .c. Use >> to keep old content.",
            ),
        ),
    ),
    Lesson(
        id = "d1-l4s",
        title = "bash cheat sheet and practice checklist",
        minutes = 10,
        summary = "When to use which command, reference table, and a walkthrough checklist in c-week.",
        blocks = listOf(
            Heading("When to use which"),
            Bullets(
                listOf(
                    "Lost → pwd; see what is on the desk → ls / ls -l.",
                    "Change room → cd; new room → mkdir; blank file → touch.",
                    "Keep original → cp; rename or move → mv; discard → rm (ls first).",
                    "Read file → cat; say a line → echo.",
                    "Messy screen → clear (files stay).",
                    "Save output → > (overwrite) or >> (append); hand to next command → |.",
                ),
            ),
            Heading("Cheat sheet"),
            Code(
                "text",
                "Command   What it does              Beginner flags   Example\n---------------------------------------------------------------\npwd       Print cwd                   (none)           pwd\nls        List files                  -l  -a           ls -l\ncd        Change directory            ..  ~            cd ~/c-week\nmkdir     Create directory            -p               mkdir -p src\ntouch     Empty file / touch time     (none)           touch notes.txt\ncp        Copy (keeps original)       -i  -r           cp hello.c hello.bak.c\nmv        Move or rename              -i               mv notes.txt src/\nrm        Delete (no undo)            -i  -r careful  rm notes.bak\ncat       Print file contents         (none)           cat hello.c\necho      Print text                  (none)           echo Hello, C\nclear     Clear screen                (none)           clear\n>         Overwrite write to file                      echo hi > out.txt\n>>        Append to file                             echo more >> out.txt\n|         Pipe to next command                           echo 100 | ./temp",
                "Print and compare. Flags here are enough for this course—not the full manual.",
            ),
            Heading("Walk through c-week (checklist)"),
            Paragraph("Open VS Code integrated terminal and do these in order. Check all before the next lesson."),
            Bullets(
                listOf(
                    "pwd output includes c-week (else cd ~/c-week).",
                    "ls shows folder contents; ls -l shows permission columns.",
                    "mkdir -p drill && cd drill && pwd shows .../c-week/drill.",
                    "touch memo.txt, echo hello > memo.txt, cat memo.txt shows hello.",
                    "echo more >> memo.txt, cat again—two lines.",
                    "cp memo.txt memo.bak, mv memo.bak ../, cd .., ls shows memo.bak.",
                    "rm memo.bak (ls the name first), clear, pwd again—files remain, screen clean.",
                    "If still in drill, rm memo.txt, cd .., rmdir drill (empty dir only—or leave it).",
                ),
            ),
            Callout(
                CalloutKind.KEY,
                "Pass criteria",
                "Without the sheet: pwd/ls/cd answer where you are, what is there, how to move; cp keeps original, mv does not, rm is permanent; > overwrites source so never aim at .c carelessly. Quiz may spot-check.",
            ),
        ),
    ),
)
