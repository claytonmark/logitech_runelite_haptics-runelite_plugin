# Logitech Haptics - RuneLite Plugin

A [RuneLite](https://runelite.net/) plugin that sends haptic feedback events to the [Logitech MX Master 4](https://www.logitech.com/en-us/products/mice/mx-master-4.html) mouse during Old School RuneScape gameplay. Feel your clicks, menu navigation, and hover targets through the mouse itself.

Requires the companion [Logi Options+ action](https://github.com/claytonmark/logitech_runelite_haptics-logi_options_action) to be installed.

## Features

- **Click haptics** for NPCs, objects, ground items, inventory items, and movement
- **Hover haptics** when the cursor moves over interactive NPCs and game objects
- **Menu haptics** with detent clicks when hovering between right-click menu entries, plus feedback on menu open and selection
- **Per-category waveform selection** via the RuneLite config panel (Off, Subtle Collision, Damp Collision, Sharp Collision, etc.)
- Automatically suppressed during camera rotation (middle mouse) and while the bank is open

## Requirements

- [RuneLite](https://runelite.net/) client
- Java 11+
- Companion Logi Options+ plugin running on the same machine

## Installation

### From GitHub Releases (Recommended)

1. Download `runelite-haptics.jar` from the [latest release](https://github.com/claytonmark/logitech_runelite_haptics-runelite_plugin/releases)
2. Place the JAR in RuneLite's sideloaded plugins directory:
   - **Windows**: `%USERPROFILE%\.runelite\sideloaded-plugins\`
   - **macOS/Linux**: `~/.runelite/sideloaded-plugins/`
3. Launch RuneLite with `--developer-mode` (required for sideloaded plugins)
4. The plugin will appear in the RuneLite settings panel as **Logitech Haptics**

### From Source

1. Build the plugin JAR:
   ```bash
   ./gradlew jar
   ```
2. Copy the JAR to RuneLite's sideloaded plugins directory:
   ```bash
   cp build/libs/runelite-haptics.jar ~/.runelite/sideloaded-plugins/
   ```
3. Launch RuneLite with `--developer-mode`

## Configuration

Open the RuneLite settings panel and search for **Logitech Haptics**. Three config sections are available:

| Section | Options |
|---|---|
| **Click Haptics** | NPC Click, Object Click, Ground Item Click, Inventory Click, Move Click |
| **Hover Haptics** | NPC Hover, Object Hover |
| **Menu Haptics** | Menu Hover Detents, Menu Open, Menu Select |

Each option is a dropdown with waveform choices: Off, Default, Subtle Collision, Damp Collision, Damp State Change, Sharp Collision, Sharp State Change, Knock.

## How It Works

The plugin observes RuneLite's event bus (read-only, no game modification) and sends JSON events over HTTP to the Logi Options+ plugin's localhost listener on port 8484:

```
Mouse action -> RuneLite event -> HTTP POST -> Logi plugin -> MX Master 4 haptic motor
```

## Building

```bash
./gradlew build       # Compile and run tests
./gradlew jar         # Build plugin JAR
```

## License

This project is licensed under the GNU General Public License v3.0. See [LICENSE.md](LICENSE.md) for details.
