package net.megavex.scoreboardlibrary.api.objective;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

/**
 * Manages a group of {@link ScoreboardObjective}s.
 * Note: this class is not thread-safe.
 */
public interface ObjectiveManager {
  /**
   * Creates an objective
   *
   * @param name Name of the objective
   * @return A newly created objective, or an existing one if the name was already registered
   */
  @NotNull ScoreboardObjective create(@NotNull String name);

  /**
   * Removes an objective.
   *
   * @param objective Objective to remove
   */
  void remove(@NotNull ScoreboardObjective objective);

  /**
   * Updates the objective shown at a display slot
   *
   * @param displaySlot Display slot value
   * @param objective   Objective to display at that slot
   */
  void display(@NotNull ObjectiveDisplaySlot displaySlot, @NotNull ScoreboardObjective objective);

  /**
   * @return Players in this ObjectiveManager
   */
  @NotNull Collection<Player> players();

  /**
   * Adds a player to this ObjectiveManager
   *
   * @param player Player to add to ObjectiveManager
   * @return Whether the player was added
   */
  boolean addPlayer(@NotNull Player player);

  /**
   * Adds multiple players to this ObjectiveManager
   *
   * @param players Players to add
   */
  default void addPlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      addPlayer(player);
    }
  }

  /**
   * Removes a player from this ObjectiveManager
   *
   * @param player Player to remove
   * @return Whether the player was removed
   */
  boolean removePlayer(@NotNull Player player);

  /**
   * Removes multiple players to this ObjectiveManager
   *
   * @param players Players to add
   */
  default void removePlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      removePlayer(player);
    }
  }

  /**
   * Closes this ObjectiveManager.
   */
  void close();

  /**
   * @return Whether this ObjectiveManager is closed
   */
  boolean closed();
}
