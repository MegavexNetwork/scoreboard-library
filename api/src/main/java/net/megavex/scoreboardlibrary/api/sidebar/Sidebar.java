package net.megavex.scoreboardlibrary.api.sidebar;

import java.util.Locale;
import javax.annotation.concurrent.NotThreadSafe;
import net.kyori.adventure.text.Component;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.interfaces.Players;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@ApiStatus.NonExtendable
@NotThreadSafe
public interface Sidebar extends HasScoreboardLibrary, Closeable, Players {
  // Constants

  int MAX_LINES = 15;

  // Main

  /**
   * Gets the max amount of lines this sidebar can have
   *
   * @return Max line amount
   */
  @Range(from = 1, to = MAX_LINES) int maxLines();

  /**
   * Gets the {@link Locale} which is used to translate {@link net.kyori.adventure.text.TranslatableComponent}s
   *
   * @return Locale
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
}
