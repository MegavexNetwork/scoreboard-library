package net.megavex.scoreboardlibrary.internal.sidebar;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class SidebarUpdaterTask extends BukkitRunnable {

    private final Set<AbstractSidebar> sidebars;

    public SidebarUpdaterTask(ScoreboardManagerImpl manager) {
        Preconditions.checkNotNull(manager);
        this.sidebars = manager.sidebars;

        runTaskTimer(manager.plugin(), 1, 1);
    }

    @Override
    public void run() {
        for (AbstractSidebar sidebar : sidebars) {
            sidebar.update();
        }
    }
}
