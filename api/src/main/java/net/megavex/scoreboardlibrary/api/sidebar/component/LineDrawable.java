package net.megavex.scoreboardlibrary.api.sidebar.component;

import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.objective.ScoreFormat;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Something lines can be drawn to. This is either the lines of a sidebar or the sidebar title.
 */
@ApiStatus.NonExtendable
public interface LineDrawable {
  /**
   * Draws a line, or does nothing if it has reached the limit.
   *
   * @param line line component
   */
  default void drawLine(@NotNull ComponentLike line) {
    drawLine(line, null);
  }

  /**
   * Draws a line with a custom score format, or does nothing if it has reached the limit.
   *
   * @param line        line component
   * @param scoreFormat score format
   */
  void drawLine(@NotNull ComponentLike line, @Nullable ScoreFormat scoreFormat);
}
