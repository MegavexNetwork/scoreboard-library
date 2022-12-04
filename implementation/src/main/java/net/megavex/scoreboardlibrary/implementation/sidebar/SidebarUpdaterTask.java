package net.megavex.scoreboardlibrary.implementation.sidebar;

import java.util.logging.Level;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class SidebarUpdaterTask extends BukkitRunnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;

  public SidebarUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
    runTaskTimerAsynchronously(scoreboardLibrary.plugin(), 1, 1);
  }

  @Override
  public void run() {
    for (var sidebar : scoreboardLibrary.mutableSidebars()) {
      try {
        sidebar.tick();
      } catch (Exception e) {
        scoreboardLibrary.plugin().getLogger().log(Level.WARNING, "an error occurred while updating a Sidebar", e);
      }
    }
  }
}
