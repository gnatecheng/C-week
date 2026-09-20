#!/usr/bin/env python3
"""Record real VS Code UI on Xvfb :99. Dismisses Copilot sign-in. Produces H.264 MP4s."""
from __future__ import annotations

import os
import signal
import subprocess
import time
from pathlib import Path

DISPLAY = ":99"
W, H = 1280, 720
USER = Path("/tmp/vscode-record-user")
EXT = Path("/tmp/vscode-record-ext")
WORK = Path("/home/ubuntu/c-week")
RAW = Path("/tmp/vscode-raw")
FINAL = Path("/workspace/app/src/main/assets/vscode_demos")
SHOTS = Path("/tmp/vscode-shots")

HELLO = "#include <stdio.h>\n\nint main(void) {\n    printf(\"Hello, C\\n\");\n    return 0;\n}\n"
LOOP = (
    "#include <stdio.h>\n\n"
    "int main(void) {\n"
    "    int i;\n"
    "    for (i = 0; i < 3; i++) {\n"
    "        printf(\"%d\\n\", i);\n"
    "    }\n"
    "    return 0;\n"
    "}\n"
)
LAUNCH = """{
    "version": "0.2.0",
    "configurations": [
        {
            "name": "gcc - hello",
            "type": "cppdbg",
            "request": "launch",
            "program": "${workspaceFolder}/hello",
            "args": [],
            "stopAtEntry": false,
            "cwd": "${workspaceFolder}",
            "MIMode": "gdb",
            "miDebuggerPath": "/usr/bin/gdb"
        }
    ]
}
"""
TASKS = """{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "C: gcc build hello",
            "type": "shell",
            "command": "gcc -g hello.c -o hello -Wall",
            "group": { "kind": "build", "isDefault": true }
        }
    ]
}
"""
DIJKSTRA = "#include <stdio.h>\n\nint main(void) {\n    printf(\"dist[2] = 5\\n\");\n    return 0;\n}\n"

SETTINGS = """{
  "workbench.startupEditor": "none",
  "workbench.colorTheme": "Default Dark Modern",
  "workbench.welcomePage.walkthroughs.openOnInstall": false,
  "workbench.secondarySideBar.defaultVisibility": "hidden",
  "chat.disableAIFeatures": true,
  "chat.commandCenter.enabled": false,
  "window.restoreWindows": "none",
  "window.zoomLevel": 1,
  "telemetry.telemetryLevel": "off",
  "update.mode": "none",
  "extensions.autoUpdate": false,
  "extensions.autoCheckUpdates": false,
  "security.workspace.trust.enabled": false,
  "git.enabled": false,
  "editor.minimap.enabled": false,
  "editor.fontSize": 18,
  "terminal.integrated.fontSize": 15,
  "explorer.confirmDelete": false,
  "debug.toolBarLocation": "docked",
  "debug.openDebug": "openOnDebugBreak",
  "editor.empty.hint": "hidden"
}
"""


def env() -> dict:
    e = os.environ.copy()
    e["DISPLAY"] = DISPLAY
    e["LIBGL_ALWAYS_SOFTWARE"] = "1"
    return e


def run(cmd, check=True, **kw):
    return subprocess.run(cmd, env=env(), check=check, **kw)


def code_cmd(*extra) -> list[str]:
    return [
        "code",
        "--no-sandbox",
        "--disable-gpu",
        "--disable-updates",
        "--disable-workspace-trust",
        "--user-data-dir",
        str(USER),
        "--extensions-dir",
        str(EXT),
        *extra,
    ]


def write_user():
    (USER / "User").mkdir(parents=True, exist_ok=True)
    EXT.mkdir(parents=True, exist_ok=True)
    (USER / "User" / "settings.json").write_text(SETTINGS, encoding="utf-8")
    (USER / "argv.json").write_text(
        '{\n  "disable-hardware-acceleration": true,\n  "enable-crash-reporter": false\n}\n',
        encoding="utf-8",
    )


def wait_window(timeout=45) -> str:
    deadline = time.time() + timeout
    while time.time() < deadline:
        r = run(
            ["xdotool", "search", "--onlyvisible", "--class", "code"],
            check=False,
            capture_output=True,
            text=True,
        )
        ids = [x for x in (r.stdout or "").split() if x]
        if ids:
            wid = ids[-1]
            run(["xdotool", "windowactivate", "--sync", wid], check=False)
            run(["wmctrl", "-i", "-r", wid, "-b", "add,maximized_vert,maximized_horz"], check=False)
            time.sleep(0.3)
            return wid
        time.sleep(0.4)
    raise RuntimeError("VS Code window not found")


