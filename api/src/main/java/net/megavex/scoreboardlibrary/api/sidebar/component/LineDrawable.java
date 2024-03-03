package net.megavex.scoreboardlibrary.api.sidebar.component;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

/**
 * Something lines can be drawn to. This is either the lines of a sidebar or the sidebar title
 */
@ApiStatus.NonExtendable
public interface LineDrawable {
  /**
   * Draws a line, or does nothing if it has reached the limit
   *
   * @param line Line component
   */
  void drawLine(@NotNull Component line);
}
