package net.megavex.scoreboardlibrary.internal.team;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class TeamUpdaterTask extends BukkitRunnable {

    private final Logger logger;
    private final Set<TeamManagerImpl> teamManagers;

    public TeamUpdaterTask(ScoreboardManagerImpl manager) {
        Preconditions.checkNotNull(manager);
        this.logger = manager.plugin().getLogger();
        this.teamManagers = manager.teamManagers;

        runTaskTimerAsynchronously(manager.plugin(), 1, 1);
    }

    @Override
    public void run() {
        for (TeamManagerImpl teamManager : teamManagers) {
            try {
                teamManager.update();
            } catch (Exception e) {
                logger.log(Level.WARNING, "Exception caught when updating TeamManager", e);
            }
        }
    }
}
