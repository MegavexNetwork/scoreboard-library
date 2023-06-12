package net.megavex.scoreboardlibrary.api;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;
import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

@ApiStatus.NonExtendable
public interface ScoreboardLibrary {
  static @NotNull ScoreboardLibrary loadScoreboardLibrary(@NotNull Plugin plugin) throws NoPacketAdapterAvailableException {
    Class<?> clazz;
    try {
      clazz = Class.forName("net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl");
    } catch (ClassNotFoundException e) {
      throw new RuntimeException("scoreboard-library implementation is not shaded into the classpath");
    }

    try {
      return (ScoreboardLibrary) clazz.getDeclaredConstructor(Plugin.class).newInstance(plugin);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      if (e instanceof InvocationTargetException invocationTargetException) {
        if (invocationTargetException.getTargetException() instanceof NoPacketAdapterAvailableException noPacketAdapterAvailableException) {
          throw noPacketAdapterAvailableException;
        }
      }

      throw new RuntimeException("failed to load scoreboard-library implementation", e);
    }
  }

  /**
   * Creates a {@link Sidebar}
   *
   * @return Sidebar
   */
  default @NotNull Sidebar createSidebar() {
    return createSidebar(Sidebar.MAX_LINES, null);
  }

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines Max sidebar lines
   * @return Sidebar
   */
  default @NotNull Sidebar createSidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines) {
    return createSidebar(maxLines, null);
  }

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines Max sidebar lines
   * @param locale   Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s
   *                 or null if the locale should depend on the player
   * @return Sidebar
   */
  @NotNull Sidebar createSidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @Nullable Locale locale);

  /**
   * Creates a {@link TeamManager}
   *
   * @return TeamManager
   */
  @NotNull TeamManager createTeamManager();

  /**
   * Closes this ScoreboardLibrary instance.
   * Should always be called when the plugin is disabled
   */
  void close();

  /**
   * @return Whether this ScoreboardLibrary instance is closed
   */
  boolean closed();
}
