package net.megavex.scoreboardlibrary.api;

import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.plugin.Plugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

/**
 * Allows creating {@link Sidebar}s and {@link TeamManager}s.
 * Each plugin should have it's own instance of this interface.
 * Note: this class is thread-safe.
 */
@ApiStatus.NonExtendable
public interface ScoreboardLibrary {
  /**
   * Creates an instance of {@link ScoreboardLibrary}.
   *
   * @param plugin The plugin that owns this instance
   * @return A new instance of {@link ScoreboardLibrary}
   * @throws NoPacketAdapterAvailableException if there is no packet adapter available in the classpath
   */
  static @NotNull ScoreboardLibrary loadScoreboardLibrary(@NotNull Plugin plugin) throws NoPacketAdapterAvailableException {
    Class<?> implClass;
    try {
      implClass = Class.forName("net.megavex.scoreboardlibrary.implementation.ScoreboardLibraryImpl");
    } catch (ClassNotFoundException e) {
      throw new IllegalStateException("scoreboard-library implementation is not shaded into the classpath");
    }

    try {
      return (ScoreboardLibrary) implClass.getDeclaredConstructor(Plugin.class).newInstance(plugin);
    } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
      if (e instanceof InvocationTargetException) {
        Throwable targetException = ((InvocationTargetException) e).getTargetException();
        if (targetException instanceof NoPacketAdapterAvailableException) {
          throw (NoPacketAdapterAvailableException) targetException;
        }
      }

      throw new RuntimeException("failed to load scoreboard-library implementation", e);
    }
  }

  /**
   * Creates a {@link Sidebar} with max amount of lines set to {@value Sidebar#MAX_LINES} (vanilla limit).
   *
   * @return Sidebar
   */
  default @NotNull Sidebar createSidebar() {
    return createSidebar(Sidebar.MAX_LINES, null);
  }

  /**
   * Creates a {@link Sidebar}.
   *
   * @param maxLines Max amount of lines the sidebar will have.
   *                 Note that vanilla clients can only display {@value Sidebar#MAX_LINES} lines at once
   * @return Sidebar
   */
  default @NotNull Sidebar createSidebar(@Range(from = 1, to = Integer.MAX_VALUE) int maxLines) {
    return createSidebar(maxLines, null);
  }

  /**
   * Creates a {@link Sidebar}.
   *
   * @param maxLines Max amount of lines the sidebar will have.
   *                 Note that vanilla clients can only display {@value Sidebar#MAX_LINES} lines at once
   * @param locale   Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s
   *                 or null if the locale should depend on the player
   * @return Sidebar
   */
  @NotNull Sidebar createSidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @Nullable Locale locale);

  /**
   * Creates a {@link TeamManager}.
   *
   * @return TeamManager
   */
  @NotNull TeamManager createTeamManager();

  @NotNull ObjectiveManager createObjectiveManager();

  /**
   * Closes this scoreboard-library instance.
   * Should always be called when the plugin is disabled
   */
  void close();

  /**
   * @return Whether this scoreboard-library instance is closed
   */
  boolean closed();
}
