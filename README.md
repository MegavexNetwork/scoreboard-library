# scoreboard-library

Powerful Scoreboard library for Paper/Spigot servers using the [adventure](https://github.com/KyoriPowered/adventure)
component library

Join the [Discord](https://discord.gg/v7nmTDTW8W) or create an issue for support

## Features

- Sidebars
- Teams
- Packet-level, meaning it works with other scoreboard plugins
- Fully async. All packet work is done asynchronously so you can (but don't have to) use the library from the main
  thread without sacrificing any performance
- Works with `TranslatableComponent`s, meaning all components are automatically translated using `GlobalTranslator` for
  each players client locale (and automatically update whenever the player changes it)

## Packet Adapters

- **1.19.3.** [Spigot](https://www.spigotmc.org/) does work, but [Paper](https://papermc.io/) is recommended because
  scoreboard-library can take advantage of the native [Adventure](https://github.com/KyoriPowered/adventure) feature to
  improve performance
- **1.8.8.** Note that you'll still need to use Java 17
- **PacketEvents.** Requires [PacketEvents 2.0](https://github.com/retrooper/packetevents/tree/2.0) to be loaded in the
  classpath. Should work on all versions 1.8+

## Installation

See installation instructions [here](https://github.com/MegavexNetwork/scoreboard-library/blob/master/INSTALLATION.md)

### Getting started

```java
ScoreboardLibrary scoreboardLibrary;
try {
  scoreboardLibrary = ScoreboardLibrary.loadScoreboardLibrary(plugin);
} catch (NoPacketAdapterAvailableException e) {
  // If no packet adapter was found, you can fallback to the no-op implementation
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
sidebar.line(3, Component.text("yourserver.net"));

sidebar.addPlayer(player); // Add the player to the sidebar
```

### Sidebar (Kotlin)

```kotlin
val sidebar = scoreboardLibrary.createSidebar()

val updateTimerLine: DynamicLine
var timer = 0

sidebar.title(Component.text("Timer Example", NamedTextColor.AQUA))
sidebar.lines {
  emptyLine()
  updateTimerLine = dynamicLine { Component.text("Timer: $timer") }
  emptyLine()
  line(Component.text("yourserver.net", NamedTextColor.AQUA))
}

plugin.server.scheduler.runTaskTimerAsynchronously(
  plugin,
  Runnable {
    timer++
    updateTimerLine()
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

    sidebar.title(Component.text("Timer Example", NamedTextColor.AQUA));

    registerEmptyLine(0);
    timerLine = registerDynamicLine(1, () -> Component.text("Timer: " + timer));
    registerEmptyLine(2);
    registerStaticLine(3, Component.text("yourserver.net", NamedTextColor.AQUA));

    task = new BukkitRunnable() {
      @Override
      public void run() {
        timer++;
        timerLine.update();
      }
    }.runTaskTimerAsynchronously(plugin, 20, 20);

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

// A TeamInfo holds all the properties that a team can have (except the name).
// The global TeamInfo is the default one that will be applied to players,
// however you can give each player a different TeamInfo
TeamInfo teamInfo = team.globalInfo();

teamInfo.displayName(Component.text("Team Name"));
teamInfo.prefix(Component.text("[Prefix] "));
teamInfo.suffix(Component.text(" [Suffix]"));
teamInfo.playerColor(NamedTextColor.RED);

teamManager.addPlayer(player); // Player will be added to the global TeamInfo

// You can change the TeamInfo like this:
TeamInfo newTeamInfo = team.createTeamInfo();
team.teamInfo(player, newTeamInfo);
```

For more examples, check out the [example plugin](https://github.com/MegavexNetwork/scoreboard-library-example)
