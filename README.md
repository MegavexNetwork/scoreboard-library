# scoreboard-library

Powerful scoreboard library for Minecraft Paper/Spigot servers using the [adventure](https://github.com/KyoriPowered/adventure)
component library

Join the [Discord](https://discord.gg/v7nmTDTW8W) or create an issue for support

## Features

- Sidebars. Up to 42 characters (depends on the formatting) for 1.12.2 and below, no limit for newer versions
- Teams. Supports showing different properties (display name, prefix, entries etc.) of the same team to different players
- Doesn't require extra dependencies (assuming you're targetting the latest version of Paper)
- Packet-level, meaning it works with other scoreboard plugins (and is faster)
- Fully async. All packet work is done asynchronously so you can (but don't have to) use the library from the main
  thread without sacrificing any performance
- Works with `TranslatableComponent`s, meaning all components are automatically translated using `GlobalTranslator` for
  each players client locale (and automatically update whenever the player changes it in their settings)

## Packet Adapters

- **1.19.4.** Takes advantage of [Paper](https://papermc.io)s native adventure support to improve performance.
  [Spigot](https://www.spigotmc.org/) is also supported, but will have worse performance
- **1.8.8.** Note that you'll still need to use Java 17
- **PacketEvents.** Requires [PacketEvents 2.0](https://github.com/retrooper/packetevents/tree/2.0) to be loaded in the
  classpath. Should work on all versions 1.8+

## Installation

See installation instructions [here](https://github.com/MegavexNetwork/scoreboard-library/blob/dev/2.0/INSTALLATION.md)

### Getting started

```java
ScoreboardLibrary scoreboardLibrary;
try {
  scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
} catch (NoPacketAdapterAvailableException e) {
  // If no packet adapter was found, you can fallback to the no-op implementation:
  scoreboardLibrary = new NoopScoreboardLibrary();
}

// On plugin shutdown:
scoreboardLibrary.close();
```

### Sidebar


```java
Sidebar sidebar = scoreboardLibrary.createSidebar();

sidebar.title(Component.text("Sidebar Title"));
sidebar.line(0, Component.empty());
sidebar.line(1, Component.text("Line 1"));
sidebar.line(2, Component.text("Line 2"));
sidebar.line(2, Component.empty());
sidebar.line(3, Component.text("epicserver.net"));

sidebar.addPlayer(player); // Add the player to the sidebar

// After you've finished using the Sidebar, make sure to close it to prevent a memory leak:
sidebar.close();
```

### Sidebar (Kotlin)

```kotlin
val sidebar = scoreboardLibrary.createSidebar(4)

val timerLine: LinesBuilder.DynamicLine
var timer = 0

sidebar.title(Component.text("Timer Example", NamedTextColor.AQUA))
sidebar.lines {
  emptyLine()
  timerLine = dynamicLine { Component.text("Timer: $timer") }
  emptyLine()
  line(Component.text("epicserver.net", NamedTextColor.AQUA))
}

plugin.server.scheduler.runTaskTimerAsynchronously(
  plugin,
  Runnable {
    timer++
    timerLine.update()
  },
  20,
  20
)

sidebar.addPlayer(player)
```

### AbstractSidebar

```java
public class TimerSidebar extends AbstractSidebar {
  private final DynamicLine timerLine;
  private int timer;

  private final BukkitTask task;

  public TimerSidebar(@NotNull Plugin plugin, @NotNull ScoreboardLibrary scoreboardLibrary, @NotNull Player player) {
    super(scoreboardLibrary.createSidebar(4));

    sidebar.title(text("Timer Example", NamedTextColor.AQUA));

    registerEmptyLine(0);
    timerLine = registerDynamicLine(1, () -> text("Timer: " + timer));
    registerEmptyLine(2);
    registerStaticLine(3, text("epicserver.net", NamedTextColor.AQUA));

    task = plugin.getServer().getScheduler().runTaskTimerAsynchronously(plugin, () -> {
      timer++;
      timerLine.update();
    }, 20, 20);

    sidebar.addPlayer(player);
  }

  @Override
  protected void onClosed() {
    task.cancel();
  }
}
```

### TeamManager

```java
TeamManager teamManager = scoreboardLibrary.createTeamManager();
ScoreboardTeam team = teamManager.createIfAbsent("team_name");

// A TeamDisplay holds all the display properties that a team can have (prefix, suffix etc.).
// You can apply different TeamDisplays for each player so different players can see
// different properties on a single ScoreboardTeam. However if you don't need that you can
// use the default TeamDisplay that is created in every ScoreboardTeam.
TeamDisplay teamDisplay = team.defaultDisplay();
teamDisplay.displayName(Component.text("Team Display Name"));
teamDisplay.prefix(Component.text("[Prefix] "));
teamDisplay.suffix(Component.text(" [Suffix]"));
teamDisplay.playerColor(NamedTextColor.RED);

teamManager.addPlayer(player); // Player will be added to the default TeamDisplay of each ScoreboardTeam

// Create a new TeamDisplay like this:
TeamDisplay newTeamDisplay = team.createDisplay();
newTeamDisplay.displayName(Component.text("Other Team Display Name"));

// Change the TeamDisplay a player sees like this:
team.display(player, newTeamDisplay);

// After you've finished using the TeamManager, make sure to close it to prevent a memory leak:
teamManager.close();
```

For more examples, check out the [example plugin](https://github.com/MegavexNetwork/scoreboard-library-example)
