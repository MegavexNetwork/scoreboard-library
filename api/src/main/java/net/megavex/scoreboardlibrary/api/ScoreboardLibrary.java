package net.megavex.scoreboardlibrary.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Collection;
import java.util.Locale;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardLibrary;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@ApiStatus.NonExtendable
public interface ScoreboardLibrary extends Closeable, HasScoreboardLibrary {
  static @NotNull ScoreboardLibrary loadScoreboardLibrary(@NotNull Plugin plugin) throws NoPacketAdapterAvailableException {
    return loadScoreboardLibrary(plugin, false);
  }

  static @NotNull ScoreboardLibrary loadScoreboardLibrary(@NotNull Plugin plugin, boolean debug) throws NoPacketAdapterAvailableException {
    Class<?> clazz;
    try {
      clazz = Class.forName("net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("scoreboard-library implementation is not shaded into the classpath");
    }

    try {
      return (ScoreboardLibrary) clazz.getDeclaredConstructor(Plugin.class, boolean.class).newInstance(plugin, debug);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      if (e instanceof InvocationTargetException invocationTargetException) {
        if (invocationTargetException.getTargetException() instanceof NoPacketAdapterAvailableException adapterNotFoundException) {
          throw adapterNotFoundException;
        }
      }

      throw new RuntimeException("failed to load scoreboard-library implementation", e);
    }
  }

  @Override
  default @NotNull ScoreboardLibrary scoreboardLibrary() {
    return this;
  }

  /**
   * Gets the Plugin owner of this ScoreboardManager
   *
   * @return Plugin owner
   */
  @NotNull Plugin plugin();

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines Max sidebar lines
   * @return Sidebar
   */
  default @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines) {
    return sidebar(maxLines, null);
  }

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines Max sidebar lines
   * @param locale   Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s
   *                 or null if the locale should depend on the player
   * @return Sidebar
   */
  @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @Nullable Locale locale);

  /**
   * Gets the sidebars associated with this ScoreboardManager
   *
   * @return Sidebars
   */
  @NotNull Collection<Sidebar> sidebars();

  /**
   * Creates a {@link TeamManager} with the global component translator
   *
   * @return TeamManager
   */
  @NotNull TeamManager teamManager();

  /**
   * Gets the team managers associated with this {@link JavaPlugin}
   *
   * @return Team Managers
   */
  @NotNull Collection<TeamManager> teamManagers();
}
