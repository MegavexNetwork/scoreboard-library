package net.megavex.scoreboardlibrary.api.team;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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
   * @return Default {@link TeamDisplay} of this team
   */
  @NotNull TeamDisplay defaultDisplay();

  /**
   * Gets the {@link TeamDisplay} of a player
   *
   * @param player Player
   * @return {@link TeamDisplay} of this player
   */
  @NotNull TeamDisplay display(@NotNull Player player);

  /**
   * Changes a player's visible {@link TeamDisplay}
   *
   * @param player      Player
   * @param teamDisplay New {@link TeamDisplay} of Player
   */
  void display(@NotNull Player player, @NotNull TeamDisplay teamDisplay);

  /**
   * @return Newly created {@link TeamDisplay} which is assigned to this team
   */
  @NotNull TeamDisplay createDisplay();
}
