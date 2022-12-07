package net.megavex.scoreboardlibrary.api.team;

import java.util.Collection;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.interfaces.Players;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.concurrent.NotThreadSafe;

@ApiStatus.NonExtendable
@NotThreadSafe
public interface TeamManager extends Closeable, HasScoreboardLibrary, Players {
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
   * @param name Name of team.
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

  // Players

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
   * Adds a list of players to this TeamManager
   *
   * @param players          Players to add
   * @param teamInfoFunction Function that returns the {@link TeamInfo} that the player should have
   */
  default void addPlayers(@NotNull Collection<Player> players, @Nullable Function<ScoreboardTeam, TeamInfo> teamInfoFunction) {
    for (var player : players) {
      addPlayer(player, teamInfoFunction);
    }
  }
}
