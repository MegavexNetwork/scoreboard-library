package net.megavex.scoreboardlibrary.api;

import java.util.Collection;
import java.util.Locale;
import net.megavex.scoreboardlibrary.api.interfaces.Closeable;
import net.megavex.scoreboardlibrary.api.interfaces.ComponentTranslator;
import net.megavex.scoreboardlibrary.api.interfaces.HasScoreboardManager;
import net.megavex.scoreboardlibrary.api.sidebar.Sidebar;
import net.megavex.scoreboardlibrary.api.team.TeamManager;
import net.megavex.scoreboardlibrary.internal.ScoreboardManagerProvider;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.Range;

public interface ScoreboardManager extends Closeable, HasScoreboardManager {

  /**
   * Gets the ScoreboardManager for a plugin
   *
   * @param plugin Plugin
   * @return ScoreboardManager
   */
  static ScoreboardManager scoreboardManager(JavaPlugin plugin) {
    return ScoreboardManagerProvider.instance().scoreboardManager(plugin);
  }

  @Override
  default @NotNull ScoreboardManager scoreboardManager() {
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
  default @NotNull Sidebar sidebar(int maxLines) {
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
  default @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @Nullable Locale locale) {
    return sidebar(maxLines, ComponentTranslator.GLOBAL, locale);
  }

  /**
   * Creates a {@link Sidebar}
   *
   * @param maxLines            Max sidebar lines
   * @param componentTranslator Component translator
   * @param locale              Locale which will be used for translating {@link net.kyori.adventure.text.TranslatableComponent}s
   *                            or null if the locale should depend on the player
   * @return Sidebar
   */
  @NotNull Sidebar sidebar(@Range(from = 1, to = Sidebar.MAX_LINES) int maxLines, @NotNull ComponentTranslator componentTranslator, @Nullable Locale locale);

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
  default @NotNull TeamManager teamManager() {
    return teamManager(ComponentTranslator.GLOBAL);
  }

  /**
   * Creates a {@link TeamManager}
   *
   * @param componentTranslator Component translator
   * @return TeamManager
   */
  @NotNull TeamManager teamManager(@NotNull ComponentTranslator componentTranslator);

  /**
   * Gets the team managers associated with this {@link JavaPlugin}
   *
   * @return Team Managers
   */
  @NotNull Collection<TeamManager> teamManagers();
}
