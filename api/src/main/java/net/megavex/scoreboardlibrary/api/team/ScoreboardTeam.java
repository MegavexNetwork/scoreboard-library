package net.megavex.scoreboardlibrary.api.team;

import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a Scoreboard Team
 */
public interface ScoreboardTeam extends Closeable {
  /**
   * @return {@link TeamManager} of this team
   */
  @NotNull TeamManager teamManager();

  /**
   * @return Global {@link TeamInfo} of this team
   */
  @NotNull TeamInfo globalInfo();

  /**
   * @return Name of this team
   */
  @NotNull String name();

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
   * @return New team info of player.
   */
  @NotNull TeamInfo teamInfo(@NotNull Player player, @Nullable TeamInfo teamInfo);
}
