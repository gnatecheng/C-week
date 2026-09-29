package com.py2c.week.data

private data class VideoL10nEntry(
    val title: String,
    val subtitle: String,
    val captions: List<VideoCaption>,
)

private val VIDEO_L10N_EN: Map<String, VideoL10nEntry> = mapOf(
    "open_folder" to VideoL10nEntry(
        title = "Open the course folder",
        subtitle = "Real VS Code: workspace, not a single file",
        captions = listOf(
            VideoCaption(0, "Open VS Code (don't treat one .c file as a notepad)"),
            VideoCaption(4000, "Command palette: File: Open Folder…"),
            VideoCaption(9000, "Pick the c-week folder—that's the workspace root"),
            VideoCaption(14000, "Confirm you opened a folder, not a single file"),
            VideoCaption(18000, "Explorer shows the tree; terminal cwd follows it"),
        ),
    ),
    "create_hello" to VideoL10nEntry(
        title = "Create and save hello.c",
        subtitle = "Write your first source in the real editor",
        captions = listOf(
            VideoCaption(0, "File: New File for a blank editor"),
            VideoCaption(5000, "Start with #include <stdio.h>"),
            VideoCaption(11000, "Add int main(void) and printf(\"Hello, C\\n\")"),
            VideoCaption(18000, "return 0; for a normal exit code"),
            VideoCaption(23000, "Ctrl+S save as hello.c (.c extension, not .c.txt)"),
            VideoCaption(28000, "Explorer should show hello.c at the workspace root"),
        ),
    ),
    "install_cpptools" to VideoL10nEntry(
        title = "Install the C/C++ extension",
        subtitle = "Microsoft C/C++ (ms-vscode.cpptools)",
        captions = listOf(
            VideoCaption(0, "Ctrl+Shift+X opens Extensions"),
            VideoCaption(5000, "Search C/C++, publisher Microsoft"),
            VideoCaption(12000, "Install—highlighting, IntelliSense, debug adapter"),
            VideoCaption(20000, "Extension ≠ compiler: you still need gcc/gdb locally"),
            VideoCaption(27000, "After install, stick to hand-typed gcc this week"),
        ),
    ),
    "launch_json" to VideoL10nEntry(
        title = "launch.json and tasks.json",
        subtitle = "Wire F5 debug and gcc tasks into the workspace",
        captions = listOf(
            VideoCaption(0, "Ctrl+P open .vscode/launch.json"),
            VideoCaption(4000, "type=cppdbg, request=launch: start with gdb/lldb"),
            VideoCaption(9000, "program points at the binary; cwd at workspace root"),
            VideoCaption(14000, "preLaunchTask runs gcc before F5"),
            VideoCaption(18000, "tasks.json: gcc -g hello.c -o hello; label must match preLaunchTask"),
        ),
    ),
    "compile_run" to VideoL10nEntry(
        title = "gcc compile and run in the terminal",
        subtitle = "Integrated terminal cwd is the workspace",
        captions = listOf(
            VideoCaption(0, "Ctrl+` terminal; pwd should show c-week"),
            VideoCaption(4000, "gcc hello.c -o hello -Wall -g"),
            VideoCaption(9000, "-Wall for warnings, -g for debug symbols used by F5"),
            VideoCaption(13000, "./hello prints Hello, C, exit code 0"),
            VideoCaption(16500, "Permission denied? chmod +x hello or rebuild with gcc"),
        ),
    ),
    "debug_bp" to VideoL10nEntry(
        title = "Breakpoints, F5, step, variables",
        subtitle = "Debug the loop instead of only printf",
        captions = listOf(
            VideoCaption(0, "Put cursor in the loop body; F9 sets a breakpoint (red dot)"),
            VideoCaption(4500, "F9 again clears; click the gutter works too"),
            VideoCaption(8000, "Run and Debug or F5 (preLaunchTask compiles first)"),
            VideoCaption(13000, "When hit, check VARIABLES: i and accumulators"),
            VideoCaption(17500, "F10 Step Over; F11 Step Into"),
            VideoCaption(22000, "CALL STACK shows callers; Shift+F5 stops debugging"),
        ),
    ),
    "new_source_dijkstra" to VideoL10nEntry(
        title = "Capstone file dijkstra.c",
        subtitle = "Same flow: open source → gcc → run",
        captions = listOf(
            VideoCaption(0, "Ctrl+P open dijkstra.c (same workspace)"),
            VideoCaption(5000, "Terminal cwd still workspace root—check with pwd"),
            VideoCaption(10000, "gcc dijkstra.c -o dijkstra -Wall"),
            VideoCaption(15000, "./dijkstra against lab test cases"),
            VideoCaption(18500, "Fix warnings first. Weighted shortest path isn't BFS +1"),
        ),
    ),
)

fun VideoDemo.withEnglishCaptions(): VideoDemo {
    val en = VIDEO_L10N_EN[id] ?: return this
    return copy(title = en.title, subtitle = en.subtitle, captions = en.captions)
}
