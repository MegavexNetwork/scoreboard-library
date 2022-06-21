package net.megavex.scoreboardlibrary.internal.listener;

import java.util.Set;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import net.megavex.scoreboardlibrary.internal.sidebar.PlayerDependantLocaleSidebar;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

public record LocaleListener(ScoreboardManagerImpl instance) implements Listener {
  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerLocaleChanged(PlayerLocaleChangeEvent event) {
    var player = event.getPlayer();
    // Need to wait a tick because the locale didn't update yet
    new BukkitRunnable() {
      @Override
      public void run() {
        var teamManager = ScoreboardManagerProviderImpl.instance().teamManager(player);
        if (teamManager != null) {
          for (var team : teamManager.teams.values()) {
            var teamInfo = team.teamInfo(player);
            teamInfo.nms.updateTeam(Set.of(player));
          }
        }

        var sidebar = ScoreboardManagerProviderImpl.instance().sidebarMap.get(player);
        if (sidebar instanceof PlayerDependantLocaleSidebar) {
          sidebar.removePlayer(player);
          sidebar.addPlayer(player);
        }
      }
    }.runTaskLater(instance.plugin, 1);
  }
}
