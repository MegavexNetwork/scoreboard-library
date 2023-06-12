package net.megavex.scoreboardlibrary.implementation.team;

import java.util.logging.Level;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.scheduler.RunningTask;
import org.jetbrains.annotations.NotNull;

public class TeamUpdaterTask implements Runnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final RunningTask task;
  private final Object lock = new Object();

  public TeamUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
    this.task = scoreboardLibrary.taskScheduler().runEveryTick(this);
  }

  public @NotNull RunningTask task() {
    return task;
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
