# 💣 NUKE — Task Manager

> *A task manager that threatens you into being productive.*

![Java](https://img.shields.io/badge/Java-17-orange?style=flat-square)
![JavaFX](https://img.shields.io/badge/JavaFX-17.0.7-blue?style=flat-square)
![Build](https://img.shields.io/badge/build-passing-brightgreen?style=flat-square)
![Vibe](https://img.shields.io/badge/vibe-chaotic-purple?style=flat-square)

NUKE is a JavaFX chatbot task manager with a glitchy UI, a robot voice, and absolutely zero chill. Built as a school assignment. Powered by spite.

---

## 📸 Features

### ✅ Task Management
Add, delete, mark, unmark, and find tasks — all through a chat interface because typing into a box is more fun than clicking buttons.

Supports five task types:

| Type | Format | Example |
|---|---|---|
| `ToDo` | `add ToDo <description>` | `add ToDo touch grass` |
| `Deadline` | `add Deadline <date> <description>` | `add Deadline 2026-12-31 finish assignment` |
| `DoAfter` | `add DoAfter <date> <description>` | `add DoAfter 2026-05-05 complete final fantasy` |
| `DoWithinPeriod` | `add DoWithinPeriod <start> <end> <description>` | `add DoWithinPeriod 2026-04-01 2026-04-06 hackathon project` |
| `Event` | `add Event <start> <end> <description>` | `add Event 2026-01-01 2026-01-02 new years` |

### 💾 Save / Load Persistence
Your tasks are automatically saved to disk and reloaded on startup. NUKE remembers everything. Even the embarrassing tasks.

Fully customisable keywords. Go to C:\Users\User\Desktop\ip\data\commands.txt and find the command, then copy the file path and edit it to your hearts content.

### 🖥️ Glitchy JavaFX GUI
The UI randomly:
- Shifts the background image around the screen
- Flickers the green tint
- Adjusts contrast and saturation

This is a feature, not a bug. Enjoy a free lobotomy while you are at it.

### 🤖 Robot Voice TTS
NUKE reads all responses aloud using your OS's built-in text-to-speech engine. Because reading is for humans.

---

## 🚀 Getting Started

### Prerequisites
- Java 17+
- Gradle

### Running

```bash
./gradlew run
```

### Building the jar

```bash
./gradlew jar
java -jar NUCLEAR.jar
```

---

## 💬 Commands

| Command | Description |
|---|---|
| `add <type> <args>` | Add a new task |
| `list` | List all tasks |
| `mark <index>` | Mark a task as done |
| `unmark <index>` | Unmark a task |
| `delete <index>` | Delete a task |
| `find <keyword>` | Search tasks by keyword |
| `save` | Save tasks to disk |
| `bye` | Exit (NUKE will miss you. Maybe.) |

---

## 🏗️ Project Structure

```
src/
├── main/java/
│   ├── process/        # Business logic
│   │   ├── Action.java         — command implementations
│   │   ├── Process.java        — reflection-based command routing
│   │   ├── Task.java           — base task class
│   │   ├── ToDoTask.java       — todo task type
│   │   ├── DeadlinesTask.java  — deadline task type
│   │   ├── EventsTask.java     — event task type
│   │   └── TaskList.java       — task list with persistence
│   └── ui/             # JavaFX GUI
│       ├── MainWindow.java     — main controller
│       ├── DialogBox.java      — chat bubble component
│       ├── Nuke.java           — chatbot logic
│       ├── VoiceEngine.java    — OS text-to-speech
│       └── Launcher.java       — JavaFX entry point
│   
└── test/java/          # JUnit 5 tests
```

---

## 🧪 Running Tests

```bash
./gradlew test
```

Tests cover command processing, task creation, and save/load behaviour. They do not cover the glitch effects because chaos cannot be unit tested.

---

## ⚙️ Configuration

NUKE reads from `config/config.txt` on startup to locate the commands and save files. If you move things around and it breaks, that's on you.

---

## 🪦 Known Issues

- The glitch effect is permanent and cannot be turned off
- The robot voice will read your most embarrassing tasks aloud
- `Microsoft Sam` is not available on all Windows installs and NUKE is upset about it

---

## 📜 Acknowledgements

- [JavaFX](https://openjfx.io/) — for the GUI
- [JUnit 5](https://junit.org/junit5/) — for the tests
- [system-lambda](https://github.com/stefanbirkner/system-lambda) — for capturing stdout in tests
- Whoever invented deadlines — you know what you did
