package net.megavex.scoreboardlibrary.api.team;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a Scoreboard Team
 */
public interface ScoreboardTeam {
  /**
   * @return {@link TeamManager} of this team
   */
  @NotNull TeamManager teamManager();

  /**
   * @return Name of this team
   */
  @NotNull String name();

  /**
   * @return Global {@link TeamInfo} of this team
   */
  @NotNull TeamInfo globalInfo();

  /**
   * Gets the {@link TeamInfo} of a player
   *
   * @param player Player
   * @return {@link TeamInfo} of this player
   */
  @NotNull TeamInfo teamInfo(@NotNull Player player);

  /**
   * Changes a player's visible {@link TeamInfo}
   *
   * @param player   Player
   * @param teamInfo New {@link TeamInfo} of Player
   */
  void teamInfo(@NotNull Player player, @NotNull TeamInfo teamInfo);
}
