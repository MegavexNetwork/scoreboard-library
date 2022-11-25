package net.megavex.scoreboardlibrary.api.noop;

import com.google.common.base.Preconditions;
import java.util.Collection;
import java.util.Collections;
import java.util.Locale;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

/**
 * no-op implementation of ScoreboardLibrary.
 * Can be used as a fallback when there is no packet adapter available
 */
public class NoopScoreboardLibrary implements ScoreboardLibrary {
  private final Plugin plugin;
  private final Set<Sidebar> sidebars = ConcurrentHashMap.newKeySet();
  private final Set<TeamManager> teamManagers = ConcurrentHashMap.newKeySet();
  private boolean closed = false;

  public NoopScoreboardLibrary(@NotNull Plugin plugin) {
    Preconditions.checkNotNull(plugin);
    this.plugin = plugin;
  }

  @Override
  public @NotNull Plugin plugin() {
    return plugin;
  }

  @Override
  public @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @Nullable Locale locale) {
    return new NoopSidebar(this, maxLines, locale);
  }

  @Override
  public @NotNull Collection<Sidebar> sidebars() {
    return closed ? Set.of():Collections.unmodifiableSet(sidebars);
  }

  @Override
  public @NotNull TeamManager teamManager() {
    return new NoopTeamManager(this);
  }

  @Override
  public @NotNull Collection<TeamManager> teamManagers() {
    return closed ? Set.of():Collections.unmodifiableSet(teamManagers);
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
