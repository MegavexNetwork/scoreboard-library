package net.megavex.scoreboardlibrary.api.util;

import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SidebarUtilities {

  private SidebarUtilities() {
  }

  public static void checkLineBounds(int line) {
    if (line > Sidebar.MAX_LINES || line < 0) {
      throw new IndexOutOfBoundsException("Invalid line: " + line);
    }
  }
}
