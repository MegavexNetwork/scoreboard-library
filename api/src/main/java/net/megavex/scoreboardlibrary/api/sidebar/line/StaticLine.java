package net.megavex.scoreboardlibrary.api.sidebar.line;

import net.kyori.adventure.text.Component;
import org.jetbrains.annotations.Nullable;

record StaticLine(@Nullable Component value) implements SidebarLine {
  @Override
  public Component computeValue() {
    return value;
  }
}
