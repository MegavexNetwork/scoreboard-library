package net.megavex.scoreboardlibrary.implementation.team;

import com.google.common.base.Preconditions;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import org.bukkit.scheduler.BukkitRunnable;

public class TeamUpdaterTask extends BukkitRunnable {
  private final Logger logger;
  private final Set<TeamManagerImpl> teamManagers;

  public TeamUpdaterTask(ScoreboardLibraryImpl manager) {
    Preconditions.checkNotNull(manager);
    this.logger = manager.plugin().getLogger();
    this.teamManagers = manager.teamManagers;

    runTaskTimerAsynchronously(manager.plugin(), 1, 1);
  }

  @Override
  public void run() {
    for (var teamManager : teamManagers) {
      try {
        teamManager.update();
      } catch (Exception e) {
        logger.log(Level.WARNING, "Exception caught when updating TeamManager", e);
      }
    }
  }
}
