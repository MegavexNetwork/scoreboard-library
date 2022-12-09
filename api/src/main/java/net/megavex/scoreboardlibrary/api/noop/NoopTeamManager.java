package net.megavex.scoreboardlibrary.api.noop;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

class NoopTeamManager implements TeamManager {
  private final Set<Player> players = new HashSet<>();
  private final Map<String, NoopScoreboardTeam> teams = new HashMap<>();
  private boolean closed;

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public boolean closed() {
    return closed;
  }

  @Override
  public @NotNull Collection<Player> players() {
    return closed ? Set.of() : Collections.unmodifiableSet(players);
  }

  @Override
  public boolean removePlayer(@NotNull Player player) {
    Preconditions.checkNotNull(player);

    if (!players.remove(player)) {
      return false;
    }

    for (var team : teams.values()) {
      team.teamInfoMap().remove(player);
    }

    return true;
  }

  @Override
  public @NotNull Collection<ScoreboardTeam> teams() {
    return closed ? Set.of() : Collections.unmodifiableCollection(teams.values());
  }

  @Override
  public @Nullable ScoreboardTeam team(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return teams.get(name.toLowerCase());
  }

  @Override
  public boolean teamExists(@NotNull String name) {
    Preconditions.checkNotNull(name);
    return teams.containsKey(name.toLowerCase());
  }

  @Override
  public @NotNull ScoreboardTeam createIfAbsent(@NotNull String name, @Nullable BiFunction<Player, ScoreboardTeam, TeamInfo> teamInfoFunction) {
    Preconditions.checkNotNull(name);

    name = name.toLowerCase();
    var team = teams.get(name);
    if (team != null) {
      return team;
    }

    team = new NoopScoreboardTeam(this, name);
    for (var player : players) {
      var teamInfo = teamInfoFunction == null ? team.globalInfo() : teamInfoFunction.apply(player, team);
      validateTeamInfo(team, teamInfo);
      team.teamInfoMap().put(player, teamInfo);
    }

    return team;
  }

  @Override
  public boolean removeTeam(@NotNull String name) {
    Preconditions.checkNotNull(name);

    return teams.remove(name.toLowerCase()) != null;
  }

  @Override
  public boolean addPlayer(@NotNull Player player, @Nullable Function<ScoreboardTeam, TeamInfo> teamInfoFunction) {
    Preconditions.checkNotNull(player);

    if (!players.add(player)) {
      return false;
    }

    for (var team : teams.values()) {
      var teamInfo = teamInfoFunction == null ? team.globalInfo() : teamInfoFunction.apply(team);
      validateTeamInfo(team, teamInfo);
      team.teamInfoMap().put(player, teamInfo);
    }

    return true;
  }

  private void validateTeamInfo(@NotNull ScoreboardTeam team, @Nullable TeamInfo teamInfo) {
    if (teamInfo == null || teamInfo.team() != team) {
      throw new IllegalArgumentException("invalid TeamInfo");
    }

    if (!(teamInfo instanceof NoopTeamInfo)) {
      throw new IllegalArgumentException("must be TeamInfoImpl");
    }
  }
}
