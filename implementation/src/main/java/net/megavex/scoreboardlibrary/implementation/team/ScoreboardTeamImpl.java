package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ScoreboardTeamImpl implements ScoreboardTeam {
  private final TeamManagerImpl teamManager;
  private final String name;
  private final TeamsPacketAdapter<?, ?> packetAdapter;

  private final TeamInfoImpl globalInfo;
  private final Map<Player, TeamInfoImpl> teamInfoMap = new ConcurrentHashMap<>();

  public ScoreboardTeamImpl(@NotNull TeamManagerImpl teamManager, @NotNull String name) {
    this.teamManager = teamManager;
    this.name = name;
    this.packetAdapter = teamManager.scoreboardLibrary().packetAdapter.createTeamPacketAdapter(name);
    this.globalInfo = new TeamInfoImpl(this);
  }

  @Override
  public @NotNull TeamManagerImpl teamManager() {
    return teamManager;
  }

  @Override
  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull TeamInfoImpl globalInfo() {
    return globalInfo;
  }

  @Override
  public @NotNull TeamInfo teamInfo(@NotNull Player player) {
    Preconditions.checkNotNull(player);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    return Objects.requireNonNull(teamInfoMap.get(player));
  }

  @Override
  public void teamInfo(@NotNull Player player, @NotNull TeamInfo teamInfo) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(teamInfo);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    if (teamInfo.team() != this || !(teamInfo instanceof TeamInfoImpl)) {
      throw new IllegalArgumentException("invalid TeamInfo");
    }

    var oldTeamInfo = Objects.requireNonNull(teamInfoMap.put(player, (TeamInfoImpl) teamInfo));
    if (oldTeamInfo == teamInfo) {
      return;
    }

    teamManager.taskQueue().add(new TeamManagerTask.ChangeTeamInfo(player, this, oldTeamInfo, (TeamInfoImpl) teamInfo));
  }

  @Override
  public TeamInfo createTeamInfo() {
    return new TeamInfoImpl(this);
  }

  public @NotNull TeamsPacketAdapter<?, ?> packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Map<Player, TeamInfoImpl> teamInfoMap() {
    return teamInfoMap;
  }

  public void addPlayer(@NotNull Player player) {
    var teamInfo = Objects.requireNonNull(teamInfoMap.get(player));
    if (teamInfo.players().add(player)) {
      teamInfo.packetAdapter().createTeam(Set.of(player));
    }
  }

  public void removePlayer(@NotNull Player player) {
    var teamInfo = Objects.requireNonNull(teamInfoMap.remove(player));
    if (teamInfo.players().remove(player)) {
      packetAdapter.removeTeam(Set.of(player));
    }
  }

  public void changeTeamInfo(@NotNull Player player, @NotNull TeamInfoImpl oldTeamInfo, @NotNull TeamInfoImpl newTeamInfo) {
    if (!oldTeamInfo.players().remove(player)) {
      return;
    }

    newTeamInfo.players().add(player);

    var singleton = Set.of(player);
    newTeamInfo.packetAdapter().updateTeam(singleton);

    var oldEntries = oldTeamInfo.entries();
    var newEntries = newTeamInfo.entries();

    if (oldEntries.isEmpty()) {
      newTeamInfo.packetAdapter().addEntries(singleton, newEntries);
      return;
    }

    var entries = new ArrayList<>(oldEntries);
    entries.removeAll(newEntries);
    if (!entries.isEmpty()) {
      newTeamInfo.packetAdapter().removeEntries(singleton, entries);
    }

    entries = new ArrayList<>(newEntries);
    entries.removeAll(oldEntries);
    if (!entries.isEmpty()) {
      newTeamInfo.packetAdapter().addEntries(singleton, entries);
    }
  }
}
