package net.megavex.scoreboardlibrary.implementation.team;

import java.util.logging.Level;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class TeamUpdaterTask extends BukkitRunnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final Object lock = new Object();

  public TeamUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
    runTaskTimerAsynchronously(scoreboardLibrary.plugin(), 1, 1);
  }

  public @NotNull Object lock() {
    return lock;
  }

  @Override
  public void run() {
    synchronized (lock) {
      for (var teamManager : scoreboardLibrary.teamManagers()) {
        try {
          teamManager.tick();
        } catch (Exception e) {
          scoreboardLibrary.plugin().getLogger().log(Level.SEVERE, "an error occurred while updating a TeamManager instance", e);
        }
      }
    }
  }
}
