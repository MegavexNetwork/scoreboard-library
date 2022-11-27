package net.megavex.scoreboardlibrary.implementation;

import java.util.List;
import java.util.UUID;
import net.megavex.scoreboardlibrary.implementation.commons.CollectionProvider;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerImpl;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerTask;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScoreboardLibraryPlayer {
  private final UUID playerUuid;
  private final List<TeamManagerImpl> teamManagers = CollectionProvider.list(1);

  public ScoreboardLibraryPlayer(@NotNull Player player) {
    this.playerUuid = player.getUniqueId();
  }

  public @Nullable TeamManagerImpl teamManager() {
    if (teamManagers.isEmpty()) {
      return null;
    } else {
      return teamManagers.get(0);
    }
  }

  public boolean isMain(@NotNull TeamManagerImpl teamManager) {
    return teamManager() == teamManager;
  }

  public void addTeamManager(@NotNull TeamManagerImpl teamManager) {
    if (teamManagers.contains(teamManager)) {
      throw new RuntimeException("TeamManager already registered");
    }

    teamManagers.add(teamManager);
  }

  public void removeTeamManager(@NotNull TeamManagerImpl teamManager) {
    if (!teamManagers.remove(teamManager)) {
      throw new RuntimeException("TeamManager not registered");
    }

    var newTeamManager = teamManager();
    if (newTeamManager != null) {
      var player = Bukkit.getPlayer(playerUuid);
      if (player != null) {
        newTeamManager.taskQueue().add(new TeamManagerTask.ShowToPlayer(player));
      }
    }
  }
}
