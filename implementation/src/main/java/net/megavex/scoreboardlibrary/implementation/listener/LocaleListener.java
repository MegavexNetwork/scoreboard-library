package net.megavex.scoreboardlibrary.implementation.listener;

import java.util.Set;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.team.TeamInfoImpl;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public record LocaleListener(ScoreboardLibraryImpl scoreboardLibrary) implements Listener {
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerLocaleChanged(PlayerLocaleChangeEvent event) {
    var player = event.getPlayer();
    // Need to wait a tick because the locale didn't update yet
    new BukkitRunnable() {
      @Override
      public void run() {
        var slPlayer = scoreboardLibrary.getPlayer(player);
        if (slPlayer != null) {
          var teamManager = slPlayer.teamManager();
          if (teamManager != null) {
            for (var team : teamManager.teams()) {
              var teamInfo = (TeamInfoImpl) team.teamInfo(player);
              teamInfo.packetAdapter().updateTeam(Set.of(player));
            }
          }
        }

        var sidebar = scoreboardLibrary.sidebarMap.get(player);
        if (sidebar instanceof PlayerDependantLocaleSidebar) {
          sidebar.removePlayer(player);
          sidebar.addPlayer(player);
        }
      }
    }.runTaskLater(scoreboardLibrary.plugin, 1);
  }
}
