package net.megavex.scoreboardlibrary.api.sidebar.renderer;

import net.megavex.scoreboardlibrary.api.sidebar.drawable.LineDrawable;
import org.bukkit.entity.Player;

public interface SidebarComponent<T extends LineDrawable> {
  void draw(Player player, T drawable);
}
