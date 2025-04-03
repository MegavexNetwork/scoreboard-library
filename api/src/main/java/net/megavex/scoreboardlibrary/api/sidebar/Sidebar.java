package net.megavex.scoreboardlibrary.api.sidebar;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Collection;
import java.util.Locale;

/**
 * Represents a sidebar.
 * To get an instance of this interface, use {@link ScoreboardLibrary#createSidebar()}.
 * Note: this interface is not thread-safe, meaning you can only use it from one thread at a time,
 * although it does not have to be the main thread.
 */
@ApiStatus.NonExtendable
public interface Sidebar {
  /**
   * The max amount of lines a vanilla client can display at once.
   */
  int MAX_LINES = 15;

  // Main

  /**
   * @return max amount of lines this sidebar can have
   */
  @Range(from = 1, to = Integer.MAX_VALUE) int maxLines();

  /**
   * @return objective name used by this sidebar
   */
  @NotNull String objectiveName();

  /**
   * @return locale which is used for translating {@link net.kyori.adventure.text.TranslatableComponent}s,
   * or null if it depends on each player's client locale.
   * @see ScoreboardLibrary#createSidebar(int, Locale)
   */
  @Nullable Locale locale();

  // Lines

  /**
   * Gets a line's value, or null if line isn't set.
   *
   * @param line line index
   * @return value of line, or null if unset
   */
  @Nullable Component line(@Range(from = 0, to = Integer.MAX_VALUE - 1) int line);

  /**
   * Sets a line's value.
   * A null value hides the line.
   *
   * @param index line index
   * @param value new value, or null to hide
   */
  default void line(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, @Nullable ComponentLike value) {
    line(index, value, null);
  }

  /**
   * Sets a line's value with a custom score format.
   * A null value hides the line.
   * Note that custom score formats are only supported in Minecraft 1.20.3+.
   *
   * @param index       line index
   * @param value       new value, or null to hide
   * @param scoreFormat score format
   */
  void line(
    @Range(from = 0, to = Integer.MAX_VALUE - 1) int index,
    @Nullable ComponentLike value,
    @Nullable ScoreFormat scoreFormat
  );

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
   * @return title of the sidebar, defaults to {@link Component#empty}
   */
  @NotNull Component title();

  /**
   * Sets the title of the sidebar.
   *
   * @param title title
   */
  void title(@NotNull ComponentLike title);

  // Players

  /**
   * @return unmodifiable collection of viewers in this Sidebar
   * @see #addPlayer
   * @see #removePlayer
   */
  @NotNull Collection<Player> players();

  /**
   * Adds a viewer to this Sidebar.
   * Note that a player can only see a single Sidebar at a time.
   * The Sidebar will internally be added to a queue for this player who
   * will start seeing it once they are removed from all previous Sidebars.
   *
   * @param player player to add
   * @return whether the player was added
   */
  boolean addPlayer(@NotNull Player player);

  /**
   * Adds multiple viewers to this Sidebar.
   *
   * @param players viewers to add
   * @see #addPlayer
   */
  default void addPlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      addPlayer(player);
    }
  }

  /**
   * Removes a viewer from this Sidebar.
   *
   * @param player viewer to remove
   * @return whether the viewer was removed
   */
  boolean removePlayer(@NotNull Player player);

  /**
   * Removes multiple viewers from this Sidebar
   *
   * @param players viewers to remove
   */
  default void removePlayers(@NotNull Collection<Player> players) {
    for (Player player : players) {
      removePlayer(player);
    }
  }

  /**
   * Closes this Sidebar.
   * This must be called once you no longer need this Sidebar to prevent a memory leak.
   */
  void close();

  /**
   * @return Whether this sidebar is closed
   * @see #close
   */
  boolean closed();
}
