package net.megavex.scoreboardlibrary.implementation;

import com.google.common.base.Preconditions;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import net.megavex.scoreboardlibrary.api.ScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.exception.PacketAdapterNotFoundException;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.implementation.commons.LocaleProvider;
import net.megavex.scoreboardlibrary.implementation.listener.LocaleListener;
import net.megavex.scoreboardlibrary.implementation.listener.PlayerListener;
import net.megavex.scoreboardlibrary.implementation.nms.base.ScoreboardLibraryPacketAdapter;
import net.megavex.scoreboardlibrary.implementation.sidebar.AbstractSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.PlayerDependantLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.sidebar.SidebarUpdaterTask;
import net.megavex.scoreboardlibrary.implementation.sidebar.SingleLocaleSidebar;
import net.megavex.scoreboardlibrary.implementation.team.TeamManagerImpl;
import net.megavex.scoreboardlibrary.implementation.team.TeamUpdaterTask;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ScoreboardLibraryImpl implements ScoreboardLibrary {
  public final JavaPlugin plugin;
  public final boolean debug;
  public final ScoreboardLibraryPacketAdapter<?> packetAdapter;
  public final LocaleProvider localeProvider;
  public final PlayerListener playerListener = new PlayerListener(this);

  public final Map<Player, AbstractSidebar> sidebarMap = new ConcurrentHashMap<>();
  public final Map<Player, TeamManagerImpl> teamManagerMap = new ConcurrentHashMap<>();

  public LocaleListener localeListener;
  public boolean closed;
  public TeamUpdaterTask teamTask;
  public SidebarUpdaterTask sidebarTask;
  public volatile Set<TeamManagerImpl> teamManagers;
  public volatile Set<AbstractSidebar> sidebars;

  private final Object lock = new Object();

  public ScoreboardLibraryImpl(JavaPlugin plugin, boolean debug) throws PacketAdapterNotFoundException {
    Preconditions.checkNotNull(plugin, "plugin");

    try {
      Class.forName("net.kyori.adventure.Adventure");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("Adventure is not in the classpath");
    }

    this.plugin = plugin;
    this.debug = debug;
    this.packetAdapter = PacketAdapterLoader.loadPacketAdapter();
    this.localeProvider = this.packetAdapter.localeProvider;

    plugin.getServer().getPluginManager().registerEvents(this.playerListener, plugin);

    try {
      Class.forName("org.bukkit.event.player.PlayerLocaleChangeEvent");
      debug("Registering PlayerLocaleChangeEvent listener");
      this.localeListener = new LocaleListener(this);
      plugin.getServer().getPluginManager().registerEvents(this.localeListener, plugin);
    } catch (ClassNotFoundException ignored) {
    }
  }

  public void debug(@NotNull String message) {
    if (debug) {
      plugin.getLogger().info("[scoreboard-library] " + message);
    }
  }

  @Override
  public @NotNull Plugin plugin() {
    return plugin;
  }

  @Override
  public @NotNull Sidebar sidebar(int maxLines, @NotNull ComponentTranslator componentTranslator, @Nullable Locale locale) {
    checkClosed();

    if (maxLines <= 0 || maxLines > Sidebar.MAX_LINES) {
      throw new IllegalArgumentException("maxLines");
    }

    AbstractSidebar sidebar;
    if (locale == null) {
      sidebar = new PlayerDependantLocaleSidebar(this, componentTranslator, maxLines);
    } else {
      sidebar = new SingleLocaleSidebar(this, componentTranslator, maxLines, locale);
    }

    getSidebars0().add(sidebar);
    return sidebar;
  }

  @Override
  public @NotNull TeamManagerImpl teamManager(@NotNull ComponentTranslator componentTranslator) {
    checkClosed();

    var teamManager = new TeamManagerImpl(this, componentTranslator);
    getTeamManagers0().add(teamManager);
    return teamManager;
  }

  @Override
  public void close() {
    if (closed)
      return;

    closed = true;

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
    if (this.teamManagers == null) {
      synchronized (this.lock) {
        if (this.teamManagers == null) {
          this.teamManagers = ConcurrentHashMap.newKeySet(4);
          this.teamTask = new TeamUpdaterTask(this);
        }
      }
    }

    return this.teamManagers;
  }

  public Set<AbstractSidebar> getSidebars0() {
    if (this.sidebars == null) {
      synchronized (this.lock) {
        if (this.sidebars == null) {
          this.sidebars = ConcurrentHashMap.newKeySet(4);
          this.sidebarTask = new SidebarUpdaterTask(this);
        }
      }
    }

    return this.sidebars;
  }

  @Override
  public @NotNull Set<TeamManager> teamManagers() {
    return teamManagers == null ? Set.of():Collections.unmodifiableSet(teamManagers);
  }

  @Override
  public @NotNull Set<Sidebar> sidebars() {
    return sidebars == null ? Set.of():Collections.unmodifiableSet(sidebars);
  }

  private void checkClosed() {
    Preconditions.checkState(!closed, "ScoreboardLibrary is closed");
  }
}
