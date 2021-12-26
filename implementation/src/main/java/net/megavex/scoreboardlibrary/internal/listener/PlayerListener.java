package net.megavex.scoreboardlibrary.internal.listener;

import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProviderImpl;
import net.megavex.scoreboardlibrary.internal.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.internal.team.TeamManagerImpl;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public record PlayerListener(ScoreboardManagerImpl instance) implements Listener {

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        AbstractSidebar sidebar = ScoreboardManagerProviderImpl.instance().sidebarMap.get(player);
        if (sidebar != null) {
            sidebar.removePlayer(player);
        }

        TeamManagerImpl teamManager = ScoreboardManagerProviderImpl.instance().teamManagerMap.get(player);
        if (teamManager != null) {
            teamManager.removePlayer(player);
        }
    }
}
