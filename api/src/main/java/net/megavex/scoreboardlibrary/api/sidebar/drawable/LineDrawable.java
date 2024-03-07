package net.megavex.scoreboardlibrary.api.sidebar.drawable;

import net.kyori.adventure.text.ComponentLike;

public interface LineDrawable {
  boolean canDraw();

  void drawLine(ComponentLike line);
}