def shot(name: str):
    SHOTS.mkdir(parents=True, exist_ok=True)
    run(["scrot", str(SHOTS / f"{name}.png")], check=False)


def dismiss():
    """Close Copilot/welcome modals so the real workbench is visible."""
    wait_window()
    time.sleep(0.4)
    for _ in range(3):
        run(["xdotool", "key", "Escape"], check=False)
        time.sleep(0.25)
    # "Continue without Signing In" (bottom-right of modal) and dialog X
    for x, y in ((1080, 620), (1020, 605), (1148, 148), (640, 400)):
        run(["xdotool", "mousemove", "--sync", str(x), str(y), "click", "1"], check=False)
        time.sleep(0.2)
    run(["xdotool", "key", "Escape"], check=False)
    time.sleep(0.3)
    # Hide secondary chat sidebar if still open
    run(["xdotool", "key", "ctrl+alt+b"], check=False)
    time.sleep(0.3)
    run(["xdotool", "key", "Escape"], check=False)
    time.sleep(0.2)


def focus():
    wait_window()
    time.sleep(0.15)


def key(*keys: str, delay: int = 80):
    focus()
    run(["xdotool", "key", "--delay", str(delay), *keys])
    time.sleep(0.2)


def type_text(text: str, delay: int = 24):
    focus()
    run(["xdotool", "type", "--delay", str(delay), "--", text])
    time.sleep(0.15)


def palette(command: str):
    key("ctrl+shift+p")
    time.sleep(0.7)
    type_text(command, delay=22)
    time.sleep(0.5)
    key("Return")
    time.sleep(0.8)


def kill_code():
    run(["killall", "code"], check=False)
    time.sleep(1.0)
    run(["killall", "-9", "code"], check=False)
    time.sleep(0.4)


def start_code(*extra):
    kill_code()
    subprocess.Popen(
        code_cmd(*extra),
        env=env(),
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
        start_new_session=True,
    )
    wait_window()
    time.sleep(1.5)
    dismiss()
    time.sleep(0.6)


def start_ffmpeg(path: Path) -> subprocess.Popen:
    path.parent.mkdir(parents=True, exist_ok=True)
    log = open(path.with_suffix(".ffmpeg.log"), "w")
    p = subprocess.Popen(
        [
            "ffmpeg",
            "-y",
            "-f",
            "x11grab",
            "-draw_mouse",
            "1",
            "-video_size",
            f"{W}x{H}",
            "-framerate",
            "12",
            "-i",
            f"{DISPLAY}.0",
            "-c:v",
            "libx264",
            "-pix_fmt",
            "yuv420p",
            "-preset",
            "ultrafast",
            "-crf",
            "28",
            str(path),
        ],
        env=env(),
        stdout=log,
        stderr=subprocess.STDOUT,
    )
    time.sleep(0.7)
    return p


def stop_ffmpeg(p: subprocess.Popen):
    time.sleep(0.5)
    p.send_signal(signal.SIGINT)
    try:
        p.wait(timeout=20)
    except subprocess.TimeoutExpired:
        p.kill()


def transcode(src: Path, dest: Path):
    dest.parent.mkdir(parents=True, exist_ok=True)
    run(
        [
            "ffmpeg",
            "-y",
            "-i",
            str(src),
            "-vf",
            "fps=12,scale=1280:720:force_original_aspect_ratio=decrease,pad=1280:720:(ow-iw)/2:(oh-ih)/2",
            "-c:v",
            "libx264",
            "-profile:v",
            "main",
            "-level",
            "4.0",
            "-pix_fmt",
            "yuv420p",
            "-preset",
            "medium",
            "-crf",
            "30",
            "-an",
            "-movflags",
            "+faststart",
            str(dest),
        ],
        capture_output=True,
    )


def record(name: str, action) -> Path:
    raw = RAW / f"{name}.mp4"
    print(f"==> {name}", flush=True)
    rec = start_ffmpeg(raw)
    try:
        action()
        shot(f"{name}_end")
    finally:
        stop_ffmpeg(rec)
    out = FINAL / f"{name}.mp4"
    transcode(raw, out)
    print(f"    {out.stat().st_size / 1024:.0f} KB", flush=True)
    return out


