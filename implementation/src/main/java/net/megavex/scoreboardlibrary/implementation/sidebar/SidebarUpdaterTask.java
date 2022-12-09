package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.logging.Level;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class SidebarUpdaterTask extends BukkitRunnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;
  private final Object lock = new Object();

  public SidebarUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
    runTaskTimerAsynchronously(scoreboardLibrary.plugin(), 1, 1);
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
