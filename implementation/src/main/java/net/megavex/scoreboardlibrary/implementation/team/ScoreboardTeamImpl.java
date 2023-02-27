package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import java.util.ArrayList;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.implementation.packetAdapter.TeamsPacketAdapter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class ScoreboardTeamImpl implements ScoreboardTeam {
  private final TeamManagerImpl teamManager;
  private final String name;
  private final TeamsPacketAdapter<?, ?> packetAdapter;

  private final TeamDisplayImpl defaultDisplay;
  private final Map<Player, TeamDisplayImpl> displayMap = new ConcurrentHashMap<>();

  public ScoreboardTeamImpl(@NotNull TeamManagerImpl teamManager, @NotNull String name) {
    this.teamManager = teamManager;
    this.name = name;
    this.packetAdapter = teamManager.scoreboardLibrary().packetAdapter().createTeamPacketAdapter(name);
    this.defaultDisplay = new TeamDisplayImpl(this);
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
  public @NotNull TeamDisplayImpl defaultDisplay() {
    return defaultDisplay;
  }

  @Override
  public @NotNull TeamDisplay display(@NotNull Player player) {
    Preconditions.checkNotNull(player);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    return Objects.requireNonNull(displayMap.get(player));
  }

  @Override
  public void display(@NotNull Player player, @NotNull TeamDisplay teamDisplay) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(teamDisplay);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    if (teamDisplay.team() != this || !(teamDisplay instanceof TeamDisplayImpl)) {
      throw new IllegalArgumentException("invalid TeamDisplay");
    }

    var oldTeamDisplay = Objects.requireNonNull(displayMap.put(player, (TeamDisplayImpl) teamDisplay));
    if (oldTeamDisplay == teamDisplay) {
      return;
    }

    teamManager.taskQueue().add(new TeamManagerTask.ChangeTeamDisplayTask(player, this, oldTeamDisplay, (TeamDisplayImpl) teamDisplay));
  }

  @Override
  public @NotNull TeamDisplay createDisplay() {
    return new TeamDisplayImpl(this);
  }

  public @NotNull TeamsPacketAdapter<?, ?> packetAdapter() {
    return packetAdapter;
  }

  public @NotNull Map<Player, TeamDisplayImpl> displayMap() {
    return displayMap;
  }

  public void addPlayer(@NotNull Player player) {
    var teamDisplay = Objects.requireNonNull(displayMap.get(player));
    if (teamDisplay.players().add(player)) {
      teamDisplay.packetAdapter().createTeam(Set.of(player));
    }
  }

  public void removePlayer(@NotNull Player player) {
    var teamDisplay = Objects.requireNonNull(displayMap.remove(player));
    if (teamDisplay.players().remove(player)) {
      packetAdapter.removeTeam(Set.of(player));
    }
  }

  public void changeTeamDisplay(@NotNull Player player, @NotNull TeamDisplayImpl oldTeamDisplay, @NotNull TeamDisplayImpl newTeamDisplay) {
    if (!oldTeamDisplay.players().remove(player)) {
      return;
    }

    newTeamDisplay.players().add(player);

    var singleton = Set.of(player);
    newTeamDisplay.packetAdapter().updateTeam(singleton);

    var oldEntries = oldTeamDisplay.entries();
    var newEntries = newTeamDisplay.entries();

    if (oldEntries.isEmpty()) {
      newTeamDisplay.packetAdapter().addEntries(singleton, newEntries);
      return;
    }

    var entries = new ArrayList<>(oldEntries);
    entries.removeAll(newEntries);
    if (!entries.isEmpty()) {
      newTeamDisplay.packetAdapter().removeEntries(singleton, entries);
    }

    entries = new ArrayList<>(newEntries);
    entries.removeAll(oldEntries);
    if (!entries.isEmpty()) {
      newTeamDisplay.packetAdapter().addEntries(singleton, entries);
    }
  }
}
