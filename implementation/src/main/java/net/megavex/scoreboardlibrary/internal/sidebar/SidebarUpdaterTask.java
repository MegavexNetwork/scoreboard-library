package net.megavex.scoreboardlibrary.internal.sidebar;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class SidebarUpdaterTask extends BukkitRunnable {

  private final Logger logger;
  private final Set<AbstractSidebar> sidebars;

  public SidebarUpdaterTask(ScoreboardManagerImpl manager) {
    Preconditions.checkNotNull(manager);
    this.logger = manager.plugin().getLogger();
    this.sidebars = manager.sidebars;

    runTaskTimerAsynchronously(manager.plugin(), 1, 1);
  }

  @Override
  public void run() {
    for (var sidebar : sidebars) {
      try {
        sidebar.update();
      } catch (Exception e) {
        logger.log(Level.WARNING, "Exception caught when updating Sidebar", e);
      }
    }
  }
}
