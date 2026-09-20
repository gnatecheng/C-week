# C一周通

7 天把编程初学者带到能用 **C 写出 Dijkstra 最短路**（正确使用数组、指针与内存）。  
A 7-day intensive Android course for beginners learning C, ending with Dijkstra on an adjacency list.

品牌名：**C一周通**（英文 C Week）  
教学 IDE 演示是 **真实 VS Code 屏幕录像**（H.264，打包进 APK），用 Media3 播放，清单按时间轴跟随画面。示意图仅作无录像时的后备。

---

## 功能 Features

- **本周计划**：Day 1–7 卡片、勾选、总体百分比；进度用 DataStore 持久化。
- **学习日历 / 打卡**（1.3.0）：一关一天点亮 D1–D7，本周日期条 + 连续打卡天数；完成课文/实验/测验自动盖章。
- **课文播放**：中文讲解（含生活类比）、测验、**VS Code 实机录像**。第 1 天含路径/文件管理与 **逐条 bash**（pwd/ls/cd/… 及速查表）。
- **代码实验**：应用内编辑 C 片段；**模拟运行**对照黄金输出。答错时按类别给出中文错因（缺头文件、差一、指针、公式、TODO 空壳、BFS/Dijkstra 搞混等）。
- **Day 7 大作业**：邻接表 + O(V²) Dijkstra 填空；测试用例；通过后可「走格子/看路径」逐步看 dist 与最短路。
- **词汇表**：pointer、栈/堆、数组退化、launch.json、bash 路径等。
- 跟随系统深色/浅色；大点击区域；等宽代码字体。
- 当前调试包版本：**1.3.0**（versionCode 5）。

---

## 课程地图 Curriculum

| 天 | 主题 | 微课 | 实验 |
| --- | --- | --- | --- |
| 1 | 路径、bash 逐条、环境、Hello World、调试 | 12 | 打印 `Hello, Ada` |
| 2 | 类型、printf/scanf、无 GC | 5 | 摄氏→华氏 |
| 3 | if/for、函数原型、传值 | 5 | `is_prime` |
| 4 | 数组、指针、C 字符串 | 5 | 原地反转字符串 |
| 5 | struct、malloc/free、文件 | 4 | 结构体最高分 |
| 6 | 选择排序、邻接表、BFS | 5 | 补全 BFS |
| 7 | Dijkstra、复杂度、练习题 | 4 | 补全松弛（大作业） |

完整课文在 `app/src/main/java/com/py2c/week/data/curriculum/`。课程地图 JSON：`app/src/main/assets/curriculum/map.json`。可在电脑上对照的 C 源文件：`app/src/main/assets/labs/`。

---

## 在 Android Studio 中打开并运行

### English

1. Install [Android Studio](https://developer.android.com/studio) (Koala / Ladybug or newer, AGP 8.7).
2. **File → Open** this repository root (the folder that contains `settings.gradle.kts`).
3. Wait for Gradle sync. SDK: **compileSdk/targetSdk 35**, **minSdk 26**.
4. Select a device or emulator (API 26+).
5. Click **Run** (green triangle) on the `app` configuration.

Command line:

```bash
# macOS/Linux — install Android SDK, then:
echo "sdk.dir=/path/to/Android/sdk" > local.properties
./gradlew :app:assembleDebug
# APK: app/build/outputs/apk/debug/app-debug.apk
```

`local.properties` is gitignored. Android Studio creates it automatically.

### 中文

1. 安装 Android Studio（建议最新稳定版，需 JDK 17）。
2. **文件 → 打开**，选中本仓库根目录（有 `settings.gradle.kts` 的那一层）。
3. 等待 Gradle 同步。若提示安装 SDK Platform 35 / Build-Tools 35，按提示安装。
4. 连接真机（开启 USB 调试）或启动模拟器，**运行** `app`。
5. 应用名「C一周通」。从第 1 天做到第 7 天；实验页可以模拟评测 Dijkstra。

---

## 架构 Architecture

```
com.py2c.week
  data/          模型、DataStore 进度、课程装配
  data/curriculum/  七天课文、测验、实验、VS Code 脚本、词汇
  ui/theme/      Material 3 跟随系统
  ui/home day lesson quiz lab glossary
  ui/components  代码高亮、内存动画、Media3 实机录像播放器
  ui/navigation  Navigation Compose + 底栏
```

- **离线优先**：无后端。课文与 `assets/vscode_demos/*.mp4` 打进 APK。
- **进度**：Jetpack DataStore Preferences（课时 / 实验 / 测验分数 / 揭晓答案）。
- **ViewModel**：`ProgressViewModel` 把进度 Flow 交给界面。
- **模拟评测**：正则/子串检查 + 常见 C 写法提示，不是手机上的 gcc。
- **VS Code 演示**：主界面是真实 VS Code 录像 + 按时间轴高亮的中文清单；默认 0.75× 语速。Compose 假编辑器只在缺视频时显示。
- **全屏播放**：点播放器控件里的全屏按钮，启动独立的 `FullscreenVideoActivity`（横屏、系统栏隐藏、`PlayerView` 铺满窗口）。系统返回键或右上角「退出全屏」回到课文。

---

## 重新录制 VS Code 演示 / Re-record demos

需要 Linux、Xvfb、ffmpeg、xdotool、wmctrl、openbox、VS Code、gcc、gdb。

```bash
# 1. 虚拟显示器
Xvfb :99 -screen 0 1280x720x24 -ac +extension RANDR &
DISPLAY=:99 openbox &

# 2. 安装 C/C++ 扩展到录像用的 extensions 目录
DISPLAY=:99 code --no-sandbox --disable-gpu \
  --user-data-dir=/tmp/vscode-ext-cache \
  --extensions-dir=/tmp/vscode-record-ext \
  --install-extension ms-vscode.cpptools

# 3. 录制（会覆盖 app/src/main/assets/vscode_demos/*.mp4）
DISPLAY=:99 python3 scripts/record_vscode_demos.py

# 4. 片尾截图在 /tmp/vscode-shots/ ，确认是真 VS Code 再打包
./gradlew :app:assembleDebug
```

清单与文件名对照：`app/src/main/assets/vscode_demos/index.json`。录完后把片长写进字幕 `atMs`。

---

## 构建要求 / Cloud 环境说明

- Gradle **8.11.1**（Wrapper）
- Android Gradle Plugin **8.7.3**
- Kotlin **2.0.21** + Compose Compiler 插件
- JDK **17**（也可用 21 运行 Gradle）

若在没有 Android SDK 的云环境：先安装 Commandline Tools，再 `sdkmanager "platforms;android-35" "build-tools;35.0.0"`，写入 `local.properties` 的 `sdk.dir`。没有模拟器时以 `./gradlew :app:assembleDebug` 是否成功为准。

---

## 电脑上跟做（VS Code）

1. 安装 VS Code + **C/C++** 扩展（Microsoft）。
2. Windows：MinGW/MSYS2 的 gcc；macOS：`xcode-select --install`；Linux：`build-essential`。
3. 打开文件夹 `c-week`，新建 `.c`，终端：

```bash
gcc hello.c -o hello -Wall -Wextra
./hello          # Windows: .\hello.exe
```

Day 7：

```bash
gcc dijkstra.c -o dijkstra -Wall
./dijkstra       # 期望：0 2 1 3
```

参考实现见 `app/src/main/assets/labs/day7_dijkstra.c`。
