# scoreboard-library

Powerful Scoreboard library for Paper/Spigot servers using the [adventure](https://github.com/KyoriPowered/adventure)
component library

Join the [Discord](https://discord.gg/v7nmTDTW8W) or create an issue for support

## Features

- Sidebars
- Teams
- Packet-level, meaning it works with other scoreboard plugins
- Fully async. All packet work is done asynchronously so you can (but don't have to!) use the library from the main
  thread without sacrificing any performance
- Works with `TranslatableComponent`s, meaning all components are automatically translated using `GlobalTranslator` for
  each players client locale (and automatically update whenever the player changes it)

## Packet Adapters

- **1.19.\*.** [Spigot](https://www.spigotmc.org/) does work, but [Paper](https://papermc.io/) is recommended because
  scoreboard-library can take advantage of the native [Adventure](https://github.com/KyoriPowered/adventure) feature to
  improve performance
- **1.8.8.** Note that you'll still need to use Java 17
- **PacketEvents.** Requires [PacketEvents 2.0](https://github.com/retrooper/packetevents/tree/2.0) to be loaded in the
  classpath. Should work on all versions 1.8+

## Installation

See installation instructions [here](https://github.com/MegavexNetwork/scoreboard-library/blob/master/INSTALLATION.md)

### Getting started

```java
try {
  ScoreboardLibraryImplementation.init();
} catch (ScoreboardLibraryLoadException e) {
  // Couldn't load the library.
  // Probably because the servers version is unsupported.
  e.printStackTrace();
  return;
}

ScoreboardManager scoreboardManager = ScoreboardManager.scoreboardManager(plugin);

// On plugin shutdown:
scoreboardManager.close();
ScoreboardLibraryImplementation.close();
```

### Sidebar


```java
Sidebar sidebar = scoreboardManager.createSidebar();

sidebar.title(Component.text("Sidebar Title"));
sidebar.line(0, Component.empty());
sidebar.line(1, Component.text("Line 1"));
sidebar.line(2, Component.text("Line 2"));
sidebar.line(2, Component.empty());
sidebar.line(3, Component.text("coolserver.net"));

sidebar.addPlayer(player); // Add the player to the sidebar
```

### TeamManager

```java
TeamManager teamManager = scoreboardManager.createTeamManager();
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
