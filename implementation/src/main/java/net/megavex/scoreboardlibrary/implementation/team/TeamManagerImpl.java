package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class TeamManagerImpl implements TeamManager {
  private final ScoreboardLibraryImpl scoreboardLibrary;

  private final Set<Player> players = CollectionProvider.set(8);
  private final Map<String, ScoreboardTeamImpl> teams = new ConcurrentHashMap<>();

  private boolean closed;

  private final Queue<TeamManagerTask> taskQueue = new ConcurrentLinkedQueue<>();

  public TeamManagerImpl(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
  }

  @Override
  public void close() {
    if (!closed) {
      closed = true;
      taskQueue.add(TeamManagerTask.Close.INSTANCE);
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public @NotNull Collection<Player> players() {
    return closed ? Collections.emptySet() : Collections.unmodifiableSet(players);
  }

  @Override
  public @NotNull Collection<ScoreboardTeam> teams() {
    return closed ? Collections.emptySet() : Collections.unmodifiableCollection(teams.values());
  }

  @Override
  public @Nullable ScoreboardTeam team(@NotNull String name) {
    return teams.get(name.toLowerCase());
  }

  @Override
  public boolean teamExists(@NotNull String name) {
    return teams.containsKey(name.toLowerCase());
  }

  @Override
  public @NotNull ScoreboardTeam createIfAbsent(@NotNull String name, @Nullable BiFunction<Player, ScoreboardTeam, TeamInfo> teamInfoFunction) {
    Preconditions.checkNotNull(name);
    checkClosed();
    name = name.toLowerCase();

    var team = teams.get(name);
    if (team != null) {
      return team;
    }

    team = new ScoreboardTeamImpl(this, name);
    teams.put(name, team);

    for (var player : players) {
      var teamInfo = teamInfoFunction == null ? team.globalInfo() : teamInfoFunction.apply(player, team);
      validateTeamInfo(team, teamInfo);
      team.teamInfoMap().put(player, (TeamInfoImpl) teamInfo);
    }

    var task = new TeamManagerTask.AddTeam(team);
    taskQueue.add(task);
    return team;
  }

  @Override
  public boolean removeTeam(@NotNull String name) {
    Preconditions.checkNotNull(name);
    checkClosed();

    var team = teams.remove(name.toLowerCase());
    if (team != null) {
      taskQueue.add(new TeamManagerTask.RemoveTeam(team));
      return true;
    }

    return false;
  }

  @Override
  public void removeTeam(@NotNull ScoreboardTeam team) {
    Preconditions.checkNotNull(team);
    Preconditions.checkArgument(team.teamManager() == this);
    checkClosed();

    if (teams.remove(team.name(), (ScoreboardTeamImpl) team)) {
      taskQueue.add(new TeamManagerTask.RemoveTeam((ScoreboardTeamImpl) team));
    }
  }

  @Override
  public boolean addPlayer(@NotNull Player player, @Nullable Function<ScoreboardTeam, TeamInfo> teamInfoFunction) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (!players.add(player)) {
      return false;
    }

    for (var team : teams.values()) {
      var teamInfo = teamInfoFunction == null ? team.globalInfo() : teamInfoFunction.apply(team);
      validateTeamInfo(team, teamInfo);
      team.teamInfoMap().put(player, (TeamInfoImpl) teamInfo);
    }

    taskQueue.add(new TeamManagerTask.AddPlayer(player));
    return true;
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);
    checkClosed();

    if (!players.remove(player)) {
      return false;
    }

    var task = new TeamManagerTask.RemovePlayer(player);
    taskQueue.add(task);
    return true;
  }

  public @NotNull ScoreboardLibraryImpl scoreboardLibrary() {
    return scoreboardLibrary;
  }

  public @NotNull Queue<TeamManagerTask> taskQueue() {
    return taskQueue;
  }

  public void show(@NotNull Player player) {
    for (var team : teams.values()) {
      team.addPlayer(player);
    }
  }

  public void tick() {
    while (true) {
      var task = taskQueue.poll();
      if (task == null) {
        break;
      }

      if (task instanceof TeamManagerTask.Close) {
        scoreboardLibrary.teamManagers().remove(this);

        for (var team : teams.values()) {
          Set<Player> removePlayers = CollectionProvider.set(players.size());
          for (TeamInfoImpl value : team.teamInfoMap().values()) {
            removePlayers.addAll(value.players());
          }
          team.packetAdapter().removeTeam(removePlayers);
        }

        for (var player : players) {
          Objects.requireNonNull(scoreboardLibrary.getPlayer(player)).removeTeamManager(this);
        }
        return;
      } else if (task instanceof TeamManagerTask.AddPlayer addPlayerTask) {
        var slPlayer = scoreboardLibrary.getOrCreatePlayer(addPlayerTask.player());
        slPlayer.addTeamManager(this);
      } else if (task instanceof TeamManagerTask.RemovePlayer removePlayerTask) {
        for (var team : teams.values()) {
          team.removePlayer(removePlayerTask.player());
        }

        Objects.requireNonNull(scoreboardLibrary.getPlayer(removePlayerTask.player())).removeTeamManager(this);
      } else if (task instanceof TeamManagerTask.ReloadPlayer reloadPlayerTask) {
        for (var team : teams.values()) {
          var teamInfo = team.teamInfoMap().get(reloadPlayerTask.player());
          if (teamInfo != null) {
            teamInfo.packetAdapter().updateTeam(Set.of(reloadPlayerTask.player()));
          }
        }
      } else if (task instanceof TeamManagerTask.AddTeam addTeamTask) {
        var team = addTeamTask.team();
        for (var player : team.teamInfoMap().keySet()) {
          var slPlayer = scoreboardLibrary.getPlayer(player);
          if (slPlayer != null && slPlayer.teamManager() == this) {
            team.addPlayer(player);
          }
        }
      } else if (task instanceof TeamManagerTask.RemoveTeam removeTeamTask) {
        List<Player> playersInTeam = CollectionProvider.list(removeTeamTask.team().teamInfoMap().size());
        for (var entry : removeTeamTask.team().teamInfoMap().entrySet()) {
          var player = entry.getKey();
          var teamInfo = entry.getValue();
          if (teamInfo.players().contains(player)) {
            playersInTeam.add(player);
          }
        }

        removeTeamTask.team().packetAdapter().removeTeam(playersInTeam);
      } else if (task instanceof TeamManagerTask.UpdateTeamInfo updateTeamInfoTask) {
        var teamInfo = updateTeamInfoTask.teamInfo();
        teamInfo.updateTeamPackets();
        teamInfo.packetAdapter().updateTeam(teamInfo.players());
      } else if (task instanceof TeamManagerTask.AddEntries addEntriesTask) {
        var teamInfo = addEntriesTask.teamInfo();
        teamInfo.packetAdapter().addEntries(teamInfo.players(), addEntriesTask.entries());
      } else if (task instanceof TeamManagerTask.RemoveEntries removeEntriesTask) {
        var teamInfo = removeEntriesTask.teamInfo();
        teamInfo.packetAdapter().removeEntries(teamInfo.players(), removeEntriesTask.entries());
      } else if (task instanceof TeamManagerTask.ChangeTeamInfo changeTeamInfoTask) {
        changeTeamInfoTask.team().changeTeamInfo(changeTeamInfoTask.player(), changeTeamInfoTask.oldTeamInfo(), changeTeamInfoTask.newTeamInfo());
      }
    }
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("TeamManager is closed");
    }
  }

  private void validateTeamInfo(@NotNull ScoreboardTeam team, @Nullable TeamInfo teamInfo) {
    if (teamInfo == null || teamInfo.team() != team) {
      throw new IllegalArgumentException("invalid TeamInfo");
    }

    if (!(teamInfo instanceof TeamInfoImpl)) {
      throw new IllegalArgumentException("must be TeamInfoImpl");
    }
  }
}