def demo_open_folder():
    start_code()
    time.sleep(1.0)
    palette("File: Open Folder...")
    time.sleep(1.2)
    type_text(str(WORK), delay=18)
    time.sleep(0.5)
    key("Return")
    time.sleep(2.0)
    key("ctrl+shift+e")
    time.sleep(2.0)


def demo_create_hello():
    (WORK / "hello.c").unlink(missing_ok=True)
    start_code(str(WORK))
    time.sleep(0.8)
    key("ctrl+shift+e")
    time.sleep(0.6)
    palette("File: New File")
    time.sleep(0.6)
    for line in HELLO.splitlines():
        type_text(line, delay=16)
        key("Return")
    time.sleep(0.5)
    key("ctrl+s")
    time.sleep(1.0)
    type_text(str(WORK / "hello.c"), delay=16)
    time.sleep(0.4)
    key("Return")
    time.sleep(2.0)


def demo_install_ext():
    start_code(str(WORK))
    time.sleep(0.8)
    key("ctrl+shift+x")
    time.sleep(1.4)
    type_text("C/C++", delay=70)
    time.sleep(2.2)
    subprocess.Popen(
        code_cmd("--install-extension", "ms-vscode.cpptools"),
        env=env(),
        stdout=subprocess.DEVNULL,
        stderr=subprocess.DEVNULL,
    )
    time.sleep(16.0)
    key("ctrl+shift+x")
    time.sleep(2.0)


def demo_launch_json():
    vs = WORK / ".vscode"
    vs.mkdir(exist_ok=True)
    (vs / "launch.json").write_text(LAUNCH, encoding="utf-8")
    (vs / "tasks.json").write_text(TASKS, encoding="utf-8")
    start_code(str(WORK))
    time.sleep(0.8)
    key("ctrl+shift+e")
    time.sleep(0.5)
    key("ctrl+p")
    time.sleep(0.6)
    type_text("launch.json", delay=30)
    time.sleep(0.4)
    key("Return")
    time.sleep(2.2)
    key("ctrl+p")
    time.sleep(0.5)
    type_text("tasks.json", delay=30)
    time.sleep(0.3)
    key("Return")
    time.sleep(2.4)


def demo_compile_run():
    (WORK / "hello.c").write_text(HELLO, encoding="utf-8")
    start_code(str(WORK), "-g", str(WORK / "hello.c"))
    time.sleep(1.0)
    key("ctrl+shift+e")
    time.sleep(0.4)
    key("ctrl+grave")
    time.sleep(1.4)
    type_text("gcc hello.c -o hello -Wall -g && ./hello", delay=20)
    key("Return")
    time.sleep(3.5)


def demo_debug():
    (WORK / "hello.c").write_text(LOOP, encoding="utf-8")
    run(["gcc", "-g", str(WORK / "hello.c"), "-o", str(WORK / "hello")], check=False)
    start_code(str(WORK), "-g", f"{WORK}/hello.c:6")
    time.sleep(1.4)
    key("F9")
    time.sleep(1.0)
    key("ctrl+shift+d")
    time.sleep(1.4)
    key("F5")
    time.sleep(6.0)
    key("F10")
    time.sleep(1.5)
    key("F10")
    time.sleep(1.8)


def demo_new_source():
    (WORK / "dijkstra.c").write_text(DIJKSTRA, encoding="utf-8")
    start_code(str(WORK))
    time.sleep(0.8)
    key("ctrl+p")
    time.sleep(0.5)
    type_text("dijkstra.c", delay=30)
    key("Return")
    time.sleep(1.4)
    key("ctrl+grave")
    time.sleep(1.2)
    type_text("gcc dijkstra.c -o dijkstra -Wall && ./dijkstra", delay=16)
    key("Return")
    time.sleep(3.0)


def main():
    run(["xdpyinfo", "-display", DISPLAY], capture_output=True)
    RAW.mkdir(parents=True, exist_ok=True)
    FINAL.mkdir(parents=True, exist_ok=True)
    WORK.mkdir(parents=True, exist_ok=True)
    write_user()

    record("01_open_folder", demo_open_folder)
    record("02_create_hello", demo_create_hello)
    record("03_install_cpptools", demo_install_ext)
    run(code_cmd("--install-extension", "ms-vscode.cpptools"), check=False)
    record("04_launch_json", demo_launch_json)
    record("05_compile_run", demo_compile_run)
    record("06_debug", demo_debug)
    record("07_new_source", demo_new_source)
    kill_code()
    print("done", flush=True)


if __name__ == "__main__":
    main()
