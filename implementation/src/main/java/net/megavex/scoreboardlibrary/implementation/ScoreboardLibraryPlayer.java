package net.megavex.scoreboardlibrary.implementation;

import java.util.List;
import java.util.UUID;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScoreboardLibraryPlayer {
  private final UUID playerUuid;
  private final List<TeamManagerImpl> teamManagers = CollectionProvider.list(1);
  private final List<AbstractSidebar> sidebars = CollectionProvider.list(1);

  public ScoreboardLibraryPlayer(@NotNull Player player) {
    this.playerUuid = player.getUniqueId();
  }

  public synchronized @Nullable TeamManagerImpl teamManager() {
    if (teamManagers.isEmpty()) {
      return null;
    } else {
      return teamManagers.get(0);
    }
  }

  public synchronized void addTeamManager(@NotNull TeamManagerImpl teamManager) {
    if (teamManagers.contains(teamManager)) {
      throw new RuntimeException("TeamManager already registered");
    }

    teamManagers.add(teamManager);

    if (teamManager() == teamManager) {
      var player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        teamManager.show(player);
      }
    }
  }

  public synchronized void removeTeamManager(@NotNull TeamManagerImpl teamManager) {
    if (!teamManagers.remove(teamManager)) {
      throw new RuntimeException("TeamManager not registered");
    }

    var newTeamManager = teamManager();
    if (newTeamManager != null) {
      var player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        newTeamManager.show(player);
      }
    }
  }

  public @Nullable AbstractSidebar sidebar() {
    if (sidebars.isEmpty()) {
      return null;
    } else {
      return sidebars.get(0);
    }
  }

  public synchronized void addSidebar(@NotNull AbstractSidebar sidebar) {
    if (sidebars.contains(sidebar)) {
      throw new RuntimeException("Sidebar already registered");
    }

    sidebars.add(sidebar);

    if (sidebar() == sidebar) {
      var player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        sidebar.show(player);
      }
    }
  }

  public synchronized void removeSidebar(@NotNull AbstractSidebar sidebar) {
    if (!sidebars.remove(sidebar)) {
      throw new RuntimeException("Sidebar not registered");
    }

    var newSidebar = sidebar();
    if (newSidebar != null) {
      var player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        newSidebar.show(player);
      }
    }
  }
}
