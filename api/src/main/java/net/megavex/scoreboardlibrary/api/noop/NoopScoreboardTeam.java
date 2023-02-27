package net.megavex.scoreboardlibrary.api.noop;

import com.google.common.base.Preconditions;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import net.megavex.scoreboardlibrary.api.team.ScoreboardTeam;
import net.megavex.scoreboardlibrary.api.team.TeamDisplay;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

class NoopScoreboardTeam implements ScoreboardTeam {
  private final NoopTeamManager teamManager;
  private final String name;
  private final Map<Player, TeamDisplay> teamDisplayMap = new HashMap<>();
  private final NoopTeamDisplay globalInfo = new NoopTeamDisplay(this);

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
  public @NotNull NoopTeamDisplay globalInfo() {
    return globalInfo;
  }

  @Override
  public @NotNull TeamDisplay display(@NotNull Player player) {
    Preconditions.checkNotNull(player);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    return Objects.requireNonNull(teamDisplayMap.get(player));
  }

  @Override
  public void display(@NotNull Player player, @NotNull TeamDisplay teamDisplay) {
    Preconditions.checkNotNull(player);
    Preconditions.checkNotNull(teamDisplay);

    if (!teamManager.players().contains(player)) {
      throw new IllegalArgumentException("player not in TeamManager");
    }

    if (teamDisplay.team() != this || !(teamDisplay instanceof NoopTeamDisplay)) {
      throw new IllegalArgumentException("invalid TeamDisplay");
    }

    teamDisplayMap.put(player, teamDisplay);
  }

  @Override
  public @NotNull TeamDisplay createTeamDisplay() {
    return new NoopTeamDisplay(this);
  }

  Map<Player, TeamDisplay> teamDisplayMap() {
    return teamDisplayMap;
  }
}
