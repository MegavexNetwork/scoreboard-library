package net.megavex.scoreboardlibrary.implementation.team;

import java.util.logging.Level;
import net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl;
import org.bukkit.scheduler.BukkitRunnable;
import org.jetbrains.annotations.NotNull;

public class TeamUpdaterTask extends BukkitRunnable {
  private final ScoreboardLibraryImpl scoreboardLibrary;

  public TeamUpdaterTask(@NotNull ScoreboardLibraryImpl scoreboardLibrary) {
    this.scoreboardLibrary = scoreboardLibrary;
    runTaskTimerAsynchronously(scoreboardLibrary.plugin(), 1, 1);
  }

  @Override
  public void run() {
    for (var teamManager : scoreboardLibrary.mutableTeamManagers()) {
      try {
        teamManager.tick();
      } catch (Exception e) {
        scoreboardLibrary.plugin().getLogger().log(Level.SEVERE, "an error occurred while updating a TeamManager", e);
      }
    }
  }
}
