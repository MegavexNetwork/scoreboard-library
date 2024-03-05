package net.megavex.scoreboardlibrary.api.sidebar;

import net.kyori.adventure.text.Component;
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
   * @return Max amount of lines this sidebar can have
   */
  @Range(from = 1, to = Integer.MAX_VALUE) int maxLines();

  /**
   * @return Locale which is used for translating {@link net.kyori.adventure.text.TranslatableComponent}s,
   * or null if it depends on each player's client locale.
   * @see ScoreboardLibrary#createSidebar(int, Locale)
   */
  @Nullable Locale locale();

  // Lines

  /**
   * Gets a line's value.
   *
   * @param line Line index
   * @return Value of line, or null if unset
   */
  @Nullable Component line(@Range(from = 0, to = Integer.MAX_VALUE - 1) int line);

  /**
   * Sets a line's value.
   *
   * @param index Line index
   * @param value New value, or null to hide
   */
  default void line(@Range(from = 0, to = Integer.MAX_VALUE - 1) int index, @Nullable Component value) {
    line(index, value, null);
  }

  /**
   * Sets a line's value with a custom score format.
   * Note that custom score formats are only supported in Minecraft 1.20.3+.
   *
   * @param index       Line index
   * @param value       New value, or null to hide
   * @param scoreFormat Score format
   */
  void line(
    @Range(from = 0, to = Integer.MAX_VALUE - 1) int index,
    @Nullable Component value,
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
   * @return Title of the sidebar, defaults to {@link Component#empty}
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
   * @return Unmodifiable collection of viewers in this Sidebar
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
   * @param player Player to add
   * @return Whether the player was added
   */
  boolean addPlayer(@NotNull Player player);

  /**
   * Adds multiple viewers to this Sidebar.
   *
   * @param players Viewers to add
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
   * @param player Viewer to remove
   * @return Whether the viewer was removed
   */
  boolean removePlayer(@NotNull Player player);

  /**
   * Removes multiple viewers from this Sidebar
   *
   * @param players Viewers to remove
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
