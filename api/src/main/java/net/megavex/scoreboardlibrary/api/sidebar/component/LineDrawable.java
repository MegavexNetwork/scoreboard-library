package net.megavex.scoreboardlibrary.api.sidebar.component;

import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Something lines can be drawn to line by line. This is either the lines of a sidebar or its title.
 */
@ApiStatus.NonExtendable
public interface LineDrawable {
  /**
   * Draws the next line, or does nothing if it has reached a limit.
   *
   * @param line line component
   */
  default void drawLine(@NotNull ComponentLike line) {
    drawLine(line, null);
  }

  /**
   * Draws the next line with a custom score format, or does nothing if it has reached a limit.
   *
   * @param line        line component
   * @param scoreFormat score format
   */
  void drawLine(@NotNull ComponentLike line, @Nullable ScoreFormat scoreFormat);
}
