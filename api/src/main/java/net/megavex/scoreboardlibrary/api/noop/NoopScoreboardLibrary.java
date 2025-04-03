package net.megavex.scoreboardlibrary.api.noop;

import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.util.Locale;

/**
 * No-op implementation of {@link ScoreboardLibrary}.
 * Can be used as a fallback when there is no packet adapter available
 * and for unit testing.
 */
public final class NoopScoreboardLibrary implements ScoreboardLibrary {
  private boolean closed = false;

  @Override
  public @NotNull Sidebar createSidebar(@Range(from = 1, to = Integer.MAX_VALUE) int maxLines,  @Nullable Locale locale, @NotNull String objectiveName) {
    checkClosed();
    return new NoopSidebar(maxLines, objectiveName, locale);
  }

  @Override
  public @NotNull TeamManager createTeamManager() {
    checkClosed();
    return new NoopTeamManager();
  }

  @Override
  public @NotNull ObjectiveManager createObjectiveManager() {
    checkClosed();
    return new NoopObjectiveManager();
  }

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public boolean closed() {
    return closed;
  }

  private void checkClosed() {
    if (closed) {
      throw new IllegalStateException("NoopScoreboardLibrary is closed");
    }
  }
}
