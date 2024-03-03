package net.megavex.scoreboardlibrary.api;

import net.megavex.scoreboardlibrary.api.exception.NoPacketAdapterAvailableException;
import net.megavex.scoreboardlibrary.api.objective.ObjectiveManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

import java.lang.reflect.InvocationTargetException;
import java.util.Locale;

/**
 * Entrypoint of the library. To create an instance of this interface, use {@link #loadScoreboardLibrary}.
 * For unit tests, take a look at {@link net.megavex.scoreboardlibrary.api.noop.NoopScoreboardLibrary}.
 * Note: this class is thread-safe, meaning you can safely use it from multiple threads at a time.
 */
@ApiStatus.NonExtendable
public interface ScoreboardLibrary {
  /**
   * Creates an instance of {@link ScoreboardLibrary}.
   *
   * @param plugin Your plugin instance
   * @throws NoPacketAdapterAvailableException if there is no packet adapter available for the current server version
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
   * Creates a new {@link Sidebar};
   *
   * @return Newly created sidebar
   */
  default @NotNull Sidebar createSidebar() {
    return createSidebar(Sidebar.MAX_LINES, null);
  }

  /**
   * Creates a new {@link Sidebar}.
   *
   * @param maxLines Max amount of lines the sidebar will have.
   *                 Note that vanilla clients can only display at most {@value Sidebar#MAX_LINES}
   * @return Newly created sidebar
   */
  default @NotNull Sidebar createSidebar(@Range(from = 1, to = Integer.MAX_VALUE) int maxLines) {
    return createSidebar(maxLines, null);
  }

  /**
   * Creates a new {@link Sidebar}.
   *
   * @param maxLines Max amount of lines the sidebar will have.
   *                 Note that vanilla clients can only display at most {@value Sidebar#MAX_LINES}
   * @param locale   Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s,
   *                 or null if the locale should depend on the player
   * @return Newly created sidebar
   */
  @NotNull Sidebar createSidebar(@Range(from = 1, to = Integer.MAX_VALUE) int maxLines, @Nullable Locale locale);

  /**
   * Creates a new {@link TeamManager}.
   *
   * @return Newly created team manager
   */
  @NotNull TeamManager createTeamManager();

  /**
   * Creates a new {@link ObjectiveManager}.
   *
   * @return Newly created objective manager
   */
  @NotNull ObjectiveManager createObjectiveManager();

  /**
   * Closes this scoreboard library instance.
   * You should call this in {@link JavaPlugin#onDisable()}.
   */
  void close();

  /**
   * @return Whether this scoreboard library instance is closed
   */
  boolean closed();
}
