package net.megavex.scoreboardlibrary.internal.team;

import com.google.common.base.Preconditions;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerImpl;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public class TeamUpdaterTask extends BukkitRunnable {

    private final Set<TeamManagerImpl> teamManagers;

    public TeamUpdaterTask(ScoreboardManagerImpl manager) {
        Preconditions.checkNotNull(manager);
        this.teamManagers = manager.teamManagers;

        runTaskTimer(manager.plugin(), 1, 1);
    }

    @Override
    public void run() {
        for (TeamManagerImpl teamManager : teamManagers) {
            teamManager.update();
        }
    }
}
