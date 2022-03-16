package net.megavex.scoreboardlibrary.internal.listener;

import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import net.megavex.scoreboardlibrary.internal.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.internal.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.internal.team.ScoreboardTeamImpl;
import net.megavex.scoreboardlibrary.internal.team.TeamInfoImpl;
import net.megavex.scoreboardlibrary.internal.team.TeamManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLocaleChangeEvent;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;

public record LocaleListener(ScoreboardManagerImpl instance) implements Listener {

  @EventHandler(ignoreCancelled = true, priority = EventPriority.MONITOR)
  public void onPlayerLocaleChanged(PlayerLocaleChangeEvent event) {
    Player player = event.getPlayer();
    // Need to wait a tick because the locale didn't update yet
    new BukkitRunnable() {
      @Override
      public void run() {
        TeamManagerImpl teamManager = ScoreboardManagerProviderImpl.instance().teamManager(player);
        if (teamManager != null) {
          for (ScoreboardTeamImpl team : teamManager.teams.values()) {
            TeamInfoImpl teamInfo = team.teamInfo(player);
            teamInfo.nms.updateTeam(Collections.singleton(player));
          }
        }

        AbstractSidebar sidebar = ScoreboardManagerProviderImpl.instance().sidebarMap.get(player);
        if (sidebar instanceof PlayerDependantLocaleSidebar) {
          sidebar.removePlayer(player);
          sidebar.addPlayer(player);
        }
      }
    }.runTaskLater(instance.plugin, 1);
  }
}
