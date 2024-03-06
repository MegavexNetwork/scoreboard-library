package net.megavex.scoreboardlibrary.api.team;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

/**
 * Represents a scoreboard team.
 * To get an instance of this interface, use {@link TeamManager#createIfAbsent}.
 * Note: this interface is not thread-safe, meaning you can only use it from one thread at a time,
 * although it does not have to be the main thread.
 *
 * @see <a href="https://minecraft.fandom.com/wiki/Scoreboard#Teams">Minecraft Wiki</a>
 */
public interface ScoreboardTeam {
  /**
   * @return TeamManager of this team
   */
  @NotNull TeamManager teamManager();

  /**
   * @return name of this team
   */
  @NotNull String name();

  /**
   * Gets the default {@link TeamDisplay} of this team.
   * All players are added to this display by default.
   *
   * @return default team display
   */
  @NotNull TeamDisplay defaultDisplay();

  /**
   * Gets the {@link TeamDisplay} that a player sees.
   *
   * @param player player
   * @return team display the player sees
   * @throws IllegalArgumentException if player is not in the TeamManager
   */
  @NotNull TeamDisplay display(@NotNull Player player);

  /**
   * Updates the team {@link TeamDisplay} that a player sees for this team.
   *
   * @param player      player
   * @param teamDisplay new {@link TeamDisplay} of player
   * @throws IllegalArgumentException if player is not in the TeamManager
   */
  void display(@NotNull Player player, @NotNull TeamDisplay teamDisplay);

  /**
   * Creates a new {@link TeamDisplay}.
   * To show it to players, use {@link #display(Player, TeamDisplay)}.
   *
   * @return newly created team display
   */
  @NotNull TeamDisplay createDisplay();
}
