package net.megavex.scoreboardlibrary.api.sidebar;

import java.util.Collection;
import java.util.Locale;
import net.kyori.adventure.text.Component;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

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
   * Gets a line's value
   *
   * @param line Line
   * @return Value of line
   */
  @Nullable Component line(@Range(from = 0, to = MAX_LINES - 1) int line);

  /**
   * Sets a line's value
   *
   * @param line  Line
   * @param value Value
   */
  void line(@Range(from = 0, to = MAX_LINES - 1) int line, @Nullable Component value);

  /**
   * Clears all lines
   */
  default void clearLines() {
    for (int i = 0; i < maxLines(); i++) {
      line(i, null);
    }
  }

  // Title

  /**
   * @return Current title of this Sidebar
   */
  @NotNull Component title();

  /**
   * Sets the title
   *
   * @param title Title
   */
  void title(@NotNull Component title);

  // Players

  @NotNull Collection<Player> players();

  boolean addPlayer(@NotNull Player player);

  boolean removePlayer(@NotNull Player player);

  default void addPlayers(@NotNull Collection<Player> players) {
    for (var player : players) {
      addPlayer(player);
    }
  }

  default void removePlayers(@NotNull Collection<Player> players) {
    for (var player : players) {
      removePlayer(player);
    }
  }

  // Close

  /**
   * Closes this Sidebar
   */
  void close();

  /**
   * @return Whether this Sidebar is closed
   */
  boolean closed();
}
