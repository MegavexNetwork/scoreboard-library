package net.megavex.scoreboardlibrary.api.noop;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamInfo;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class NoopScoreboardTeam implements ScoreboardTeam {
  private final NoopTeamManager teamManager;
  private final String name;
  private final Map<Player, TeamInfo> teamInfoMap = new HashMap<>();
  private final NoopTeamInfo globalInfo = new NoopTeamInfo(this);

  NoopScoreboardTeam(@NotNull NoopTeamManager teamManager, String name) {
    this.teamManager = teamManager;
    this.name = name;
  }

  @Override
  public @NotNull TeamManager teamManager() {
    return teamManager;
  }

  @Override
  public @NotNull String name() {
    return name;
  }

  @Override
  public @NotNull NoopTeamInfo globalInfo() {
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

    if (teamInfo.team() != this || !(teamInfo instanceof NoopTeamInfo)) {
      throw new IllegalArgumentException("invalid TeamInfo");
    }

    teamInfoMap.put(player, teamInfo);
  }

  @Override
  public TeamInfo createTeamInfo() {
    return new NoopTeamInfo(this);
  }

  public Map<Player, TeamInfo> teamInfoMap() {
    return teamInfoMap;
  }
}
