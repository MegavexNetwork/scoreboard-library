package net.megavex.scoreboardlibrary.implementation;

import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerImpl;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.UUID;

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
      Player player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        teamManager.show(player);
      }
    }
  }

  public synchronized void removeTeamManager(@NotNull TeamManagerImpl teamManager) {
    if (!teamManagers.remove(teamManager)) {
      throw new RuntimeException("TeamManager not registered");
    }

    TeamManagerImpl newTeamManager = teamManager();
    if (newTeamManager != null) {
      Player player = Bukkit.getPlayer(playerUuid);
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
      Player player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        sidebar.show(player);
      }
    }
  }

  public synchronized void removeSidebar(@NotNull AbstractSidebar sidebar) {
    if (!sidebars.remove(sidebar)) {
      throw new RuntimeException("Sidebar not registered");
    }

    @Nullable AbstractSidebar newSidebar = sidebar();
    if (newSidebar != null) {
      Player player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        newSidebar.show(player);
      }
    }
  }
}
