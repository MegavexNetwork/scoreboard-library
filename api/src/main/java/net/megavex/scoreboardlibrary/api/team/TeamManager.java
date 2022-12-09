package net.megavex.scoreboardlibrary.api.team;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import javax.annotation.concurrent.NotThreadSafe;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ApiStatus.NonExtendable
@NotThreadSafe
public interface TeamManager {
  // Teams

  /**
   * Returns an unmodifiable set of teams in this team manager
   *
   * @return Teams
   */
  @NotNull Collection<ScoreboardTeam> teams();

  /**
   * Returns team based on its name
   *
   * @param name Name of team
   * @return Team with this name
   */
  @Nullable ScoreboardTeam team(@NotNull String name);

  /**
   * Returns whether a team by the name exists
   *
   * @param name Name of team
   * @return Whether a team by the name exists
   */
  boolean teamExists(@NotNull String name);

  /**
   * Returns team based on its name. If it doesn't already exist, creates it
   *
   * @param name Name of team
   * @return Team with this name, null if not found
   */
  default @NotNull ScoreboardTeam createIfAbsent(@NotNull String name) {
    return createIfAbsent(name, null);
  }

  /**
   * Returns a team based on its name. If it doesn't already exist, it creates it
   *
   * @param name             Name of team
   * @param teamInfoFunction A function that provides team info's for each player
   * @return Team with this name
   */
  @NotNull ScoreboardTeam createIfAbsent(@NotNull String name, @Nullable BiFunction<Player, ScoreboardTeam, TeamInfo> teamInfoFunction);

  /**
   * Removes a team
   *
   * @param name Name of team
   * @return If there was a team with than name
   */
  boolean removeTeam(@NotNull String name);

  // Players

  /**
   * @return Players in this TeamManager
   */
  @NotNull Collection<Player> players();

  /**
   * Adds a player to this TeamManager
   *
   * @param player Player to add to TeamManager
   * @return Whether the player was added
   */
  default boolean addPlayer(@NotNull Player player) {
    return addPlayer(player, null);
  }

  /**
   * Adds a player to this TeamManager
   *
   * @param player           Player to add
   * @param teamInfoFunction A function that returns the {@link TeamInfo} that the player should have
   * @return Whether the player was added
   */
  boolean addPlayer(@NotNull Player player, @Nullable Function<ScoreboardTeam, TeamInfo> teamInfoFunction);

  /**
   * Adds a player to this TeamManager
   *
   * @param players Players to add
   */
  default void addPlayers(@NotNull Collection<Player> players) {
    addPlayers(players, null);
  }

  /**
   * Adds a collection of players to this TeamManager
   *
   * @param players          Players to add
   * @param teamInfoFunction Function that returns the {@link TeamInfo} that the player should have
   */
  default void addPlayers(@NotNull Collection<Player> players, @Nullable Function<ScoreboardTeam, TeamInfo> teamInfoFunction) {
    for (var player : players) {
      addPlayer(player, teamInfoFunction);
    }
  }

  /**
   * Removes a player from this TeamManager
   *
   * @param player Player to remove
   * @return Whether the player wasn't already in this TeamManager
   */
  boolean removePlayer(@NotNull Player player);

  /**
   * Removes a collection of players from this TeamManager
   *
   * @param players Players to remove
   */
  default void removePlayers(@NotNull Collection<Player> players) {
    for (var player : players) {
      removePlayer(player);
    }
  }

  // Close

  /**
   * Closes this TeamManager
   */
  void close();

  /**
   * @return Whether this TeamManager is closed
   */
  boolean closed();
}
