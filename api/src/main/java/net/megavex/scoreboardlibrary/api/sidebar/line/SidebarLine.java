package net.megavex.scoreboardlibrary.api.sidebar.line;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface SidebarLine {

  static @NotNull SidebarLine staticLine(@Nullable Component component) {
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

  @Nullable Component computeValue();
}
