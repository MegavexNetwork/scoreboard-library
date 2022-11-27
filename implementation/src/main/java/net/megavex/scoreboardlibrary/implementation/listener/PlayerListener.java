package net.megavex.scoreboardlibrary.implementation.listener;

import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerListener(ScoreboardLibraryImpl scoreboardLibrary) implements Listener {
  @EventHandler(priority = EventPriority.MONITOR)
  void onPlayerQuit(PlayerQuitEvent event) {
    var player = event.getPlayer();
    var sidebar = scoreboardLibrary.sidebarMap.get(player);
    if (sidebar != null) {
      sidebar.removePlayer(player);
    }
  }
}
