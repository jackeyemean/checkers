# Checkers Game

A classic two-player Checkers (Draughts) game written in Java with a Swing GUI.
Supports piece movement rules, kinging, saving/loading games, draw offers, and resignation.

![alt text](image.png)  ![alt text](image-1.png)

## Features

- Standard American Checkers rules
- Piece moves, forced captures, king promotions
- Resignation and draw offers
- Save games in savedGame.txt
- GUI built with Java Swing and `.png` assets

---

## Requirements

- **Java Development Kit (JDK) 8** or newer
- **Git** (to clone the repo)
- Java IDE or a terminal

---

## Getting Started

### 1. Clone the Repository

```bash
git clone https://github.com/your-username/checkers.git
cd checkers
```

### 2. Compile & Run via Command Line

```bash
javac -d bin -sourcepath src src/Checkers.java
java -cp bin Checkers
```

> If images fail to load, verify that the `images/` folder is alongside `bin/` in the same directory.

## Project Structure

```
checkers/
├── images/                         # All images
│   ├── blackPawn.png
│   ├── blackKing.png
│   ├── redPawn.png
│   ├── redKing.png
│   └── board.png
├── src/
│   ├── controller/                 # Handles input & updates model
│   │   └── GameController.java
│   ├── model/                      # Core game logic and state
│   │   └── GameModel.java
│   ├── util/                       # Image loading, save/load
│   │   ├── ImageLoader.java
│   │   └── SaveLoadManager.java
│   ├── view/                       # GUI classes
│   │   ├── BoardPanel.java
│   ├── Checkers.java               # Entry point
├── savedGame.txt                   # Last saved game
├── gameSaved.txt                   # Stores boolean variable
├── README.md
└── .gitignore
```
