# ScoreboardLibrary

A Scoreboard library for Paper/Spigot servers using the [Adventure](https://github.com/KyoriPowered/adventure) library.
Note that this project is not yet stable, so expect bugs.

## Features:

- Sidebar API: max 42 characters per line on 1.12.2 and below, unlimited for newer versions. If you have
  the [ProtocolSupport](https://github.com/ProtocolSupport/ProtocolSupport/) plugin on your 1.18.1 server, it will be
  automatically utilized for players below at or below 1.12.2.
- Teams API
- Packet-level

## Supported Versions

- 1.18.1 (sidebars will not flicker for players on new versions, but will flicker on 1.8 players playing
  through [ProtocolSupport](https://github.com/ProtocolSupport/ProtocolSupport/) if a line is long enough)
- 1.8.8 (sidebars will flicker if a line is long enough)

## Getting started

See installation instructions [here](https://github.com/MegavexNetwork/scoreboard-library/blob/master/INSTALLATION.md)

### Getting a `ScoreboardManager`:

```java
// If you're using the standalone plugin, you don't need to do this:
try {
    ScoreboardLibraryImplementation.init(this);
} catch (ScoreboardLibraryLoadException e) {
    // Couldn't load the library.
    // Probably because the servers version is unsupported.
    e.printStackTrace();
    return;
}

ScoreboardManager scoreboardManager = ScoreboardManager.scoreboardManager(plugin);

// On plugin shutdown:
scoreboardManager.close();
ScoreboardLibraryImplementation.close(); // Also not needed if using the standalone plugin
```

### Sidebar

```java
Sidebar sidebar = scoreboardManager.sidebar(
    Sidebar.MAX_LINES, // 15
    null // Locale that should be used for translating Components, or null if it should depend on each player's client locale
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
TeamManager teamManager = scoreboardManager.teamManager();
ScoreboardTeam team = teamManager.createIfAbsent("TeamName");

// A TeamInfo holds all the properties that a team can have (except the name).
// The global TeamInfo is the default one that will be applied to players,
// however you can give each player a different TeamInfo
TeamInfo teamInfo = team.globalInfo();

teamInfo.displayName(Component.text("Team Name"));
teamInfo.prefix(Component.text("[Prefix] "));
teamInfo.suffix(Component.text("[Suffix] "));
teamInfo.playerColor(NamedTextColor.RED);

teamManager.addPlayer(player); // Player will be added to the global TeamInfo

// You can change the TeamInfo like this:
TeamInfo newTeamInfo = TeamInfo.teamInfo(); // Creates a blank TeamInfo
team.teamInfo(player, newTeamInfo);
```

See the [example plugin](https://github.com/MegavexNetwork/scoreboard-library-example) for more examples.

## Notes

- When a player leaves, they will be automatically removed from any Sidebar or TeamManager they were in to prevent a
  memory leak.
- One player cannot have more than one Sidebar or TeamManager at the same time, it will throw an exception (atleast for
  now).
- Not thread-safe and should only be used from the main thread (atleast for now).

## Building

Clone the project, and just do `gradlew build`, you'll find the jars in `build/` of every module. If you also want to
publish the artifacts to your Maven local repository, you can do `gradlew publishToMavenLocal`.