package net.megavex.scoreboardlibrary.api.sidebar;

import org.bukkit.entity.Player;

public interface Sidebar {
  /**
   * The max amount of lines a vanilla client can display at once.
   */
  int MAX_LINES = 15;

  void addViewer(Player player);

  void removeViewer(Player player);

  void refresh(Player player, RefreshScope scope);

  void refresh(Iterable<Player> players, RefreshScope scope);

  void refresh(RefreshScope scope);
}
