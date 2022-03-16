package net.megavex.scoreboardlibrary.api.sidebar.line;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;

public interface SidebarLine {

  static @NotNull SidebarLine staticLine(@NotNull Component component) {
    return new StaticLine(component);
  }

  default boolean lineStatic() {
    return true;
  }

  /**
   * Gets the line index
   *
   * @return Line index
   * @throws UnsupportedOperationException If this is a static line
   */
  default int line() {
    throw new UnsupportedOperationException();
  }

  Component computeValue();
}
