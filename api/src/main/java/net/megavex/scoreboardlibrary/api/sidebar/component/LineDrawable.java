package net.megavex.scoreboardlibrary.api.sidebar.component;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

/**
 * Something lines can be drawn to
 */
public interface LineDrawable {
  /**
   * Draws a line, or does nothing if has reached the end
   *
   * @param line Line component
   */
  void drawLine(@NotNull Component line);
}
