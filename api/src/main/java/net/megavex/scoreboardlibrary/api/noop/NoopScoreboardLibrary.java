package net.megavex.scoreboardlibrary.api.noop;

import java.util.Locale;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * no-op implementation of ScoreboardLibrary.
 * Can be used as a fallback when there is no packet adapter available
 */
public final class NoopScoreboardLibrary implements ScoreboardLibrary {
  private boolean closed = false;

  @Override
  public @NotNull Sidebar createSidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @Nullable Locale locale) {
    return new NoopSidebar(maxLines, locale);
  }

  @Override
  public @NotNull TeamManager createTeamManager() {
    return new NoopTeamManager();
  }

  @Override
  public void close() {
    closed = true;
  }

  @Override
  public boolean closed() {
    return closed;
  }
}
