<div align="center">

<img src="https://dev.talken.in/assets/img/icons/notes.png" width="96" alt="Notes logo">

# Notes

**An advanced text editor that goes beyond Windows Notepad.**

[Project page](https://dev.talken.in/projects/notes.html) · [Download](https://github.com/Shashwat-Gupta57/Notes/releases/latest) · [All my projects](https://dev.talken.in/#work)

**2,500+** lines of Java · **8** source files

</div>

---

<img src="https://dev.talken.in/assets/img/shots/notes-1.webp" alt="A note, with the status bar tracking line, column and font size" width="100%">
<p align="center"><sub>A note, with the status bar tracking line, column and font size</sub></p>

<img src="https://dev.talken.in/assets/img/shots/notes-2.webp" alt="The Design menu: per-file and universal colours, image backgrounds, fonts" width="100%">
<p align="center"><sub>The Design menu: per-file and universal colours, image backgrounds, fonts</sub></p>

<img src="https://dev.talken.in/assets/img/shots/notes-3.webp" alt="A file with its own background colour" width="100%">
<p align="center"><sub>A file with its own background colour</sub></p>

## About

Notepad hasn’t really changed in thirty years. Notes started as my Notepad clone and kept going: themes per file and for everything at once, image backgrounds, fonts, automatic backups — and its own encryption, so a text file can be locked.

It’s the direct ancestor of Notes+: the same instinct, years earlier, in Swing.

## What it does

- **Per-file design** — Give any single file its own background and text colour.
- **Universal themes** — Or change the background and text colour of every file at once.
- **Image backgrounds** — Set a picture as the background behind your text.
- **Fonts** — Change the font, its size and style.
- **Backups** — Automatic backups into a folder you choose.
- **Encryption** — Encrypt a file, decrypt it back — built into the editor’s Privacy menu.

## Download & run

1. These apps need **Java 21 or newer**. If you don't have it, install it once with the [JRE Installer](https://github.com/Shashwat-Gupta57/JRE-Installer/releases/latest) — one click, no admin rights. Every app looks for Java in the place it installs to first, then in `JAVA_HOME`.
2. Download **`Notes-1.0.0-windows.zip`** from the [latest release](https://github.com/Shashwat-Gupta57/Notes/releases/latest).
3. Unzip it anywhere you can write to (Desktop, Documents…) and run **`Notes.exe`**.

The folders next to the exe (images, fonts, saves) are part of the app — keep them together.

## Run from source

Open the folder in **BlueJ** (it's a BlueJ project — `package.bluej`) and run `Notes.main()`, or with any JDK 21+ (add the JNA + swing-jnafilechooser jars to the classpath):

```bash
javac *.java
java Notes
```

## Under the hood

| | |
|---|---|
| UI | Java Swing |
| Crypto | Its own Encrypter / Decrypter classes |
| Dialogs | Native file chooser via JNA |

## License

[GPL-3.0](LICENSE) © Shashwat Gupta · Flex Studios

Made by [Shashwat Gupta](https://dev.talken.in/) — see more at **[dev.talken.in](https://dev.talken.in/)**.
