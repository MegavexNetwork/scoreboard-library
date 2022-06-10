package net.megavex.scoreboardlibrary.internal;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import java.util.List;
import net.megavex.scoreboardlibrary.api.ScoreboardManager;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.internal.listener.LocaleListener;
import net.megavex.scoreboardlibrary.internal.listener.PlayerListener;
import net.megavex.scoreboardlibrary.internal.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.internal.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.internal.sidebar.SidebarUpdaterTask;
import net.megavex.scoreboardlibrary.internal.sidebar.SingleLocaleSidebar;
import net.megavex.scoreboardlibrary.internal.team.TeamManagerImpl;
import net.megavex.scoreboardlibrary.internal.team.TeamUpdaterTask;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ScoreboardManagerImpl implements ScoreboardManager {

  public final JavaPlugin plugin;
  public final PlayerListener playerListener = new PlayerListener(this);
  public LocaleListener localeListener;
  public boolean closed;
  public TeamUpdaterTask teamTask;
  public SidebarUpdaterTask sidebarTask;
  public Set<TeamManagerImpl> teamManagers;
  public Set<AbstractSidebar> sidebars;

  public ScoreboardManagerImpl(JavaPlugin plugin) {
    Preconditions.checkNotNull(plugin, "JavaPlugin cannot be null");
    this.plugin = plugin;

    plugin.getServer().getPluginManager().registerEvents(playerListener, plugin);

    try {
      Class.forName("org.bukkit.event.player.PlayerLocaleChangeEvent");
      ScoreboardLibraryLogger.logMessage("PlayerLocaleChangeEvent was found");
      localeListener = new LocaleListener(this);
      plugin.getServer().getPluginManager().registerEvents(localeListener, plugin);
    } catch (ClassNotFoundException ignored) {
    }
  }

  @Override
  public @NotNull Plugin plugin() {
    return plugin;
  }

  @Override
  public @NotNull Sidebar sidebar(int maxLines, @NotNull ComponentTranslator componentTranslator, @Nullable Locale locale) {
    checkDestroyed();
    getSidebars0();

    if (maxLines < 0 || maxLines > Sidebar.MAX_LINES) {
      throw new IllegalArgumentException();
    }

    AbstractSidebar sidebar;
    if (locale == null) {
      sidebar = new PlayerDependantLocaleSidebar(this, componentTranslator, maxLines);
    } else {
      sidebar = new SingleLocaleSidebar(this, componentTranslator, maxLines, locale);
    }

    sidebars.add(sidebar);
    return sidebar;
  }

  @Override
  public @NotNull TeamManagerImpl teamManager(@NotNull ComponentTranslator componentTranslator) {
    checkDestroyed();
    getTeamManagers0();

    var teamManager = new TeamManagerImpl(this, componentTranslator);
    teamManagers.add(teamManager);
    return teamManager;
  }

  @Override
  public void close() {
    if (closed)
      return;

    closed = true;

    ScoreboardManagerProviderImpl.instance().scoreboardManagerMap.remove(plugin);
    HandlerList.unregisterAll(playerListener);

    if (teamManagers != null) {
      teamTask.cancel();
      for (var teamManager : teamManagers) {
        teamManager.close();
      }
    }

    if (sidebars != null) {
      sidebarTask.cancel();
      for (var sidebar : List.copyOf(sidebars)) {
        sidebar.close();
      }
    }
  }

  @Override
  public boolean closed() {
    return closed;
  }

  public Set<TeamManagerImpl> getTeamManagers0() {
    if (teamManagers == null) {
      teamManagers = ConcurrentHashMap.newKeySet(4);
      teamTask = new TeamUpdaterTask(this);
    }
    return teamManagers;
  }

  public Set<AbstractSidebar> getSidebars0() {
    if (sidebars == null) {
      sidebars = ConcurrentHashMap.newKeySet(4);
      sidebarTask = new SidebarUpdaterTask(this);
    }
    return sidebars;
  }

  @Override
  public Set<TeamManager> teamManagers() {
    return Collections.unmodifiableSet(getTeamManagers0());
  }

  @Override
  public @NotNull Set<Sidebar> sidebars() {
    return Collections.unmodifiableSet(getSidebars0());
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    var that = (ScoreboardManagerImpl) o;
    return closed == that.closed &&
      Objects.equals(plugin, that.plugin) &&
      Objects.equals(teamTask, that.teamTask) &&
      Objects.equals(sidebarTask, that.sidebarTask) &&
      Objects.equals(teamManagers, that.teamManagers) &&
      Objects.equals(sidebars, that.sidebars);
  }

  @Override
  public int hashCode() {
    return plugin.getName().hashCode();
  }

  private void checkDestroyed() {
    Preconditions.checkState(!closed, "ScoreboardManager is closed");
  }
}
