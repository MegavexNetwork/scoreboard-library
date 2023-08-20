package net.megavex.scoreboardlibrary.api.sidebar;

import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.Locale;

/**
 * Provides a low-level utility for showing sidebars to players.
 * Note: this class is not thread-safe.
 */
@ApiStatus.NonExtendable
public interface Sidebar {
  // Constants

  int MAX_LINES = 15;

  // Main

  /**
   * @return Max amount of lines this sidebar can have
   */
  @Range(from = 1, to = MAX_LINES) int maxLines();

  /**
   * @return {@link Locale} which is used to translate {@link net.kyori.adventure.text.TranslatableComponent}s
   */
  @Nullable Locale locale();

  // Lines

  /**
   * Gets a line's value.
   *
   * @param line Line
   * @return Value of line
   */
  @Nullable Component line(@Range(from = 0, to = MAX_LINES - 1) int line);

  /**
   * Sets a line's value.
   *
   * @param line  Line
   * @param value Value
   */
  void line(@Range(from = 0, to = MAX_LINES - 1) int line, @Nullable Component value);

  /**
   * Clears all lines in this sidebar.
   */
  default void clearLines() {
    for (int i = 0; i < maxLines(); i++) {
      line(i, null);
    }
  }

  // Title

  /**
   * @return Title of the sidebar
   */
  @NotNull Component title();

  /**
   * Sets the title of the sidebar.
   *
   * @param title Title
   */
  void title(@NotNull Component title);

  // Players

  /**
   * @return The players that have been added to this sidebar.
   */
  @NotNull Collection<Player> players();

  /**
   * Adds a player to this sidebar, making it be able to see it.
   *
   * @param player Player to add
   * @return Whether the player has been added
   */
  boolean addPlayer(@NotNull Player player);

  /**
   * Removes a player from this sidebar, making it not able to see it.
   *
   * @param player Player to remove
   * @return Whether the player has been removed
   */
  boolean removePlayer(@NotNull Player player);

  /**
   * Adds multiple players to this sidebar.
   *
   * @param players Players to add
   * @see #addPlayer
   */
  default void addPlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      addPlayer(player);
    }
  }

  /**
   * Removes multiple players from this sidebar.
   *
   * @param players Players to remove
   * @see #removePlayer
   */
  default void removePlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      removePlayer(player);
    }
  }

  // Close

  /**
   * Closes this sidebar
   */
  void close();

  /**
   * @return Whether this sidebar is closed
   */
  boolean closed();
}
