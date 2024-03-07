package net.megavex.scoreboardlibrary.api.sidebar.renderer;

import net.kyori.adventure.text.ComponentLike;
import net.megavex.scoreboardlibrary.api.sidebar.drawable.LineDrawable;
import org.bukkit.entity.Player;

public interface SingleLineComponent extends SidebarComponent {
  ComponentLike line(Player viewer);

  @Override
  default void draw(Player player, LineDrawable drawable) {
    drawable.drawLine(line(player));
  }
}
