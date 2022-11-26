package net.megavex.scoreboardlibrary.api.util;

import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public final class SidebarUtilities {
  private SidebarUtilities() {
  }

  public static void checkLineBounds(int maxLines, int line) {
    if (line >= maxLines || line < 0) {
      throw new IndexOutOfBoundsException("invalid line " + line);
    }
  }
}
