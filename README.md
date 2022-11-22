# scoreboard-library

A Scoreboard library for Paper/Spigot servers using the [adventure](https://github.com/KyoriPowered/adventure) library.

Join the [Discord](https://discord.gg/v7nmTDTW8W) or create an issue for support

## Features:

- Sidebars
- Teams
- Packet-level
- Mostly async

## Supported Versions

- **1.19.\*.** [Spigot](https://www.spigotmc.org/) does work, but [Paper](https://papermc.io/) is recommended because
  scoreboard-library
  takes advantage of the native [Adventure](https://github.com/KyoriPowered/adventure) feature to improve performance
- **1.8.8.** Note that you'll still need to use Java 17
- **PacketEvents.** Requires [PacketEvents 2.0](https://github.com/retrooper/packetevents/tree/2.0) to be loaded in the
  classpath.
  Should work with all versions 1.8+

**Only the latest version and 1.8.8 will be supported (at least for now)**

## Getting started

See installation instructions [here](https://github.com/MegavexNetwork/scoreboard-library/blob/master/INSTALLATION.md)

### Getting a `ScoreboardManager`:

```java
try {
    ScoreboardLibraryImplementation.init();
} catch (ScoreboardLibraryLoadException e) {
    // Couldn't load the library.
    // Probably because the servers version is unsupported.
    e.printStackTrace();
    return;
}

ScoreboardManager scoreboardLibrary = ScoreboardManager.scoreboardLibrary(plugin);

// On plugin shutdown:
scoreboardLibrary.close();
ScoreboardLibraryImplementation.close();
```

### Sidebar

```java
Sidebar sidebar = scoreboardLibrary.sidebar(
    Sidebar.MAX_LINES, // 15
    null // Locale which should be used for translating Components, or null if it should depend on each player's client locale
);

sidebar.title(Component.text("Sidebar Title"));
sidebar.line(0, Component.empty());
sidebar.line(1, Component.text("Line 1"));
sidebar.line(2, Component.text("Line 2"));
sidebar.line(2, Component.empty());
sidebar.line(3, Component.text("coolserver.net"));

sidebar.addPlayer(player); // Add the player to the sidebar
sidebar.visible(true); // Make the sidebar visible
```

### TeamManager

```java
TeamManager teamManager = scoreboardLibrary.teamManager();
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
TeamInfo newTeamInfo = TeamInfo.teamInfo(); // Creates a blank TeamInfo
team.teamInfo(player, newTeamInfo);
```

For more examples, check out the [example plugin](https://github.com/MegavexNetwork/scoreboard-library-example)

## Notes

- When a player leaves, they will be automatically removed from any Sidebar or TeamManager they were in to prevent
  memory leaks
- One player cannot have more than one Sidebar or TeamManager at the same time, it will throw an exception (at least for
  now)
- Should only be used from the main thread (most of the work will still be done async anyway)
