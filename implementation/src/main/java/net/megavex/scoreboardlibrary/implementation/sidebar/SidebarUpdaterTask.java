package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.logging.Level;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import net.megavex.scoreboardlibrary.implementation.util.DispatchUtil;
import org.jetbrains.annotations.NotNull;

public class SidebarUpdaterTask implements Runnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final DispatchUtil.RunningTask task;
  private final Object lock = new Object();

  public SidebarUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
    this.task = DispatchUtil.runEveryTick(scoreboardLibrary.plugin(), this);
  }

  public @NotNull DispatchUtil.RunningTask task() {
    return task;
  }

  public @NotNull Object lock() {
    return lock;
  }

  @Override
  public void run() {
    synchronized (lock) {
      for (var sidebar : scoreboardLibrary.sidebars()) {
        try {
          sidebar.tick();
        } catch (Exception e) {
          scoreboardLibrary.plugin().getLogger().log(Level.WARNING, "an error occurred while updating a Sidebar instance", e);
        }
      }
    }
  }
}
