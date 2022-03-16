# ScoreboardLibrary

A Scoreboard library for Paper/Spigot servers using the [Adventure](https://github.com/KyoriPowered/adventure) library.
Note that this project is not yet stable, so expect bugs.

Join the [Discord](https://discord.gg/v7nmTDTW8W) or create an issue for support.

## Features:

- Sidebar API: max 42 characters per line on 1.12.2 and below, unlimited for newer versions. If you have
  the [ProtocolSupport](https://github.com/ProtocolSupport/ProtocolSupport/) plugin on your 1.18.1 server, it will be
  automatically utilized for players at or below 1.12.2.
- Teams API
- Packet-level
- Mostly async

## Supported Versions

- **1.18.1.** [Spigot](https://www.spigotmc.org/) does work, but with [Paper](https://papermc.io/) it has better performance
  because of native [Adventure](https://github.com/KyoriPowered/adventure)
- **1.8.8.** Note that you'll need to use Java 17.

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

ScoreboardManager scoreboardManager = ScoreboardManager.scoreboardManager(plugin);

// On plugin shutdown:
scoreboardManager.close();
ScoreboardLibraryImplementation.close();
```

### Sidebar

```java
Sidebar sidebar = scoreboardManager.sidebar(
    Sidebar.MAX_LINES, // 15
    null // Locale that should be used for translating Components, or null if it should depend on each player's client locale (it will use a bit more memory though)
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

For more examples, check out the [example plugin](https://github.com/MegavexNetwork/scoreboard-library-example).

## Notes

- When a player leaves, they will be automatically removed from any Sidebar or TeamManager they were in to prevent a
  memory leak.
- One player cannot have more than one Sidebar or TeamManager at the same time, it will throw an exception (atleast for
  now).
- Should only be used from the main thread (most of the packet work will still be done async anyways).

## Building

Make sure you have Java 17, then just run `gradlew build`, you'll find the jars in `build/libs/` of every module. If you
also want to publish the artifacts to your Maven local repository, you can do `gradlew publishToMavenLocal`.
